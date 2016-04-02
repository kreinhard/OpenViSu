package org.openvisu.video;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.Validate;
import org.openvisu.OpenVisuConfig;

/**
 * Caches files.
 * @author kai
 *
 */
public abstract class AbstractFileCache
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AbstractFileCache.class);

  protected File directory;

  private long refreshIntervall = 60 * 60 * 1000; // 1 hour is the refresh interval.

  /**
   * in ms.
   */
  private long expireTimeInMs;

  private long lastCleanup = 0;

  private long currentCleanUpStartTime = -1;

  private int numberOfDeletedFiles = 0;

  private int numberOfDeletedDirs = 0;

  protected AbstractFileCache()
  {
  }

  public File getDirectory()
  {
    return directory;
  }

  /**
   * Gets the configured directory including the given fileOrDirectory.
   * @param fileOrDirectory.
   * @return
   */
  public File getFile(String fileOrDirectory)
  {
    Validate.isTrue(new File(fileOrDirectory).isAbsolute() == false,
        "getFile(String) makes no sense for absolute fileOrDirectory param: " + fileOrDirectory);
    return new File(directory, fileOrDirectory);
  }

  /**
   * Creates directory if not already exist or cleans existing directory.
   */
  protected void init(String configKeyDir, String defaultDir, String configKeyExpireTime, long defaultExpireTimeInHours)
  {
    OpenVisuConfig config = OpenVisuConfig.instance();
    String dir = config.getProperty(configKeyDir, config.getBaseDir() + File.separatorChar + defaultDir).replace("${base.dir}",
        config.getBaseDir());
    directory = new File(dir);
    expireTimeInMs = OpenVisuConfig.instance().getProperty(configKeyExpireTime, defaultExpireTimeInHours) * 60 * 60 * 1000;
    if (directory.exists() == false) {
      log.info("Creating " + this.getClass().getName() + " cache directory: " + directory.getAbsolutePath());
      if (directory.mkdirs() == false) {
        log.fatal("Couldn't create "
            + this.getClass().getName()
            + " cache directory '"
            + directory.getAbsolutePath()
            + "'! Please configure another directory in '"
            + OpenVisuConfig.instance().getConfigFile().getAbsolutePath()
            + "': "
            + configKeyDir
            + "=...");
      }
    } else {
      refresh();
    }
  }

  protected void refresh()
  {
    if (System.currentTimeMillis() - lastCleanup < refreshIntervall) {
      // Nothing to do.
      return;
    }
    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.submit(() -> {
      synchronized (this) {
        if (currentCleanUpStartTime > 0) {
          // Another jobs is already running.
          log.debug("Another clean-up jobs seems to be running. Do nothing for now.");
          return;
        }
        currentCleanUpStartTime = System.currentTimeMillis();
      }
      try {
        log.info("Cleaning up " + this.getClass().getName() + " in directory: " + directory.getAbsolutePath());
        lastCleanup = currentCleanUpStartTime;
        numberOfDeletedFiles = 0;
        numberOfDeletedDirs = 0;

        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
          // cache dir seems to be empty. Do nothing.
          return;
        }
        for (File file : files) {
          deleteEmptySubdirectoriesAndExpiredFiles(file);
        }
      } finally {
        log.info(this.getClass().getName()
            + ": Number of deleted files: "
            + numberOfDeletedFiles
            + ", number of deleted empty directories: "
            + numberOfDeletedDirs);
        currentCleanUpStartTime = -1;
      }
    });
  }

  private boolean deleteEmptySubdirectoriesAndExpiredFiles(File file)
  {
    if (file.isFile() == true) { // Not a directory.
      BasicFileAttributes attr;
      try {
        attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
      } catch (IOException e) {
        log.error("Can't get time of creation of file '" + file.getAbsolutePath() + "'. Can't check and delete it.");
        return false;
      }
      FileTime fileTime = attr.lastAccessTime();
      if (currentCleanUpStartTime - fileTime.toMillis() > expireTimeInMs) {
        return deleteFile(file);
      }
      return false; // File not expired.
    }
    // file is a directory.
    File[] files = file.listFiles();
    if (files == null || files.length == 0) {
      return deleteFile(file); // Directory has now sub entries, so delete it.
    }
    boolean empty = true;
    for (File child : files) {
      if (deleteEmptySubdirectoriesAndExpiredFiles(child) == false) {
        // Has at least one sub entry.
        empty = false;
      }
    }
    if (empty == true) {
      return deleteFile(file);
    }
    return false;
  }

  private boolean deleteFile(File file)
  {
    boolean isDirectory = file.isDirectory();
    if (file.delete() == false) {
      if (isDirectory == true) {
        log.error("Cant't delete directory '" + file.getAbsolutePath() + "'.");
      } else {
        log.error("Cant't delete file '" + file.getAbsolutePath() + "'.");
      }
      return false;
    }
    if (isDirectory == true) {
      ++numberOfDeletedDirs;
    } else {
      ++numberOfDeletedFiles;
    }
    return true;
  }
}

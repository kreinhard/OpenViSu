package org.openvisu.video;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openvisu.OpenVisuConfig;

/**
 * Caches remote images on local filesystem.
 * @author kai
 *
 */
public class FileCache
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FileCache.class);

  private static final FileCache instance = new FileCache();

  /**
   * Expire time in ms.
   */
  private static final long DEFAULT_EXPIRE_TIME_OF_CACHED_FILES_HOURS = 24;

  private static final String DEFAULT_CACHE_DIR = "./cache";

  private static final String CONFIG_KEY_CACHE_DIR = "cache.dir";

  private static final String CONFIG_KEY_CACHE_EXPIRE_TIME = "cache.expireTimeInHours";

  private static DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd_HH-mm-ss_SSS");

  public static FileCache instance()
  {
    return instance;
  }

  private File cacheDir, workingDir;

  private long refreshIntervall = 60 * 60 * 1000; // 1 hour is the refresh interval.

  /**
   * in ms.
   */
  private long expireTime;

  private long lastCleanup = 0;

  private long currentCleanUpStartTime = -1;

  private int numberOfDeletedFiles = 0;

  private int numberOfDeletedDirs = 0;

  private FileCache()
  {
    expireTime = OpenVisuConfig.instance().getProperty(CONFIG_KEY_CACHE_EXPIRE_TIME, DEFAULT_EXPIRE_TIME_OF_CACHED_FILES_HOURS)
        * 60
        * 60
        * 1000;
    cacheDir = new File(OpenVisuConfig.instance().getProperty(CONFIG_KEY_CACHE_DIR, DEFAULT_CACHE_DIR));
    if (cacheDir.exists() == false) {
      log.info("Creating images cache directory: " + cacheDir.getAbsolutePath());
      if (cacheDir.mkdirs() == false) {
        log.fatal("Couldn't create cache directory for images '"
            + cacheDir.getAbsolutePath()
            + "'! Please configure another directory in '"
            + OpenVisuConfig.instance().getConfigFile().getAbsolutePath()
            + "': "
            + CONFIG_KEY_CACHE_DIR
            + "=...");
      }
    } else {
      cleanUp();
    }
    workingDir = new File(cacheDir, "work");
    if (workingDir.exists() == false) {
      log.info("Creating working directory in images cache directory: " + workingDir.getAbsolutePath());
      if (workingDir.mkdirs() == false) {
        log.fatal("Couldn't create working cache directory for images '" + workingDir.getAbsolutePath() + "'!");
      }
    }
  }

  /**
   * @param path
   * @param ba File content
   */
  public void writeImage(String path, byte[] ba)
  {
    File file = new File(cacheDir, path);
    try {
      FileUtils.writeByteArrayToFile(file, ba);
    } catch (IOException e) {
      log.error("Can't write file '" + file.getAbsolutePath() + "' to cache! " + e.getMessage(), e);
    }
  }

  public byte[] getCachedImage(String path)
  {
    cleanUp();
    File file = new File(cacheDir, path);
    if (file.exists() == false) {
      return null;
    }
    try {
      byte[] ba = FileUtils.readFileToByteArray(file);
      // file.setLastModified(System.currentTimeMillis());
      return ba;
    } catch (IOException e) {
      log.error("Can't read cached file '" + file.getAbsolutePath() + "'. Try to delete it manually. " + e.getMessage(), e);
      return null;
    }
  }

  /**
   * If subdirectory of destFile doesn't exist it will be created automatically.
   * @param srcImage
   * @param type
   * @param destFile relative path inside cache working directory.
   */
  public void copyImageToCache(Image srcImage, ImageType type, String destFile)
  {
    cleanUp();
    Validate.isTrue(new File(destFile).isAbsolute() == false,
        "Don't use absolute destFile parameter in copyImageToCache(Image, ImageType, String): " + destFile);
    try {
      File file = new File(workingDir, destFile);
      File dir = file.getParentFile();
      if (dir.exists() == false) {
        if (dir.mkdirs() == false) {
          String error = "Couldn't create cache directory '" + dir.getAbsolutePath() + "'!";
          log.error(error);
          throw new RuntimeException(error);
        }
      }
      File srcFile = new File(cacheDir, srcImage.getFile(type));
      FileUtils.copyFile(srcFile, file);
    } catch (IOException e) {
      log.error("Can't copy file " + srcImage.getFile(type) + " to " + destFile + ". " + e.getMessage(), e);
    }
  }

  /**
   * @param prefix
   * @return prefix + "yyyy-MM-dd_HH-mm-ss_SSS"
   */
  public String getNewWorkingDirectory(String prefix)
  {
    return prefix + dateFormatter.print(System.currentTimeMillis());
  }

  /**
   * Gets the configured working dir including the given subdir.
   * @param subdir - created by e. g. {@link #getNewWorkingDirectory(String)}.
   * @return
   */
  public File getWorkingDirectory(String subdir)
  {
    return new File(workingDir, subdir);
  }

  private void cleanUp()
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
          log.warn("Another clean-up jobs seems to be running. Do nothing for now.");
          return;
        }
        currentCleanUpStartTime = System.currentTimeMillis();
      }
      try {
        log.info("Cleaning up image cache in directory: " + cacheDir.getAbsolutePath());
        lastCleanup = currentCleanUpStartTime;
        numberOfDeletedFiles = 0;
        numberOfDeletedDirs = 0;

        File[] files = cacheDir.listFiles();
        if (files == null || files.length == 0) {
          // image cache dir seems to be empty. Do nothing.
          return;
        }
        for (File file : files) {
          deleteEmptySubdirectoriesAndExpiredFiles(file);
        }
      } finally {
        log.info("Number of deleted files: " + numberOfDeletedFiles + ", number of deleted empty directories: " + numberOfDeletedDirs);
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
      if (currentCleanUpStartTime - fileTime.toMillis() > expireTime) {
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

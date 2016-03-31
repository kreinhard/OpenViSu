package org.openvisu.zoneminder.remote;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import org.apache.commons.io.FileUtils;
import org.openvisu.OpenVisuConfig;

/**
 * Caches remote images on local filesystem.
 * @author kai
 *
 */
public class ImageCache
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ImageCache.class);

  private static final ImageCache instance = new ImageCache();

  /**
   * Expire time in ms.
   */
  private static final long DEFAULT_EXPIRE_TIME_OF_CACHED_FILES_HOURS = 24;

  private static final String IMAGE_CACHE_DIR_CONFIG_KEY = "zoneminder.imagecache.dir";

  public static ImageCache instance()
  {
    return instance;
  }

  private File cacheDir;

  private long refreshIntervall = 60 * 60 * 1000; // 1 hour is the refresh interval.

  /**
   * in ms.
   */
  private long expireTime;

  private long lastCleanup = 0;

  private long currentCleanUpStartTime = -1;

  private int numberOfDeletedFiles = 0;

  private int numberOfDeletedDirs = 0;

  private ImageCache()
  {
    expireTime = OpenVisuConfig.instance().getProperty("zoneminder.imagecache.expireTimeInHours", DEFAULT_EXPIRE_TIME_OF_CACHED_FILES_HOURS)
        * 60
        * 60
        * 1000;
    cacheDir = new File(OpenVisuConfig.instance().getProperty(IMAGE_CACHE_DIR_CONFIG_KEY, "./imagecache"));
    if (cacheDir.exists() == false) {
      log.info("Creating images cache directory: " + cacheDir.getAbsolutePath());
      if (cacheDir.mkdirs() == false) {
        log.fatal("Couldn't create cache directory for images '"
            + cacheDir.getAbsolutePath()
            + "'! Please configure another directory in '"
            + OpenVisuConfig.instance().getConfigFile().getAbsolutePath()
            + "': "
            + IMAGE_CACHE_DIR_CONFIG_KEY
            + "=...");
      }
    } else {
      cleanUp();
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
      //file.setLastModified(System.currentTimeMillis());
      return ba;
    } catch (IOException e) {
      log.error("Can't read cached file '" + file.getAbsolutePath() + "'. Try to delete it manually. " + e.getMessage(), e);
      return null;
    }
  }

  private void cleanUp()
  {
    if (System.currentTimeMillis() - lastCleanup < refreshIntervall) {
      // Nothing to do.
      return;
    }
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

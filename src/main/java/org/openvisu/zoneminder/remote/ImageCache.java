package org.openvisu.zoneminder.remote;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.EmptyFileFilter;
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

  public byte[] getCachedImage(String path)
  {
    cleanUp();
    File file = new File(cacheDir, path);
    if (file.exists() == false) {
      return null;
    }
    try {
      byte[] ba = FileUtils.readFileToByteArray(file);
      return ba;
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  private void cleanUp()
  {
    if (System.currentTimeMillis() - lastCleanup < refreshIntervall) {
      // Nothing to do.
      return;
    }
    log.info("Cleaning up image cache in directory: " + cacheDir.getAbsolutePath());
    Collection<File> files = FileUtils.listFiles(cacheDir, null, true); // Get all files recursive of image cache dir.
    long now = System.currentTimeMillis();
    for (File file : files) {
      BasicFileAttributes attr;
      try {
        attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
      } catch (IOException e) {
        log.error("Can't get time of creation of file '" + file.getAbsolutePath() + "'. Can't check and delete it.");
        continue;
      }
      FileTime fileTime = attr.creationTime();
      if (now - fileTime.toMillis() > expireTime) {
        if (file.delete() == false) {
          log.error("Cant't delete file '" + file.getAbsolutePath() + "'.");
        }
      }
    }
    // Remove empty directories:
    // This code may-be has to be executed several time for deleting all empty sub-directories. Therefore several clean-up runs are needed
    // before all empty directories are removed.
    Collection<File> emptyDirs = FileUtils.listFilesAndDirs(cacheDir, DirectoryFileFilter.INSTANCE, EmptyFileFilter.EMPTY);
    for (File emptyDir : emptyDirs) {
      emptyDir.delete(); // Try to delete this dir (may-be, it's not empty anymore, then it will not be deleted).
    }
  }
}

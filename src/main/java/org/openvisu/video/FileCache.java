package org.openvisu.video;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * Caches remote images on local filesystem.
 * @author kai
 *
 */
public class FileCache extends AbstractFileCache
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FileCache.class);

  private static final FileCache instance = new FileCache();

  private static final long DEFAULT_EXPIRE_TIME_OF_CACHED_FILES_HOURS = 24;

  private static final String DEFAULT_CACHE_DIR = "cache"; // ${base.dir}+ File.separatorChar + "cache";

  private static final String CONFIG_KEY_CACHE_DIR = "base.cache.dir";

  private static final String CONFIG_KEY_CACHE_EXPIRE_TIME = "base.cache.expireTimeInHours";

  public static FileCache instance()
  {
    return instance;
  }

  private FileCache()
  {
    init(CONFIG_KEY_CACHE_DIR, DEFAULT_CACHE_DIR, CONFIG_KEY_CACHE_EXPIRE_TIME, DEFAULT_EXPIRE_TIME_OF_CACHED_FILES_HOURS);
  }
  
  /**
   * @param path
   * @param ba File content
   */
  public void writeImage(String path, byte[] ba)
  {
    File file = new File(directory, path);
    try {
      FileUtils.writeByteArrayToFile(file, ba);
    } catch (IOException e) {
      log.error("Can't write file '" + file.getAbsolutePath() + "' to cache! " + e.getMessage(), e);
    }
  }

  public byte[] getCachedImage(String path)
  {
    refresh();
    File file = new File(directory, path);
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
}

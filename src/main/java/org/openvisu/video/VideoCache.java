package org.openvisu.video;

/**
 * Caches remote images on local filesystem.
 * @author kai
 *
 */
public class VideoCache extends AbstractFileCache
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(VideoCache.class);

  private static final VideoCache instance = new VideoCache();

  private static final long DEFAULT_EXPIRE_TIME_OF_CACHED_FILES_HOURS = 2;

  private static final String DEFAULT_CACHE_DIR = "video-cache"; // ${base.dir} + File.separatorChar + "image-cache";

  private static final String CONFIG_KEY_CACHE_DIR = "base.cache.videos.dir";

  private static final String CONFIG_KEY_CACHE_EXPIRE_TIME = "base.cache.videos.expireTimeInHours";

  public static VideoCache instance()
  {
    return instance;
  }

  private VideoCache()
  {
    init(CONFIG_KEY_CACHE_DIR, DEFAULT_CACHE_DIR, CONFIG_KEY_CACHE_EXPIRE_TIME, DEFAULT_EXPIRE_TIME_OF_CACHED_FILES_HOURS);
  }
}

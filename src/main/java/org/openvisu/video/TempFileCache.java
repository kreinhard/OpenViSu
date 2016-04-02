package org.openvisu.video;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Caches remote images on local filesystem.
 * @author kai
 *
 */
public class TempFileCache extends AbstractFileCache
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TempFileCache.class);

  private static final TempFileCache instance = new TempFileCache();

  private static final long DEFAULT_EXPIRE_TIME_OF_CACHED_FILES_HOURS = 1;

  private static final String DEFAULT_TMP_DIR = "tmp"; // ${base.dir} + File.separatorChar + "tmp";

  private static final String CONFIG_KEY_TMP_DIR = "base.cache.tmp.dir";

  private static final String CONFIG_KEY_TMP_EXPIRE_TIME = "base.cache.tmp.expireTimeInHours";

  private static DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd_HH-mm-ss_SSS");

  public static TempFileCache instance()
  {
    return instance;
  }

  private TempFileCache()
  {
    init(CONFIG_KEY_TMP_DIR, DEFAULT_TMP_DIR, CONFIG_KEY_TMP_EXPIRE_TIME, DEFAULT_EXPIRE_TIME_OF_CACHED_FILES_HOURS);
  }

  /**
   * If subdirectory of destFile doesn't exist it will be created automatically.
   * @param cachedSrcImage src image of {@link ImageCache}.
   * @param type
   * @param destFile relative path inside tmp directory.
   */
  public void copyImageToTmp(Image cachedSrcImage, ImageType type, String destFile)
  {
    refresh();
    Validate.isTrue(new File(destFile).isAbsolute() == false,
        "Don't use absolute destFile parameter in copyImageToTmp(Image, ImageType, String): " + destFile);
    try {
      File file = new File(directory, destFile);
      File dir = file.getParentFile();
      if (dir.exists() == false) {
        if (dir.mkdirs() == false) {
          String error = "Couldn't create cache directory '" + dir.getAbsolutePath() + "'!";
          log.error(error);
          throw new RuntimeException(error);
        }
      }
      File srcFile = new File(ImageCache.instance().getDirectory(), cachedSrcImage.getFile(type));
      FileUtils.copyFile(srcFile, file);
    } catch (IOException e) {
      log.error("Can't copy file " + cachedSrcImage.getFile(type) + " to " + destFile + ". " + e.getMessage(), e);
    }
  }

  /**
   * @param prefix
   * @return prefix + "yyyy-MM-dd_HH-mm-ss_SSS"
   */
  public String getNewTmpDirectory(String prefix)
  {
    return prefix + dateFormatter.print(System.currentTimeMillis());
  }
}

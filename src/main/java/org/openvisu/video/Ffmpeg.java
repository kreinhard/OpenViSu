package org.openvisu.video;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.Validate;

/**
 * Create and modify videos with ffmeg (calls System.exec).
 * @author kai
 *
 */
public class Ffmpeg
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Ffmpeg.class);

  private static final String CONFIG_KEY_FFMPEG = "command.ffmpeg.path";

  private static final String DEFAULT_FFMPEG = "ffmpeg";

  private String framerate = "2";

  /**
   * If the destFile does already exist then nothing will be done.
   * @param images
   * @param type
   * @param destFile relativ to cache directory 'work'. This should be unique (camera id, event, frame, type of video etc.) for proper caching.
   */
  public void generate(Collection< ? extends Image> images, ImageType type, String destFile)
  {
    Validate.notEmpty(images, "No images given in method generate(Collection<Image>, ImageType).");
    // ffmpeg -y -framerate 2 -pattern_type glob -i '1_*.jpg' out.mp4
    // -y - Overwrite existing files
    TempFileCache tmpFileCache = TempFileCache.instance();
    VideoCache videoCache = VideoCache.instance();
    File destination = videoCache.getFile(destFile);
    if (destination.exists() == true) {
      // Exists already.
      return;
    }
    String command = CommandExecuter.instance().getCommandPath("ffmpeg", CONFIG_KEY_FFMPEG, DEFAULT_FFMPEG);
    String workingDir = tmpFileCache.getNewTmpDirectory("ffmpeg-");
    int counter = 0;
    String imageExtension = FilenameUtils.getExtension(images.iterator().next().getFile());
    for (Image image : images) {
      tmpFileCache.copyImageToTmp(image, type,
          new File(workingDir, "image-" + VideoUtils.getFormattedFrameId(++counter) + "." + imageExtension).getPath());
    }
    String[] commandWithArgs = { command, "-y", "-framerate", "50", "-pattern_type", "glob", "-i", "image-*." + imageExtension,
        destination.getAbsolutePath()};
    File executionDir = tmpFileCache.getFile(workingDir);
    CommandExecuter.instance().execute(executionDir, commandWithArgs);
  }

  public String getFramerate()
  {
    return framerate;
  }

  /**
   * @param framerate
   * @return this for fluent pattern.
   */
  public Ffmpeg setFramerate(String framerate)
  {
    this.framerate = framerate;
    return this;
  }
}

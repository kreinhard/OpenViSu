package org.openvisu.video;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
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

  public void generate(Collection< ? extends Image> images, ImageType type)
  {
    Validate.notEmpty(images, "No images given in method generate(Collection<Image>, ImageType).");
    // ffmpeg -y -framerate 2 -pattern_type glob -i '1_*.jpg' out.mp4
    // -y - Overwrite existing files
    try {
      String command = CommandExecuter.instance().getCommandPath("ffmpeg", CONFIG_KEY_FFMPEG, DEFAULT_FFMPEG);
      FileCache cache = FileCache.instance();
      String workingDir = cache.getNewWorkingDirectory("ffmpeg-");
      int counter = 0;
      String imageExtension = FilenameUtils.getExtension(images.iterator().next().getFile());
      for (Image image : images) {
        cache.copyImageToCache(image, type, new File(workingDir, "image-" + VideoUtils.getFormattedFrameId(++counter) + "." + imageExtension).getPath());
      }
      ProcessBuilder pb = new ProcessBuilder(command, "-y", "-framerate", "2", "-pattern_type", "glob", "-i", "image-*." + imageExtension,
          "out-test.mp4");
      File executionDir = cache.getWorkingDirectory(workingDir);
      log.info("Executing command: " + pb.command() + " in dir: " + executionDir);
      pb.directory(executionDir);
      pb.redirectOutput(Redirect.INHERIT);
      pb.redirectError(Redirect.INHERIT);
      Process p = pb.start();
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
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

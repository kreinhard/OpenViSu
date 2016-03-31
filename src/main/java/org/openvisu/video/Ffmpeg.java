package org.openvisu.video;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;

import org.openvisu.OpenVisuConfig;

/**
 * Create and modify videos with ffmeg (calls System.exec).
 * @author kai
 *
 */
public class Ffmpeg
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Ffmpeg.class);

  private String framerate = "2";

  public void generate()
  {
    // ffmpeg -framerate 2 -pattern_type glob -i '1_*.jpg' out.mp4
    try {
      String ffmpeg = OpenVisuConfig.instance().getProperty("environment.video.ffmpeg.path", "/opt/local/bin/ffmpeg");

      String command = ffmpeg;// + " ";
      ProcessBuilder pb = new ProcessBuilder(command, "-framerate", "2", "-pattern_type", "glob", "-i", "1_2016-03-30_20-40*.jpg",
          "out-test.mp4");
      File workingDir = new File(System.getProperty("user.dir"), "imagecache");
      log.info("Executing command: " + pb.command() + " in dir: " + workingDir.getAbsolutePath());
      pb.directory(workingDir);
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

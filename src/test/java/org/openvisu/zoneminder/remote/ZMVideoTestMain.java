package org.openvisu.zoneminder.remote;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;
import org.openvisu.OpenVisuConfig;
import org.openvisu.video.Ffmpeg;
import org.openvisu.video.Image;
import org.openvisu.video.ImageType;
import org.openvisu.video.VideoSize;
import org.openvisu.video.VideoType;
import org.openvisu.video.VideoUtils;
import org.openvisu.zoneminder.ZMEvent;

public class ZMVideoTestMain
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ZMVideoTestMain.class);

  private ZMClientSession session;

  private ZMApiRepository repo;

  /**
   * Main method.
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws Exception
  {
    ZMVideoTestMain main = new ZMVideoTestMain();
    main.test1();
    // main.writeEventJson("1542");
    main.close();
  }

  public void test1()
  {
    ZMEvent event = repo.getEvent("3453");
    List< ? extends Image> images = repo.readImages(event.getId());
    for (Image image : images) {
      if (image.hasAnalyseFile() == true) {
        session.getEventImage(image.getAnalyseFile());
      } else {
        session.getEventImage(image.getFile());
      }
    }
    Ffmpeg ffmpeg = new Ffmpeg();
    ffmpeg.generate(images, ImageType.ANALYSIS,
        "ZMVideoTestMain" + VideoUtils.getVideoFilename("5", "42", VideoType.ANALYSIS, VideoSize.NORMAL));
  }

  public ZMVideoTestMain()
  {
    OpenVisuConfig cfg = OpenVisuConfig.instance();
    String url = cfg.getProperty("zoneminder.url", "http://localhost/zm");
    String user = cfg.getProperty("zoneminder.viewUser", "view");
    String password = cfg.getProperty("zoneminder.viewUser.password", "test");
    session = new ZMClientSession(url);
    session.authenticate(user, password);
    repo = new ZMApiRepository(session);
  }

  public void close()
  {
    session.closeQuietly();
  }

}

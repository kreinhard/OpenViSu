package org.openvisu.zoneminder.remote;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;
import org.openvisu.OpenVisuConfig;
import org.openvisu.video.Ffmpeg;
import org.openvisu.video.Image;
import org.openvisu.video.ImageType;
import org.openvisu.zoneminder.ZMEvent;
import org.openvisu.zoneminder.ZMFrame;
import org.openvisu.zoneminder.ZMFrameType;
import org.openvisu.zoneminder.ZMImage;

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
    DateTime from = new DateTime().minusDays(1).withTime(10, 0, 0, 0);
    DateTime until = from.plusHours(1);
    List<ZMEvent> events = repo.getEvents(from.toDate(), until.toDate());
    log.info("" + events.size() + " events read for all monitors.");
    if (events.size() == 0) {
      log.info("No events found. Giving up.");
      return;
    }
    List< ? extends Image> images = repo.readImages(events.get(0).getId());
    for (Image image : images) {
      session.getEventImage(image.getFile());
    }
    Ffmpeg ffmpeg = new Ffmpeg();
    ffmpeg.generate(images, ImageType.NORMAL);
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

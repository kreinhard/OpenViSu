package org.openvisu.zoneminder.json;

import java.io.IOException;
import java.util.List;

import org.openvisu.OpenVisuConfig;
import org.openvisu.zoneminder.ZMConfig;
import org.openvisu.zoneminder.ZMEvent;
import org.openvisu.zoneminder.ZMMonitor;

/**
 * Main class.
 *
 */
public class ZMApiRepositoryTestMain
{
   private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ZMApiRepositoryTestMain.class);

  /**
   * Main method.
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws Exception
  {
    OpenVisuConfig cfg = OpenVisuConfig.instance();
    String url = cfg.getProperty("zoneminder.url", "http://localhost/zm");
    String user = cfg.getProperty("zoneminder.viewUser", "view");
    String password = cfg.getProperty("zoneminder.viewUser.password", "test");
    ZMClientSession session = new ZMClientSession(url);
    session.authenticate(user, password);
    ZMApiRepository repo = new ZMApiRepository(session);
    List<ZMMonitor> monitors = repo.getMonitors();
    log.info("Number of read monitors: " + (monitors != null ? monitors.size() : 0));
    ZMConfig config = repo.getConfig("ZM_WEB_EVENTS_PER_PAGE");
    log.info("Config parameter WEB_EVENTS_PER_PAGE=" + config.getIntValue());
    List<ZMEvent> events = repo.getEvents();
    int counter = 0;
    for (ZMEvent event : events) {
      if (event.getAlarmFrames() > 0) {
        log.info("Event with alarms: " + event);
        repo.getEvent(event.getId());
        // for (ZMFrame frame : event.getFrames()) {
        // if ("Alarm".equals(frame.getValue("Type")) == true) {
        // log.info("Frame" + frame);
        // }
        // }
        if (++counter >= 5) {
          // get only frames of the first 5 events
          break;
        }
      }
    }
    session.closeQuietly();
    // https://debian/zm/events/1/16/03/27/23/40/00/00517-analyse.jpg
  }
}

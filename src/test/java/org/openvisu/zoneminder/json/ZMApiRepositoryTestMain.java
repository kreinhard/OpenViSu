package org.openvisu.zoneminder.json;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openvisu.OpenVisuConfig;
import org.openvisu.zoneminder.ZMConfig;
import org.openvisu.zoneminder.ZMEvent;
import org.openvisu.zoneminder.ZMMonitor;

/**
 * Main class for testing ZoneMinder API and playing around.
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
    int numberOfMonitors = monitors != null ? monitors.size() : 0;
    log.info("Number of read monitors: " + numberOfMonitors);
    if (numberOfMonitors > 0) {
      ZMMonitor monitor = monitors.get(0);
      if (monitor.getId() == null) {
        log.error("******* Oups, monitorId is null for monitor: " + monitor);
      }
      List<ZMEvent> events = repo.getAllEvents(monitor.getId());
      log.info("" + events.size() + " events read for monitor: " + monitor + " (" + getNumberOfNewEvents(events) + " new events)");
    }
    ZMConfig config = repo.getConfig("ZM_WEB_EVENTS_PER_PAGE");
    log.info("Config parameter WEB_EVENTS_PER_PAGE=" + config.getIntValue());
    {
      List<ZMEvent> events = repo.getAllEvents();
      log.info("" + events.size() + " events read for all monitors (" + getNumberOfNewEvents(events) + " new events)");
      int counter = 0;
      for (ZMEvent event : events) {
        if (event.getNumberOfAlarmFrames() > 0) {
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
      Calendar cal = Calendar.getInstance();
      cal.clear();
      cal.set(2016, Calendar.MARCH, 28, 0, 0, 0);
      Date from = cal.getTime();
      cal.set(2016, Calendar.MARCH, 28, 23, 59, 59);
      Date until = cal.getTime();
      events = repo.getEvents(from, until);
      log.info("" + events.size() + " events read for all monitors (" + getNumberOfNewEvents(events) + " new events)");
      if (numberOfMonitors > 0) {
        ZMMonitor monitor = monitors.get(0);
        events = repo.getEvents(monitor.getId(), from, until);
        log.info("" + events.size() + " events read for monitor: " + monitor + " (" + getNumberOfNewEvents(events) + " new events)");
      }
    }
    session.closeQuietly();
  }

  private static int getNumberOfNewEvents(List<ZMEvent> events)
  {
    int numberOfNewEvents = 0;
    for (ZMEvent event : events) {
      if (event.isNewEvent() == true) {
        numberOfNewEvents++;
      }
    }
    return numberOfNewEvents;
  }
}

package org.openvisu.zoneminder.remote;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openvisu.OpenVisuConfig;
import org.openvisu.zoneminder.ZMConfig;
import org.openvisu.zoneminder.ZMEvent;
import org.openvisu.zoneminder.ZMFrame;
import org.openvisu.zoneminder.ZMFrameType;
import org.openvisu.zoneminder.ZMImage;
import org.openvisu.zoneminder.ZMMonitor;

/**
 * Main class for testing ZoneMinder API and playing around.
 */
public class ZMApiRepositoryTestMain
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ZMApiRepositoryTestMain.class);

  private ZMClientSession session;

  private ZMApiRepository repo;

  /**
   * Main method.
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws Exception
  {
    ZMApiRepositoryTestMain main = new ZMApiRepositoryTestMain();
    main.readMonitors().readConfig().readAllEvents();
    Calendar cal = Calendar.getInstance();
    cal.clear();
    cal.set(2016, Calendar.MARCH, 28, 0, 0, 0);
    Date from = cal.getTime();
    cal.set(2016, Calendar.MARCH, 28, 23, 59, 59);
    Date until = cal.getTime();
    main.readEvents(from, until);
    main.readAlarmImages("1542");
    // main.writeEventJson("1542");
    main.close();
  }

  public ZMApiRepositoryTestMain()
  {
    OpenVisuConfig cfg = OpenVisuConfig.instance();
    String url = cfg.getProperty("zoneminder.url", "http://localhost/zm");
    String user = cfg.getProperty("zoneminder.viewUser", "view");
    String password = cfg.getProperty("zoneminder.viewUser.password", "test");
    session = new ZMClientSession(url);
    session.authenticate(user, password);
    repo = new ZMApiRepository(session);
  }

  public ZMApiRepositoryTestMain readMonitors()
  {
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
    return this;
  }

  public ZMApiRepositoryTestMain readConfig()
  {
    ZMConfig config = repo.getConfig("ZM_WEB_EVENTS_PER_PAGE");
    log.info("Config parameter WEB_EVENTS_PER_PAGE=" + config.getIntValue());
    return this;
  }

  public ZMApiRepositoryTestMain readAllEvents()
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
    return this;
  }

  public ZMApiRepositoryTestMain readEvents(Date from, Date until)
  {
    List<ZMEvent> events = repo.getEvents(from, until);
    log.info("" + events.size() + " events read for all monitors (" + getNumberOfNewEvents(events) + " new events)");
    List<ZMMonitor> monitors = repo.getMonitors();
    if (monitors.size() > 0) {
      ZMMonitor monitor = monitors.get(0);
      events = repo.getEvents(monitor.getId(), from, until);
      log.info("" + events.size() + " events read for monitor: " + monitor + " (" + getNumberOfNewEvents(events) + " new events)");
    }
    return this;
  }

  public ZMApiRepositoryTestMain writeEventJson(String eventId)
  {
    String json = repo.getEventJson(eventId);
    try {
      FileWriter writer = new FileWriter("event-" + eventId + "-json.txt");
      writer.write(json);
      writer.close();
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    return this;
  }

  public ZMApiRepositoryTestMain readAlarmImages(String eventId)
  {
    List<ZMImage> images = repo.readImages(eventId);
    for (ZMImage image : images) {
      ZMFrame frame = image.getFrame();
      if (frame.getType() == ZMFrameType.ALARM) {
        session.getEventImage(image.getAnalyseFilename());
      }
    }
    return this;
  }

  public void close()
  {
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

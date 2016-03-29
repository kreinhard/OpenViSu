package org.openvisu.zoneminder.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openvisu.zoneminder.ZMConfig;
import org.openvisu.zoneminder.ZMEvent;
import org.openvisu.zoneminder.ZMFrame;
import org.openvisu.zoneminder.ZMFrameType;
import org.openvisu.zoneminder.ZMImage;
import org.openvisu.zoneminder.ZMMonitor;
import org.openvisu.zoneminder.ZMZone;
import org.springframework.util.CollectionUtils;

public class ZMApiRepository
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ZMApiRepository.class);

  private ZMClientSession session;

  private Map<String, ZMConfig> configMap;

  private List<ZMMonitor> monitors;

  public ZMApiRepository(ZMClientSession session)
  {
    this.session = session;
  }

  public List<ZMMonitor> getMonitors()
  {
    if (monitors == null) {
      String json;
      if (session.isTestMode() == true) {
        json = session.readFile("zoneminder-monitors-json.txt");
      } else {
        json = session.httpGet("api/monitors.json");
      }
      Json2MapReader jsonReader = new Json2MapReader(json);
      List<Map<String, ? >> monitorObjects = jsonReader.getList("monitors");

      ArrayList<ZMMonitor> newMonitors = new ArrayList<>();
      if (monitorObjects != null && monitorObjects.isEmpty() == false) {
        for (Map<String, ? > obj : monitorObjects) {
          @SuppressWarnings("unchecked")
          ZMMonitor monitor = new ZMMonitor((Map<String, String>) obj.get("Monitor"));
          newMonitors.add(monitor);
        }
      }
      monitors = newMonitors;
    }
    return monitors;
  }

  /**
   * Force reload of monitors from ZoneMinder.
   * @return this for fluent usage.
   */
  public ZMApiRepository refreshMonitors()
  {
    this.monitors = null;
    return this;
  }

  /**
   * Monitors are cached. If you want to get the current state of the monitor, please call {@link #refreshMonitors()} before.
   * @param monitorId
   * @return monitor read by {@link #getMonitors()} before.
   */
  public ZMMonitor getMonitor(String monitorId)
  {
    for (ZMMonitor monitor : getMonitors()) {
      if (monitorId.equals(monitor.getId()) == true) {
        return monitor;
      }
    }
    return null;
  }

  public List<ZMZone> getZones()
  {
    throw new UnsupportedOperationException("Not yet implemented.");
    // if (zones == null) {
    // String json;
    // json = session.httpGet("api/zones.json");
    // JsonReader jsonReader = new JsonReader(json);
    // List<Map<String, ? >> zoneObjects = jsonReader.getList("data");
    //
    // ArrayList<ZMZone> newZones = new ArrayList<>();
    // if (zoneObjects != null && zoneObjects.isEmpty() == false) {
    // for (Map<String, ? > obj : zoneObjects) {
    // @SuppressWarnings("unchecked")
    // ZMZone zone = new ZMZone((Map<String, String>) obj); // Doesn't work yet: Map of maps.
    // newZones.add(zone);
    // }
    // }
    // zones = newZones;
    // }
    // return zones;
  }

  public ZMConfig getConfig(String name)
  {
    if (configMap == null) {
      Map<String, ZMConfig> newConfigMap = new HashMap<>();
      String json;
      if (session.isTestMode() == true) {
        json = session.readFile("zoneminder-config-json.txt");
      } else {
        json = session.httpGet("api/configs.json");
      }
      Json2MapReader jsonReader = new Json2MapReader(json);
      List<Map<String, ? >> configObjects = jsonReader.getList("configs");
      if (configObjects != null && configObjects.isEmpty() == false) {
        for (Object obj : configObjects) {
          @SuppressWarnings("unchecked")
          Map<String, String> map = (Map<String, String>) ((Map<String, ? >) obj).get("Config");
          ZMConfig config = new ZMConfig(map);
          newConfigMap.put(config.getName(), config);
        }
      }
      configMap = newConfigMap;
    }
    return configMap.get(name);
  }

  /**
   * Force reload of config from ZoneMinder.
   * @return this for fluent usage.
   */
  public ZMApiRepository refreshConfig()
  {
    this.configMap = null;
    return this;
  }

  public List<ZMEvent> getAllEvents()
  {
    return getEvents("api/events.json");
  }

  /**
   * StartTime >=:from UND StartTime <=: until
   * @param from
   * @param until
   * @return
   */
  public List<ZMEvent> getEvents(Date from, Date until)
  {
    return getEvents("api/events/index" + getDateParams(from, until) + ".json");
  }

  public List<ZMEvent> getAllEvents(String monitorId)
  {
    return getEvents("api/events/index/MonitorId:" + monitorId + ".json");
  }

  public List<ZMEvent> getEvents(String monitorId, Date from, Date until)
  {
    return getEvents("api/events/index/MonitorId:" + monitorId + getDateParams(from, until) + ".json");
  }

  private String getDateParams(Date from, Date until)
  {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss");
    String startTimeString = format.format(from); // "2016-03-28%2000:00:00";
    String endTimeString = format.format(until); // "2016-03-28%2023:59:59";
    return "/StartTime%20%3E=:" + startTimeString + "/StartTime%20%3C=:" + endTimeString;
  }

  private List<ZMEvent> getEvents(String url)
  {
    String json;
    if (session.isTestMode() == true) {
      json = session.readFile("zoneminder-events-json?page=1.txt");
    } else {
      json = session.httpGet(url + "?page=1");
    }
    int pageCount = 1;
    Json2MapReader jsonReader = new Json2MapReader(json);
    Map<String, ? > pagination = (Map<String, ? >) jsonReader.getMap("pagination");
    try {
      pageCount = (int) pagination.get("pageCount");
    } catch (Exception ex) {
      log.warn("Can't read any events (parameter pageCount expected but not given.", ex);
    }
    int current = 1;
    List<ZMEvent> events = new ArrayList<>();
    do {
      List<Map<String, ? >> eventObjects = jsonReader.getList("events");
      if (eventObjects != null && eventObjects.isEmpty() == false) {
        for (Object obj : eventObjects) {
          @SuppressWarnings("unchecked")
          Map<String, Object> map = (Map<String, Object>) obj;
          @SuppressWarnings("unchecked")
          ZMEvent event = new ZMEvent((Map<String, String>) map.get("Event"));
          event.setMonitor(getMonitor(event.getMonitorId()));
          events.add(event);
        }
      }
      if (++current > pageCount) {
        break;
      }
      if (session.isTestMode() == true) {
        json = session.readFile("zoneminder-events-json?page=" + current + ".txt");
      } else {
        json = session.httpGet(url + "?page=" + current);
      }
      jsonReader = new Json2MapReader(json);
    } while (true);
    log.info("Read " + events.size() + " events from server.");
    return events;
  }

  public ZMEvent getEvent(String eventId)
  {
    String json;
    if (session.isTestMode() == true) {
      json = session.readFile("zoneminder-event-with-frames-json.txt");
    } else {
      json = session.httpGet("api/events/" + eventId + ".json");
    }
    Json2MapReader jsonReader = new Json2MapReader(json);
    @SuppressWarnings("unchecked")
    Map<String, String> eventObject = (Map<String, String>) jsonReader.getMap("event", "Event");
    ZMEvent event = new ZMEvent(eventObject);
    event.setMonitor(getMonitor(event.getMonitorId()));
    List<Map<String, ? >> frameObjects = jsonReader.getList("event", "Frame");
    List<ZMFrame> frames = new ArrayList<>();
    int alarmCounter = 0;
    if (CollectionUtils.isEmpty(frameObjects) == false) {
      for (Map<String, ? > obj : frameObjects) {
        @SuppressWarnings("unchecked")
        ZMFrame frame = new ZMFrame((Map<String, String>) obj);
        if (frame.getType() == ZMFrameType.ALARM) {
          alarmCounter++;
        }
        frames.add(frame);
      }
    }
    log.info("Number of read frames: " + frames.size() + " with " + alarmCounter + " alarms.");
    event.setFrames(frames);
    return event;
  }

  public List<ZMImage> readImages(String eventId)
  {
    return readImages(getEvent(eventId));
  }

  /**
   * Event with frames or without. If no frames are given, the frames will be get.
   * @param event
   */
  public List<ZMImage> readImages(ZMEvent event)
  {
    if (event.getFrames() == null) {
      event = getEvent(event.getId());
    }
    List<ZMImage> images = new LinkedList<>();
    if (event.getFrames() == null) {
      log.warn("Event '" + event.getId() + "' has no frames!?");
      return images;
    }
    StringBuilder sb = new StringBuilder();
    buildImagePath(event, sb);
    String baseFilename = sb.toString();
    int imageCounter = -1;
    boolean bulk = false;
    for (ZMFrame frame : event.getFrames()) {
      int frameId = frame.getFrameId();
      if (bulk == true) {
        // Generate all frames of last bulk:
        while (++imageCounter < frameId) {
          ZMImage image = new ZMImage(baseFilename, event, frame);
          images.add(image);
        }
      }
      imageCounter = frameId;
      ZMImage image = new ZMImage(baseFilename, event, frame);
      images.add(image);
      bulk = frame.getType() == ZMFrameType.BULK;
      // Bulk frame has always successor frame!
    }
    return images;
  }

  /**
   * (baseurl)/zm/events/(monitorId)/(yy)/(MM)/(dd)/(HH)/(mm)/(ss)/(FrameId)-(analyse|capture).jpg<br>
   * yy - year: 16 (2016)<br>
   * MM - month: 01 (January)<br>
   * dd - day of month (01, 02, ...)<br>
   * HH - hour of day (00, 01, ..., 23) (start time of event)<br>
   * mm - minutes (00, 01, ..., 59) (start time of event)<br>
   * ss - seconds (00, 01, ..., 59) (start time of event)<br>
   * FrameId - 01002 (5 digits), if bulk, then all next frameIds (sequencer) are used until next frame.<br>
   * analyse|capture - analyse only for alarms?
   * @param event
   * @return
   */
  String getImagePath(ZMEvent event)
  {
    StringBuilder sb = new StringBuilder();
    buildImagePath(event, sb);
    return sb.toString();
  }

  private void buildImagePath(ZMEvent event, StringBuilder sb)
  {
    sb.append(event.getMonitorId()).append("/");
    // 2016-03-29 18:20:00 -> 16/03/29/18/20/00
    char[] startTime = event.getValue("StartTime").toCharArray(); // yyyy-MM-dd HH:mm:ss
    sb.append(startTime[2]).append(startTime[3]).append('/')// yy
        .append(startTime[5]).append(startTime[6]).append('/') // MM
        .append(startTime[8]).append(startTime[9]).append('/') // dd
        .append(startTime[11]).append(startTime[12]).append('/') // HH
        .append(startTime[14]).append(startTime[15]).append('/') // mm
        .append(startTime[17]).append(startTime[18]).append('/'); // ss
  }
}

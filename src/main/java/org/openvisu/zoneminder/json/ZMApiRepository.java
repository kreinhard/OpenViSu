package org.openvisu.zoneminder.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openvisu.zoneminder.ZMConfig;
import org.openvisu.zoneminder.ZMEvent;
import org.openvisu.zoneminder.ZMFrame;
import org.openvisu.zoneminder.ZMFrameType;
import org.openvisu.zoneminder.ZMMonitor;
import org.openvisu.zoneminder.ZMZone;
import org.springframework.util.CollectionUtils;

public class ZMApiRepository
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ZMApiRepository.class);

  private ZMClientSession session;

  private Map<String, ZMConfig> configMap;

  private List<ZMMonitor> monitors;

  private List<ZMZone> zones;

  public ZMApiRepository(ZMClientSession session)
  {
    this.session = session;
  }

  public List<ZMMonitor> getMonitors()
  {
    if (monitors == null) {
      String json;
      json = session.httpGet("api/monitors.json");
      JsonReader jsonReader = new JsonReader(json);
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

  public ZMConfig getConfig(String name)
  {
    if (configMap == null) {
      Map<String, ZMConfig> newConfigMap = new HashMap<>();
      String json = session.httpGet("api/configs.json");
      JsonReader jsonReader = new JsonReader(json);
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
    String json = session.httpGet(url + "?page=1");
    int pageCount = 1;
    JsonReader jsonReader = new JsonReader(json);
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
      json = session.httpGet(url + "?page=" + current);
      jsonReader = new JsonReader(json);
    } while (true);
    log.info("Read " + events.size() + " events from server.");
    return events;
  }

  public ZMEvent getEvent(String eventId)
  {
    String json;
    json = session.httpGet("api/events/" + eventId + ".json");
    JsonReader jsonReader = new JsonReader(json);
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
}

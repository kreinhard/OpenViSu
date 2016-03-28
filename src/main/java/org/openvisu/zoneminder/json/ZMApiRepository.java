package org.openvisu.zoneminder.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openvisu.zoneminder.ZMConfig;
import org.openvisu.zoneminder.ZMEvent;
import org.openvisu.zoneminder.ZMFrame;
import org.openvisu.zoneminder.ZMFrameType;
import org.openvisu.zoneminder.ZMMonitor;
import org.springframework.util.CollectionUtils;

public class ZMApiRepository
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ZMApiRepository.class);

  private ZMClientSession session;

  private Map<String, ZMConfig> configMap;

  private List<ZMMonitor> monitors;

  private List<ZMEvent> events;

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
      List<Object> monitorObjects = jsonReader.getList("monitors");

      monitors = new ArrayList<>();
      if (monitorObjects != null && monitorObjects.isEmpty() == false) {
        for (Object obj : monitorObjects) {
          ZMMonitor monitor = new ZMMonitor((Map) obj);
          monitors.add(monitor);
        }
      }
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

  public ZMConfig getConfig(String name)
  {
    if (configMap == null) {
      configMap = new HashMap<>();
      String json = session.httpGet("api/configs.json");
      JsonReader jsonReader = new JsonReader(json);
      List<Object> configObjects = jsonReader.getList("configs");
      if (configObjects != null && configObjects.isEmpty() == false) {
        for (Object obj : configObjects) {
          Map map = (Map) ((Map) obj).get("Config");
          ZMConfig config = new ZMConfig(map);
          configMap.put(config.getName(), config);
        }
      }
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

  public List<ZMEvent> getEvents()
  {
    if (events == null) {
      String json;
      json = session.httpGet("api/events.json?page=1");
      int pageCount = 1;
      JsonReader jsonReader = new JsonReader(json);
      Map<String, Object> pagination = (Map) jsonReader.getMap().get("pagination");
      try {
        pageCount = (int)pagination.get("pageCount");
      } catch (Exception ex) {
        log.warn("Can't read any events (parameter pageCount expected but not given.", ex);
      }
      int current = 1;
      List<Object> eventObjects = jsonReader.getList("events");
      events = new ArrayList<>();
      do {
        if (eventObjects != null && eventObjects.isEmpty() == false) {
          for (Object obj : eventObjects) {
            Map<String, Object> map = (Map) obj;
            ZMEvent event = new ZMEvent((Map) map.get("Event"));
            events.add(event);
          }
        }
        if (++current > pageCount) {
          break;
        }
        json = session.httpGet("api/events.json?page=" + current);
      } while (true);
    }
    log.info("Read " + events.size() + " events from server.");
    return events;
  }

  /**
   * Force reload of events from ZoneMinder.
   * @return this for fluent usage.
   */
  public ZMApiRepository refreshEvents()
  {
    this.events = null;
    return this;
  }

  public void readFrames(ZMEvent event)
  {
    String json;
    json = session.httpGet("api/events/" + event.getId() + ".json");
    JsonReader jsonReader = new JsonReader(json);
    List<Object> frameObjects = jsonReader.getList("event", "Frame");
    List<ZMFrame> frames = new ArrayList<>();
    int alarmCounter = 0;
    if (CollectionUtils.isEmpty(frameObjects) == false) {
      for (Object obj : frameObjects) {
        ZMFrame frame = new ZMFrame((Map) obj);
        if (frame.getType() == ZMFrameType.ALARM) {
          alarmCounter++;
        }
        frames.add(frame);
      }
    }
    log.info("Number of read frames: " + frames.size() + " with " + alarmCounter + " alarms.");
    event.setFrames(frames);
  }
}

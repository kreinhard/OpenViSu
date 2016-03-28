package org.openvisu.zoneminder.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openvisu.zoneminder.ZMConfig;
import org.openvisu.zoneminder.ZMEvent;
import org.openvisu.zoneminder.ZMFrame;
import org.openvisu.zoneminder.ZMMonitor;

import com.jayway.jsonpath.JsonPath;

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
      List<Map<String, Object>> elements = JsonPath.parse(json).read("$.monitors", List.class);
      monitors = new ArrayList<>();
      if (elements != null && elements.isEmpty() == false) {
        for (Map<String, Object> map : elements) {
          ZMMonitor monitor = new ZMMonitor((Map) map.get("Monitor"));
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
      List<Map<String, Object>> elements = JsonPath.parse(json).read("$.configs", List.class);
      Map<String, ZMConfig> configMap = new HashMap<>();
      if (elements != null && elements.isEmpty() == false) {
        for (Map<String, Object> map : elements) {
          ZMConfig config = new ZMConfig((Map) map.get("Config"));
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
      try {
        List<Integer> pageCountString = JsonPath.parse(json).read("$..pageCount", List.class);
        pageCount = pageCountString.get(0);
      } catch (Exception ex) {
        log.warn("Can't read any events (parameter pageCount expected but not given.", ex);
      }
      int current = 1;
      events = new ArrayList<>();
      do {
        List<Map<String, Object>> elements = JsonPath.parse(json).read("$.events", List.class);
        if (elements != null && elements.isEmpty() == false) {
          for (Map<String, Object> map : elements) {
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
    List<ZMFrame> frames = new ArrayList<>();
    List<Map<String, Object>> elements = JsonPath.parse(json).read("$.event", List.class);
    if (elements != null && elements.isEmpty() == false) {
      for (Map<String, Object> map : elements) {
        Map obj = (Map) map.get("Frame");
        if (obj == null) {
          continue; // Should be Event or Monitor.
        }
        ZMFrame frame = new ZMFrame(obj);
        frames.add(frame);
      }
    }
    event.setFrames(frames);
  }
}

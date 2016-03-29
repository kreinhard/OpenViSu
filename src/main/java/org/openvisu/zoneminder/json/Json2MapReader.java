package org.openvisu.zoneminder.json;

import java.util.List;
import java.util.Map;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;

public class Json2MapReader
{
  private Map<String, Object> map;

  public Json2MapReader(String json)
  {
    JsonParser parser = JsonParserFactory.getJsonParser();
    map = parser.parseMap(json);
    // For creating test files:
    // try {
    // FileWriter writer = new FileWriter(new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss.SSS").format(new Date()) + "-json.txt");
    // writer.append(json);
    // writer.close();
    // } catch (IOException e) {
    // System.out.println(e.getMessage());
    // }
  }

  public Map<String, Object> getMap()
  {
    return map;
  }

  @SuppressWarnings({ "rawtypes", "unchecked"})
  public List<Map<String, ? >> getList(String... path)
  {
    Object current = map;
    for (String el : path) {
      if (current instanceof Map) {
        current = ((Map) current).get(el);
        if (current == null) {
          throw new RuntimeException("Can't get element '" + el + "' from json string because element isn't part of the map.");
        }
      } else {
        throw new RuntimeException(
            "Can't get element '" + el + "' from json string because parent object isn't a map: " + current.getClass());
      }
    }
    return (List<Map<String, ? >>) current;
  }

  @SuppressWarnings("unchecked")
  public Map<String, ? > getMap(String... path)
  {
    Object current = map;
    for (String el : path) {
      if (current instanceof Map) {
        current = ((Map<String, ? >) current).get(el);
        if (current == null) {
          throw new RuntimeException("Can't get element '" + el + "' from json string because element isn't part of the map.");
        }
      } else {
        throw new RuntimeException(
            "Can't get element '" + el + "' from json string because parent object isn't a map: " + current.getClass());
      }
    }
    return (Map<String, ? >) current;
  }
}

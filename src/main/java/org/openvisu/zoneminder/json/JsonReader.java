package org.openvisu.zoneminder.json;

import java.util.List;
import java.util.Map;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;

public class JsonReader
{
  private Map<String, Object> map;

  public JsonReader(String json)
  {
    JsonParser parser = JsonParserFactory.getJsonParser();
    map = parser.parseMap(json);
  }

  public Map<String, Object> getMap()
  {
    return map;
  }

  public List<Object> getList(String... path)
  {
    Object current = map;
    for (String el : path) {
      if (current instanceof Map) {
        current = ((Map) current).get(el);
        if (current == null) {
          throw new RuntimeException(
              "Can't get element '" + el + "' from json string because element isn't part of the map.");
        }
      } else {
        throw new RuntimeException(
            "Can't get element '" + el + "' from json string because parent object isn't a map: " + current.getClass());
      }
    }
    return (List<Object>)current;
  }
}

package org.openvisu.zoneminder;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class ZMBaseObject
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ZMBaseObject.class);

  private Map<String, String> map;

  public ZMBaseObject(Map<String, String> map)
  {
    this.map = map;
  }

  public int getIntValue(String key)
  {
    String str = map.get(key);
    if (StringUtils.isEmpty(str) == true) {
      return 0;
    }
    try {
      return Integer.valueOf(str);
    } catch (NumberFormatException ex) {
      log.warn("Couldn't convert '" + str + "' to integer (0 is returned instead).");
      return 0;
    }
  }

  public String getValue(String key)
  {
    return map.get(key);
  }

  public String getId()
  {
    return map.get("Id");
  }
}

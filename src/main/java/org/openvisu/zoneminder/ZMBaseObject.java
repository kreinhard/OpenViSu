package org.openvisu.zoneminder;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ZMBaseObject
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ZMBaseObject.class);

  private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss"; // 2016-03-29 18:20:00

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

  public Date getTimestampValue(String key)
  {
    String str = map.get(key);
    if (StringUtils.isEmpty(str) == true) {
      return null;
    }
    DateTimeFormatter fmt = DateTimeFormat.forPattern(TIMESTAMP_FORMAT);
    return fmt.parseDateTime(str).toDate();
  }

  public DateTime getJodaTimestampValue(String key)
  {
    String str = map.get(key);
    if (StringUtils.isEmpty(str) == true) {
      return null;
    }
    DateTimeFormatter fmt = DateTimeFormat.forPattern(TIMESTAMP_FORMAT);
    return fmt.parseDateTime(str);
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

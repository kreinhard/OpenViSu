package org.openvisu.zoneminder;

import java.util.Date;

import org.joda.time.DateTime;

public interface ZMMapping
{
  public static String getId(ZMMapping mapping)
  {
    ZMMappingObject mo = mapping.getMappingObject();
    return mo.getId();
  }

  public static DateTime getJodaTimestampValue(ZMMapping mapping, String key)
  {
    ZMMappingObject mo = mapping.getMappingObject();
    return mo.getJodaTimestampValue(key);
  }

  public static Date getTimestampValue(ZMMapping mapping, String key)
  {
    ZMMappingObject mo = mapping.getMappingObject();
    return mo.getTimestampValue(key);
  }

  public static String getValue(ZMMapping mapping, String key)
  {
    ZMMappingObject mo = mapping.getMappingObject();
    return mo.getValue(key);
  }

  public ZMMappingObject getMappingObject();
}

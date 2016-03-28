package org.openvisu.zoneminder;

import org.apache.commons.lang3.StringUtils;

public enum ZMFrameType
{
  NORMAL, BULK, ALARM, UNKNOWN;

  public static ZMFrameType get(String str)
  {
    if (StringUtils.isEmpty(str) == true) {
      return null;
    } else if ("Normal".equals(str) == true) {
      return NORMAL;
    } else if ("Bulk".equals(str) == true) {
      return BULK;
    } else if ("Alarm".equals(str) == true) {
      return ALARM;
    } else {
      return UNKNOWN;
    }
  }
}

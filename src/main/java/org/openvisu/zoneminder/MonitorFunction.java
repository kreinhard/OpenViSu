package org.openvisu.zoneminder;

import org.apache.commons.lang3.StringUtils;

public enum MonitorFunction
{
  NONE, MONITOR, MODECT, RECORD, MOCORD, NODECT;

  public static MonitorFunction get(String str)
  {
    if (StringUtils.isEmpty(str) == true) {
      return null;
    } else if ("None".equals(str) == true) {
      return NONE;
    } else if ("Monitor".equals(str) == true) {
      return MONITOR;
    } else if ("Modect".equals(str) == true) {
      return MODECT;
    } else if ("Record".equals(str) == true) {
      return RECORD;
    } else if ("Mocord".equals(str) == true) {
      return MOCORD;
    } else if ("Nodect".equals(str) == true) {
      return NODECT;
    }
    return null;
  }
}

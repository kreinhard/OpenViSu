package org.openvisu.zoneminder;

import java.util.Map;

public class ZMZone extends ZMBaseObject
{
  public ZMZone(Map<String, String> map)
  {
    super(map);
  }

  @Override
  public String toString()
  {
    return "Zone #" + getId();
  }
}

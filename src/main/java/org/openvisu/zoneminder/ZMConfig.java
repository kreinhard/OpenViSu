package org.openvisu.zoneminder;

import java.util.Map;

public class ZMConfig extends ZMBaseObject
{
  public ZMConfig(Map<String, String> map)
  {
    super(map);
  }

  public String getName()
  {
    return super.getValue("Name");
  }

  @Override
  public String toString()
  {
    return "Config #" + getId() + ": " + getName() + " = " + getValue("Value");
  }
}

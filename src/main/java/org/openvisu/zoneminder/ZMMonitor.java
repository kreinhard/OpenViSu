package org.openvisu.zoneminder;

import java.util.Map;

public class ZMMonitor extends ZMBaseObject
{
  public ZMMonitor(Map<String, String> map)
  {
    super(map);
  }

  public String getName()
  {
    return super.getValue("Name");
  }

  public ZMMonitorFunction getFunction()
  {
    return ZMMonitorFunction.get(getValue("Function"));
  }

  @Override
  public String toString()
  {
    return "Monitor #" + getId() + ": " + getName();
  }
}

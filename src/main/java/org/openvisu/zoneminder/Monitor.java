package org.openvisu.zoneminder;

import java.util.Map;

public class Monitor extends ZMBaseObject
{
  public Monitor(Map<String, String> map)
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
    return "Monitor #" + getId() + ": " + getName();
  }
}

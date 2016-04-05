package org.openvisu.zoneminder;

import java.util.Map;

public class ZMMonitor implements ZMMapping
{
  private ZMMappingObject mappingObject;

  public ZMMonitor(Map<String, String> map)
  {
    this.mappingObject = new ZMMappingObject(map);
  }

  @Override
  public ZMMappingObject getMappingObject()
  {
    return this.mappingObject;
  }

  public String getName()
  {
    return mappingObject.getValue("Name");
  }

  public ZMMonitorFunction getFunction()
  {
    return ZMMonitorFunction.get(mappingObject.getValue("Function"));
  }

  @Override
  public String toString()
  {
    return "Monitor #" + mappingObject.getId() + ": " + getName();
  }
}

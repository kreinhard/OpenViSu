package org.openvisu.zoneminder;

import java.util.Map;

public class ZMZone implements ZMMapping
{
  private ZMMappingObject mappingObject;

  public ZMZone(Map<String, String> map)
  {
    this.mappingObject = new ZMMappingObject(map);
  }

  @Override
  public ZMMappingObject getMappingObject()
  {
    return this.mappingObject;
  }

  @Override
  public String toString()
  {
    return "Zone #" + mappingObject.getId();
  }
}

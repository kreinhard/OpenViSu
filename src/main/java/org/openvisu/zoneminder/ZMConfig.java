package org.openvisu.zoneminder;

import java.util.Map;

public class ZMConfig implements ZMMapping
{
  private ZMMappingObject mappingObject;

  public ZMConfig(Map<String, String> map)
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

  public String getValue()
  {
    return mappingObject.getValue("Value");
  }

  public int getIntValue()
  {
    return mappingObject.getIntValue("Value");
  }

  @Override
  public String toString()
  {
    return "Config #" + mappingObject.getId() + ": " + getName() + " = " + mappingObject.getValue("Value");
  }
}

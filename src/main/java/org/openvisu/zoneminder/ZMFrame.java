package org.openvisu.zoneminder;

import java.util.Map;

public class ZMFrame extends ZMBaseObject
{
  public ZMFrame(Map<String, String> map)
  {
    super(map);
  }

  public String getFrameId()
  {
    return super.getValue("FrameId");
  }

  public ZMFrameType getType()
  {
    return ZMFrameType.get(getValue("Type"));
  }

  @Override
  public String toString()
  {
    return "Frame #" + getId() + ", frameId=" + getFrameId() + ", type=" + getType();
  }
}

package org.openvisu.zoneminder;

import java.util.Map;

import org.openvisu.video.VideoUtils;

public class ZMFrame implements ZMMapping
{
  private ZMMappingObject mappingObject;

  private int frameId = -1;
  
  public ZMFrame(Map<String, String> map)
  {
    this.mappingObject = new ZMMappingObject(map);
  }

  @Override
  public ZMMappingObject getMappingObject()
  {
    return this.mappingObject;
  }

  public int getFrameId()
  {
    if (frameId < 0) {
      frameId = mappingObject.getIntValue("FrameId");
    }
    return frameId;
  }

  /**
   * As 6 digit number (with leading zeros).
   * @return 00001, 01532, ...
   */
  public String getFormattedFrameId()
  {
    return VideoUtils.getFormattedFrameId(getFrameId());
  }

  public ZMFrameType getType()
  {
    return ZMFrameType.get(mappingObject.getValue("Type"));
  }

  public int getScore()
  {
    return mappingObject.getIntValue("Score");
  }

  @Override
  public String toString()
  {
    return "Frame #" + mappingObject.getId() + ", frameId=" + getFrameId() + ", type=" + getType();
  }
}

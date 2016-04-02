package org.openvisu.zoneminder;

import java.util.Map;

import org.openvisu.video.VideoUtils;

public class ZMFrame extends ZMBaseObject
{
  private int frameId = -1;
  
  public ZMFrame(Map<String, String> map)
  {
    super(map);
  }

  public int getFrameId()
  {
    if (frameId < 0) {
      frameId = super.getIntValue("FrameId");
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
    return ZMFrameType.get(getValue("Type"));
  }

  public int getScore()
  {
    return getIntValue("Score");
  }

  @Override
  public String toString()
  {
    return "Frame #" + getId() + ", frameId=" + getFrameId() + ", type=" + getType();
  }
}

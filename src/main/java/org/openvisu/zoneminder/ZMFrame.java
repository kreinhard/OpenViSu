package org.openvisu.zoneminder;

import java.util.Map;

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
    if (getFrameId() < 10) {
      return "0000" + frameId;
    } else if (frameId < 100) {
      return "000" + frameId;
    } else if (frameId < 1000) {
      return "00" + frameId;
    } else if (frameId < 10000) {
      return "0" + frameId;
    } else {
      return String.valueOf(frameId);
    }
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

package org.openvisu.zoneminder;

import java.util.List;
import java.util.Map;

public class ZMEvent extends ZMBaseObject
{
  private int alarmFrames = -1;

  private List<ZMFrame> frames;

  public ZMEvent(Map<String, String> map)
  {
    super(map);
  }

  public ZMEvent setFrames(List<ZMFrame> frames)
  {
    this.frames = frames;
    return this;
  }

  public List<ZMFrame> getFrames()
  {
    return frames;
  }

  public String getName()
  {
    return super.getValue("Name");
  }

  public int getAlarmFrames()
  {
    if (alarmFrames == -1) {
      alarmFrames = getIntValue("AlarmFrames");
    }
    return alarmFrames;
  }

  public String getMonitorId()
  {
    return getValue("MonitorId");
  }

  @Override
  public String toString()
  {
    return "Event #" + getId() + " - " + getName() + ", monitorId=" + getMonitorId() + ", alarmFrames=" + getAlarmFrames();
  }
}

package org.openvisu.zoneminder;

import java.util.List;
import java.util.Map;

public class ZMEvent extends ZMBaseObject
{
  private int alarmFrames = -1;

  private boolean newEvent;

  private ZMMonitor monitor;

  private List<ZMFrame> frames;

  public ZMEvent(Map<String, String> map)
  {
    super(map);
    if ("New Event".equals(getName()) == true) {
      newEvent = true;
    }
  }

  /**
   * New events are events of the server, whose frames aren't yet finished. Those events are current built events.
   */
  public boolean isNewEvent()
  {
    return newEvent;
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

  public ZMMonitor getMonitor()
  {
    return monitor;
  }

  public void setMonitor(ZMMonitor monitor)
  {
    this.monitor = monitor;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    if (newEvent == true) {
      sb.append("*New*");
    }
    sb.append("Event #").append(getId()).append(" - ").append(getName()).append(", monitorId=").append(getMonitorId())
        .append(", alarmFrames=").append(getAlarmFrames());
    return sb.toString();
  }
}

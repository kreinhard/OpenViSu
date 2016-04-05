package org.openvisu.zoneminder;

import java.util.List;
import java.util.Map;

import org.openvisu.video.Event;

public class ZMEvent extends Event implements ZMMapping
{
  private ZMMappingObject mappingObject;

  private int alarmFrames = -1;

  private boolean newEvent;

  private ZMMonitor monitor;

  private List<ZMFrame> frames;

  public ZMEvent(Map<String, String> map)
  {
    this.mappingObject = new ZMMappingObject(map);
    if ("New Event".equals(getName()) == true) {
      newEvent = true;
    }
    this.setId(mappingObject.getId());
  }

  @Override
  public ZMMappingObject getMappingObject()
  {
    return this.mappingObject;
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
    return mappingObject.getValue("Name");
  }

  public int getNumberOfAlarmFrames()
  {
    if (alarmFrames == -1) {
      alarmFrames = mappingObject.getIntValue("AlarmFrames");
    }
    return alarmFrames;
  }

  public String getMonitorId()
  {
    return mappingObject.getValue("MonitorId");
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
        .append(", alarmFrames=").append(getNumberOfAlarmFrames());
    return sb.toString();
  }
}

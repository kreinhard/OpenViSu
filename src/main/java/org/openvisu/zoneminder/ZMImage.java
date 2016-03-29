package org.openvisu.zoneminder;

public class ZMImage
{
  private String filename;

  private ZMEvent event;

  private ZMFrame frame;

  private ZMImageType type;

  /**
   * (baseurl)/zm/events/(monitorId)/(yy)/(MM)/(dd)/(HH)/(mm)/(ss)/(FrameId)-(analyse|capture).jpg<br>
   * yy - year: 16 (2016)<br>
   * MM - month: 01 (January)<br>
   * dd - day of month (01, 02, ...)<br>
   * HH - hour of day (00, 01, ..., 23) (start time of event)<br>
   * mm - minutes (00, 01, ..., 59) (start time of event)<br>
   * ss - seconds (00, 01, ..., 59) (start time of event)<br>
   * FrameId - 01002 (5 digits), if bulk, then all next frameIds (sequencer) are used until next frame.<br>
   * analyse|capture - analyse only for alarms?
   * 
   * @param path
   * @param event
   * @param frame
   * @param type
   * @param formattedFrameId
   */
  public ZMImage(String path, ZMEvent event, ZMFrame frame, ZMImageType type, String formattedFrameId)
  {
    this.type = type;
    this.filename = path + formattedFrameId + (type == ZMImageType.ANALYSE ? "-analyse" : "-capture") + ".jpg";
    this.event = event;
    this.frame = frame;
  }

  public String getFilename()
  {
    return filename;
  }

  public ZMEvent getEvent()
  {
    return event;
  }

  public ZMFrame getFrame()
  {
    return frame;
  }

  public ZMImageType getType()
  {
    return type;
  }

  public String toString()
  {
    return "ZMImage " + filename;
  }
}

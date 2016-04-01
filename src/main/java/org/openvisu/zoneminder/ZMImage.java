package org.openvisu.zoneminder;

import java.util.Date;

import org.openvisu.video.Image;

public class ZMImage implements Image
{
  private String filename;

  private String analyseFilename;

  private String path;

  private Date timestamp;

  private ZMEvent event;

  private ZMFrame frame;

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
   * @param formattedFrameId
   */
  public ZMImage(String path, ZMEvent event, ZMFrame frame, String formattedFrameId)
  {
    this.path = path;
    this.filename = formattedFrameId + "-capture.jpg";
    this.analyseFilename = formattedFrameId + "-analyse.jpg";
    this.event = event;
    this.frame = frame;
  }

  /**
   * @return file name without path.
   */
  public String getFilename()
  {
    return filename;
  }

  public String getAbsoluteFilename()
  {
    return path + filename;
  }

  public String getAnalyseFilename()
  {
    return analyseFilename;
  }

  public String getAbsoluteAnalyseFilename()
  {
    return path + analyseFilename;
  }

  public ZMEvent getEvent()
  {
    return event;
  }

  public ZMFrame getFrame()
  {
    return frame;
  }

  /**
   * @param timestamp
   * @return this for fluent pattern.
   */
  public ZMImage setTimestamp(Date timestamp)
  {
    this.timestamp = timestamp;
    return this;
  }

  public Date getTimestamp()
  {
    return timestamp;
  }

  public String toString()
  {
    return "ZMImage " + filename;
  }
}

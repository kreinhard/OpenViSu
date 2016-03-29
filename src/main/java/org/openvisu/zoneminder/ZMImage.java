package org.openvisu.zoneminder;

public class ZMImage
{
  private String filename;

  private ZMEvent event;

  private ZMFrame frame;

  public ZMImage(String filename, ZMEvent event, ZMFrame frame)
  {
    this.filename = filename;
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
}

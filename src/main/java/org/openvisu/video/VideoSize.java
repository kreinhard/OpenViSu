package org.openvisu.video;

public enum VideoSize
{
  /** Normal size. */
  NORMAL("S100"), PERCENT_50("S50");

  private String name;

  VideoSize(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }
}

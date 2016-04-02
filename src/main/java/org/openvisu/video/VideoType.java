package org.openvisu.video;

public enum VideoType
{
  /** Normal video */
  NORMAL("normal"),
  /** Video with with analysis information. */
  ANALYSIS("analysis");

  private String name;

  private VideoType(String name)
  {
    this.name = name;
  }
  
  public String getName()
  {
    return name;
  }
}

package org.openvisu.video;

public class Event
{
  private String id;

  public String getId()
  {
    return id;
  }
  
  /**
   * 
   * @param id
   * @return this for chaining.
   */
  public Event setId(String id)
  {
    this.id = id;
    return this;
  }
}

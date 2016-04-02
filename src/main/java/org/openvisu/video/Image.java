package org.openvisu.video;

public interface Image
{
  public String getAbsoluteFilename();

  public String getAbsoluteAnalyseFilename();

  /**
   * @param type
   * @return {@link #getAbsoluteFilename()} for {@link ImageType#NORMAL} and {@link #getAbsoluteAnalyseFilename()} for {@link ImageType#ANALYSIS}
   */
  public String getAbsoluteFilename(ImageType type);
}

package org.openvisu.video;

public interface Image
{
  /**
   * @return File name with path relative to image cache dir.
   */
  public String getFile();

  /**
   * @return File name of analyse image with path relative to image cache dir.
   */
  public String getAnalyseFile();

  /**
   * @param type
   * @return {@link #getFile()} for {@link ImageType#NORMAL} and {@link #getAnalyseFile()} for {@link ImageType#ANALYSIS}
   */
  public String getFile(ImageType type);
}

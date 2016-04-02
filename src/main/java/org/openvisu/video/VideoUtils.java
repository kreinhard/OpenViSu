package org.openvisu.video;

public class VideoUtils
{
  public static String getFormattedFrameId(int frameId)
  {
    if (frameId < 10) {
      return "0000" + frameId;
    } else if (frameId < 100) {
      return "000" + frameId;
    } else if (frameId < 1000) {
      return "00" + frameId;
    } else if (frameId < 10000) {
      return "0" + frameId;
    } else {
      return String.valueOf(frameId);
    }
  }

  public static String getVideoFilename(String cameraId, String eventId, VideoType type, VideoSize size)
  {
    return "c" + cameraId + "-event" + eventId + "-" + type.getName() + "-" + size.getName() + ".mp4";
  }
}

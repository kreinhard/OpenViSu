package org.openvisu.zoneminder.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openvisu.zoneminder.ZMConfig;
import org.openvisu.zoneminder.ZMEvent;
import org.openvisu.zoneminder.ZMFrame;
import org.openvisu.zoneminder.ZMFrameType;
import org.openvisu.zoneminder.ZMMonitor;
import org.openvisu.zoneminder.ZMMonitorFunction;

public class ZMFrameTest
{

  @Test
  public void getFormattedFrameId()
  {
    Map<String, String> map = new HashMap<>();
    testFrame(map, "00001", "1");
    testFrame(map, "00009", "9");
    testFrame(map, "00010", "10");
    testFrame(map, "00099", "99");
    testFrame(map, "00100", "100");
    testFrame(map, "00999", "999");
    testFrame(map, "01000", "1000");
    testFrame(map, "09999", "9999");
    testFrame(map, "10000", "10000");
    testFrame(map, "99999", "99999");
  }
  
  private void testFrame(Map<String, String> map, String expected, String frameId) {
    map.put("FrameId", frameId);
    ZMFrame frame = new ZMFrame(map);
    assertEquals(expected, frame.getFormattedFrameId());
  }
}

package org.openvisu.zoneminder.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openvisu.zoneminder.ZMConfig;
import org.openvisu.zoneminder.ZMEvent;
import org.openvisu.zoneminder.ZMFrame;
import org.openvisu.zoneminder.ZMFrameType;
import org.openvisu.zoneminder.ZMImage;
import org.openvisu.zoneminder.ZMMonitor;
import org.openvisu.zoneminder.ZMMonitorFunction;

public class ZMApiRepositoryTest
{

  @Test
  public void readMonitors()
  {
    ZMClientSession session = new ZMClientSession("file://src/test/data/zoneminder");
    ZMApiRepository repo = new ZMApiRepository(session);
    List<ZMMonitor> monitors = repo.getMonitors();
    assertNotNull(monitors);
    assertEquals(monitors.size(), 5);
    assertTrue("Tuer".equals(monitors.get(0).getName()));
    assertTrue("1".equals(monitors.get(0).getId()));
    assertEquals(monitors.get(0).getFunction(), ZMMonitorFunction.MOCORD);
    assertEquals(monitors.get(1).getFunction(), ZMMonitorFunction.MOCORD);
    assertEquals(monitors.get(2).getFunction(), ZMMonitorFunction.RECORD);
  }

  @Test
  public void readEvents()
  {
    ZMClientSession session = new ZMClientSession("file://src/test/data/zoneminder");
    ZMApiRepository repo = new ZMApiRepository(session);
    List<ZMEvent> events = repo.getAllEvents();
    assertNotNull(events);
    assertEquals(events.size(), 931);
    assertTrue(events.get(930).isNewEvent());
    assertFalse(events.get(0).isNewEvent());
    ZMEvent event = events.get(34);
    assertEquals("Event-50", event.getName());
    assertEquals(21, event.getNumberOfAlarmFrames());
    assertEquals("2", event.getMonitorId());
    assertEquals("2", event.getMonitor().getId());
    assertEquals("Garten-Ost", event.getMonitor().getName());
  }

  @Test
  public void readConfig()
  {
    ZMClientSession session = new ZMClientSession("file://src/test/data/zoneminder");
    ZMApiRepository repo = new ZMApiRepository(session);
    ZMConfig config = repo.getConfig("ZM_WEB_EVENTS_PER_PAGE");
    assertEquals(100, config.getIntValue());
  }

  @Test
  public void readFrames()
  {
    ZMClientSession session = new ZMClientSession("file://src/test/data/zoneminder");
    ZMApiRepository repo = new ZMApiRepository(session);
    ZMEvent event = repo.getEvent("Event-50");
    List<ZMFrame> frames = event.getFrames();
    assertEquals(84, frames.size());
    int alarmCounter = 0;
    int bulkCounter = 0;
    for (ZMFrame frame : frames) {
      if (frame.getType() == ZMFrameType.ALARM) {
        ++alarmCounter;
      } else if (frame.getType() == ZMFrameType.BULK) {
        ++bulkCounter;
      }
    }
    assertEquals(21, alarmCounter);
    assertEquals(12, bulkCounter);
    ZMFrame frame = frames.get(34);
    assertEquals("2334", frame.getId());
    assertEquals(895, frame.getFrameId());
    assertEquals(3, frame.getScore());
    assertEquals(ZMFrameType.ALARM, frame.getType());
  }

  @Test
  public void readImages()
  {
    ZMClientSession session = new ZMClientSession("file://src/test/data/zoneminder");
    ZMApiRepository repo = new ZMApiRepository(session);
    ZMEvent event = repo.getEvent("Event-50");
    List<ZMImage> images = repo.readImages(event);
    int counter = 1;
    for (ZMImage image : images) {
      assertTrue(image.getFilename() + "-" + counter, image.getFilename().contains(String.valueOf(counter++)));
    }
    assertEquals(1364, images.size());
  }

  @Test
  public void getImagePath()
  {
    ZMClientSession session = new ZMClientSession("file://src/test/data/zoneminder");
    ZMApiRepository repo = new ZMApiRepository(session);
    ZMEvent event = repo.getEvent("Event-50");
    assertEquals("2/16/03/27/17/00/00/", repo.getImagePath(event));
  }
}

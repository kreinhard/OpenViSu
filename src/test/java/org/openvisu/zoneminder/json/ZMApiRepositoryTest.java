package org.openvisu.zoneminder.json;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.openvisu.zoneminder.ZMEvent;
import org.openvisu.zoneminder.ZMMonitor;


public class ZMApiRepositoryTest
{

  @Test
  public void test()
  {
    ZMClientSession session = new ZMClientSession("file://src/test/data/zoneminder");
    ZMApiRepository repo = new ZMApiRepository(session);
    List<ZMMonitor> monitors = repo.getMonitors();
    assertNotNull(monitors);
    assertEquals(monitors.size(), 5);
    assertTrue("Tuer".equals(monitors.get(0).getName()));
    assertTrue("1".equals(monitors.get(0).getId()));
    List<ZMEvent> events = repo.getAllEvents();
    assertNotNull(events);
    assertEquals(events.size(), 931);
  }

}

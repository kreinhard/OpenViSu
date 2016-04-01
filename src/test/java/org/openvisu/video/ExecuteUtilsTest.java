package org.openvisu.video;

import static org.junit.Assert.*;

import org.junit.Test;

public class ExecuteUtilsTest
{
  @Test
  public void test()
  {
    assertNotNull(ExecuteUtils.instance().getCommandPath("ls", "/usr/bin"));
    assertTrue(ExecuteUtils.instance().getCommandPath("ls", "/usr/bin").endsWith("/ls"));
    assertNotNull(ExecuteUtils.instance().getCommandPath("ffmpeg", "/usr/bin"));
    assertTrue(ExecuteUtils.instance().getCommandPath("ffmpeg", "/usr/bin").endsWith("/ffmpeg"));

    assertNotNull(ExecuteUtils.instance().getCommandPath("passwd", "/etc/passwd"));
    assertTrue(ExecuteUtils.instance().getCommandPath("passwd", "/etc/passwd").endsWith("/passwd"));

    assertNotNull(ExecuteUtils.instance().getCommandPath("hosts", "/etc/"));
    assertTrue(ExecuteUtils.instance().getCommandPath("hosts", "/etc/").endsWith("/hosts"));
}
}

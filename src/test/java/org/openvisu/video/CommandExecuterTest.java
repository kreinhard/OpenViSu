package org.openvisu.video;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openvisu.OpenVisuConfig;

public class CommandExecuterTest
{
  @Test
  public void test()
  {
    assertNotNull(CommandExecuter.instance().getCommandPath("ls", "/usr/bin"));
    assertTrue(CommandExecuter.instance().getCommandPath("ls", "/usr/bin").endsWith("/ls"));
    assertNotNull(CommandExecuter.instance().getCommandPath("ffmpeg", "/usr/bin"));
    assertTrue(CommandExecuter.instance().getCommandPath("ffmpeg", "/usr/bin").endsWith("/ffmpeg"));

    assertNotNull(CommandExecuter.instance().getCommandPath("passwd", "/etc/passwd"));
    assertTrue(CommandExecuter.instance().getCommandPath("passwd", "/etc/passwd").endsWith("/passwd"));

    assertNotNull(CommandExecuter.instance().getCommandPath("hosts", "/etc/"));
    assertTrue(CommandExecuter.instance().getCommandPath("hosts", "/etc/").endsWith("/hosts"));

    OpenVisuConfig.instance().setProperty("environment.path.networks", "/etc");
    assertNotNull(CommandExecuter.instance().getCommandPath("networks", "environment.path.networks", "/"));
    assertTrue(CommandExecuter.instance().getCommandPath("networks", "environment.path.networks", "/").endsWith("/networks"));

    assertNotNull(CommandExecuter.instance().getCommandPath("group", "environment.path.group", "/etc"));
    assertTrue(CommandExecuter.instance().getCommandPath("group", "environment.path.group", "/etc").endsWith("/group"));
}
}

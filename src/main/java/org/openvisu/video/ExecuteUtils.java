package org.openvisu.video;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openvisu.OpenVisuConfig;

public class ExecuteUtils
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ExecuteUtils.class);

  private static final ExecuteUtils instance = new ExecuteUtils();

  private static String[] PATH = { "/bin", "/usr/bin", "/usr/local/bin", "/opt/bin", "/opt/local/bin"};

  private Map<String, String> commandMap = new HashMap<String, String>();

  public static ExecuteUtils instance()
  {
    return instance;
  }

  public String getCommandPath(String command, String configVar, String configDefault)
  {
    if (commandMap.containsKey(command) == true) {
      return commandMap.get(command);
    }
    String defaultPath = OpenVisuConfig.instance().getProperty(configVar, configDefault);
    return getCommandPath(command, defaultPath);
  }

  public String getCommandPath(String command, String defaultPath)
  {
    if (commandMap.containsKey(command) == true) {
      return commandMap.get(command);
    }
    File path = new File(defaultPath).getAbsoluteFile();
    String result = check(command, path);
    if (result != null) {
      return result;
    }
    if ((result = check(command, new File(defaultPath, command))) != null) {
      return result;
    }
    if ((result = check(command, PATH)) != null) {
      return result;
    }
    String envPath = System.getenv("PATH");
    if (envPath == null) {
      return null;
    }
    String[] dirs = StringUtils.split(envPath, ':');
    if (dirs != null && dirs.length > 0) {
      if ((result = check(command, PATH)) != null) {
        return result;
      }
    }
    return null;
  }

  private String check(String command, String... dirs)
  {
    String result;
    for (String dir : dirs) {
      result = check(command, new File(dir, command));
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  private String check(String command, File path)
  {
    if (path.exists() && path.isDirectory() == false) {
      log.info("Found " + command + ": " + path.getAbsolutePath());
      commandMap.put(command, path.getAbsolutePath());
      return path.getAbsolutePath();
    }
    return null;
  }

  private ExecuteUtils()
  {
  }
}

package org.openvisu.video;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openvisu.OpenVisuConfig;

/**
 * Searches for command lines in path {@link #PATH} and environment variable PATH. Caches found command for faster access.
 * @author kai
 *
 */
public class CommandExecuter
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CommandExecuter.class);

  private static final CommandExecuter instance = new CommandExecuter();

  private static String[] PATH = { "/bin", "/usr/bin", "/usr/local/bin", "/opt/bin", "/opt/local/bin"};

  private Map<String, String> commandMap = new HashMap<String, String>();

  public static CommandExecuter instance()
  {
    return instance;
  }

  public void execute(File executionDir, String... commandWithArgs)
  {
    ProcessBuilder pb = new ProcessBuilder(commandWithArgs);
    log.info("Executing command: " + pb.command() + " in dir: " + executionDir);
    pb.directory(executionDir);
    pb.redirectOutput(Redirect.INHERIT);
    pb.redirectError(Redirect.INHERIT);
    Process p = null;
    try {
      p = pb.start();
    } catch (IOException e) {
      String error = "Error while executing command '" + pb.command() + " in dir: " + executionDir + ": " + e.getMessage();
      log.error(error);
      throw new RuntimeException(error, e);
    }
    try {
      p.waitFor();
    } catch (InterruptedException e) {
      String error = "Error while waiting for command '" + pb.command() + " in dir: " + executionDir + ": " + e.getMessage();
      log.error(error);
      throw new RuntimeException(error, e);
    }
    if (executionDir.getAbsolutePath().startsWith(TempFileCache.instance().getDirectory().getAbsolutePath()) == true) {
      try {
        FileUtils.deleteDirectory(executionDir);
      } catch (IOException e) {
        log.error("Can't delete tmp working directory '" + executionDir.getAbsolutePath() + "': " + e.getMessage(), e);
      }
    }
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

  private CommandExecuter()
  {
  }
}

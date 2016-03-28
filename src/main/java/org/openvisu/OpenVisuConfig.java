package org.openvisu;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

public class OpenVisuConfig
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(OpenVisuConfig.class);

  private static final OpenVisuConfig instance = new OpenVisuConfig();

  public static OpenVisuConfig instance()
  {
    return instance;
  }

  private Properties props;

  /**
   * @param key
   * @param defaultValue default value to return if property not found.
   * @return propert
   */
  public String getProperty(String key, String defaultValue)
  {
    if (props == null) {
      read();
    }
    String val = props.getProperty(key);
    return val != null ? val : defaultValue;
  }

  /**
   * 
   * @return this for fluent pattern.
   */
  public OpenVisuConfig reread()
  {
    this.props = null;
    return this;
  }

  private void read()
  {
    String userHomeDir = System.getProperty("user.home");
    File propFile = new File(new File(userHomeDir, ".openvisu"), "config.props");
    if (propFile.exists() == false) {
      log.info("Property file '" + propFile.getAbsolutePath() + "' doesn't exist. Assuming default values.");
      props = new Properties();
      return;
    }
    Properties newProps = new Properties();
    try {
      InputStream is = new FileInputStream(propFile);
      newProps.load(is);
      IOUtils.closeQuietly(is);
    } catch (IOException e) {
      log.error("Error while loading property file: '" + propFile.getAbsolutePath() + "': " + e.getMessage(), e);
    }
    props = newProps;
  }
}

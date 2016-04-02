package org.openvisu;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Used parameters with their default values:
 * <ul>
 * <li>cache.dir=./cache</li>
 * <li>cache.expireTimeInHours=24</li>
 * <li>command.ffmpeg.path=ffmpeg</li>
 * <li>zoneminder.url=http://localhost/zm</li>
 * <li>zoneminder.viewUser=view</li>
 * <li>zoneminder.viewUser.password=test</li>
 * </ul>
 * Please configure your settings in ~/.openvisu/config.props
 * @author kai
 *
 */
public class OpenVisuConfig
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(OpenVisuConfig.class);

  private static final OpenVisuConfig instance = new OpenVisuConfig();

  public static OpenVisuConfig instance()
  {
    return instance;
  }

  private Properties props;

  private File propFile;

  private OpenVisuConfig()
  {
  }

  /**
   * @param key
   * @param defaultValue default value to return if property not found.
   * @return property
   */
  public String getProperty(String key, String defaultValue)
  {
    String val = getProperty(key);
    return val != null ? val : defaultValue;
  }

  /**
   * @param key
   * @param defaultValue default value to return if property not found.
   * @return property as long value.
   */
  public long getProperty(String key, long defaultValue)
  {
    String val = getProperty(key);
    if (StringUtils.isEmpty(val) == true) {
      return defaultValue;
    }
    try {
      return Long.valueOf(val);
    } catch (NumberFormatException ex) {
      log.warn("Couldn't convert config value '"
          + key
          + "' to long (default value "
          + defaultValue
          + " is used instead) from config file: "
          + propFile.getAbsolutePath());
      return defaultValue;
    }
  }
  
  /**
   * 
   * @param key
   * @param value
   * @return for fluent pattern.
   */
  public OpenVisuConfig setProperty(String key, String value) {
    if (props == null) {
      read();
    }
    props.setProperty(key, value);
    return this;
  }

  private String getProperty(String key)
  {
    if (props == null) {
      read();
    }
    String val = props.getProperty(key);
    return val;
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
  
  public File getConfigFile()
  {
    return propFile;
  }

  private void read()
  {
    String userHomeDir = System.getProperty("user.home");
    propFile = new File(new File(userHomeDir, ".openvisu"), "config.props");
    if (propFile.exists() == false) {
      log.info("Property file '" + propFile.getAbsolutePath() + "' doesn't exist. Assuming default values.");
      props = new Properties();
      return;
    }
    log.info("Read configuration from property file '" + propFile.getAbsolutePath() + "'.");
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

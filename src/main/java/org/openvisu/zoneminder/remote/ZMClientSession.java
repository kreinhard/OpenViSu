package org.openvisu.zoneminder.remote;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.openvisu.video.ImageCache;

/**
 * TODO: Reconnect (after time out, server restart etc.).
 * @author kai
 */
public class ZMClientSession
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ZMClientSession.class);

  private String baseUrl;

  private String baseImageUrl;

  private CloseableHttpClient httpclient;

  private SSLConnectionSocketFactory sslsf;

  private CookieStore cookieStore;

  private boolean authorized;

  private boolean testMode;

  public ZMClientSession(String url)
  {
    this.baseUrl = url;
    this.baseImageUrl = concatePath(baseUrl, "events");
    if (url.startsWith("file://") == true) {
      this.baseUrl = url.replace("file:/", System.getProperty("user.dir"));
      log.info("Initialization with file data (should only be used for test cases!");
      authorized = true; // File system needs no authorization.
      testMode = true;
    } else if (url.startsWith("https://") == true) {
      // Create a trust manager that does not validate certificate chains
      TrustManager[] trustAllCerts = new TrustManager[] { new TrustAllManager()};

      // Install the all-trusting trust manager
      SSLContext sslContext = null;
      try {
        sslContext = SSLContext.getInstance("SSL");
      } catch (NoSuchAlgorithmException e) {
        log.warn(e.getMessage(), e);
        throw new RuntimeException("Error while trying to instantiate SSL context.", e);
      }
      try {
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
      } catch (KeyManagementException e) {
        log.warn(e.getMessage(), e);
        throw new RuntimeException("Error while trying to instantiate SSL context.", e);
      }
      // Create all-trusting host name verifier
      HostnameVerifier allHostsValid = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session)
        {
          return true;
        }
      };
      sslsf = new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1"}, null, allHostsValid);
    }
  }

  public void closeQuietly()
  {
    authorized = false;
    if (httpclient == null) {
      return;
    }
    try {
      httpclient.close();
    } catch (IOException e) {
      log.info("Error while closing http client in ZMClientSession (maybe OK). Reason: " + e.getMessage());
    }
  }

  public boolean authenticate(String username, String password)
  {
    closeQuietly();
    cookieStore = new BasicCookieStore();
    httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).setDefaultCookieStore(cookieStore).build();
    String url = concatePath(baseUrl, "index.php");
    String result = internalHttpPost(url, new BasicNameValuePair("username", username), new BasicNameValuePair("password", password),
        new BasicNameValuePair("action", "login"), new BasicNameValuePair("view", "console"));
    if (result.contains("loginForm") == true) {
      log.warn("Authentication for user '" + username + "' wasn't sucessful.");
      return false;
    }
    log.info("Authentication for user '" + username + "' was sucessful.");
    authorized = true;
    return true;
  }

  public String readFile(String path)
  {
    if (testMode == false) {
      throw new RuntimeException("Should only be called for test cases");
    }
    File file = new File(baseUrl, path);
    try {
      String content = FileUtils.readFileToString(file);
      return content;
    } catch (IOException e) {
      log.error("Error while reading from file '" + file.getAbsolutePath() + "': " + e.getMessage(), e);
      return "";
    }
  }

  public String restCall(String path)
  {
    String url = concatePath(baseUrl, path);
    if (authorized == false) {
      throw new RuntimeException("Ignoring call, because user not authorized: " + url);
    }
    HttpGet httpGet = new HttpGet(url);
    return request(httpGet, "HTTP Get");
  }

  public String httpPut(String path)
  {
    String url = concatePath(baseUrl, path);
    if (authorized == false) {
      throw new RuntimeException("Ignoring call, because user not authorized: " + url);
    }
    HttpPut httpPut = new HttpPut(url);
    return request(httpPut, "HTTP Put");
  }

  public String httpDelete(String path)
  {
    String url = concatePath(baseUrl, path);
    if (authorized == false) {
      throw new RuntimeException("Ignoring call, because user not authorized: " + url);
    }
    HttpDelete httpDelete = new HttpDelete(url);
    return request(httpDelete, "HTTP Delete");
  }

  public String httpGet(String path)
  {
    String url = concatePath(baseUrl, path);
    if (authorized == false) {
      throw new RuntimeException("Ignoring call, because user not authorized: " + url);
    }
    HttpGet httpGet = new HttpGet(url);
    return request(httpGet, "HTTP Get");
  }

  public String httpPost(String path, NameValuePair... params)
  {
    String url = concatePath(baseUrl, path);
    if (authorized == false) {
      throw new RuntimeException("Ignoring call, because user not authorized: " + url);
    }
    return internalHttpPost(url, params);
  }

  public byte[] getEventImage(String path)
  {
    String url = concatePath(this.baseImageUrl, path);
    if (authorized == false) {
      throw new RuntimeException("Ignoring call, because user not authorized: " + url);
    }
    byte[] file = ImageCache.instance().getCachedImage(path);
    if (file != null) {
      return file;
    }
    HttpGet httpGet = new HttpGet(url);
    CloseableHttpResponse response = null;
    try {
      response = httpclient.execute(httpGet);
      HttpEntity entity = response.getEntity();
      InputStream inputStream = entity.getContent();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      IOUtils.copy(inputStream, outputStream);
      outputStream.close();
      file = outputStream.toByteArray();
      ImageCache.instance().writeImage(path, file);
      return file;
    } catch (IOException ex) {
      log.warn(ex.getMessage(), ex);
      throw new RuntimeException("Error while requesting http get '" + url + "'.", ex);
    } finally {
      if (response != null) {
        try {
          response.close();
        } catch (IOException ex) {
          log.warn(ex.getMessage(), ex);
          throw new RuntimeException("Error while requesting http get '" + url + "'.", ex);
        }
      }
    }
  }

  public boolean isTestMode()
  {
    return testMode;
  }

  private String internalHttpPost(String url, NameValuePair... params)
  {
    HttpPost httpPost = new HttpPost(url);
    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    for (NameValuePair param : params) {
      nvps.add(param);
    }
    try {
      httpPost.setEntity(new UrlEncodedFormEntity(nvps));
    } catch (UnsupportedEncodingException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Error while trying to encode post params.", e);
    }
    return request(httpPost, "HTTP Post");
  }

  private String request(HttpUriRequest request, String method)
  {
    String url = "" + request.getURI();
    CloseableHttpResponse response = null;
    try {
      response = httpclient.execute(request);
      HttpEntity entity = response.getEntity();
      log.info(method + " '" + url + "': " + response.getStatusLine());
      // for (Cookie cookie : cookieStore.getCookies()) {
      // System.out.println("Cookie=" + cookie.getName() + ", value=" + cookie.getValue());
      // }
      return EntityUtils.toString(entity);
    } catch (IOException ex) {
      log.warn(ex.getMessage(), ex);
      throw new RuntimeException("Error while requesting http post '" + url + "'.", ex);
    } finally {
      if (response != null) {
        try {
          response.close();
        } catch (IOException ex) {
          log.warn(ex.getMessage(), ex);
          throw new RuntimeException("Error while requesting http post '" + url + "'.", ex);
        }
      }
    }
  }

  private String concatePath(String url, String path)
  {
    if (url.endsWith("/") == true || path.startsWith("/") == true) {
      return url + path;
    }
    return url + "/" + path;
  }

}

package org.openvisu.zoneminder.json;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class TrustAllManager implements X509TrustManager
{

  public java.security.cert.X509Certificate[] getAcceptedIssuers()
  {
    return null;
  }

  public void checkClientTrusted(X509Certificate[] certs, String authType)
  {
  }

  public void checkServerTrusted(X509Certificate[] certs, String authType)
  {
  }

}

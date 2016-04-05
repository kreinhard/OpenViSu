package org.openvisu.web.rest;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.openvisu.security.SecurityConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class OpenVisuApplication
{
  @Configuration
  @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
  protected static class MySecurityConfiguration extends SecurityConfiguration
  {
  }

  @RequestMapping("/user")
  public Principal user(Principal user)
  {
    return user;
  }

  @RequestMapping("/resource")
  public Map<String, Object> home()
  {
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("id", UUID.randomUUID().toString());
    model.put("content", "Hello World!");
    return model;
  }

  public static void main(String[] args)
  {
    SpringApplication.run(OpenVisuApplication.class, args);
  }
}

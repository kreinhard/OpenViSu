package org.openvisu.web.rest;

import javax.validation.Valid;

import org.openvisu.video.Event;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PlayEventResource
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PlayEventResource.class);

  @RequestMapping(value = "/playEvent/{id}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public String getMonitor(@PathVariable Long id) {
      log.info("REST request to get Monitor : {}" + id);
      return "";
  }


  @RequestMapping(name = "/playEvent", method = RequestMethod.POST)
  public void create(@RequestBody @Valid Event event)
  {
    log.info("Play eventId: " + event.getId());
  }
}

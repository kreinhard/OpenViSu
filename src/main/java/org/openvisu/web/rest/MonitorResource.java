package org.openvisu.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.openvisu.domain.Monitor;
import org.openvisu.repository.MonitorRepository;
import org.openvisu.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Monitor.
 */
@RestController
@RequestMapping("/api")
public class MonitorResource {

    private final Logger log = LoggerFactory.getLogger(MonitorResource.class);
        
    @Inject
    private MonitorRepository monitorRepository;
    
    /**
     * POST  /monitors : Create a new monitor.
     *
     * @param monitor the monitor to create
     * @return the ResponseEntity with status 201 (Created) and with body the new monitor, or with status 400 (Bad Request) if the monitor has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/monitors",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Monitor> createMonitor(@RequestBody Monitor monitor) throws URISyntaxException {
        log.debug("REST request to save Monitor : {}", monitor);
        if (monitor.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("monitor", "idexists", "A new monitor cannot already have an ID")).body(null);
        }
        Monitor result = monitorRepository.save(monitor);
        return ResponseEntity.created(new URI("/api/monitors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("monitor", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /monitors : Updates an existing monitor.
     *
     * @param monitor the monitor to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated monitor,
     * or with status 400 (Bad Request) if the monitor is not valid,
     * or with status 500 (Internal Server Error) if the monitor couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/monitors",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Monitor> updateMonitor(@RequestBody Monitor monitor) throws URISyntaxException {
        log.debug("REST request to update Monitor : {}", monitor);
        if (monitor.getId() == null) {
            return createMonitor(monitor);
        }
        Monitor result = monitorRepository.save(monitor);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("monitor", monitor.getId().toString()))
            .body(result);
    }

    /**
     * GET  /monitors : get all the monitors.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of monitors in body
     */
    @RequestMapping(value = "/monitors",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Monitor> getAllMonitors() {
        log.debug("REST request to get all Monitors");
        List<Monitor> monitors = monitorRepository.findAll();
        return monitors;
    }

    /**
     * GET  /monitors/:id : get the "id" monitor.
     *
     * @param id the id of the monitor to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the monitor, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/monitors/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Monitor> getMonitor(@PathVariable Long id) {
        log.debug("REST request to get Monitor : {}", id);
        Monitor monitor = monitorRepository.findOne(id);
        return Optional.ofNullable(monitor)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /monitors/:id : delete the "id" monitor.
     *
     * @param id the id of the monitor to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/monitors/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteMonitor(@PathVariable Long id) {
        log.debug("REST request to delete Monitor : {}", id);
        monitorRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("monitor", id.toString())).build();
    }

}

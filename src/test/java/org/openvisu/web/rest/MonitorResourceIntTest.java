package org.openvisu.web.rest;

import org.openvisu.OpenViSuApp;
import org.openvisu.domain.Monitor;
import org.openvisu.repository.MonitorRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the MonitorResource REST controller.
 *
 * @see MonitorResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OpenViSuApp.class)
@WebAppConfiguration
@IntegrationTest
public class MonitorResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    @Inject
    private MonitorRepository monitorRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restMonitorMockMvc;

    private Monitor monitor;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MonitorResource monitorResource = new MonitorResource();
        ReflectionTestUtils.setField(monitorResource, "monitorRepository", monitorRepository);
        this.restMonitorMockMvc = MockMvcBuilders.standaloneSetup(monitorResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        monitor = new Monitor();
        monitor.setName(DEFAULT_NAME);
        monitor.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createMonitor() throws Exception {
        int databaseSizeBeforeCreate = monitorRepository.findAll().size();

        // Create the Monitor

        restMonitorMockMvc.perform(post("/api/monitors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(monitor)))
                .andExpect(status().isCreated());

        // Validate the Monitor in the database
        List<Monitor> monitors = monitorRepository.findAll();
        assertThat(monitors).hasSize(databaseSizeBeforeCreate + 1);
        Monitor testMonitor = monitors.get(monitors.size() - 1);
        assertThat(testMonitor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMonitor.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllMonitors() throws Exception {
        // Initialize the database
        monitorRepository.saveAndFlush(monitor);

        // Get all the monitors
        restMonitorMockMvc.perform(get("/api/monitors?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(monitor.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getMonitor() throws Exception {
        // Initialize the database
        monitorRepository.saveAndFlush(monitor);

        // Get the monitor
        restMonitorMockMvc.perform(get("/api/monitors/{id}", monitor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(monitor.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingMonitor() throws Exception {
        // Get the monitor
        restMonitorMockMvc.perform(get("/api/monitors/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMonitor() throws Exception {
        // Initialize the database
        monitorRepository.saveAndFlush(monitor);
        int databaseSizeBeforeUpdate = monitorRepository.findAll().size();

        // Update the monitor
        Monitor updatedMonitor = new Monitor();
        updatedMonitor.setId(monitor.getId());
        updatedMonitor.setName(UPDATED_NAME);
        updatedMonitor.setDescription(UPDATED_DESCRIPTION);

        restMonitorMockMvc.perform(put("/api/monitors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedMonitor)))
                .andExpect(status().isOk());

        // Validate the Monitor in the database
        List<Monitor> monitors = monitorRepository.findAll();
        assertThat(monitors).hasSize(databaseSizeBeforeUpdate);
        Monitor testMonitor = monitors.get(monitors.size() - 1);
        assertThat(testMonitor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMonitor.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void deleteMonitor() throws Exception {
        // Initialize the database
        monitorRepository.saveAndFlush(monitor);
        int databaseSizeBeforeDelete = monitorRepository.findAll().size();

        // Get the monitor
        restMonitorMockMvc.perform(delete("/api/monitors/{id}", monitor.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Monitor> monitors = monitorRepository.findAll();
        assertThat(monitors).hasSize(databaseSizeBeforeDelete - 1);
    }
}

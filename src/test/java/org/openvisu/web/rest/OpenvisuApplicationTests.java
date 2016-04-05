package org.openvisu.web.rest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openvisu.web.rest.OpenVisuApplication;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OpenVisuApplication.class)
@WebAppConfiguration
public class OpenvisuApplicationTests {

	@Test
	public void contextLoads() {
	}

}

package org.openvisu;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openvisu.OpenvisuApplication;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OpenvisuApplication.class)
@WebAppConfiguration
public class OpenvisuApplicationTests {

	@Test
	public void contextLoads() {
	}

}

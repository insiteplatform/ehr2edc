package com.custodix.insite.local.ehr2edc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.custodix.insite.local.GetUsersController;

@SpringBootTest(classes = SpringBootEHR2EDC.class)
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "jms.local.enabled=false",
								   "ehr2edc.oidc.enabled=true",
								   "users.datasource.username=sa",
								   "users.datasource.password=sa",
								   "users.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1",
								   "users.datasource.driver-class-name=org.h2.Driver" })
@MockBean(JmsTemplate.class)
public class SpringBootEHR2EDCTest {
	@Autowired
	private GetUsersController getUsersController;

	@Test
	public void springboot_ehr2edc_starts() throws Exception {
		assertThat(getUsersController).isNotNull();
	}
}
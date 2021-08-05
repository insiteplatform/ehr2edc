package com.custodix.workbench.local.scheduling;

import eu.ehr4cr.workbench.local.eventpublisher.EventPublisher;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SchedulingConfiguration.class)
abstract class SchedulerTest {
	@MockBean
	protected EventPublisher eventPublisher;
}

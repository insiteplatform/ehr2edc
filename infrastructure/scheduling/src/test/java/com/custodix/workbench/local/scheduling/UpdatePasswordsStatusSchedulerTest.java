package com.custodix.workbench.local.scheduling;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import com.custodix.insite.local.user.application.api.UpdatePasswordsStatus;

@TestPropertySource(properties = "user.password.expiration.enabled=true")
public class UpdatePasswordsStatusSchedulerTest extends SchedulerTest {
	@MockBean
	private UpdatePasswordsStatus updatePasswordsStatus;

	@Test
	public void updatePasswordsStatusIsCalled() {
		verify(updatePasswordsStatus).updatePasswordsStatus();
	}
}

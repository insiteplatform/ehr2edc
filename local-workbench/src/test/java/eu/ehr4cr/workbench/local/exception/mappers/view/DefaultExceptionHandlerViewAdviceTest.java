package eu.ehr4cr.workbench.local.exception.mappers.view;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import eu.ehr4cr.workbench.local.WebRoutes;

public class DefaultExceptionHandlerViewAdviceTest {

	@Test
	public void handleException() throws Exception {
		DefaultExceptionHandlerViewAdvice handler = new DefaultExceptionHandlerViewAdvice();
		String response = handler.handleException(new RuntimeException("Test"));
		assertThat(response.startsWith("redirect:" + WebRoutes.error + "?trackingId=")).as("redirect url incorrect")
				.isTrue();
	}

}

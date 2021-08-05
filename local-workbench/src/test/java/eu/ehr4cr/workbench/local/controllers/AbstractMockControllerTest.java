package eu.ehr4cr.workbench.local.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.MessageFormat;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.ehr4cr.workbench.local.service.DomainTime;
import eu.ehr4cr.workbench.local.service.TestTimeService;

public class AbstractMockControllerTest {

	static {
		DomainTime.setTime(new TestTimeService());
	}

	protected MockHttpServletRequestBuilder createBuilder(RequestMethod requestMethod, String url) {
		MockHttpServletRequestBuilder mvcBuilder;
		if (!requestMethod.equals(RequestMethod.GET) && !requestMethod.equals(RequestMethod.POST)) {
			throw new RuntimeException(
					MessageFormat.format("Request method {0} is not supported yet!", requestMethod.toString()));
		}
		if (requestMethod.equals(RequestMethod.GET)) {
			mvcBuilder = get(url);
		} else {
			mvcBuilder = post(url);
		}
		return mvcBuilder;
	}

	protected MvcResult doMockMvcRequestAndGetResult(MockMvc mvc, MockHttpServletRequestBuilder mvcBuilder,
			int expectedStatus) throws Exception {
		return preformMvcRequestAndExpectStatus(mvc, mvcBuilder.with(csrf()), expectedStatus);
	}

	private MvcResult preformMvcRequestAndExpectStatus(MockMvc mvc, MockHttpServletRequestBuilder mvcBuilder,
			int status) throws Exception {
		return mvc.perform(mvcBuilder)
				.andExpect(status().is(status))
 				.andReturn();
	}
}

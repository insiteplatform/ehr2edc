package eu.ehr4cr.workbench.local.controllers;

import java.lang.reflect.Method;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

/**
 * Spring Boot's error handling is based on Servlet container error mappings
 * that result in an ERROR dispatch to an ErrorController. MockMvc however is
 * container-less testing so with no Servlet container the exception simply
 * bubbles up with nothing to stop it.
 * <p>
 * So MockMvc tests simply aren't enough to test error responses generated
 * through Spring Boot. You probably shouldn't be testing Spring Boot's error
 * handling. If you're customizing it in any way you can write Spring Boot
 * integration tests (with an actual container) to verify error responses. And
 * then for MockMvc tests focus on fully testing the web layer while expecting
 * exceptions to bubble up.
 * <p>
 * This is a typical unit vs integration tests trade off. You do unit tests even
 * if they don't test everything because they give you more control and run
 * faster.
 * <p>
 * Having said all of that, you can inject this exception resolver in mockmvc to
 * handler the errors if you want to test the api on errors.
 */
public class MockMVCExceptionResolver extends ExceptionHandlerExceptionResolver {

	private final Object handlerAdvice;
	private final ExceptionHandlerMethodResolver exceptionHandlerMethodResolver;

	public static ExceptionHandlerExceptionResolver createExceptionResolver(Object handlerAdvice) {
		final MockMVCExceptionResolver exceptionResolver = new MockMVCExceptionResolver(handlerAdvice);
		exceptionResolver.afterPropertiesSet();
		exceptionResolver.getMessageConverters()
				.add(buildJacksonMapper());
		return exceptionResolver;
	}

	private static HttpMessageConverter<?> buildJacksonMapper() {
		final MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		return jackson2HttpMessageConverter;
	}

	private MockMVCExceptionResolver(Object handlerAdvice) {
		this.handlerAdvice = handlerAdvice;
		this.exceptionHandlerMethodResolver = new ExceptionHandlerMethodResolver(handlerAdvice.getClass());

	}

	@Override
	protected ServletInvocableHandlerMethod getExceptionHandlerMethod(final HandlerMethod handlerMethod,
			final Exception exception) {
		final Method method = exceptionHandlerMethodResolver.resolveMethod(exception);
		if (method != null) {
			return new ServletInvocableHandlerMethod(handlerAdvice, method);
		} else {
			return super.getExceptionHandlerMethod(handlerMethod, exception);
		}
	}
}

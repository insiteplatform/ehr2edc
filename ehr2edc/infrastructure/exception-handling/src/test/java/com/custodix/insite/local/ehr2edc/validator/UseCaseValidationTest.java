package com.custodix.insite.local.ehr2edc.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

import java.util.Locale;

import javax.validation.Valid;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.annotation.Validated;

import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolation;
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException;
import com.custodix.insite.local.ehr2edc.shared.exceptions.conversion.EHR2EDCValidationConfiguration;
import com.custodix.insite.local.ehr2edc.usecase.AccessDeniedExample;
import com.custodix.insite.local.ehr2edc.usecase.SomeExample;
import com.custodix.insite.local.ehr2edc.usecase.SomeExampleRequest;
import com.custodix.insite.local.ehr2edc.usecase.SystemExceptionExample;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { EHR2EDCValidationConfiguration.class, UseCaseValidationTest.TestConfiguration.class })
public class UseCaseValidationTest {

	@Autowired
	private SomeExample someExampleUseCase;
	@Autowired
	private SystemExceptionExample systemExceptionExample;
	@Autowired
	private AccessDeniedExample accessDeniedExample;

	@Test
	public void testThatRequestGetsValidated() {
		// given
		final SomeExampleRequest request = buildInvalidRequest();

		// when
		Throwable thrown = catchThrowable(() -> someExampleUseCase.doExample(request));

		// then
		assertThat(thrown).isInstanceOf(UseCaseConstraintViolationException.class);
		UseCaseConstraintViolationException ue = (UseCaseConstraintViolationException) thrown;
		assertThat(ue.getConstraintViolations()).containsExactlyInAnyOrder(
				UseCaseConstraintViolation.constraintViolation("somethingElse",
						"must be less than or equal to 5"),
				UseCaseConstraintViolation.constraintViolation("something", "size must be between 5 and 10"));
	}

	@Test
	public void testThatOnlyOneFieldGetsValidated() {
		// given
		final SomeExampleRequest request = buildInvalidRequest();

		// when
		Throwable thrown = catchThrowable(() -> {
			someExampleUseCase.doExample(request);
		});

		// then
		assertThat(thrown).isInstanceOf(UseCaseConstraintViolationException.class)
				.hasMessageContaining("somethingElse: must be less than or equal to 5");
	}

	@Test
	public void testThatRequestCanStillBeValid() {
		final SomeExampleRequest request = SomeExampleRequest.newBuilder()
				.withSomething("abcdef")
				.withSomethingElse(4)
				.build();
		someExampleUseCase.doExample(request);
	}

	@Test
	public void testSystemExceptionCatcher() {
		// when
		Throwable thrown = catchThrowable(() -> {
			systemExceptionExample.doSystemExceptionExample();
		});
		// then
		assertThat(thrown).isInstanceOf(SystemException.class);
	}

	@Test
	public void testAccessDeniedExceptionNotModified() {
		// when
		Throwable thrown = catchThrowable(() -> {
			accessDeniedExample.doAccessDeniedExample();
		});
		// then
		assertThat(thrown).isInstanceOf(AccessDeniedException.class);
	}

	private SomeExampleRequest buildInvalidRequest() {
		return SomeExampleRequest.newBuilder()
				.withSomething("x")
				.withSomethingElse(100)
				.build();
	}

	@Configuration
	@EnableAutoConfiguration
	public static class TestConfiguration {
		static {
			Locale english = Locale.ENGLISH;
			Locale.setDefault(english);
		}

		@Bean
		public SomeExample someExample() {
			return new AlwaysTrueUseCase();
		}

		@Bean
		public SystemExceptionExample systemExceptionExample() {
			return new SystemExceptionUseCase();
		}

		@Bean
		public AccessDeniedExample accessDeniedExample() {
			return new AccessDeniedUseCase();
		}
	}

	@Validated
	static class AlwaysTrueUseCase implements SomeExample {

		@Override
		public boolean doExample(@Valid SomeExampleRequest request) {
			return true;
		}
	}

	@Validated
	static class SystemExceptionUseCase implements SystemExceptionExample {

		@Override
		public void doSystemExceptionExample() {
			throw new RuntimeException();
		}
	}

	@Validated
	static class AccessDeniedUseCase implements AccessDeniedExample {

		@Override
		public void doAccessDeniedExample() {
			throw new AccessDeniedException("Access Denied");
		}
	}
}
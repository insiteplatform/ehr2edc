package eu.ehr4cr.workbench.local.conf;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

@Configuration
class ValidationConfiguration {
	@Bean
	Validator validator(AutowireCapableBeanFactory autowireCapableBeanFactory) {
		final ValidatorFactory validatorFactory = Validation.byDefaultProvider()
				.configure()
				.constraintValidatorFactory(new SpringConstraintValidatorFactory(autowireCapableBeanFactory))
				.buildValidatorFactory();
		return validatorFactory.getValidator();
	}

	@Bean
	@Primary
	MethodValidationPostProcessor methodValidationPostProcessorLwb(Validator validator) {
		final MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
		methodValidationPostProcessor.setValidator(validator);
		return methodValidationPostProcessor;
	}
}

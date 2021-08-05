package com.custodix.insite.local.ehr2edc.infrastructure.validation;

import java.lang.annotation.*;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
// Note: We use here already a validator which we will add in a sec too
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateRange {
	// used to get later in the resource bundle the i18n text
	String message() default "Date must be between {min} and {max}.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	// min value, we for now just a string
	String min() default "1900-01-01";
	// max date value we support
	String max() default "today";
}
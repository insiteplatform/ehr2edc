package com.custodix.insite.local.ehr2edc.vocabulary

import spock.lang.Specification

import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

abstract class AbstractValidatorSpecification<T> extends Specification {
    static {
        Locale english = Locale.ENGLISH;
        Locale.setDefault(english);
    }

    protected static Validator validator

    def setupSpec() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory()
        validator = factory.getValidator()
    }

    def validate(T obj, String property, String message) {
        Set<ConstraintViolation<T>> validate = validator.validate(obj)
        return validate
                .findAll { it.propertyPath.toString() == property }
                .find { it.getMessage() == message } != null
    }
}

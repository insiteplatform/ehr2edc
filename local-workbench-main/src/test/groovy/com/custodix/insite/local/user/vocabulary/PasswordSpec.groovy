package com.custodix.insite.local.user.vocabulary

import com.custodix.insite.local.user.vocabulary.validation.PasswordValidationSettingsConfiguration
import eu.ehr4cr.workbench.local.conf.ValidationConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.PropertySource
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.validation.ConstraintViolation
import javax.validation.Validator

@EnableConfigurationProperties(PasswordValidationSettingsConfiguration)
@PropertySource("classpath:lwb.properties")
@ContextConfiguration(classes = ValidationConfiguration)
class PasswordSpec extends Specification {
    @Autowired
    Validator validator

    def "Password that does not have any strength issues is valid"() {
        given: "A password that does not have any strength issues"
        Password password = Password.of("P4ssword_")

        when: "The password is validated"
        Set<ConstraintViolation<Password>> violations = validator.validate(password)

        then: "The password is valid"
        violations.empty
    }

    def "Password must not be too short"() {
        given: "A password that is too short"
        Password password = Password.of("P4ss_")

        when: "The password is validated"
        Set<ConstraintViolation<Password>> violations = validator.validate(password)

        then: "An appropriate violation message is returned"
        validateMessages(violations, "Password must be 8 or more characters in length.")
    }

    def "Password must not be too long"() {
        given: "A password that is too long"
        Password password = Password.of("P4ssword_ThatIsLongerThanTheMaximumAllowedNumber")

        when: "The password is validated"
        Set<ConstraintViolation<Password>> violations = validator.validate(password)

        then: "An appropriate violation message is returned"
        validateMessages(violations, "Password must be no more than 30 characters in length.")
    }

    def "Password must contain an uppercase letter"() {
        given: "A password that contains no uppercase letter"
        Password password = Password.of("p4ssword_")

        when: "The password is validated"
        Set<ConstraintViolation<Password>> violations = validator.validate(password)

        then: "An appropriate violation message is returned"
        validateMessages(violations, "Password must contain 1 or more uppercase characters.")
    }

    def "Password must contain a digit"() {
        given: "A password that contains no digits"
        Password password = Password.of("Password_")

        when: "The password is validated"
        Set<ConstraintViolation<Password>> violations = validator.validate(password)

        then: "An appropriate violation message is returned"
        validateMessages(violations, "Password must contain 1 or more digit characters.")
    }

    def "Password must contain a special character"() {
        given: "A password that contains no special characters"
        Password password = Password.of("P4ssword")

        when: "The password is validated"
        Set<ConstraintViolation<Password>> violations = validator.validate(password)

        then: "An appropriate violation message is returned"
        validateMessages(violations, "Password must contain 1 or more special characters.")
    }

    def "Password must not contain an alphabetical sequence"() {
        given: "A password that contains an alphabetical sequence"
        Password password = Password.of("P4ssword_cdef")

        when: "The password is validated"
        Set<ConstraintViolation<Password>> violations = validator.validate(password)

        then: "An appropriate violation message is returned"
        validateMessages(violations, "Password contains the illegal alphabetical sequence 'cdef'.")
    }

    def "Password must not contain a numerical sequence"() {
        given: "A password that contains a numerical sequence"
        Password password = Password.of("P4ssword_456")

        when: "The password is validated"
        Set<ConstraintViolation<Password>> violations = validator.validate(password)

        then: "An appropriate violation message is returned"
        validateMessages(violations, "Password contains the illegal numerical sequence '456'.")
    }

    def "Password must not contain a qwerty sequence"() {
        given: "A password that contains an qwerty sequence"
        Password password = Password.of("P4ssword_wert")

        when: "The password is validated"
        Set<ConstraintViolation<Password>> violations = validator.validate(password)

        then: "An appropriate violation message is returned"
        validateMessages(violations, "Password contains the illegal QWERTY sequence 'wert'.")
    }

    def "Password must not have multiple strength issues"() {
        given: "A password that has multiple strength issues"
        Password password = Password.of("password")

        when: "The password is validated"
        Set<ConstraintViolation<Password>> violations = validator.validate(password)

        then: "An appropriate violation message for each strength issue is returned"
        validateMessages(violations, "Password must contain 1 or more special characters.",
                "Password must contain 1 or more digit characters.",
                "Password must contain 1 or more uppercase characters.")
    }

    private void validateMessages(Set<ConstraintViolation<Password>> violations, String... messages) {
        assert violations.size() == messages.length
        Arrays.stream(messages).forEach({ m -> validateContainsMessage(violations, m) })
    }

    private void validateContainsMessage(Set<ConstraintViolation<Password>> violations, String message) {
        boolean contains = false
        for (ConstraintViolation<Password> violation : violations) {
            contains = contains || violation.message == message
        }
        assert contains
    }
}

package com.custodix.insite.local.user.vocabulary.validation;

import static org.passay.EnglishCharacterData.Digit;
import static org.passay.EnglishCharacterData.Special;
import static org.passay.EnglishCharacterData.UpperCase;
import static org.passay.EnglishSequenceData.Alphabetical;
import static org.passay.EnglishSequenceData.Numerical;
import static org.passay.EnglishSequenceData.USQwerty;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.passay.CharacterData;
import org.passay.*;

import com.custodix.insite.local.user.vocabulary.validation.PasswordValidationSettings.MaxRule;
import com.custodix.insite.local.user.vocabulary.validation.PasswordValidationSettings.MinMaxRule;
import com.custodix.insite.local.user.vocabulary.validation.PasswordValidationSettings.MinRule;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
	private final PasswordValidator validator;

	public PasswordConstraintValidator(PasswordValidationSettings settings) {
		this.validator = createPasswordValidator(settings);
	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
		RuleResult result = validator.validate(new PasswordData(password));
		addValidationMessages(constraintValidatorContext, result);
		return result.isValid();
	}

	private PasswordValidator createPasswordValidator(PasswordValidationSettings settings) {
		List<Rule> rules = new ArrayList<>();
		addLengthRule(rules, settings.getLengthRule());
		addCharacterRule(rules, settings.getUppercaseCharactersRule(), UpperCase);
		addCharacterRule(rules, settings.getDigitCharactersRule(), Digit);
		addCharacterRule(rules, settings.getSpecialCharactersRule(), Special);
		addSequenceRule(rules, settings.getAlphabeticalSequenceRule(), Alphabetical);
		addSequenceRule(rules, settings.getNumericalSequenceRule(), Numerical);
		addSequenceRule(rules, settings.getQwertySequenceRule(), USQwerty);
		addWhitespaceRule(rules, settings.getWhitespaceRule());
		return new PasswordValidator(rules);
	}

	private void addLengthRule(List<Rule> rules, MinMaxRule rule) {
		if (rule.isEnabled()) {
			rules.add(new LengthRule(rule.getMin(), rule.getMax()));
		}
	}

	private void addCharacterRule(List<Rule> rules, MinRule rule, CharacterData characterData) {
		if (rule.isEnabled()) {
			rules.add(new CharacterRule(characterData, rule.getMin()));
		}
	}

	private void addSequenceRule(List<Rule> rules, MaxRule rule, SequenceData sequenceData) {
		if (rule.isEnabled()) {
			rules.add(new IllegalSequenceRule(sequenceData, rule.getMax(), false));
		}
	}

	private void addWhitespaceRule(List<Rule> rules, PasswordValidationSettings.Rule rule) {
		if (rule.isEnabled()) {
			rules.add(new WhitespaceRule());
		}
	}

	private void addValidationMessages(ConstraintValidatorContext constraintValidatorContext, RuleResult result) {
		constraintValidatorContext.disableDefaultConstraintViolation();
		validator.getMessages(result)
				.forEach(m -> addValidationMessage(constraintValidatorContext, m));
	}

	private void addValidationMessage(ConstraintValidatorContext constraintValidatorContext, String message) {
		constraintValidatorContext.buildConstraintViolationWithTemplate(message)
				.addConstraintViolation();
	}
}

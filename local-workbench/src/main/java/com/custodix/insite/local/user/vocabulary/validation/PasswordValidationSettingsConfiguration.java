package com.custodix.insite.local.user.vocabulary.validation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "user.password.validation.rule")
@Component
public class PasswordValidationSettingsConfiguration implements PasswordValidationSettings {
	private MinMaxRule length;
	private CharactersRules characters;
	private SequenceRules sequence;
	private Rule whitespace;

	@Override
	public MinMaxRule getLengthRule() {
		return length;
	}

	@Override
	public MinRule getUppercaseCharactersRule() {
		return characters.getUppercase();
	}

	@Override
	public MinRule getDigitCharactersRule() {
		return characters.getDigit();
	}

	@Override
	public MinRule getSpecialCharactersRule() {
		return characters.getSpecial();
	}

	@Override
	public MaxRule getAlphabeticalSequenceRule() {
		return sequence.getAlphabetical();
	}

	@Override
	public MaxRule getNumericalSequenceRule() {
		return sequence.getNumerical();
	}

	@Override
	public MaxRule getQwertySequenceRule() {
		return sequence.getQwerty();
	}

	@Override
	public Rule getWhitespaceRule() {
		return whitespace;
	}

	public void setLength(MinMaxRule length) {
		this.length = length;
	}

	public void setCharacters(CharactersRules characters) {
		this.characters = characters;
	}

	public void setSequence(SequenceRules sequence) {
		this.sequence = sequence;
	}

	public void setWhitespace(Rule whitespace) {
		this.whitespace = whitespace;
	}
}

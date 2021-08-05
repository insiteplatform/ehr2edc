package com.custodix.insite.local.user.vocabulary.validation;

public interface PasswordValidationSettings {
	MinMaxRule getLengthRule();

	MinRule getUppercaseCharactersRule();

	MinRule getDigitCharactersRule();

	MinRule getSpecialCharactersRule();

	MaxRule getAlphabeticalSequenceRule();

	MaxRule getNumericalSequenceRule();

	MaxRule getQwertySequenceRule();

	Rule getWhitespaceRule();

	final class CharactersRules {
		private MinRule uppercase;
		private MinRule digit;
		private MinRule special;

		public MinRule getUppercase() {
			return uppercase;
		}

		public void setUppercase(MinRule uppercase) {
			this.uppercase = uppercase;
		}

		public MinRule getDigit() {
			return digit;
		}

		public void setDigit(MinRule digit) {
			this.digit = digit;
		}

		public MinRule getSpecial() {
			return special;
		}

		public void setSpecial(MinRule special) {
			this.special = special;
		}
	}

	final class SequenceRules {
		private MaxRule alphabetical;
		private MaxRule numerical;
		private MaxRule qwerty;

		public MaxRule getAlphabetical() {
			return alphabetical;
		}

		public void setAlphabetical(MaxRule alphabetical) {
			this.alphabetical = alphabetical;
		}

		public MaxRule getNumerical() {
			return numerical;
		}

		public void setNumerical(MaxRule numerical) {
			this.numerical = numerical;
		}

		public MaxRule getQwerty() {
			return qwerty;
		}

		public void setQwerty(MaxRule qwerty) {
			this.qwerty = qwerty;
		}
	}

	final class MinMaxRule extends Rule {
		private int min;
		private int max;

		public int getMin() {
			return min;
		}

		public void setMin(int min) {
			this.min = min;
		}

		public int getMax() {
			return max;
		}

		public void setMax(int max) {
			this.max = max;
		}
	}

	final class MinRule extends Rule {
		private int min;

		public int getMin() {
			return min;
		}

		public void setMin(int min) {
			this.min = min;
		}
	}

	final class MaxRule extends Rule {
		private int max;

		public int getMax() {
			return max;
		}

		public void setMax(int max) {
			this.max = max;
		}
	}

	class Rule {
		private boolean enabled;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}
}

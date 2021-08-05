package eu.ehr4cr.workbench.local.global;

import java.text.MessageFormat;
import java.util.Locale;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class CustomSpringMessageResolver extends ReloadableResourceBundleMessageSource {

	@Override
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		String message = super.resolveCodeWithoutArguments(code, locale);
		if (message == null) {
			message = getResourceMissingMessage(code);
		}
		return message;
	}

	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		MessageFormat message = super.resolveCode(code, locale);
		if (message == null) {
			message = new MessageFormat(getResourceMissingMessage(code));
		}
		return message;
	}

	private String getResourceMissingMessage(String code) {
		return "<i>Resource missing: " + code + "</i>";
	}

}

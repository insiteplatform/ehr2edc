package eu.ehr4cr.workbench.local.global;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.LocaleResolver;

public class CustomLocaleResolver implements LocaleResolver {

	private boolean resolveLocale;
	private LocaleResolver usedLocaleResolver;

	public void setResolveLocale(boolean resolveLocale) {
		this.resolveLocale = resolveLocale;
	}

	public void setUsedLocaleResolver(LocaleResolver localeResolver) {
		this.usedLocaleResolver = localeResolver;
	}

	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		if (resolveLocale)
			return usedLocaleResolver.resolveLocale(request);
		else {
			return new Locale("en");
		}
	}

	@Override
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		usedLocaleResolver.setLocale(request, response, locale);
	}

}

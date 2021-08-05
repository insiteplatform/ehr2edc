package eu.ehr4cr.workbench.local.controllers;

import java.net.MalformedURLException;

import javax.servlet.ServletContext;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import eu.ehr4cr.workbench.local.model.security.User;

/**
 * To be used by Spring Controllers (once they are migrated from regular web
 * servlets)
 */
@Component
public class IndexSegmentSpring {
	private static final String HAS_JS_SEGMENT = "hasJSSegment";
	private static final String HAS_CSS_SEGMENT = "hasCSSSegment";
	private static final String ENABLE_INTERNATIONALIZATION = "enableInternationalization";

	private final Environment env;
	private final LoginRouteProvider loginRouteProvider;

	public IndexSegmentSpring(Environment env, LoginRouteProvider loginRouteProvider) {
		this.env = env;
		this.loginRouteProvider = loginRouteProvider;
	}

	public void process(Model model, ServletContext context, User user, NavigationItem navigationItem, String view) {
		model.addAttribute("contextPath", context.getContextPath());
		model.addAttribute("title", "InSite - a TriNetX Company");
		model.addAttribute("sections", NavigationItem.getNavigationItems(user, false, env));
		model.addAttribute("userMenu", NavigationItem.getNavigationItems(user, true, env));
		model.addAttribute("user", user);
		model.addAttribute("loginRoute", loginRouteProvider.getRoute());

		addCss(model, context, view);
		addJavascript(model, context, view);

		model.addAttribute("section", navigationItem);
		model.addAttribute("bodySegment", view);

		model.addAttribute(ENABLE_INTERNATIONALIZATION,
				env.getProperty(ENABLE_INTERNATIONALIZATION, Boolean.class, false));
	}

	private void addCss(Model model, ServletContext context, String view) {
		final String itemCSS = view.replace(".jsp", "-css.jsp");
		try {
			if (context.getResource(itemCSS) != null) {
				model.addAttribute(HAS_CSS_SEGMENT, true);
				model.addAttribute("CSSSegment", itemCSS);
			} else {
				model.addAttribute(HAS_CSS_SEGMENT, false);
			}
		} catch (MalformedURLException t) {
			model.addAttribute(HAS_CSS_SEGMENT, false);
		}
	}

	private void addJavascript(Model model, ServletContext context, String view) {
		final String itemJS = view.replace(".jsp", "-js.jsp");
		try {
			if (context.getResource(itemJS) != null) {
				model.addAttribute(HAS_JS_SEGMENT, true);
				model.addAttribute("JSSegment", itemJS);
			} else {
				model.addAttribute(HAS_JS_SEGMENT, false);
			}
		} catch (MalformedURLException t) {
			model.addAttribute(HAS_JS_SEGMENT, false);
		}
	}
}

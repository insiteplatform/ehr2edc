package eu.ehr4cr.workbench.local.controllers;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

import eu.ehr4cr.workbench.local.WebViews;
import eu.ehr4cr.workbench.local.exception.security.InvalidPermissionsException;
import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.model.security.AnonymousUser;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.security.SecurityContextUser;

public abstract class BaseController implements ServletContextAware {
	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	public static final String MIME_TYPE_JSON_UTF8 = "application/json; charset=UTF-8";

	protected ServletContext context;
	@Autowired
	protected IndexSegmentSpring indexSegmentSpring;

	protected Map<String, AuthorityType> methodAuthorities = new HashMap<>();

	protected User getUser() {
		final Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return new AnonymousUser();
		}
		final SecurityContextUser securityContextUser = (SecurityContextUser) authentication.getPrincipal();
		return securityContextUser.getWorkbenchUser();
	}

	protected String getPageUrlWithRootPath(final String pagePath) {
		return context.getContextPath() + pagePath;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.context = servletContext;
	}

	@ExceptionHandler(InvalidPermissionsException.class)
	public ModelAndView handleInvalidPermissionsException(InvalidPermissionsException ex, HttpServletResponse httpServletResponse) {
		httpServletResponse.setStatus(SC_FORBIDDEN);
		return renderErrorPage(ex.getUser());
	}

	@ExceptionHandler({AccessDeniedException.class})
	public ModelAndView handleAccessDeniedException(HttpServletResponse httpServletResponse) {
		httpServletResponse.setStatus(SC_FORBIDDEN);
		return renderErrorPage(getUser());
	}

	private ModelAndView renderErrorPage(User user) {
		Model model = new ExtendedModelMap();
		indexSegmentSpring.process(model, this.context, user, NavigationItem.myAccount, WebViews.unauthorised);
		return new ModelAndView(WebViews.index, model.asMap());
	}
}
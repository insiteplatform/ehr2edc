package eu.ehr4cr.workbench.local.exception.mappers.view;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.custodix.insite.local.shared.annotations.ViewController;

import eu.ehr4cr.workbench.local.WebRoutes;

@ControllerAdvice(basePackages = "eu.ehr4cr.workbench.local.controllers.view",
				  annotations = ViewController.class)
@Order(Ordered.LOWEST_PRECEDENCE)
class DefaultExceptionHandlerViewAdvice {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultExceptionHandlerViewAdvice.class);

	@ExceptionHandler(Exception.class)
	String handleException(Exception e) {
		String trackingId = UUID.randomUUID()
				.toString();
		LOGGER.error("Uncaught exception from view-controller (trackingId {})", trackingId, e);
		return "redirect:" + WebRoutes.error + "?trackingId=" + trackingId;
	}

}

package eu.ehr4cr.workbench.local.exception.mappers.api;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(basePackages = "eu.ehr4cr.workbench.local.controllers.api",
				  annotations = RestController.class)
@Order(Ordered.LOWEST_PRECEDENCE)
class DefaultExceptionHandlerApiAdvice {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultExceptionHandlerApiAdvice.class);

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	ResponseEntity<Void> handleException(Exception e) {
		String trackingId = UUID.randomUUID()
				.toString();
		LOGGER.error("Uncaught exception from api-controller (trackingId {})", trackingId, e);
		return ResponseEntity.badRequest()
				.header("InSite-error-code", trackingId)
				.build();
	}

}

package eu.ehr4cr.workbench.local.global;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class StartupSpringBoot implements ApplicationListener<ApplicationReadyEvent>, Ordered {
	private static final Logger LOGGER = LoggerFactory.getLogger(StartupSpringBoot.class);

	private final IDatabaseInitiator databaseInitiator;

	@Autowired
	public StartupSpringBoot(final IDatabaseInitiator databaseInitiator) {
		this.databaseInitiator = databaseInitiator;
	}

	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		LOGGER.info("Initializing context - begin");
		try {
			databaseInitiator.launch();
		} catch (Exception t) {
			LOGGER.error("Initializing context - could not initialise the application", t);
			throw t;
		}
		Locale.setDefault(new Locale("en"));
		LOGGER.info("Initializing context - end");
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
}

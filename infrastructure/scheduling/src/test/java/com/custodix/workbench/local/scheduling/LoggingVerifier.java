package com.custodix.workbench.local.scheduling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.mockito.ArgumentCaptor;

public class LoggingVerifier {
	private final Appender errorAppender;
	private final Appender debugAppender;
	private Level originalLevel;

	public LoggingVerifier() {
		this.errorAppender = mock(Appender.class);
		this.debugAppender = mock(Appender.class);
		setupLogger();
	}

	public void verifyErrorLog(String message) {
		verifyLog(errorAppender, message);
	}

	public void verifyDebugLog(String message, Class exceptionClass) {
		verifyLog(debugAppender, message, exceptionClass);
	}

	public void close() {
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		config.getRootLogger().setLevel(originalLevel);
		config.getRootLogger().removeAppender(errorAppender.getName());
		config.getRootLogger().removeAppender(debugAppender.getName());
		ctx.updateLoggers(config);
	}

	private LogEvent verifyLog(Appender appender) {
		ArgumentCaptor<LogEvent> captorLoggingEvent = ArgumentCaptor.forClass(LogEvent.class);
		verify(appender, atLeastOnce()).append(captorLoggingEvent.capture());
		return captorLoggingEvent.getValue();
	}

	private LogEvent verifyLog(Appender appender, String message) {
		LogEvent event = verifyLog(appender);
		assertThat(event.getMessage().getFormattedMessage()).isEqualTo(message);
		return event;
	}

	private LogEvent verifyLog(Appender appender, String message, Class exceptionClass) {
		LogEvent event = verifyLog(appender, message);
		assertThat(event.getThrown()).isInstanceOf(exceptionClass);
		return event;
	}

	private void setupLogger() {
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		setupMockAppenders();
		setupRootLogger(config);
		ctx.updateLoggers(config);
	}

	private void setupRootLogger(Configuration config) {
		originalLevel = config.getRootLogger().getLevel();
		config.getRootLogger().setLevel(Level.DEBUG);
		config.getRootLogger().addAppender(errorAppender, Level.ERROR, mock(Filter.class));
		config.getRootLogger().addAppender(debugAppender, Level.DEBUG, mock(Filter.class));
	}

	private void setupMockAppenders() {
		when(errorAppender.getName()).thenReturn("errorLogger");
		when(errorAppender.isStarted()).thenReturn(true);
		when(debugAppender.getName()).thenReturn("debugLogger");
		when(debugAppender.isStarted()).thenReturn(true);
	}
}

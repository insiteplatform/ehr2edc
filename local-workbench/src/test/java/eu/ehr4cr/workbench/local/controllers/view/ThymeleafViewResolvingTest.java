package eu.ehr4cr.workbench.local.controllers.view;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.thymeleaf.spring5.dialect.SpringStandardDialect;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;
import org.thymeleaf.testing.templateengine.messages.ITestMessagesForLocale;
import org.thymeleaf.testing.templateengine.messages.TestMessages;
import org.thymeleaf.testing.templateengine.messages.TestMessagesForLocale;
import org.thymeleaf.testing.templateengine.report.AbstractTextualTestReporter;
import org.thymeleaf.testing.templateengine.standard.resolver.StandardTestableResolver;
import org.thymeleaf.testing.templateengine.standard.test.StandardTest;
import org.thymeleaf.testing.templateengine.standard.test.builder.StandardTestBuilder;
import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestEvaluatedData;
import org.thymeleaf.testing.templateengine.testable.ITest;

import nz.net.ultraq.thymeleaf.LayoutDialect;

public class ThymeleafViewResolvingTest {
	@Test
	public void test() {
		TestExecutor executor = createTestExecutor();
		executor.execute("classpath:templates/thymeleaf");
		assertTrue(executor.isAllOK());
	}

	private TestExecutor createTestExecutor() {
		TestExecutor executor = new TestExecutor();
		executor.setReporter(new CustomReporter());
		executor.setDialects(Arrays.asList(new SpringStandardDialect(), new LayoutDialect()));
		StandardTestableResolver testableResolver = (StandardTestableResolver) executor.getTestableResolver();
		testableResolver.setTestBuilder(new CustomTestBuilder());
		return executor;
	}

	private static final class CustomReporter extends AbstractTextualTestReporter {
		private static final Logger LOGGER = LoggerFactory.getLogger(ThymeleafViewResolvingTest.class);

		protected void output(String line, boolean error) {
			if (error) {
				LOGGER.error(line);
			} else {
				LOGGER.info(line);
			}
		}
	}

	private static final class CustomTestBuilder extends StandardTestBuilder {
		@Override
		protected void additionalInitialization(final StandardTest test, final ITest parentTest,
				final StandardTestEvaluatedData data) {
			TestMessages messages = new TestMessages();
			Map<Locale, ITestMessagesForLocale> messagesByLocale = new HashMap<>();
			TestMessagesForLocale messagesForEnglish = new TestMessagesForLocale();
			messagesForEnglish.setMessagesForLocale(getMessages());
			messagesByLocale.put(Locale.ENGLISH, messagesForEnglish);
			messages.setMessagesByLocale(messagesByLocale);
			test.setMessages(messages);
		}

		private Map<String, String> getMessages() {
			try {
				Resource resource = new ClassPathResource("/html-locales/messages.properties");
				Properties props = PropertiesLoaderUtils.loadProperties(resource);
				return toMap(props);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		private Map<String, String> toMap(Properties props) {
			return props.entrySet()
					.stream()
					.collect(Collectors.toMap(e -> e.getKey()
							.toString(), e -> e.getValue()
							.toString()));
		}
	}
}

package eu.ehr4cr.workbench.local.conf;

import static java.lang.Long.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.provider.ConfigProperties;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TransactionConfiguration.class)
public class TransactionConfigurationTest {

	@Autowired
	private AbstractPlatformTransactionManager platformTransactionManager;
	@Autowired
	private UserTransactionService userTransactionService;

	@Test
	public void maxTimeoutGreaterThanTimeout() {
		Properties properties = (Properties) ReflectionTestUtils.getField(userTransactionService, "properties_");
		Long maxTimeout = valueOf(properties.getProperty(ConfigProperties.MAX_TIMEOUT_PROPERTY_NAME));
		Long timeout = valueOf(platformTransactionManager.getDefaultTimeout());
		assertThat(maxTimeout).isGreaterThan(timeout);
	}

}
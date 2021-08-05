package com.custodix.insite.local.ehr2edc.transactions;

import java.util.Properties;

import javax.naming.Context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.icatch.provider.ConfigProperties;

@EnableTransactionManagement
@Configuration
public class TransactionConfiguration {
	@ConditionalOnProperty(value = "integration-tests",
						   havingValue = "false",
						   matchIfMissing = true)
	@Bean(name = { "localTransactionManager", "crcTransactionManager" })
	@Primary
	@DependsOn("userTransactionService")
	JtaTransactionManager jtaTransactionManager(UserTransactionManager userTransactionManager,
			UserTransactionImp userTransactionImp,
			@Value("${transaction.timeout.seconds:3600}") int transactionTimeout) {
		JtaTransactionManager jtaTransactionManager = new JtaTransactionManager(userTransactionImp,
				userTransactionManager);
		jtaTransactionManager.setDefaultTimeout(transactionTimeout);
		return jtaTransactionManager;
	}

	@Bean(initMethod = "init",
		  destroyMethod = "close")
	@DependsOn("userTransactionService")
	UserTransactionManager userTransactionManager() {
		UserTransactionManager userTransactionManager = new UserTransactionManager();
		userTransactionManager.setStartupTransactionService(false);
		return userTransactionManager;
	}

	@Bean
	@DependsOn("userTransactionService")
	UserTransactionImp userTransactionImp() {
		return new UserTransactionImp();
	}

	@Bean(initMethod = "init",
		  destroyMethod = "shutdownForce")
	UserTransactionService userTransactionService(AtomikosLogSettings atomikosLogSettings) {
		Properties properties = new Properties();
		properties.setProperty(ConfigProperties.LOG_BASE_DIR_PROPERTY_NAME, atomikosLogSettings.getDir());
		properties.setProperty(ConfigProperties.LOG_BASE_NAME_PROPERTY_NAME, atomikosLogSettings.getName());
		properties.setProperty(ConfigProperties.MAX_TIMEOUT_PROPERTY_NAME, String.valueOf(Long.MAX_VALUE));
		properties.setProperty(Context.PROVIDER_URL, "rmi://localhost:1100");
		return new UserTransactionServiceImp(properties);
	}

	@Bean
	AtomikosLogSettings atomikosLogSettings() {
		return new AtomikosLogSettingsProperties();
	}

}
package eu.ehr4cr.workbench.local.global;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.sf.ehcache.config.CacheConfiguration;

@Configuration
public class CachingContextLwb {

	@Bean(name = "stsTokensCache")
	public CacheConfiguration stsTokensCacheConfiguration() {
		CacheConfiguration cacheConfiguration = new CacheConfiguration();
		cacheConfiguration.setName("stsTokensCache");
		long tenMegabytes = 10L * 1024L * 1024L;
		cacheConfiguration.setMaxBytesLocalHeap(tenMegabytes);
		return cacheConfiguration;
	}

}

package eu.ehr4cr.workbench.local.global.web;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import eu.ehr4cr.workbench.local.global.web.WebResourcesCacheSettings.MaxAge;

@Configuration
@EnableConfigurationProperties(WebResourcesCacheSettingsProperties.class)
public class WebMvcConfigurerImpl implements WebMvcConfigurer {
	private final WebResourcesCacheSettings cacheSettings;

	public WebMvcConfigurerImpl(WebResourcesCacheSettings cacheSettings) {
		this.cacheSettings = cacheSettings;
	}

	@Override
	public void configurePathMatch(PathMatchConfigurer matcher) {
		matcher.setUseSuffixPatternMatch(false);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/locales/**", "/public/locales/**")
				.addResourceLocations("/locales/", "classpath:/META-INF/resources/public/locales/")
				.setCacheControl(CacheControl.noStore());
		registry.addResourceHandler("/**")
				.addResourceLocations("/", "classpath:/META-INF/resources/public/","classpath:/public")
				.setCacheControl(createCacheControl())
				.resourceChain(true)
				.addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));
	}

	@Bean
	ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
		return new ResourceUrlEncodingFilter();
	}

	private CacheControl createCacheControl() {
		MaxAge maxage = cacheSettings.getMaxage();
		return CacheControl.maxAge(maxage.getValue(), maxage.getUnit());
	}
}
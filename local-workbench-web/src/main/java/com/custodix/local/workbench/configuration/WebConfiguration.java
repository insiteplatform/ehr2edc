package com.custodix.local.workbench.configuration;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class WebConfiguration {
	@Bean
	public CommonsMultipartResolver multipartResolver() {
		return new CommonsMultipartResolver() {
			@Override
			public boolean isMultipart(HttpServletRequest request) {
				final String method = request.getMethod();
				if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
					return FileUploadBase.isMultipartContent(new ServletRequestContext(request));
				}
				return false;
			}
		};
	}
}

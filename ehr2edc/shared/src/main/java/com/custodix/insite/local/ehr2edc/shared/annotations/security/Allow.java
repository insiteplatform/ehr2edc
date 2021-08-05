package com.custodix.insite.local.ehr2edc.shared.annotations.security;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Allow {

	enum Rule {
		ANYONE,
		AUTHENTICATED,
		ASSIGNED_INVESTIGATOR,
		DRM
	}

	Rule[] value() default {};
}

package com.custodix.insite.local.ehr2edc.shared.annotations.security;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DenyAll {
}

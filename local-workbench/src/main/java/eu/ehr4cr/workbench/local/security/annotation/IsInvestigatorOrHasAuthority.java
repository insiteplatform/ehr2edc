package eu.ehr4cr.workbench.local.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import eu.ehr4cr.workbench.local.global.AuthorityType;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IsInvestigatorOrHasAuthority {
	AuthorityType value();
}

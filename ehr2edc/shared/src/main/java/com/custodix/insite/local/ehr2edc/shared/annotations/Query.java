package com.custodix.insite.local.ehr2edc.shared.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.custodix.insite.local.ehr2edc.shared.annotations.security.DenyAll;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Transactional(transactionManager = "ehr2edcMongoAppMongoTransactionManager", readOnly = true)
@Validated
@Component
@Audit
@DenyAll
public @interface Query {
}

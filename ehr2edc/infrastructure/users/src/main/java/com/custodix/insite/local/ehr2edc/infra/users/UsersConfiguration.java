package com.custodix.insite.local.ehr2edc.infra.users;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan
@Import(UsersDataSourceConfiguration.class)
public class UsersConfiguration {

}

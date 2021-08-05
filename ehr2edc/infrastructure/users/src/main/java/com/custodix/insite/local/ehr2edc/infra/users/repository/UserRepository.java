package com.custodix.insite.local.ehr2edc.infra.users.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.custodix.insite.local.ehr2edc.infra.users.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	List<User> findByCredentialEnabledTrueAndCredentialDeletedFalse();
}

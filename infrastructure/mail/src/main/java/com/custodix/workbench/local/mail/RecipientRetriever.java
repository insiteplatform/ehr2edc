package com.custodix.workbench.local.mail;

import java.util.List;
import java.util.stream.Collectors;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.model.security.User;

class RecipientRetriever {
	private final SecurityDao securityDao;

	RecipientRetriever(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}

	public List<String> findRecipientEmailAddresses(GroupType type) {
		List<User> users = securityDao.findUsersByGroupname(type.getInnerName());
		return users.stream()
				.map(User::getEmail)
				.collect(Collectors.toList());
	}

}

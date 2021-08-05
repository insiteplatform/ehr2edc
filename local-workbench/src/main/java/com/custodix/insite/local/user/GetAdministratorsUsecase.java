package com.custodix.insite.local.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.model.security.User;

@Component
class GetAdministratorsUsecase implements GetAdministrators {
	private final SecurityDao securityDao;

	GetAdministratorsUsecase(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}

	@Override
	public Response getAdministrators() {
		List<User> admins = securityDao.findUsersByGroupname(GroupType.ADM.getInnerName());
		List<String> emails = admins.stream()
				.map(User::getEmail)
				.collect(Collectors.toList());
		return Response.newBuilder()
				.withEmails(emails)
				.build();
	}
}

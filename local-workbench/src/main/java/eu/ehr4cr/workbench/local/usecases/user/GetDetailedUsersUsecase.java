package eu.ehr4cr.workbench.local.usecases.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.security.annotation.HasPermission;
import eu.ehr4cr.workbench.local.usecases.user.GetDetailedUsers.Response.UserInfo;

@Component
class GetDetailedUsersUsecase implements GetDetailedUsers {
	private final SecurityDao securityDao;

	GetDetailedUsersUsecase(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}

	@HasPermission(AuthorityType.VIEW_ACCOUNTS)
	@Transactional(readOnly = true)
	@Override
	public Response getUsers() {
		List<User> users = securityDao.findAllUsers();
		List<UserInfo> usersInfo = users.stream()
				.map(Response.UserInfo::new)
				.collect(Collectors.toList());
		return Response.newBuilder()
				.withUsers(usersInfo)
				.build();
	}
}

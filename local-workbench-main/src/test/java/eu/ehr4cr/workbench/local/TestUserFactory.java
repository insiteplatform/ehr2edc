package eu.ehr4cr.workbench.local;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;

import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.model.security.Group;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.service.IUserMgrService;

public class TestUserFactory {
	@Autowired
	private IUserMgrService userService;

	public User createTestUser() {
		String userName = "test user";
		User result = userService.findUserByUsername(userName);

		if (result == null) {
			Long userId = userService.createUser("test@custodix.com", userName, "test")
					.getId();

			Group testGroup = userService.findGroupByGroupname(GroupType.DRM.getInnerName());
			if (testGroup == null) {
				testGroup = userService.createGroup(GroupType.DRM.getInnerName());
			}

			userService.assignUserToGroups(userId, Arrays.asList(GroupType.DRM));
			result = userService.findUserById(userId);
		}
		return result;
	}
}

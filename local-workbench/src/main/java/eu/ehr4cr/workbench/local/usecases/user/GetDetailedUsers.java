package eu.ehr4cr.workbench.local.usecases.user;

import java.util.List;
import java.util.stream.Collectors;

import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.model.security.Group;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.model.security.UserRole;
import eu.ehr4cr.workbench.local.model.security.UserStatus;

public interface GetDetailedUsers {
	Response getUsers();

	final class Response {
		private final List<UserInfo> users;

		private Response(Builder builder) {
			users = builder.users;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public List<UserInfo> getUsers() {
			return users;
		}

		public static final class Builder {
			private List<UserInfo> users;

			private Builder() {
			}

			public Builder withUsers(List<UserInfo> users) {
				this.users = users;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}

		public static final class UserInfo {
			private final long id;
			private final String username;
			private final String email;
			private final UserStatus status;
			private final List<UserRole> roles;

			UserInfo(User user) {
				this.id = user.getId();
				this.username = user.getUsername();
				this.email = user.getEmail();
				this.status = user.getStatus();
				this.roles = user.getGroups()
						.stream()
						.map(Group::getType)
						.filter(g -> GroupType.USR != g)
						.map(UserRole::fromGroupType)
						.collect(Collectors.toList());
			}

			public long getId() {
				return id;
			}

			public String getUsername() {
				return username;
			}

			public String getEmail() {
				return email;
			}

			public UserStatus getStatus() {
				return status;
			}

			public List<UserRole> getRoles() {
				return roles;
			}
		}
	}

}

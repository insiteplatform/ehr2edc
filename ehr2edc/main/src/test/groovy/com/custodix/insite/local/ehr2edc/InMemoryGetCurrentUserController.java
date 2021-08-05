package com.custodix.insite.local.ehr2edc;

import com.custodix.insite.local.GetCurrentUserController;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

public class InMemoryGetCurrentUserController implements GetCurrentUserController {

	private UserIdentifier id;
	private boolean drm = false;

	@Override
	public Response get() {
		return Response.newBuilder()
				.withUser(buildUser())
				.build();
	}

	private User buildUser() {
		return User.newBuilder()
				.withId(id())
				.withName(name())
				.withDrm(drm)
				.build();
	}

	private Long id() {
		return id != null ? Long.valueOf(id.getId()) : null;
	}

	private String name() {
		return id != null ? id.getId() : null;
	}

	public void withUserId(UserIdentifier id) {
		this.id = id;
	}

	public void withDRM(boolean drm) {
		this.drm = drm;
	}

	public void withoutUser() {
		id = null;
		drm = false;
	}

}

package com.custodix.insite.local.user.infra.rest;

import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;

import org.springframework.web.bind.annotation.*;

import com.custodix.insite.local.user.UpdateProfile;
import com.custodix.insite.local.user.application.api.UpdatePassword;
import com.custodix.insite.local.user.infra.rest.model.UpdatePasswordRequest;
import com.custodix.insite.local.user.infra.rest.model.UpdatePhysicianRequest;
import com.custodix.insite.local.user.infra.rest.model.UpdateProfileRequest;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.controllers.BaseController;
import eu.ehr4cr.workbench.local.exception.feasibility.DomainException;
import eu.ehr4cr.workbench.local.usecases.threatingphysician.update.UpdatePhysicianForUser;

@RestController
class AccountUpdateController extends BaseController {
	private final UpdateProfile updateProfile;
	private final UpdatePassword updatePassword;

	AccountUpdateController(UpdateProfile updateProfile,
			UpdatePassword updatePassword) {
		this.updateProfile = updateProfile;
		this.updatePassword = updatePassword;
	}

	@PostMapping(WebRoutes.updateProfile)
	protected void updateProfile(@RequestBody UpdateProfileRequest updateProfileRequest) {
		UpdateProfile.Request request = UpdateProfile.Request.newBuilder()
				.withUserIdentifier(getUser().getIdentifier())
				.withEmail(updateProfileRequest.getEmail())
				.withUsername(updateProfileRequest.getUsername())
				.build();
		updateProfile.update(request);
	}

	@PostMapping(WebRoutes.updatePassword)
	protected void updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest) {
		UpdatePassword.Request request = UpdatePassword.Request.newBuilder()
				.withUserIdentifier(getUser().getIdentifier())
				.withOldPassword(updatePasswordRequest.getOldPassword())
				.withNewPassword(updatePasswordRequest.getNewPassword())
				.build();
		updatePassword.update(request);
	}

	@PostMapping(WebRoutes.updatePhysician)
	protected void updatePhysician(@RequestBody UpdatePhysicianRequest updatePhysicianRequest) {
		UpdatePhysicianForUser.Request request = UpdatePhysicianForUser.Request.newBuilder()
				.withUserIdentifier(getUser().getIdentifier())
				.withProviderId(updatePhysicianRequest.getProviderId())
				.withDefaultAssignee(updatePhysicianRequest.isDefaultAssignee())
				.build();
	}

	@ExceptionHandler(DomainException.class)
	@ResponseStatus(PRECONDITION_FAILED)
	String domainException(DomainException e) {
		return e.getMessage();
	}
}

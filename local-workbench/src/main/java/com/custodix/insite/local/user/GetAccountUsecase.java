package com.custodix.insite.local.user;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.custodix.insite.local.user.GetAccount.Response.TreatingPhysicianInfo;
import com.custodix.insite.local.user.vocabulary.PasswordExpirySettings;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.model.clinicalStudy.TreatingPhysician;
import eu.ehr4cr.workbench.local.model.security.Question;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.security.annotation.HasPermissionForAccount;
import eu.ehr4cr.workbench.local.service.ISecurityQuestionService;
import eu.ehr4cr.workbench.local.vocabulary.UserIdentifier;

@Component
class GetAccountUsecase implements GetAccount {
	private final SecurityDao securityDao;
	private final ISecurityQuestionService securityQuestionService;
	private final PasswordExpirySettings passwordExpirySettings;

	GetAccountUsecase(SecurityDao securityDao, ISecurityQuestionService securityQuestionService,
					  PasswordExpirySettings passwordExpirySettings) {
		this.securityDao = securityDao;
		this.securityQuestionService = securityQuestionService;
		this.passwordExpirySettings = passwordExpirySettings;
	}

	@HasPermissionForAccount
	@Transactional(readOnly = true)
	@Override
	public Response getAccount(Request request) {
		UserIdentifier userIdentifier = request.getUserIdentifier();
		User user = securityDao.findUserById(userIdentifier);
		List<Question> questions = securityQuestionService.getQuestionsForLocale(request.getLocale());
		Response.Builder builder = Response.newBuilder()
				.withUserEmail(user.getEmail())
				.withUserName(user.getUsername())
				.withSecurityQuestionId(user.getSecurityQuestionId())
				.withSecurityQuestionAnswer(user.getSecurityAnswer())
				.withQuestions(questions);
		user.findPasswordExpiryDate(passwordExpirySettings)
				.ifPresent(builder::withPasswordExpiryDate);
		return builder.build();
	}

	private TreatingPhysicianInfo createTreatingPhysicianDto(TreatingPhysician physician) {
		return new TreatingPhysicianInfo(physician.getProviderId(), physician.getIsDefault());
	}
}

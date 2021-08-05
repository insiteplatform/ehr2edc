package eu.ehr4cr.workbench.local.service.email.model;

import org.thymeleaf.context.Context;

import java.util.List;

class StudyApprovalRequestMail extends Mail {
	private static final String TEMPLATE_NAME = "studyApprovalRequestMessage";
	private static final String SUBJECT = "New HCO visibility request for the InSite study";

	private final StudyApprovalRequestMailContent content;

	StudyApprovalRequestMail(String senderAddress, List<String> recipientAddresses,
			StudyApprovalRequestMailContent content) {
		super(senderAddress, recipientAddresses);
		this.content = content;
	}

	@Override
	public String getSubject() {
		return SUBJECT;
	}

	@Override
	public Context getContext() {
		Context ctx = new Context();
		ctx.setVariable("messageTemplate", TEMPLATE_NAME);
		ctx.setVariable("mailInfo_subject", SUBJECT);
		ctx.setVariable("sponsor", content.getSponsor());
		ctx.setVariable("study_id", content.getStudyId());
		ctx.setVariable("study_name", content.getStudyName());
		ctx.setVariable("study_description", content.getStudyDescription());
		ctx.setVariable("approval_url", content.getApprovalUrl());
		return ctx;
	}
}

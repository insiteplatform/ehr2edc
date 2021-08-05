package eu.ehr4cr.workbench.local.service.email.model;

import org.thymeleaf.context.Context;

import java.util.List;

class StudyApprovedMail extends Mail {
	private static final String TEMPLATE_NAME = "studyApprovedMessage";
	private static final String SUBJECT = "HCO visibility request for the InSite study has been approved";

	private final StudyApprovedMailContent content;

	StudyApprovedMail(String senderAddress, List<String> recipientAddresses, StudyApprovedMailContent content) {
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
		ctx.setVariable("study_name", content.getStudyName());
		ctx.setVariable("actor_name", content.getUserName());
		ctx.setVariable("approval_url", content.getApprovalUrl());
		ctx.setVariable("support_mail_address", content.getSupportMailAddress());
		return ctx;
	}
}

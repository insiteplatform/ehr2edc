package eu.ehr4cr.workbench.local.service.email.model;

import org.thymeleaf.context.Context;

import java.util.List;

class StudyAutoApprovalScheduledMail extends Mail {
	private static final String TEMPLATE_NAME = "studyAutoApprovalScheduledMessage";
	private static final String SUBJECT = "Scheduled automatic approval of a HCO visibility request";

	private final StudyAutoApprovalScheduledMailContent content;

	StudyAutoApprovalScheduledMail(String senderAddress, List<String> recipientAddresses, StudyAutoApprovalScheduledMailContent content) {
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
		ctx.setVariable("auto_approval_date", content.getAutoApprovalDate());
		ctx.setVariable("approval_url", content.getApprovalUrl());
		ctx.setVariable("support_mail_address", content.getSupportMailAddress());
		return ctx;
	}
}

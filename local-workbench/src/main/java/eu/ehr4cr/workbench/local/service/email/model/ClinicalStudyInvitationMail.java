package eu.ehr4cr.workbench.local.service.email.model;

import org.thymeleaf.context.Context;

import java.util.List;

class ClinicalStudyInvitationMail extends Mail {
	private static final String TEMPLATE_NAME = "clinicalStudyInvitationMessage";
	private static final String SUBJECT = "New recruiting InSite study participation request";

	private final ClinicalStudyInvitationMailContent content;

	ClinicalStudyInvitationMail(String senderAddress, List<String> recipientAddresses,
			ClinicalStudyInvitationMailContent content) {
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
		ctx.setVariable("participation_url", content.getParticipationUrl());
		return ctx;
	}
}

package eu.ehr4cr.workbench.local.service.email.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public final class StudyAutoApprovalScheduledMailContent implements MailContent {
	private final String sponsor;
	private final String studyName;
	private final Date autoApprovalDate;
	private final String approvalUrl;
	private final String supportMailAddress;

	private StudyAutoApprovalScheduledMailContent(Builder builder) {
		sponsor = builder.sponsor;
		studyName = builder.studyName;
		autoApprovalDate = builder.autoApprovalDate;
		approvalUrl = builder.approvalUrl;
		supportMailAddress = builder.supportMailAddress;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public Mail createMail(String senderAddress, List<String> recipientAddresses) {
		return new StudyAutoApprovalScheduledMail(senderAddress, recipientAddresses, this);
	}

	public String getSponsor() {
		return sponsor;
	}

	public String getStudyName() {
		return studyName;
	}

	public Date getAutoApprovalDate() {
		return autoApprovalDate;
	}

	public String getApprovalUrl() {
		return approvalUrl;
	}

	public String getSupportMailAddress() {
		return supportMailAddress;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		StudyAutoApprovalScheduledMailContent content = (StudyAutoApprovalScheduledMailContent) o;
		return Objects.equals(sponsor, content.sponsor) && Objects.equals(studyName, content.studyName)
				&& Objects.equals(autoApprovalDate, content.autoApprovalDate) && Objects.equals(approvalUrl,
				content.approvalUrl) && Objects.equals(supportMailAddress, content.supportMailAddress);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sponsor, studyName, autoApprovalDate, approvalUrl, supportMailAddress);
	}

	public static final class Builder {
		private String sponsor;
		private String studyName;
		private Date autoApprovalDate;
		private String approvalUrl;
		private String supportMailAddress;

		private Builder() {
		}

		public Builder withSponsor(String sponsor) {
			this.sponsor = sponsor;
			return this;
		}

		public Builder withStudyName(String studyName) {
			this.studyName = studyName;
			return this;
		}

		public Builder withAutoApprovalDate(Date autoApprovalDate) {
			this.autoApprovalDate = autoApprovalDate;
			return this;
		}

		public Builder withApprovalUrl(String approvalUrl) {
			this.approvalUrl = approvalUrl;
			return this;
		}

		public Builder withSupportMailAddress(String supportMailAddress) {
			this.supportMailAddress = supportMailAddress;
			return this;
		}

		public StudyAutoApprovalScheduledMailContent build() {
			return new StudyAutoApprovalScheduledMailContent(this);
		}
	}
}

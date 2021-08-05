package eu.ehr4cr.workbench.local.service.email.model;

import java.util.List;
import java.util.Objects;

public final class StudyRejectedMailContent implements MailContent {
	private final String sponsor;
	private final String studyName;
	private final String userName;
	private final String approvalUrl;
	private final String supportMailAddress;

	private StudyRejectedMailContent(Builder builder) {
		sponsor = builder.sponsor;
		studyName = builder.studyName;
		userName = builder.userName;
		approvalUrl = builder.approvalUrl;
		supportMailAddress = builder.supportMailAddress;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public Mail createMail(String senderAddress, List<String> recipientAddresses) {
		return new StudyRejectedMail(senderAddress, recipientAddresses, this);
	}

	public String getSponsor() {
		return sponsor;
	}

	public String getStudyName() {
		return studyName;
	}

	public String getUserName() {
		return userName;
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
		StudyRejectedMailContent that = (StudyRejectedMailContent) o;
		return Objects.equals(sponsor, that.sponsor) && Objects.equals(studyName, that.studyName) && Objects.equals(
				userName, that.userName) && Objects.equals(approvalUrl, that.approvalUrl) && Objects.equals(
				supportMailAddress, that.supportMailAddress);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sponsor, studyName, userName, approvalUrl, supportMailAddress);
	}

	public static final class Builder {
		private String sponsor;
		private String studyName;
		private String userName;
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

		public Builder withUserName(String userName) {
			this.userName = userName;
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

		public StudyRejectedMailContent build() {
			return new StudyRejectedMailContent(this);
		}
	}
}

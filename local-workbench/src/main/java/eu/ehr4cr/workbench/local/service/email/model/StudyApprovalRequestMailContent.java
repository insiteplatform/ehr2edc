package eu.ehr4cr.workbench.local.service.email.model;

import java.util.List;
import java.util.Objects;

public final class StudyApprovalRequestMailContent implements MailContent {
	private final String sponsor;
	private final String studyId;
	private final String studyName;
	private final String studyDescription;
	private final String approvalUrl;

	private StudyApprovalRequestMailContent(Builder builder) {
		sponsor = builder.sponsor;
		studyId = builder.studyId;
		studyName = builder.studyName;
		studyDescription = builder.studyDescription;
		approvalUrl = builder.approvalUrl;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public Mail createMail(String senderAddress, List<String> recipientAddresses) {
		return new StudyApprovalRequestMail(senderAddress, recipientAddresses, this);
	}

	public static final class Builder {
		private String sponsor;
		private String studyId;
		private String studyName;
		private String studyDescription;
		private String approvalUrl;

		private Builder() {
		}

		public Builder withSponsor(String sponsor) {
			this.sponsor = sponsor;
			return this;
		}

		public Builder withStudyId(String studyId) {
			this.studyId = studyId;
			return this;
		}

		public Builder withStudyName(String studyName) {
			this.studyName = studyName;
			return this;
		}

		public Builder withStudyDescription(String studyDescription) {
			this.studyDescription = studyDescription;
			return this;
		}

		public Builder withApprovalUrl(String approvalUrl) {
			this.approvalUrl = approvalUrl;
			return this;
		}

		public StudyApprovalRequestMailContent build() {
			return new StudyApprovalRequestMailContent(this);
		}
	}

	public String getSponsor() {
		return sponsor;
	}

	public String getStudyId() {
		return studyId;
	}

	public String getStudyName() {
		return studyName;
	}

	public String getStudyDescription() {
		return studyDescription;
	}

	public String getApprovalUrl() {
		return approvalUrl;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		StudyApprovalRequestMailContent that = (StudyApprovalRequestMailContent) o;
		return Objects.equals(sponsor, that.sponsor) && Objects.equals(studyId, that.studyId) && Objects.equals(
				studyName, that.studyName) && Objects.equals(studyDescription, that.studyDescription) && Objects.equals(
				approvalUrl, that.approvalUrl);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sponsor, studyId, studyName, studyDescription, approvalUrl);
	}
}

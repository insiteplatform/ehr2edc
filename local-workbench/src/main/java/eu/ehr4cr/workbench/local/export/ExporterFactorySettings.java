package eu.ehr4cr.workbench.local.export;

import eu.ehr4cr.workbench.local.model.security.User;

public final class ExporterFactorySettings {
	private final ExporterFormatType type;
	private final User user;
	private final String fileName;
	private final ExportRow headerRow;

	private ExporterFactorySettings(Builder builder) {
		type = builder.type;
		user = builder.user;
		fileName = builder.fileName;
		headerRow = builder.headerRow;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public ExporterFormatType getType() {
		return type;
	}

	public User getUser() {
		return user;
	}

	public String getFileName() {
		return fileName;
	}

	public ExportRow getHeaderRow() {
		return headerRow;
	}

	public static final class Builder {
		private ExporterFormatType type;
		private User user;
		private String fileName;
		private ExportRow headerRow;

		private Builder() {
		}

		public Builder withType(ExporterFormatType type) {
			this.type = type;
			return this;
		}

		public Builder withUser(User user) {
			this.user = user;
			return this;
		}

		public Builder withFileName(String fileName) {
			this.fileName = fileName;
			return this;
		}

		public Builder withHeaderRow(ExportRow headerRow) {
			this.headerRow = headerRow;
			return this;
		}

		public ExporterFactorySettings build() {
			return new ExporterFactorySettings(this);
		}
	}
}
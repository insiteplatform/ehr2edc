package eu.ehr4cr.workbench.local.usecases.model.dto;

import java.util.Arrays;
import java.util.Set;

import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.NotThreadSafe;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Min;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import eu.ehr4cr.workbench.local.model.clinicalStudy.ProtocolVersion;

/**
 * Immutable implementation of {@link AbstractProtocolDocumentDto}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ProtocolDocumentDto.builder()}.
 */
@SuppressWarnings({ "all" })

@ParametersAreNonnullByDefault
@Generated({ "Immutables.generator", "AbstractProtocolDocumentDto" })

public final class ProtocolDocumentDto implements AbstractProtocolDocumentDto {
	private final java.lang.@Min(1) Long id;
	private final ProtocolVersion version;
	private final byte[] documentContent;
	private final String fileType;
	private final String fileName;
	private final java.lang.Long fileSizeInBytes;
	private final java.lang.Long fileSize;

	private ProtocolDocumentDto(java.lang.@Min(1) Long id, ProtocolVersion version, byte[] documentContent,
			String fileType, String fileName, java.lang.Long fileSizeInBytes, java.lang.Long fileSize) {
		this.id = id;
		this.version = version;
		this.documentContent = documentContent;
		this.fileType = fileType;
		this.fileName = fileName;
		this.fileSizeInBytes = fileSizeInBytes;
		this.fileSize = fileSize;
	}

	/**
	 * @return The value of the {@code id} attribute
	 */
	@Override
	public java.lang.@Min(1) Long getId() {
		return id;
	}

	/**
	 * @return The value of the {@code version} attribute
	 */
	@Override
	public ProtocolVersion getVersion() {
		return version;
	}

	/**
	 * @return A cloned {@code documentContent} array
	 */
	@Override
	public byte[] getDocumentContent() {
		return documentContent;
	}

	/**
	 * @return The value of the {@code fileType} attribute
	 */
	@Override
	public String getFileType() {
		return fileType;
	}

	/**
	 * @return The value of the {@code fileName} attribute
	 */
	@Override
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return The value of the {@code fileSizeInBytes} attribute
	 */
	@Override
	public java.lang.Long getFileSizeInBytes() {
		return fileSizeInBytes;
	}

	/**
	 * @return The value of the {@code fileSize} attribute
	 */
	@Override
	public java.lang.Long getFileSize() {
		return fileSize;
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractProtocolDocumentDto#getId() id} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for id (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ProtocolDocumentDto withId(java.lang.@Min(1) Long value) {
		if (Objects.equal(this.id, value))
			return this;
		return validate(new ProtocolDocumentDto(value, this.version, this.documentContent, this.fileType, this.fileName,
				this.fileSizeInBytes, this.fileSize));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractProtocolDocumentDto#getVersion() version} attribute.
	 * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for version (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ProtocolDocumentDto withVersion(ProtocolVersion value) {
		if (this.version == value)
			return this;
		return validate(new ProtocolDocumentDto(this.id, value, this.documentContent, this.fileType, this.fileName,
				this.fileSizeInBytes, this.fileSize));
	}

	/**
	 * Copy the current immutable object with elements that replace the content of {@link AbstractProtocolDocumentDto#getDocumentContent() documentContent}.
	 * The array is cloned before being saved as attribute values.
	 * @param elements The non-null elements for documentContent
	 * @return A modified copy of {@code this} object
	 */
	public final ProtocolDocumentDto withDocumentContent(byte... elements) {
		byte[] newValue = elements == null ? null : elements.clone();
		return validate(new ProtocolDocumentDto(this.id, this.version, newValue, this.fileType, this.fileName,
				this.fileSizeInBytes, this.fileSize));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractProtocolDocumentDto#getFileType() fileType} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for fileType (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ProtocolDocumentDto withFileType(String value) {
		if (Objects.equal(this.fileType, value))
			return this;
		return validate(new ProtocolDocumentDto(this.id, this.version, this.documentContent, value, this.fileName,
				this.fileSizeInBytes, this.fileSize));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractProtocolDocumentDto#getFileName() fileName} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for fileName (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ProtocolDocumentDto withFileName(String value) {
		if (Objects.equal(this.fileName, value))
			return this;
		return validate(new ProtocolDocumentDto(this.id, this.version, this.documentContent, this.fileType, value,
				this.fileSizeInBytes, this.fileSize));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractProtocolDocumentDto#getFileSizeInBytes() fileSizeInBytes} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for fileSizeInBytes (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ProtocolDocumentDto withFileSizeInBytes(java.lang.Long value) {
		if (Objects.equal(this.fileSizeInBytes, value))
			return this;
		return validate(
				new ProtocolDocumentDto(this.id, this.version, this.documentContent, this.fileType, this.fileName,
						value, this.fileSize));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractProtocolDocumentDto#getFileSize() fileSize} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for fileSize (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ProtocolDocumentDto withFileSize(java.lang.Long value) {
		if (Objects.equal(this.fileSize, value))
			return this;
		return validate(
				new ProtocolDocumentDto(this.id, this.version, this.documentContent, this.fileType, this.fileName,
						this.fileSizeInBytes, value));
	}

	/**
	 * This instance is equal to all instances of {@code ProtocolDocumentDto} that have equal attribute values.
	 * @return {@code true} if {@code this} is equal to {@code another} instance
	 */
	@Override
	public boolean equals(@Nullable Object another) {
		if (this == another)
			return true;
		return another instanceof ProtocolDocumentDto && equalTo((ProtocolDocumentDto) another);
	}

	private boolean equalTo(ProtocolDocumentDto another) {
		return Objects.equal(id, another.id) && Objects.equal(version, another.version) && Arrays.equals(
				documentContent, another.documentContent) && Objects.equal(fileType, another.fileType) && Objects.equal(
				fileName, another.fileName) && Objects.equal(fileSizeInBytes, another.fileSizeInBytes) && Objects.equal(
				fileSize, another.fileSize);
	}

	/**
	 * Computes a hash code from attributes: {@code id}, {@code version}, {@code documentContent}, {@code fileType}, {@code fileName}, {@code fileSizeInBytes}, {@code fileSize}.
	 * @return hashCode value
	 */
	@Override
	public int hashCode() {
		int h = 5381;
		h += (h << 5) + Objects.hashCode(id);
		h += (h << 5) + Objects.hashCode(version);
		h += (h << 5) + Arrays.hashCode(documentContent);
		h += (h << 5) + Objects.hashCode(fileType);
		h += (h << 5) + Objects.hashCode(fileName);
		h += (h << 5) + Objects.hashCode(fileSizeInBytes);
		h += (h << 5) + Objects.hashCode(fileSize);
		return h;
	}

	/**
	 * Prints the immutable value {@code ProtocolDocumentDto} with attribute values.
	 * @return A string representation of the value
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper("ProtocolDocumentDto")
				.omitNullValues()
				.add("id", id)
				.add("version", version)
				.add("documentContent", Arrays.toString(documentContent))
				.add("fileType", fileType)
				.add("fileName", fileName)
				.add("fileSizeInBytes", fileSizeInBytes)
				.add("fileSize", fileSize)
				.toString();
	}

	private static final Validator validator = Validation.buildDefaultValidatorFactory()
			.getValidator();

	private static ProtocolDocumentDto validate(ProtocolDocumentDto instance) {
		Set<ConstraintViolation<ProtocolDocumentDto>> constraintViolations = validator.validate(instance);
		if (!constraintViolations.isEmpty()) {
			throw new ConstraintViolationException(constraintViolations);
		}
		return instance;
	}

	/**
	 * Creates an immutable copy of a {@link AbstractProtocolDocumentDto} value.
	 * Uses accessors to get values to initialize the new immutable instance.
	 * If an instance is already immutable, it is returned as is.
	 * @param instance The instance to copy
	 * @return A copied immutable ProtocolDocumentDto instance
	 */
	public static ProtocolDocumentDto copyOf(AbstractProtocolDocumentDto instance) {
		if (instance instanceof ProtocolDocumentDto) {
			return (ProtocolDocumentDto) instance;
		}
		return ProtocolDocumentDto.builder()
				.from(instance)
				.build();
	}

	/**
	 * Creates a builder for {@link ProtocolDocumentDto ProtocolDocumentDto}.
	 * @return A new ProtocolDocumentDto builder
	 */
	public static ProtocolDocumentDto.Builder builder() {
		return new ProtocolDocumentDto.Builder();
	}

	/**
	 * Builds instances of type {@link ProtocolDocumentDto ProtocolDocumentDto}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	@NotThreadSafe
	public static final class Builder {
		private @Nullable
		java.lang.@Min(1) Long id;
		private @Nullable
		ProtocolVersion version;
		private @Nullable
		byte[] documentContent;
		private @Nullable
		String fileType;
		private @Nullable
		String fileName;
		private @Nullable
		java.lang.Long fileSizeInBytes;
		private @Nullable
		java.lang.Long fileSize;

		private Builder() {
		}

		/**
		 * Fill a builder with attribute values from the provided {@code AbstractProtocolDocumentDto} instance.
		 * Regular attribute values will be replaced with those from the given instance.
		 * Absent optional values will not replace present values.
		 * @param instance The instance from which to copy values
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder from(AbstractProtocolDocumentDto instance) {
			Preconditions.checkNotNull(instance, "instance");
			java.lang.@Min(1) Long idValue = instance.getId();
			if (idValue != null) {
				withId(idValue);
			}
			ProtocolVersion versionValue = instance.getVersion();
			if (versionValue != null) {
				withVersion(versionValue);
			}
			byte[] documentContentValue = instance.getDocumentContent();
			if (documentContentValue != null) {
				withDocumentContent(documentContentValue);
			}
			String fileTypeValue = instance.getFileType();
			if (fileTypeValue != null) {
				withFileType(fileTypeValue);
			}
			String fileNameValue = instance.getFileName();
			if (fileNameValue != null) {
				withFileName(fileNameValue);
			}
			java.lang.Long fileSizeInBytesValue = instance.getFileSizeInBytes();
			if (fileSizeInBytesValue != null) {
				withFileSizeInBytes(fileSizeInBytesValue);
			}
			java.lang.Long fileSizeValue = instance.getFileSize();
			if (fileSizeValue != null) {
				withFileSize(fileSizeValue);
			}
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractProtocolDocumentDto#getId() id} attribute.
		 * @param id The value for id (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withId(java.lang.@Min(1) Long id) {
			this.id = id;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractProtocolDocumentDto#getVersion() version} attribute.
		 * @param version The value for version (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withVersion(ProtocolVersion version) {
			this.version = version;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractProtocolDocumentDto#getDocumentContent() documentContent} attribute.
		 * @param documentContent The elements for documentContent
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withDocumentContent(byte... documentContent) {
			this.documentContent = documentContent;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractProtocolDocumentDto#getFileType() fileType} attribute.
		 * @param fileType The value for fileType (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withFileType(String fileType) {
			this.fileType = fileType;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractProtocolDocumentDto#getFileName() fileName} attribute.
		 * @param fileName The value for fileName (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withFileName(String fileName) {
			this.fileName = fileName;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractProtocolDocumentDto#getFileSizeInBytes() fileSizeInBytes} attribute.
		 * @param fileSizeInBytes The value for fileSizeInBytes (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withFileSizeInBytes(java.lang.Long fileSizeInBytes) {
			this.fileSizeInBytes = fileSizeInBytes;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractProtocolDocumentDto#getFileSize() fileSize} attribute.
		 * @param fileSize The value for fileSize (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withFileSize(java.lang.Long fileSize) {
			this.fileSize = fileSize;
			return this;
		}

		/**
		 * Builds a new {@link ProtocolDocumentDto ProtocolDocumentDto}.
		 * @return An immutable instance of ProtocolDocumentDto
		 * @throws java.lang.IllegalStateException if any required attributes are missing
		 */
		public ProtocolDocumentDto build() {
			return ProtocolDocumentDto.validate(
					new ProtocolDocumentDto(id, version, documentContent, fileType, fileName, fileSizeInBytes,
							fileSize));
		}
	}
}

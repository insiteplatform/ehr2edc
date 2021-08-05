package eu.ehr4cr.workbench.local.export;

import java.io.File;
import java.util.List;

import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Immutable implementation of {@link ExporterSettings}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableExporterSettings.builder()}.
 */
@SuppressWarnings({ "all" })
@ParametersAreNonnullByDefault
@Generated({ "Immutables.generator", "ExporterSettings" })

public final class ImmutableExporterSettings implements ExporterSettings {
	private final File dir;
	private final String fileName;
	private final int maxFileCount;
	private final int maxRowsPerFile;
	private final ExportRow headerRow;

	private ImmutableExporterSettings(File dir, String fileName, int maxFileCount, int maxRowsPerFile,
			ExportRow headerRow) {
		this.dir = dir;
		this.fileName = fileName;
		this.maxFileCount = maxFileCount;
		this.maxRowsPerFile = maxRowsPerFile;
		this.headerRow = headerRow;
	}

	/**
	 * @return The value of the {@code dir} attribute
	 */
	@Override
	public File getDir() {
		return dir;
	}

	/**
	 * @return The value of the {@code fileName} attribute
	 */
	@Override
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return The value of the {@code maxFileCount} attribute
	 */
	@Override
	public int getMaxFileCount() {
		return maxFileCount;
	}

	/**
	 * @return The value of the {@code maxRowsPerFile} attribute
	 */
	@Override
	public int getMaxRowsPerFile() {
		return maxRowsPerFile;
	}

	/**
	 * @return The value of the {@code headerRow} attribute
	 */
	@Override
	public ExportRow getHeaderRow() {
		return headerRow;
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link ExporterSettings#getDir() dir} attribute.
	 * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for dir
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableExporterSettings withDir(File value) {
		if (this.dir == value)
			return this;
		File newValue = Preconditions.checkNotNull(value, "dir");
		return new ImmutableExporterSettings(newValue, this.fileName, this.maxFileCount, this.maxRowsPerFile,
				this.headerRow);
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link ExporterSettings#getFileName() fileName} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for fileName
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableExporterSettings withFileName(String value) {
		if (this.fileName.equals(value))
			return this;
		String newValue = Preconditions.checkNotNull(value, "fileName");
		return new ImmutableExporterSettings(this.dir, newValue, this.maxFileCount, this.maxRowsPerFile,
				this.headerRow);
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link ExporterSettings#getMaxFileCount() maxFileCount} attribute.
	 * A value equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for maxFileCount
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableExporterSettings withMaxFileCount(int value) {
		if (this.maxFileCount == value)
			return this;
		return new ImmutableExporterSettings(this.dir, this.fileName, value, this.maxRowsPerFile, this.headerRow);
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link ExporterSettings#getMaxRowsPerFile() maxRowsPerFile} attribute.
	 * A value equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for maxRowsPerFile
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableExporterSettings withMaxRowsPerFile(int value) {
		if (this.maxRowsPerFile == value)
			return this;
		return new ImmutableExporterSettings(this.dir, this.fileName, this.maxFileCount, value, this.headerRow);
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link ExporterSettings#getHeaderRow() headerRow} attribute.
	 * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for headerRow
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableExporterSettings withHeaderRow(ExportRow value) {
		if (this.headerRow == value)
			return this;
		ExportRow newValue = Preconditions.checkNotNull(value, "headerRow");
		return new ImmutableExporterSettings(this.dir, this.fileName, this.maxFileCount, this.maxRowsPerFile, newValue);
	}

	/**
	 * This instance is equal to all instances of {@code ImmutableExporterSettings} that have equal attribute values.
	 * @return {@code true} if {@code this} is equal to {@code another} instance
	 */
	@Override
	public boolean equals(@Nullable Object another) {
		if (this == another)
			return true;
		return another instanceof ImmutableExporterSettings && equalTo((ImmutableExporterSettings) another);
	}

	private boolean equalTo(ImmutableExporterSettings another) {
		return dir.equals(another.dir) && fileName.equals(another.fileName) && maxFileCount == another.maxFileCount
				&& maxRowsPerFile == another.maxRowsPerFile && headerRow.equals(another.headerRow);
	}

	/**
	 * Computes a hash code from attributes: {@code dir}, {@code fileName}, {@code maxFileCount}, {@code maxRowsPerFile}, {@code headerRow}.
	 * @return hashCode value
	 */
	@Override
	public int hashCode() {
		int h = 5381;
		h += (h << 5) + dir.hashCode();
		h += (h << 5) + fileName.hashCode();
		h += (h << 5) + maxFileCount;
		h += (h << 5) + maxRowsPerFile;
		h += (h << 5) + headerRow.hashCode();
		return h;
	}

	/**
	 * Prints the immutable value {@code ExporterSettings} with attribute values.
	 * @return A string representation of the value
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper("ExporterSettings")
				.omitNullValues()
				.add("dir", dir)
				.add("fileName", fileName)
				.add("maxFileCount", maxFileCount)
				.add("maxRowsPerFile", maxRowsPerFile)
				.add("headerRow", headerRow)
				.toString();
	}

	/**
	 * Creates an immutable copy of a {@link ExporterSettings} value.
	 * Uses accessors to get values to initialize the new immutable instance.
	 * If an instance is already immutable, it is returned as is.
	 * @param instance The instance to copy
	 * @return A copied immutable ExporterSettings instance
	 */
	public static ImmutableExporterSettings copyOf(ExporterSettings instance) {
		if (instance instanceof ImmutableExporterSettings) {
			return (ImmutableExporterSettings) instance;
		}
		return ImmutableExporterSettings.builder()
				.from(instance)
				.build();
	}

	/**
	 * Creates a builder for {@link ImmutableExporterSettings ImmutableExporterSettings}.
	 * @return A new ImmutableExporterSettings builder
	 */
	public static ImmutableExporterSettings.Builder builder() {
		return new ImmutableExporterSettings.Builder();
	}

	/**
	 * Builds instances of type {@link ImmutableExporterSettings ImmutableExporterSettings}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	@NotThreadSafe
	public static final class Builder {
		private static final long INIT_BIT_DIR = 0x1L;
		private static final long INIT_BIT_FILE_NAME = 0x2L;
		private static final long INIT_BIT_MAX_FILE_COUNT = 0x4L;
		private static final long INIT_BIT_MAX_ROWS_PER_FILE = 0x8L;
		private static final long INIT_BIT_HEADER_ROW = 0x10L;
		private long initBits = 0x1fL;

		private @Nullable
		File dir;
		private @Nullable
		String fileName;
		private int maxFileCount;
		private int maxRowsPerFile;
		private @Nullable
		ExportRow headerRow;

		private Builder() {
		}

		/**
		 * Fill a builder with attribute values from the provided {@code ExporterSettings} instance.
		 * Regular attribute values will be replaced with those from the given instance.
		 * Absent optional values will not replace present values.
		 * @param instance The instance from which to copy values
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder from(ExporterSettings instance) {
			Preconditions.checkNotNull(instance, "instance");
			dir(instance.getDir());
			fileName(instance.getFileName());
			maxFileCount(instance.getMaxFileCount());
			maxRowsPerFile(instance.getMaxRowsPerFile());
			headerRow(instance.getHeaderRow());
			return this;
		}

		/**
		 * Initializes the value for the {@link ExporterSettings#getDir() dir} attribute.
		 * @param dir The value for dir
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder dir(File dir) {
			this.dir = Preconditions.checkNotNull(dir, "dir");
			initBits &= ~INIT_BIT_DIR;
			return this;
		}

		/**
		 * Initializes the value for the {@link ExporterSettings#getFileName() fileName} attribute.
		 * @param fileName The value for fileName
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder fileName(String fileName) {
			this.fileName = Preconditions.checkNotNull(fileName, "fileName");
			initBits &= ~INIT_BIT_FILE_NAME;
			return this;
		}

		/**
		 * Initializes the value for the {@link ExporterSettings#getMaxFileCount() maxFileCount} attribute.
		 * @param maxFileCount The value for maxFileCount
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder maxFileCount(int maxFileCount) {
			this.maxFileCount = maxFileCount;
			initBits &= ~INIT_BIT_MAX_FILE_COUNT;
			return this;
		}

		/**
		 * Initializes the value for the {@link ExporterSettings#getMaxRowsPerFile() maxRowsPerFile} attribute.
		 * @param maxRowsPerFile The value for maxRowsPerFile
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder maxRowsPerFile(int maxRowsPerFile) {
			this.maxRowsPerFile = maxRowsPerFile;
			initBits &= ~INIT_BIT_MAX_ROWS_PER_FILE;
			return this;
		}

		/**
		 * Initializes the value for the {@link ExporterSettings#getHeaderRow() headerRow} attribute.
		 * @param headerRow The value for headerRow
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder headerRow(ExportRow headerRow) {
			this.headerRow = Preconditions.checkNotNull(headerRow, "headerRow");
			initBits &= ~INIT_BIT_HEADER_ROW;
			return this;
		}

		/**
		 * Builds a new {@link ImmutableExporterSettings ImmutableExporterSettings}.
		 * @return An immutable instance of ExporterSettings
		 * @throws java.lang.IllegalStateException if any required attributes are missing
		 */
		public ImmutableExporterSettings build() {
			if (initBits != 0) {
				throw new IllegalStateException(formatRequiredAttributesMessage());
			}
			return new ImmutableExporterSettings(dir, fileName, maxFileCount, maxRowsPerFile, headerRow);
		}

		private String formatRequiredAttributesMessage() {
			List<String> attributes = Lists.newArrayList();
			if ((initBits & INIT_BIT_DIR) != 0)
				attributes.add("dir");
			if ((initBits & INIT_BIT_FILE_NAME) != 0)
				attributes.add("fileName");
			if ((initBits & INIT_BIT_MAX_FILE_COUNT) != 0)
				attributes.add("maxFileCount");
			if ((initBits & INIT_BIT_MAX_ROWS_PER_FILE) != 0)
				attributes.add("maxRowsPerFile");
			if ((initBits & INIT_BIT_HEADER_ROW) != 0)
				attributes.add("headerRow");
			return "Cannot build ExporterSettings, some of required attributes are not set " + attributes;
		}
	}
}

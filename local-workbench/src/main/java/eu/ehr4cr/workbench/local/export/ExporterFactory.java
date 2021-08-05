package eu.ehr4cr.workbench.local.export;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import eu.ehr4cr.workbench.local.model.security.User;

@Service
public class ExporterFactory {
	private static final String COHORT_EXPORTS_DIR = "/cohortExports";
	private static final String COHORT_EXPORTS_USER_DIR_PART = "/cohortExport-user-";

	private final int maxRowsPerFile;
	private final int maxFileCount;
	private final String tempDirectory;

	@Autowired
	public ExporterFactory(@Value("${cohortExport.maximumRowsPerFile:10000}") int maxRowsPerFile,
			@Value("${cohortExport.maximumFileCount:20}") int maxFileCount,
			@Value("${tempdir:#{systemProperties['java.io.tmpdir']}}") String tempDirectory) {
		this.maxRowsPerFile = maxRowsPerFile;
		this.maxFileCount = maxFileCount;
		this.tempDirectory = tempDirectory;
	}

	public Exporter createExporter(ExporterFactorySettings settings) {
		ExporterFormatType type = settings.getType();
		return type.getExporter(map(settings));
	}

	private ExporterSettings map(ExporterFactorySettings settings) {
		return ImmutableExporterSettings.builder()
				.dir(getCohortExportsDirectoryForUser(settings.getUser()))
				.fileName(settings.getFileName())
				.maxFileCount(maxFileCount)
				.maxRowsPerFile(maxRowsPerFile)
				.headerRow(settings.getHeaderRow())
				.build();
	}

	private File getCohortExportsDirectoryForUser(User user) {
		File cohortExportsDir = getCohortExportsDir();
		File subDir = new File(
				cohortExportsDir.getAbsolutePath() + COHORT_EXPORTS_USER_DIR_PART + user.getId() + "-"
						+ user.getUsername());
		createDirIfNotExists(subDir);
		return subDir;
	}

	private File getCohortExportsDir() {
		File cohortExportsDir = new File(tempDirectory + COHORT_EXPORTS_DIR);
		createDirIfNotExists(cohortExportsDir);
		return cohortExportsDir;
	}

	private void createDirIfNotExists(File subDir) {
		if (!subDir.exists() && !subDir.mkdir()) {
			throw new IllegalStateException("Creation of subdirectory '" + subDir.getAbsolutePath() + "' failed");
		}
	}

}

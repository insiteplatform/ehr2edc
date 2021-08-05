package eu.ehr4cr.workbench.local.usecases.checkflywayhealth;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CheckFlywayHealthUseCase implements CheckFlywayHealth {
	private final Flyway flyway;

	@Autowired
	public CheckFlywayHealthUseCase(Flyway flyway) {
		this.flyway = flyway;
	}

	@Override
	public Response check() {
		MigrationInfoService info = flyway.info();
		List<String> failedVersions = getAllFailedVersions(info);
		List<String> pendingVersions = getAllPendingVersions(info);

		return Response.builder()
				.withHealthy(failedVersions.isEmpty() && pendingVersions.isEmpty())
				.withCurrentVersion(getVersionAsString(info.current()))
				.withFailedVersions(failedVersions)
				.withPendingVersions(pendingVersions)
				.build();
	}

	private List<String> getAllFailedVersions(MigrationInfoService info) {
		return Arrays.stream(info.applied())
				.filter(this::versionHasFailed)
				.map(this::getVersionAsString)
				.collect(toList());
	}

	private List<String> getAllPendingVersions(MigrationInfoService info) {
		return Arrays.stream(info.pending())
				.map(this::getVersionAsString)
				.collect(toList());
	}

	private boolean versionHasFailed(MigrationInfo mi) {
		return mi.getState()
				.isFailed();
	}

	private String getVersionAsString(MigrationInfo migrationInfo) {
		return migrationInfo.getVersion()
				.getVersion();
	}
}
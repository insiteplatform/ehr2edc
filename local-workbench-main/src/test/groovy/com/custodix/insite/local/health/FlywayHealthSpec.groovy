package com.custodix.insite.local.health

import eu.ehr4cr.workbench.local.usecases.checkflywayhealth.CheckFlywayHealth
import eu.ehr4cr.workbench.local.usecases.checkflywayhealth.CheckFlywayHealthUseCase
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.MigrationInfo
import org.flywaydb.core.api.MigrationInfoService
import org.flywaydb.core.api.MigrationState
import org.flywaydb.core.api.MigrationVersion
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import spock.lang.Specification
import spock.lang.Title

import java.util.stream.IntStream

import static java.util.stream.Collectors.toList
import static org.assertj.core.api.Java6Assertions.assertThat
import static org.flywaydb.core.api.MigrationState.*
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@Title("Check status of flyway migration")
@ContextConfiguration(classes = CheckFlywayHealthUseCase.class)
@EnableWebMvc
@WebAppConfiguration
class FlywayHealthSpec extends Specification {
    public static final int NR_OF_VERSIONS = 10
    public static final String CURRENT_VERSION = "10.0"

    @MockBean
    private Flyway flyway
    @Autowired
    private CheckFlywayHealth checkFlywayHealth

    def "Health of all successful migrations"() {
        given: "All database migrations happened successfully"
        MigrationInfoService infoService = new TestMigrationInfoService(createAllSuccessfulMigrations())
        when(flyway.info()).thenReturn(infoService)

        when: "I check for the flyway health"
        def health = checkFlywayHealth.check()

        then: "The result is marked as healthy"
        assertThat(health.healthy).isTrue()
        assertThat(health.currentVersion).isEqualTo(CURRENT_VERSION)
        assertThat(health.failedVersions).isEmpty()
        assertThat(health.pendingVersions).isEmpty()
    }

    def "Health of all failing migrations"() {
        given: "All database migrations have failed"
        MigrationInfoService infoService = new TestMigrationInfoService(createAllFailedMigrations())
        when(flyway.info()).thenReturn(infoService)

        when: "I check for the flyway health"
        def health = checkFlywayHealth.check()

        then: "The result is marked as healthy"
        assertThat(health.healthy).isFalse()
        assertThat(health.currentVersion).isEqualTo(CURRENT_VERSION)
        assertThat(health.failedVersions).containsExactlyInAnyOrder(allVersionNumbers())
        assertThat(health.pendingVersions).isEmpty()
    }

    def "Health of all pending migrations"() {
        given: "All database migrations are pending"
        MigrationInfoService infoService = new TestMigrationInfoService(createAllPendingMigrations())
        when(flyway.info()).thenReturn(infoService)

        when: "I check for the flyway health"
        def health = checkFlywayHealth.check()

        then: "The result is marked as healthy"
        assertThat(health.healthy).isFalse()
        assertThat(health.currentVersion).isEqualTo(CURRENT_VERSION)
        assertThat(health.failedVersions).isEmpty()
        assertThat(health.pendingVersions).containsExactlyInAnyOrder(allVersionNumbers())
    }

    private List<MigrationInfo> createAllSuccessfulMigrations() {
        return IntStream.range(0, NR_OF_VERSIONS)
                .mapToObj({ i -> createMigrationInfo(i + 1, SUCCESS) })
                .collect(toList())
    }

    private List<MigrationInfo> createAllFailedMigrations() {
        return IntStream.range(0, NR_OF_VERSIONS)
                .mapToObj({ i -> createMigrationInfo(i + 1, FAILED) })
                .collect(toList())
    }

    private List<MigrationInfo> createAllPendingMigrations() {
        return IntStream.range(0, NR_OF_VERSIONS)
                .mapToObj({ i -> createMigrationInfo(i + 1, PENDING) })
                .collect(toList())
    }

    private MigrationInfo createMigrationInfo(int version, MigrationState state) {
        MigrationInfo info = mock(MigrationInfo.class)
        when(info.state).thenReturn(state)
        when(info.version).thenReturn(new MigrationVersion(version, createVersionNumber(version)))
        return info
    }

    private String[] allVersionNumbers() {
        return IntStream.range(0, NR_OF_VERSIONS)
                .mapToObj({ i -> createVersionNumber(i + 1) })
                .toArray({ size -> new String[size] });
    }

    private String createVersionNumber(int version) {
        return version + ".0"
    }
}
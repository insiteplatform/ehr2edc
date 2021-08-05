package com.custodix.insite.local.health

import org.flywaydb.core.api.MigrationInfo
import org.flywaydb.core.api.MigrationInfoService

class TestMigrationInfoService implements MigrationInfoService {

    private final List<MigrationInfo> infoItems;

    TestMigrationInfoService(List<MigrationInfo> infoItems) {
        this.infoItems = infoItems
    }

    @Override
    MigrationInfo[] all() {
        return infoItems.toArray()
    }

    @Override
    MigrationInfo current() {
        return infoItems.last()
    }

    @Override
    MigrationInfo[] pending() {
        return infoItems.stream()
                .filter({ i -> !i.state.applied })
                .toArray({ size -> new MigrationInfo[size] })
    }

    @Override
    MigrationInfo[] applied() {
        return infoItems.stream()
                .filter({ i -> i.state.applied })
                .toArray({ size -> new MigrationInfo[size] })
    }
}

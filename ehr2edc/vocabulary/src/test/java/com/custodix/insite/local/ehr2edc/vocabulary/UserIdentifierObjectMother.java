package com.custodix.insite.local.ehr2edc.vocabulary;


public  final class UserIdentifierObjectMother {

    public static UserIdentifier aDefaultUserIdentifier() {
        return aDefaultUserIdentifierIdBuilder().build();
    }

    private static UserIdentifier.Builder aDefaultUserIdentifierIdBuilder() {
        return UserIdentifier.newBuilder().withId("defaultUserIdentifierId");
    }
}
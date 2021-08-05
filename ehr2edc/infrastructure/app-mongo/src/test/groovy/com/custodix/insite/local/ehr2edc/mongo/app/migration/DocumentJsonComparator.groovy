package com.custodix.insite.local.ehr2edc.mongo.app.migration

import org.skyscreamer.jsonassert.Customization
import org.skyscreamer.jsonassert.JSONCompareMode
import org.skyscreamer.jsonassert.comparator.CustomComparator

final class DocumentJsonComparator extends CustomComparator {
    private DocumentJsonComparator(Builder builder) {
        super(JSONCompareMode.STRICT, *builder.customizations)
    }

    static Builder newBuilder() {
        return new Builder()
    }

    private static Customization generatedUUIDCustomization(String path) {
        new Customization(path, { String actual, String expected ->
            return UUID.fromString(actual) != null
        })
    }

    static final class Builder {
        def customizations = []

        private Builder() {
        }

        Builder withGeneratedUUID(String path) {
            customizations.add(generatedUUIDCustomization(path))
            return this
        }

        DocumentJsonComparator build() {
            return new DocumentJsonComparator(this)
        }
    }
}
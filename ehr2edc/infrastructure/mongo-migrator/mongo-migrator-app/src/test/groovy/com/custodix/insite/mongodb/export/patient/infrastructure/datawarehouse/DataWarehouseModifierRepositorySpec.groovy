package com.custodix.insite.mongodb.export.patient.infrastructure.datawarehouse

import com.custodix.insite.mongodb.export.patient.application.command.AbstractExportPatientSpec
import com.custodix.insite.mongodb.export.patient.domain.model.common.Modifier
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import spock.lang.Unroll

import static com.custodix.insite.mongodb.export.patient.domain.model.common.ModifierCategory.*

class DataWarehouseModifierRepositorySpec extends AbstractExportPatientSpec {
    private static final String SQL_DATA_CONTAINS_MODIFIER = "select count(*) from modifier_dimension where modifier_cd = '%s' and modifier_path = '%s'"

    @Autowired
    DataWarehouseModifierRepository dataWarehouseModifierRepository
    @Autowired
    JdbcTemplate jdbcTemplate

    @Unroll
    def "Get a modifier by reference concept '#concept'"() {
        given: "A modifier with reference concept '#concept' and path '#path'"
        assertGivenModifier(concept, path)

        when: "I get the modifier by its reference concept '#concept'"
        Optional<Modifier> modifierOptional = dataWarehouseModifierRepository.getModifier(concept)

        then: "I receive the modifier with category '#expectedCategory' and reference code '#expectedReferenceCode'"
        modifierOptional.isPresent()
        with(modifierOptional.get()) {
            it.category == expectedCategory
            it.referenceCode == expectedReferenceCode
        }

        where:
        concept               | path                                                                                                                                                                                                       || expectedCategory | expectedReferenceCode
        "SNOMED-CT:7771000"   | "\\insite\\EHR2EDC_Laterality\\SNOMED-CT_272424004\\SNOMED-CT_182353008\\SNOMED-CT_7771000\\"                                                                                                              || LATERALITY       | "7771000"
        "SNOMED-CT:33586001"  | "\\insite\\EHR2EDC_Position\\SNOMED-CT_9851009\\SNOMED-CT_33586001\\"                                                                                                                                      || POSITION         | "33586001"
        "SNOMED-CT:40983000"  | "\\insite\\EHR2EDC_Anatomic_location\\SNOMED-CT_91723000\\SNOMED-CT_52530000\\SNOMED-CT_38866009\\SNOMED-CT_128262006\\SNOMED-CT_70104002\\SNOMED-CT_120574008\\SNOMED-CT_420657004\\SNOMED-CT_40983000\\" || LOCATION         | "40983000"
        "SNOMED-CT:261754007" | "\\insite\\EHR2EDC_Route\\SNOMED-CT_284009009\\SNOMED-CT_445755006\\SNOMED-CT_47625008\\SNOMED-CT_261754007\\"                                                                                             || ROUTE            | "261754007"
        "SNOMED-CT:11190007"  | "\\insite\\EHR2EDC_Dose_format\\SNOMED-CT_105904009\\SNOMED-CT_421967003\\SNOMED-CT_421378002\\SNOMED-CT_11190007\\"                                                                                       || DOSE_FORMAT      | "11190007"
        "SNOMED-CT:396134005" | "\\insite\\EHR2EDC_Medication_frequency\\SNOMED-CT_272123002\\SNOMED-CT_307430002\\SNOMED-CT_307459002\\SNOMED-CT_307474000\\SNOMED-CT_396134005\\"                                                        || FREQUENCY        | "396134005"
    }

    @Unroll
    def "Get a modifier by local concept '#concept'"() {
        given: "A modifier with local concept '#concept' and path '#path'"
        assertGivenModifier(concept, path)

        when: "I get the modifier by its local concept '#concept'"
        Optional<Modifier> modifierOptional = dataWarehouseModifierRepository.getModifier(concept)

        then: "I receive the modifier with category '#expectedCategory' and reference code '#expectedReferenceCode'"
        modifierOptional.isPresent()
        with(modifierOptional.get()) {
            it.category == expectedCategory
            it.referenceCode == expectedReferenceCode
        }

        where:
        concept                | path                                                                                                                                                                                                                                || expectedCategory | expectedReferenceCode
        "LOCAL_CODE:0001777"   | "\\insite\\EHR2EDC_Laterality\\SNOMED-CT_272424004\\SNOMED-CT_182353008\\SNOMED-CT_7771000\\((LOCAL_CODE_0001777))\\"                                                                                                               || LATERALITY       | "7771000"
        "LOCAL_CODE:10068533"  | "\\insite\\EHR2EDC_Position\\SNOMED-CT_9851009\\SNOMED-CT_33586001\\((LOCAL_CODE_10068533))\\"                                                                                                                                      || POSITION         | "33586001"
        "LOCAL_CODE:00038904"  | "\\insite\\EHR2EDC_Anatomic_location\\SNOMED-CT_91723000\\SNOMED-CT_52530000\\SNOMED-CT_38866009\\SNOMED-CT_128262006\\SNOMED-CT_70104002\\SNOMED-CT_120574008\\SNOMED-CT_420657004\\SNOMED-CT_40983000\\((LOCAL_CODE_00038904))\\" || LOCATION         | "40983000"
        "LOCAL_CODE:700457162" | "\\insite\\EHR2EDC_Route\\SNOMED-CT_284009009\\SNOMED-CT_445755006\\SNOMED-CT_47625008\\SNOMED-CT_261754007\\((LOCAL_CODE_700457162))\\"                                                                                            || ROUTE            | "261754007"
        "LOCAL_CODE:70009111"  | "\\insite\\EHR2EDC_Dose_format\\SNOMED-CT_105904009\\SNOMED-CT_421967003\\SNOMED-CT_421378002\\SNOMED-CT_11190007\\((LOCAL_CODE_70009111))\\"                                                                                       || DOSE_FORMAT      | "11190007"
        "LOCAL_CODE:500431693" | "\\insite\\EHR2EDC_Medication_frequency\\SNOMED-CT_272123002\\SNOMED-CT_307430002\\SNOMED-CT_307459002\\SNOMED-CT_307474000\\SNOMED-CT_396134005\\((LOCAL_CODE_500431693))\\"                                                       || FREQUENCY        | "396134005"
    }

    def "Get a modifier that has multiple paths"() {
        given: "A modifier with concept '#concept' and path '#path1'"
        assertGivenModifier(concept, path1)
        and: "A modifier with concept '#concept' and path '#path2'"
        assertGivenModifier(concept, path2)

        when: "I get the modifier by its concept '#concept'"
        Optional<Modifier> modifierOptional = dataWarehouseModifierRepository.getModifier(concept)

        then: "I receive the modifier with category '#expectedCategory' and reference code '#expectedReferenceCode'"
        modifierOptional.isPresent()
        with(modifierOptional.get()) {
            it.category == expectedCategory
            it.referenceCode == expectedReferenceCode
        }

        where:
        concept = "MULTIPLE_PATHS:1"
        path1 = "\\insite\\EHR2EDC_Position\\SNOMED-CT_1\\SNOMED-CT_2\\SNOMED-CT_4\\((MULTIPLE_PATHS_1))\\"
        path2 = "\\insite\\EHR2EDC_Position\\SNOMED-CT_1\\SNOMED-CT_3\\SNOMED-CT_4\\((MULTIPLE_PATHS_1))\\"
        expectedCategory = POSITION
        expectedReferenceCode = "4"
    }

    def "Get a modifier that has a legacy path and a valid path"() {
        given: "A modifier with concept '#concept' and path '#pathLegacy'"
        assertGivenModifier(concept, pathLegacy)
        and: "A modifier with concept '#concept' and path '#pathValid'"
        assertGivenModifier(concept, pathValid)

        when: "I get the modifier by its concept '#concept'"
        Optional<Modifier> modifierOptional = dataWarehouseModifierRepository.getModifier(concept)

        then: "I receive the modifier with category '#expectedCategory' and reference code '#expectedReferenceCode'"
        modifierOptional.isPresent()
        with(modifierOptional.get()) {
            it.category == expectedCategory
            it.referenceCode == expectedReferenceCode
        }

        where:
        concept = "LEGACY_PATH:1"
        pathLegacy = "\\insite\\((EHR2EDC_Position))\\((SNOMED-CT_1))\\((SNOMED-CT_2))\\((SNOMED-CT_4))\\((LEGACY_PATH_1))\\"
        pathValid = "\\insite\\EHR2EDC_Position\\SNOMED-CT_1\\SNOMED-CT_3\\SNOMED-CT_4\\((LEGACY_PATH_1))\\"
        expectedCategory = POSITION
        expectedReferenceCode = "4"
    }

    def "Get a modifier with an unknown category"() {
        given: "A modifier with concept '#concept' and path '#path'"
        assertGivenModifier(concept, path)

        when: "I get the modifier by its concept '#concept'"
        Optional<Modifier> modifierOptional = dataWarehouseModifierRepository.getModifier(concept)

        then: "I receive the modifier with category '#expectedCategory' and reference code '#expectedReferenceCode'"
        modifierOptional.isPresent()
        with(modifierOptional.get()) {
            it.category == expectedCategory
            it.referenceCode == expectedReferenceCode
        }

        where:
        concept = "UNKNOWN_CATEGORY:1"
        path = "\\insite\\EHR2EDC_Unknown\\SNOMED-CT_1\\SNOMED-CT_2\\SNOMED-CT_4\\((UNKNOWN_CATEGORY_1))\\"
        expectedCategory = UNKNOWN
        expectedReferenceCode = "4"
    }

    def "Get a modifier with an unknown reference code because of an unknown namespace"() {
        given: "A modifier with concept '#concept' and path '#path'"
        assertGivenModifier(concept, path)

        when: "I get the modifier by its concept '#concept'"
        Optional<Modifier> modifierOptional = dataWarehouseModifierRepository.getModifier(concept)

        then: "I receive nothing"
        !modifierOptional.isPresent()

        where:
        concept = "UNKNOWN_NAMESPACE:1"
        path = "\\insite\\EHR2EDC_Position\\UNKNOWN-NAMESPACE_1\\UNKNOWN-NAMESPACE_2\\UNKNOWN-NAMESPACE_4\\((UNKNOWN_NAMESPACE_1))\\"
    }

    def "Get a modifier that does not exist"() {
        when: "I get a modifier by the concept '#concept'"
        Optional<Modifier> modifierOptional = dataWarehouseModifierRepository.getModifier(concept)

        then: "I receive nothing"
        !modifierOptional.isPresent()

        where:
        concept = "NOT_EXISTS:1"
    }

    def assertGivenModifier(String concept, String path) {
        assert jdbcTemplate.queryForObject(String.format(SQL_DATA_CONTAINS_MODIFIER, concept, path), Integer.class) == 1
    }
}

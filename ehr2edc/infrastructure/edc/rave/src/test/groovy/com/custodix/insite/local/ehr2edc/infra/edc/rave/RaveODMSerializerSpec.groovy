package com.custodix.insite.local.ehr2edc.infra.edc.rave

import com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.RaveODMSerializer
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

class RaveODMSerializerSpec extends AbstractSpecification {

    @Autowired
    RaveODMSerializer clinicalDataODMParser

    @Unroll
    def "Parsing the Clinical Data ODM, parsing goes wrong"(String odm) {
        when: "Parsing the ODM text `#odm`"
        def result = clinicalDataODMParser.deserialize(odm)

        then: "Expect the parser to return an ODM without any Clinical Data"
        result != null
        result.clinicalData == []

        where:
        odm                                                    | _
        readSample("rave/getsubjects/GetSubjects-invalid.xml") | _
        null                                                   | _
    }

    @Unroll
    def "Successfully parsing the Clinical Data ODM"(String studyId, String odm, int noSubjects) {
        when: "Parsing the ODM text `#odm` for study with id `#studyId`"
        def result = clinicalDataODMParser.deserialize(odm)

        then: "An ODM file is returned with `#noSubjects` subjects"
        result != null
        if (studyId == null) {
            result.clinicalData == null
        } else {
            result.clinicalData.studyOID == studyId
            result.clinicalData.subjectData.subjectKey.size() == noSubjects
        }

        where:
        studyId   | odm                                                  | noSubjects
        null      | readSample("rave/getsubjects/GetSubjects-valid.xml") | 0
        "SID_001" | readSample("rave/getsubjects/GetSubjects.xml")       | 100

    }
}

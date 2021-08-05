package com.custodix.insite.mongodb.export.patient.application.query


import com.custodix.insite.mongodb.export.patient.application.api.GetActiveSubjects
import com.custodix.insite.mongodb.export.patient.application.command.AbstractSubjectActivationSpec
import com.custodix.insite.mongodb.vocabulary.Namespace
import com.custodix.insite.mongodb.vocabulary.PatientId
import com.custodix.insite.mongodb.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

class GetActiveSubjectSpec extends AbstractSubjectActivationSpec{

    @Autowired
    GetActiveSubjects getActiveSubject

    @Unroll
    def "Returns all the active subjects" (SubjectId subjectId, PatientId patientId, Namespace patientIdSource) {
        given: '''An active subject with
                 subject id #subjectId and
                 patient id #patientId and
                 patient id source #patientIdSource 
             '''
        assertActiveSubject(subjectId, patientId, patientIdSource)

        when: "get all active subjects"
        def response = getActiveSubject.getAll()

        then: "the active subjects is active"
        response.patientIdentifiers.size() == 1
        def activeSubject =  response.patientIdentifiers.get(0)
        activeSubject.subjectId == subjectId
        activeSubject.patientId.id == patientId.id
        activeSubject.namespace.name == patientIdSource.name

        where:
        subjectId               | patientId               | patientIdSource
        SubjectId.of("123-123") | PatientId.of("567-567") | Namespace.of("patient id source")
    }
}

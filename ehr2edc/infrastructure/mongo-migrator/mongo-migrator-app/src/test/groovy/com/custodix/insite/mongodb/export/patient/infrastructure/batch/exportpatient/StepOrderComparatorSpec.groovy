package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient

import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.removepatientdata.RemovePatientDataStepFactory
import org.springframework.batch.core.JobInterruptedException
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepExecution
import spock.lang.Specification

class StepOrderComparatorSpec extends Specification {

    def "Order steps correctly"() {
        given: "a remove patient data step"
        def removePatientDataStep = new StepImpl(RemovePatientDataStepFactory.REMOVE_PATIENT_DATA_STEP)
        and: "a step"
        def aStep = new StepImpl("aStepName")
        and: "unOrdered steps list"
        List<Step> steps = Arrays.asList(aStep, removePatientDataStep)
        assertUnOrder(steps)

        when: "ordering steps"
        Collections.sort(steps, new StepOrderComparator())

        then: "the first step is the remove patient data step"
        steps[0].name == RemovePatientDataStepFactory.REMOVE_PATIENT_DATA_STEP

    }

    Object assertUnOrder(final List<Step> unOrderedSteps) {
        assert unOrderedSteps[0].name != RemovePatientDataStepFactory.REMOVE_PATIENT_DATA_STEP
    }

    class StepImpl implements Step{

        private final String name

        StepImpl(final String name) {
            this.name = name
        }

        @Override
        String getName() {
            return name
        }

        @Override
        boolean isAllowStartIfComplete() {
            throw new IllegalAccessException("Not implemented")
        }

        @Override
        int getStartLimit() {
            throw new IllegalAccessException("Not implemented")
        }

        @Override
        void execute(final StepExecution stepExecution) throws JobInterruptedException {
            throw new IllegalAccessException("Not implemented")
        }
    }
}

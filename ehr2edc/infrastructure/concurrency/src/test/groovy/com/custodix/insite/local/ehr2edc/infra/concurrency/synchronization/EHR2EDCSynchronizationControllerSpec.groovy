package com.custodix.insite.local.ehr2edc.infra.concurrency.synchronization

import org.awaitility.Awaitility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import spock.lang.Stepwise
import spock.lang.Title

@Stepwise
@Title("AssignedInvestigator Authorization")
@ContextConfiguration(classes = InnerTestConfig.class)
class EHR2EDCSynchronizationControllerSpec extends AbstractSynchronizationSpec {

    @Autowired
    FastCommand command;
    @Autowired
    LongRunningCommand longRunningCommand;

    def values = [];

    def before() {
        values = [];
    }


    def "Commands with the same correlator run sequential without editing the request before it's turn"() {
        when: "Calling a long-running command with a correlator 10 times"
        def request = new SynchronizedRequest("correlator", values, 1)
        runInThread { longRunningCommand.doCommand(request) }
        runInThread { longRunningCommand.doCommand(request) }
        runInThread { longRunningCommand.doCommand(request) }
        runInThread { longRunningCommand.doCommand(request) }
        runInThread { longRunningCommand.doCommand(request) }
        runInThread { longRunningCommand.doCommand(request) }
        runInThread { longRunningCommand.doCommand(request) }
        runInThread { longRunningCommand.doCommand(request) }
        runInThread { longRunningCommand.doCommand(request) }
        runInThread { longRunningCommand.doCommand(request) }
        and: "waiting for the long running command"
        Awaitility.await().until { values.size() == 10 }
        then:
        noExceptionThrown()
    }

    def runInThread(Runnable runnable) {
        new Thread(runnable).start()
    }

    @Import([SynchronizationConfiguration.class, SynchronizationTestConfiguration.class, EHR2EDCSynchronizationController.class])
    static class InnerTestConfig {
    }

}

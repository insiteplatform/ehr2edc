package eu.ehr4cr.workbench.local.model.security


import eu.ehr4cr.workbench.local.service.DomainTime
import eu.ehr4cr.workbench.local.service.TestTimeService
import spock.lang.Specification

class AbstractSecuritySpec extends Specification {

    TestTimeService timeService = new TestTimeService()

    def setup() {
        DomainTime.setTime(timeService)
    }
}

package eu.ehr4cr.workbench.local.eventhandlers.user

import com.custodix.insite.local.user.application.api.UnlockAccount
import com.custodix.insite.local.user.infra.eventcontrollers.ImminentlyExpiringPasswordEventController
import com.custodix.insite.local.user.infra.eventcontrollers.PasswordRecoveredEventController
import com.custodix.insite.local.user.infra.eventcontrollers.UserEventHandler
import com.custodix.insite.local.user.infra.notifications.TestUserMailNotificationService
import eu.ehr4cr.workbench.local.eventhandlers.AsyncEventsTest
import eu.ehr4cr.workbench.local.eventhandlers.AsynchronousEventHandler
import eu.ehr4cr.workbench.local.properties.PropertyProvider
import eu.ehr4cr.workbench.local.security.SystemUserAuthenticator
import eu.ehr4cr.workbench.local.security.annotation.RunAsSystemUserAspect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import

@EnableAspectJAutoProxy
@Import([UserEventHandler,
        AsynchronousEventHandler,
        RunAsSystemUserAspect,
        PropertyProvider,
        TestUserMailNotificationService,
        ImminentlyExpiringPasswordEventController,
        PasswordRecoveredEventController])
class UserAsyncEventsTest extends AsyncEventsTest {
    @Autowired
    protected TestUserMailNotificationService mailNotificationService
    @MockBean
    protected UnlockAccount unlockAccount
    @MockBean
    protected SystemUserAuthenticator systemUserAuthenticator
}

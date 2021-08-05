package com.custodix.insite.local.user

import com.custodix.insite.local.user.application.api.UpdatePasswordsStatus
import com.custodix.insite.local.user.domain.events.ImminentlyExpiringPasswordEvent
import com.custodix.insite.local.user.main.PasswordExpirySettingsConfiguration
import eu.ehr4cr.workbench.local.model.security.User
import eu.ehr4cr.workbench.local.model.security.UserStatus
import spock.lang.Ignore
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title
import spock.lang.Unroll

import java.util.stream.Collectors

import static eu.ehr4cr.workbench.local.model.security.UserStatus.*
import static eu.ehr4cr.workbench.local.service.DomainTime.now
import static org.apache.commons.lang3.time.DateUtils.addDays
import static org.apache.commons.lang3.time.DateUtils.isSameDay

@Title("All user passwords are updated to support passwords having become expired")
class UpdatePasswordsStatusSpec extends AbstractSpecification {
    @Autowired
    private UpdatePasswordsStatus updatePasswordsStatus
    @Autowired
    private PasswordExpirySettingsConfiguration passwordExpirySettings

    @Unroll
    @Ignore
    // Issues with DST and timezoneing
    def "When password expiration is enabled, a #status user with password expiring imminently on next update results in an event"() {
        given: "Password expiration is enabled"
        passwordExpirySettings.enabled = true
        and: "A #status user"
        User user = users.aUser(status)
        and: "The user's password expires imminently on next update"
        users.setImminentlyExpiringPasswordOnNextUpdate(user)

        when: "I update passwords status"
        updatePasswordsStatus.updatePasswordsStatus()

        then: "The user's password expires imminently"
        user.passwordImminentlyExpiring

        and: "An ImminentlyExpiringPasswordEvent is published"
        ImminentlyExpiringPasswordEvent event = eventPublisher.poll() as ImminentlyExpiringPasswordEvent
        event.userMailAddress == user.email
        def duration = passwordExpirySettings.expiryDuration.minus(passwordExpirySettings.imminentDuration)
        def calculatedExpirationDate = addDays(now(), (int) duration.toDays())
        isSameDay(event.expirationDate, calculatedExpirationDate)

        where:
        status           | _
        ACTIVE           | _
        RECOVERING       | _
        EXPIRED_RECOVERY | _
    }

    @Unroll
    def "When password expiration is enabled, a #status user with password expiring imminently does not result in an event"() {
        given: "Password expiration is enabled"
        passwordExpirySettings.enabled = true
        and: "A #status user"
        User user = users.aUser(status)
        and: "The user's password expires imminently"
        users.setImminentlyExpiringPassword(user)
        eventPublisher.clear()

        when: "I update passwords status"
        updatePasswordsStatus.updatePasswordsStatus()

        then: "No event is published"
        eventPublisher.empty

        where:
        status           | _
        ACTIVE           | _
        RECOVERING       | _
        EXPIRED_RECOVERY | _
    }

    def "When password expiration is enabled, a DELETED user with password expiring imminently on next update does not result in an event"() {
        given: "Password expiration is enabled"
        passwordExpirySettings.enabled = true
        and: "A DELETED user"
        User user = users.aDeletedUser()
        and: "The user's password expires imminently on next update "
        users.setImminentlyExpiringPasswordOnNextUpdate(user)

        when: "I update passwords status"
        updatePasswordsStatus.updatePasswordsStatus()

        then: "No event is published"
        eventPublisher.empty
    }

    @Unroll
    def "When password expiration is disabled, a #status user with password expiring imminently on next update does not result in an event"() {
        given: "Password expiration is disabled"
        passwordExpirySettings.enabled = false
        and: "A #status user"
        User user = users.aUser(status)
        and: "The user's password expires imminently on next update"
        users.setImminentlyExpiringPasswordOnNextUpdate(user)

        when: "I update passwords status"
        updatePasswordsStatus.updatePasswordsStatus()

        then: "No event is published"
        eventPublisher.empty

        where:
        status           | _
        ACTIVE           | _
        RECOVERING       | _
        EXPIRED_RECOVERY | _
        DELETED          | _
    }

    @Unroll
    def "When password expiration is enabled, a #status user with password active does not result in an event"() {
        given: "Password expiration is enabled"
        passwordExpirySettings.enabled = true
        and: "A #status user with password not expiring imminently"
        users.aUser(status as UserStatus)

        when: "I update passwords status"
        updatePasswordsStatus.updatePasswordsStatus()

        then: "No event is published"
        eventPublisher.empty

        where:
        status << Arrays.stream(values()).filter({ s -> s != UNKNOWN }).collect(Collectors.toList())
    }
}

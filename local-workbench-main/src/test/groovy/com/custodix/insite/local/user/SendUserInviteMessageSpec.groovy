package com.custodix.insite.local.user

import eu.ehr4cr.workbench.local.model.security.User
import eu.ehr4cr.workbench.local.service.email.model.ImmutableUserInviteMailContent
import eu.ehr4cr.workbench.local.service.email.model.MailContent
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title

import static com.custodix.insite.local.user.SendUserInviteMessage.Request
import static eu.ehr4cr.workbench.local.service.email.TestMailService.RecipientMail

@Title("Send user invitation message")
class SendUserInviteMessageSpec extends AbstractSpecification {
    @Autowired
    private SendUserInviteMessage sendUserInviteMessage

    def "The system can send an invitation email to the user"() {
        given: "An invited user"
        User user = users.anInvitedUser()

        when: "The system sends an invitation email to the user"
        Request request = Request.newBuilder().withUser(user).build()
        sendUserInviteMessage.send(request)

        then: "An invitation e-mail is sent to the user"
        RecipientMail mail = testMailService.poll()
        mail.recipient == user.getEmail()
        mail.content == createExpectedMailContent(user)
    }

    private MailContent createExpectedMailContent(User user) {
        return ImmutableUserInviteMailContent.builder()
                .inviteExpireValue(invitationSettings.getInviteExpireValue())
                .inviteExpireUnit(invitationSettings.getInviteExpireUnit())
                .inviteAcceptUrl(createExpectedCompleteAccountUrl(user))
                .build()
    }

    private String createExpectedCompleteAccountUrl(User user) {
        return invitationSettings.getInvitationUri( user.getId(), user.getTempPassword())
    }
}

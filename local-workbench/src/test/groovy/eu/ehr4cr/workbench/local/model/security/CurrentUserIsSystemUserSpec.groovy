package eu.ehr4cr.workbench.local.model.security

import eu.ehr4cr.workbench.local.security.SecurityContextUser
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Title

import static eu.ehr4cr.workbench.local.model.security.UserObjectMother.aDefaultSystemUser
import static eu.ehr4cr.workbench.local.model.security.UserObjectMother.aDefaultUser

@Title("CurrentUser.isSystemUser")
class CurrentUserIsSystemUserSpec extends AbstractSecuritySpec {

    CurrentUser currentUser = new CurrentUserImpl()

    def "return true when the authenticated user is system user"() {
        given: "an authenticated system user"
        authenticate(aDefaultSystemUser())

        when: "checking whether user is system user"
        def isSystemUser = currentUser.isSystemUser()

        then: "user is system user"
        isSystemUser
    }

    def "return false when the authenticated user is not system user"() {
        given: "an authenticated user"
        authenticate(aDefaultUser())

        when: "checking whether user is system user"
        def isSystemUser = currentUser.isSystemUser()

        then: "user is NOT system user"
        !isSystemUser
    }

    def authenticate(final User user) {
        SecurityContextUser principal = new SecurityContextUser(user)
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, "")
        SecurityContextHolder.getContext()
                .setAuthentication(authentication)
    }
}

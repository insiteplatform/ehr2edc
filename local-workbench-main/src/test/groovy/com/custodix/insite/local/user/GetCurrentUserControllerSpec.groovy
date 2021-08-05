package com.custodix.insite.local.user

import com.custodix.insite.local.GetCurrentUserController
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title

@Title("Get current user in process")
class GetCurrentUserControllerSpec extends AbstractSpecification {

    @Autowired
    GetCurrentUserController getCurrentUser

    def "Retrieves the logged in user"() {
        given: "A user is signed in"
        def user = users.anActiveUser()
        users.login(user)

        when: "Retrieving the current user"
        GetCurrentUserController.Response result = getCurrentUser.get()

        then: "The correct user is returned"
        with(result.user) {
            id == user.id
            name == user.username
            !drm
        }
    }

    def "Retrieves a DRM user"() {
        given: "A user is signed in"
        def user = users.aDrmUser()
        users.login(user)

        when: "Retrieving the current user"
        GetCurrentUserController.Response result = getCurrentUser.get()

        then: "The correct user is returned"
        with(result.user) {
            id == user.id
            name == user.username
            drm
        }
    }

    def "Does not fail without authenticated user"() {
        given: "No user is signed in"
        users.logout()

        when: "Retrieving the current user"
        GetCurrentUserController.Response result = getCurrentUser.get()

        then: "No user is returned"
        result.user == null
    }

}

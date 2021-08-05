package com.custodix.insite.local.user

import com.custodix.insite.local.GetUsersController
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title

@Title("Get users in process")
class GetUsersControllerSpec extends AbstractSpecification {
    @Autowired
    private GetUsersController getUsers

    def "Retrieve the simple active users list"() {
        given: "An active user"
        def user = users.anActiveUser()
        and: "A pending user"
        users.aPendingUser()
        when: "The user views the users"
        GetUsersController.Response response = getUsers.getUsers()

        then: "Only the active user is returned"
        def first = response.users.stream().filter({ u -> u.id == user.id }).findFirst()
        first.isPresent()
        first.get().name == user.username
    }
}

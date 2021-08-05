package com.custodix.insite.local.user

import com.custodix.insite.local.user.application.api.GetSimpleUserList
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title

@Title("Get simple users list")
class GetSimpleUserListSpec extends AbstractSpecification {
    @Autowired
    private GetSimpleUserList getSimpleUserList

    def "Retrieve the simple active users list"() {
        given: "An active user"
        def user = users.anActiveUser()
        and: "A pending user"
        def pending = users.aPendingUser()

        when: "The user views the users"
        GetSimpleUserList.Response response = getSimpleUserList.getActiveUsers()

        then: "The active user is returned"
        response.users.any {
            it.id.id == user.id
            it.name == user.username
        }
        and: "The pending user is not returned"
        !response.users.any {
            it.id.id == pending.id
            it.name == pending.username
        }
    }
}

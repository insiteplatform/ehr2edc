package com.custodix.insite.local.ehr2edc.infra.users

import com.custodix.insite.local.GetUsersController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification
import spock.lang.Title

@Title("Get simple users list")
@SpringBootTest(classes = GetUsersControllerSpec)
@Import(UsersConfiguration.class)
@EnableAutoConfiguration
class GetUsersControllerSpec extends Specification {
    @Autowired
    private GetUsersController getUsers

    def "Retrieve the simple active users list"() {
        given: "An active user"
        and: "A pending user"
        and: "A deleted user"

        when: "The user views the users"
        GetUsersController.Response response = getUsers.getUsers()


        then: "The active user is returned"
        !response.users.isEmpty()
        response.users.any {
            it.id == 1L
            it.name == "gert"
        }
        and: "The pending user is not returned"
        !response.users.any {
            it.id == 2L
            it.name == "gert-pending"
        }
        and: "The deleted user is not returned"
        !response.users.any {
            it.id == 3L
            it.name == "gert-deleted"
        }
    }

    def "Retrieve the users list with drm access rights"() {
        given: "A drm user"
        and: "A non drm user"

        when: "The user views the users"
        GetUsersController.Response response = getUsers.getUsers()


        then: "The drm user is returned with drm true"
        !response.users.isEmpty()
        response.users.any {
            it.id == 4L
            it.name == "gert-drm"
            it.drm
        }
        and: "The non drm user is returned with drm false"
        response.users.any {
            it.id == 5L
            it.name == "gert-no-drm"
            !it.drm
        }
    }
}

package com.custodix.insite.local.user

import com.custodix.insite.local.user.application.api.UpdateSecurityQuestion
import eu.ehr4cr.workbench.local.dao.SecurityDao
import eu.ehr4cr.workbench.local.model.security.User
import eu.ehr4cr.workbench.local.vocabulary.UserIdentifier
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import spock.lang.Title

import javax.validation.ConstraintViolationException

import static com.custodix.insite.local.user.application.api.UpdateSecurityQuestion.Request
import static eu.ehr4cr.workbench.local.testingutils.ConstraintViolationAssert.assertThat

@Title("A user can update his security question")
class UpdateSecurityQuestionSpec extends AbstractSpecification {
    private static final String QUESTION_ID = "Test question ID"
    private static final String QUESTION_ANSWER = "Test question answer"

    @Autowired
    UpdateSecurityQuestion updateSecurityQuestion
    @Autowired
    SecurityDao securityDao

    def "A user can update his security question"() {
        given: "I am authenticated"
        User user = users.anActiveUser()
        users.login(user)

        when: "I update my security question"
        updateSecurityQuestion.updateSecurityQuestion(createRequest(user.identifier, QUESTION_ID, QUESTION_ANSWER))

        then: "My security question has been updated"
        User userValidate = securityDao.findUserById(user.getIdentifier())
        userValidate.securityQuestionId == QUESTION_ID
        userValidate.securityAnswer == QUESTION_ANSWER
    }

    def "A user cannot update the security question of another user"() {
        given: "I am authenticated"
        User user = users.anActiveUser()
        users.login(user)
        and: "Another user"
        User anotherUser = users.aRegularUser()

        when: "I update the security question of the other user"
        updateSecurityQuestion.updateSecurityQuestion(createRequest(anotherUser.identifier, QUESTION_ID, QUESTION_ANSWER))

        then: "Access is denied"
        thrown AccessDeniedException
    }

    def "A user cannot update his security question with a blank user identifier"() {
        given: "I am authenticated"
        User user = users.anActiveUser()
        users.login(user)

        when: "I update my security question with a blank user identifier"
        updateSecurityQuestion.updateSecurityQuestion(createRequest(null, QUESTION_ID, QUESTION_ANSWER))

        then: "Access is denied"
        thrown AccessDeniedException
    }

    def "A user cannot update his security question with a blank question id"() {
        given: "I am authenticated"
        User user = users.anActiveUser()
        users.login(user)

        when: "I update my security question with a blank question id"
        updateSecurityQuestion.updateSecurityQuestion(createRequest(user.identifier, "", QUESTION_ANSWER))

        then: "A clear error message is returned"
        ConstraintViolationException exception = thrown ConstraintViolationException
        assertThat(exception).containsExactly("arg0.questionId", "must not be blank")
    }

    def "A user cannot update his security question with a blank question answer"() {
        given: "I am authenticated"
        User user = users.anActiveUser()
        users.login(user)

        when: "I update my security question with a blank question answer"
        updateSecurityQuestion.updateSecurityQuestion(createRequest(user.identifier, QUESTION_ID, ""))

        then: "A clear error message is returned"
        ConstraintViolationException exception = thrown ConstraintViolationException
        assertThat(exception).containsExactly("arg0.questionAnswer", "must not be blank")
    }

    private Request createRequest(UserIdentifier userIdentifier, String questionId, String questionAnswer) {
        return Request.newBuilder()
                .withUserIdentifier(userIdentifier)
                .withQuestionId(questionId)
                .withQuestionAnswer(questionAnswer)
                .build()
    }
}

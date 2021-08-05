package eu.ehr4cr.workbench.local.usecases.userrecovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import eu.ehr4cr.workbench.local.exception.security.UserRecoveryUnavailableException;
import eu.ehr4cr.workbench.local.model.security.Question;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.service.ISecurityQuestionService;

@RunWith(MockitoJUnitRunner.class)
public class RetrieveUserSecurityQuestionUseCaseTest {

	private static final String QUESTION_ID = "1";
	private static final String QUESTION_TEXT = "Test question?";

	private static final String USER_NAME = "test user";
	private static final String USER_MAIL = "test.user@custodix.com";
	private static final String USER_PASSWORD = "Test password";

	@Mock
	private ISecurityQuestionService securityQuestionService;
	@InjectMocks
	private RetrieveUserSecurityQuestionUseCase retrieveUserSecurityQuestion;

	@Before
	public void init() {
		when(securityQuestionService.getQuestionByIdForLocale(anyString(), any())).thenReturn(
				new Question(QUESTION_ID, QUESTION_TEXT));
	}

	@Test
	public void validUserWithSecurityQuestion() {
		User user = createValidUser();
		user.updateSecurityQuestion(QUESTION_ID, "");
		RetrieveUserSecurityQuestionRequest request = createRequest(user);

		String question = retrieveUserSecurityQuestion.retrieveQuestion(request);

		assertThat(question).as("Expected the question to be returned")
				.isEqualTo(QUESTION_TEXT);
	}

	@Test(expected = UserRecoveryUnavailableException.class)
	public void validUserWithoutSecurityQuestion() {
		User user = createValidUser();
		RetrieveUserSecurityQuestionRequest request = createRequest(user);

		String question = retrieveUserSecurityQuestion.retrieveQuestion(request);

		assertThat(question).as("Expected the question to be returned")
				.isEqualTo(QUESTION_TEXT);
	}

	@Test(expected = UserRecoveryUnavailableException.class)
	public void deletedUser() {
		User user = spy(createValidUser());
		when(user.isRecoverable()).thenReturn(false);
		RetrieveUserSecurityQuestionRequest request = createRequest(user);

		retrieveUserSecurityQuestion.retrieveQuestion(request);
	}

	private User createValidUser() {
		return new User(USER_NAME, USER_PASSWORD, USER_MAIL, true);
	}

	private RetrieveUserSecurityQuestionRequest createRequest(User user) {
		return RetrieveUserSecurityQuestionRequest.builder()
				.withLocale(Locale.getDefault())
				.withUser(user)
				.build();
	}
}

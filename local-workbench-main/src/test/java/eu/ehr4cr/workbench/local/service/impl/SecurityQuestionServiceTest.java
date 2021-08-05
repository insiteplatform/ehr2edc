package eu.ehr4cr.workbench.local.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import eu.ehr4cr.workbench.local.AbstractWorkbenchTest;
import eu.ehr4cr.workbench.local.model.security.Question;

/**
 * Created by aleksandar on 15/11/16.
 */
@TestPropertySource(properties = { "enableInternationalization=true" })
public class SecurityQuestionServiceTest extends AbstractWorkbenchTest {

	private Locale LOCALE_NL = Locale.forLanguageTag("NL");

	@Autowired
	private SecurityQuestionService securityQuestionService;

	private Map<String, String> expectedQuestionsEn = new HashMap<String, String>() {
		{
			put("1", "What was your childhood nickname?");
			put("2", "What is the name of your favorite childhood friend?");
			put("3", "In what city or town did your mother and father meet?");
		}
	};
	private Map<String, String> expectedQuestionsNl = new HashMap<String, String>() {
		{
			put("1", "NL_What was your childhood nickname?");
			put("2", "NL_What is the name of your favorite childhood friend?");
			put("3", "NL_In what city or town did your mother and father meet?");
		}
	};

	@Test
	public void getQuestionsForLocaleEn() throws Exception {
		List<Question> questions = securityQuestionService.getQuestionsForLocale(Locale.ENGLISH);
		assertThat(questions).hasSize(3);
		assertQuestions(questions, expectedQuestionsEn);
	}

	@Test
	public void getQuestionsForLocaleKo() throws Exception {
		List<Question> questions = securityQuestionService.getQuestionsForLocale(Locale.KOREAN);
		assertThat(questions).hasSize(3);
		assertQuestions(questions, expectedQuestionsEn);
	}

	@Test
	public void getQuestionsForLocaleNl() throws Exception {
		List<Question> questions = securityQuestionService.getQuestionsForLocale(LOCALE_NL);
		assertThat(questions).hasSize(3);
		assertQuestions(questions, expectedQuestionsNl);
	}

	@Test
	public void getQuestionByIdForLocaleEn() throws Exception {
		Question questionByIdForLocale = securityQuestionService.getQuestionByIdForLocale("2", Locale.ENGLISH);
		assertThat(questionByIdForLocale.getText()).isEqualTo(expectedQuestionsEn.get("2"));
	}

	@Test
	public void getQuestionByIdForLocaleNl() throws Exception {
		Question questionByIdForLocale = securityQuestionService.getQuestionByIdForLocale("2", LOCALE_NL);
		assertThat(questionByIdForLocale.getText()).isEqualTo(expectedQuestionsNl.get("2"));
	}

	@Test
	public void getQuestionByIdForLocaleKo() throws Exception {
		Question questionByIdForLocale = securityQuestionService.getQuestionByIdForLocale("2", Locale.KOREAN);
		assertThat(questionByIdForLocale.getText()).isEqualTo(expectedQuestionsEn.get("2"));
	}

	private void assertQuestions(List<Question> questions, Map<String, String> expectedQuestions) {
		for (int i = 0; i < questions.size(); i++) {
			String questionText = expectedQuestions.get(String.valueOf(i + 1));
			assertThat(questions.get(i).getText()).isEqualTo(questionText);
		}
	}

}

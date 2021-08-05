package eu.ehr4cr.workbench.local.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import eu.ehr4cr.workbench.local.exception.security.SecurityQuestionException;
import eu.ehr4cr.workbench.local.model.security.Question;
import eu.ehr4cr.workbench.local.service.ISecurityQuestionService;

/**
 * Created by aleksandar on 14/11/16.
 */

@Service
public class SecurityQuestionService implements ISecurityQuestionService {

	private ApplicationContext context;

	@Value("${enableInternationalization:false}")
	private boolean enableInternationalization;

	@Autowired
	public SecurityQuestionService(ApplicationContext context) {
		this.context = context;
	}

	@Override
	public List<Question> getQuestionsForLocale(Locale locale) {
		List<String> questionStrings = loadQuestions(locale);
		return questionStringsToQuestionObjects(questionStrings);
	}

	@Override
	public Question getQuestionByIdForLocale(String id, Locale locale) {
		List<Question> questionsForLocale = getQuestionsForLocale(locale);
		for (Question question : questionsForLocale) {
			if (question.getId()
					.contentEquals(id))
				return question;
		}
		return new Question("-1", "Selected question no longer exists");
	}

	private List<String> loadQuestions(Locale locale) {
		try {
			Resource questionsFile = loadQuestionFileOrDefault(locale);
			return IOUtils.readLines(questionsFile.getInputStream(), "UTF-8");
		} catch (IOException e) {
			throw new SecurityQuestionException(e);
		}
	}

	private Resource loadQuestionFileOrDefault(Locale locale)  {
		String language = locale.getLanguage();
		Resource questionResource = context.getResource("classpath:questions_" + language + ".txt");
		if (!i18nEnabledAndResourceExists(questionResource)) {
			questionResource = context.getResource("classpath:questions.txt");
		}
		return questionResource;
	}

	private boolean i18nEnabledAndResourceExists(Resource questionResource) {
		return enableInternationalization && questionResource.exists();
	}

	private List<Question> questionStringsToQuestionObjects(List<String> questionStrings) {
		List<Question> questions = new ArrayList<>();
		for (String questionString : questionStrings) {
			String[] questionIdPair = questionString.split(";");
			String questionId = questionIdPair[0];
			String questionContent = questionIdPair[1];
			questions.add(new Question(questionId, questionContent));
		}
		return questions;
	}

}

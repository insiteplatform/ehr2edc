package eu.ehr4cr.workbench.local.service;

import java.util.List;
import java.util.Locale;

import eu.ehr4cr.workbench.local.model.security.Question;

/**
 * Created by aleksandar on 14/11/16.
 */
public interface ISecurityQuestionService {
	List<Question> getQuestionsForLocale(Locale locale);

	Question getQuestionByIdForLocale(String id, Locale locale);
}

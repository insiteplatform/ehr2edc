package eu.ehr4cr.workbench.local.model.security;

/**
 * Created by aleksandar on 14/11/16.
 */
public class Question {
	private final String id;
	private final String text;

	public Question(String id, String text) {
		this.id = id;
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}
}

package eu.ehr4cr.workbench.local.model.analysis;

import java.text.DecimalFormat;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Bin {
	private String variableName;
	private String variableLabel;
	@Column(name = "[count]")
	private double count;

	public Bin() {

	}

	public Bin(String variableName, String variableLabel, double count) {
		this.variableName = variableName;
		this.variableLabel = variableLabel;
		this.count = count;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public String getVariableLabel() {
		return variableLabel;
	}

	public void setVariableLabel(String variableLabel) {
		this.variableLabel = variableLabel;
	}

	public double getCount() {
		return count;
	}

	public String getCountFormatted() { // Removes trailing zeros
		DecimalFormat df = new DecimalFormat("#.#########");
		return df.format(count);
	}

	public void setCount(double count) {
		this.count = count;
	}

}

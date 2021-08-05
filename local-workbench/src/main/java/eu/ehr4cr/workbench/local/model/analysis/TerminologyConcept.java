package eu.ehr4cr.workbench.local.model.analysis;

import javax.persistence.Column;
import javax.persistence.Embedded;

public class TerminologyConcept {
	@Column(name = "conceptLabel")
	private String label;
	@Column(name = "conceptCategory")
	private String category;
	@Column(name = "conceptId")
	private String id;
	@Column(name = "conceptCoding")
	private String coding;
	@Column(name = "conceptOid")
	private String oid;
	@Column(name = "conceptQualifier")
	private String qualifier;
	@Column(name = "conceptUrn")
	private String urn;
	@Column(name = "conceptUnit")
	private String unit;
	@Column(name = "conceptOperator")
	private String operator;
	@Column(name = "conceptValue")
	private String value;
	@Column(name = "conceptDefaultUnit")
	private String defaultUnit;
	@Column(name = "conceptRuleType")
	private String ruleType;
	@Embedded
	private TerminologyConceptUnit unitNode;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCoding() {
		return coding;
	}

	public void setCoding(String coding) {
		this.coding = coding;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public String getUrn() {
		return urn;
	}

	public void setUrn(String urn) {
		this.urn = urn;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDefaultUnit() {
		return defaultUnit;
	}

	public void setDefaultUnit(String defaultUnit) {
		this.defaultUnit = defaultUnit;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public TerminologyConceptUnit getUnitNode() {
		return unitNode;
	}

	public void setUnitNode(TerminologyConceptUnit unitNode) {
		this.unitNode = unitNode;
	}

}

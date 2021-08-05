package eu.ehr4cr.workbench.local.model.analysis;

import javax.persistence.Column;

public class TerminologyConceptUnit {
	@Column(name = "conceptUnitLabel")
	private String label;
	@Column(name = "conceptUnitId")
	private String id;
	@Column(name = "conceptUnitCoding")
	private String coding;
	@Column(name = "conceptUnitOid")
	private String oid;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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

}

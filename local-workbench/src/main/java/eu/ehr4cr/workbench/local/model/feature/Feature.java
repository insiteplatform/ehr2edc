package eu.ehr4cr.workbench.local.model.feature;

import java.util.Objects;

import javax.persistence.*;

@Entity
@Table(name = "feature")
public class Feature {
	@Id
	private String featureId;
	private boolean isEnabled;

	private Feature() {
		// Default constructor for JPA
	}

	public Feature(String featureId, boolean isEnabled) {
		this.featureId = featureId;
		this.isEnabled = isEnabled;
	}

	public String getFeatureId() {
		return featureId;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void enable() {
		isEnabled = true;
	}

	public void disable() {
		isEnabled = false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Feature feature = (Feature) o;
		return Objects.equals(featureId, feature.featureId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(featureId);
	}
}

package eu.ehr4cr.workbench.local.model;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Objects;

import javax.persistence.*;

/**
 * The base class to be used by all Entity classes. It contains an id and a
 * version field.
 */
@MappedSuperclass
public class BaseEntity implements Serializable {

	private static final String MSG_ENTITY_TOSTRING = "{0} [id={1,number,#}]";
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	@Column(name = "version", nullable = false)
	private Long version;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 79 * hash + Objects.hashCode(this.id);
		hash = 79 * hash + Objects.hashCode(this.version);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final BaseEntity other = (BaseEntity) obj;
		if (!Objects.equals(this.id, other.id)) {
			return false;
		}
		if (!Objects.equals(this.version, other.version)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return MessageFormat.format(MSG_ENTITY_TOSTRING, this.getClass()
				.getSimpleName(), this.id);
	}
}
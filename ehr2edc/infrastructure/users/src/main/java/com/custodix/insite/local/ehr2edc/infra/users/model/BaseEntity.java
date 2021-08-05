package com.custodix.insite.local.ehr2edc.infra.users.model;

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
	@Column(name = "version",
			nullable = false)
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
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final BaseEntity that = (BaseEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(version, that.version);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, version);
	}

	@Override
	public String toString() {
		return MessageFormat.format(MSG_ENTITY_TOSTRING, this.getClass()
				.getSimpleName(), this.id);
	}
}
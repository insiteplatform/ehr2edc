package com.custodix.insite.local.user.infra.jpa;

import java.util.Date;

import javax.persistence.*;

import com.custodix.insite.local.user.domain.AuthenticationAttemptResult;
import com.custodix.insite.local.user.vocabulary.Email;

@Entity
@Table(name = "authentication_attempt")
public class AuthenticationAttemptJpaEntity {
	@Id
	@GeneratedValue
	private Long id;
	@Column(nullable = false)
	@Convert(converter = EmailConverter.class)
	private Email email;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private AuthenticationAttemptResult result;
	@Column(name = "attempt_timestamp",
			nullable = false)
	private Date timestamp;

	protected AuthenticationAttemptJpaEntity() {

	}

	private AuthenticationAttemptJpaEntity(Builder builder) {
		id = builder.id;
		email = builder.email;
		result = builder.result;
		timestamp = builder.timestamp;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public Long getId() {
		return id;
	}

	public Email getEmail() {
		return email;
	}

	public AuthenticationAttemptResult getResult() {
		return result;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public static final class Builder {
		private Long id;
		private Email email;
		private AuthenticationAttemptResult result;
		private Date timestamp;

		private Builder() {
		}

		public Builder withId(Long id) {
			this.id = id;
			return this;
		}

		public Builder withEmail(Email email) {
			this.email = email;
			return this;
		}

		public Builder withResult(AuthenticationAttemptResult result) {
			this.result = result;
			return this;
		}

		public Builder withTimestamp(Date timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public AuthenticationAttemptJpaEntity build() {
			return new AuthenticationAttemptJpaEntity(this);
		}
	}
}

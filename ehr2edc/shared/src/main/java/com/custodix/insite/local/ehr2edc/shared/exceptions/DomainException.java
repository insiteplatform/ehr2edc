package com.custodix.insite.local.ehr2edc.shared.exceptions;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DomainException extends RuntimeException implements Message {
	private final String key;
	private final transient Object[] parameters;

	private DomainException(String key, Object... parameters) {
		this.key = key;
		this.parameters = parameters;
	}

	/**
	 *
	 * @deprecated Use {@link #of(String, Object...)} ()} instead
	 */
	@Deprecated
	public DomainException(String message) {
		super(message);
		key = null;
		parameters = new Object[0];
	}

	/**
	 *
	 * @deprecated Use {@link #DomainException(String, Object...)} ()} instead
	 */
	@Deprecated
	public static DomainException getInstance(Type type, Object object) {
		return new DomainException(Type.getMessage(type, object));
	}

	public static DomainException of(String key, Object... parameters) {
		return new DomainException(key, parameters);
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public Object[] getParameters() {
		return parameters;
	}

	public enum Type {
		EXISTS("%s already exists"),
		NOT_EXISTS("Unknown %s"),
		INVALID("Invalid %s"),
		NOT_NULL("%s can not be null");

		private String messageTemplate;

		Type(String messageTemplate) {
			this.messageTemplate = messageTemplate;
		}

		public static String getMessage(Type type, Object obj) {
			return String.format(type.messageTemplate,
					ReflectionToStringBuilder.toString(obj, ToStringStyle.SHORT_PREFIX_STYLE));
		}
	}
}

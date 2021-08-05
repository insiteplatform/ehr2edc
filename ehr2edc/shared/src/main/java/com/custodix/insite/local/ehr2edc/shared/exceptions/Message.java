package com.custodix.insite.local.ehr2edc.shared.exceptions;

public interface Message {
	String getKey();

	default Object[] getParameters(){
		return new Object[0];
	}
}

package eu.ehr4cr.workbench.local.security.sts;

import org.w3c.dom.Element;

public interface STSTokenProvider {
	Element getSecurityToken(String audience) throws Exception;
}

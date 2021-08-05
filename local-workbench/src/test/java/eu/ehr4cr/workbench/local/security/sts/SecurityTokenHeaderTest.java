package eu.ehr4cr.workbench.local.security.sts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.w3c.dom.Element;

@RunWith(MockitoJUnitRunner.class)
public class SecurityTokenHeaderTest {
	private static final String NAME = "x-securitytoken";
	private static final String VALUE_PREFIX = "SAML auth=eJzlW";
	private static final String URL = "http://sts-secured-url.custodix.com";

	@Mock
	private STSTokenProvider stsTokenProvider;
	private SecurityTokenHeader securityTokenHeader;

	@Before
	public void before() throws Exception {
		securityTokenHeader = new SecurityTokenHeader(stsTokenProvider, URL);
		Element securityToken = new TestSTSTokenProvider().getSecurityToken(URL);
		when(stsTokenProvider.getSecurityToken(eq(URL))).thenReturn(securityToken);
	}

	@Test
	public void getName() {
		assertThat(securityTokenHeader.getName()).isEqualTo(NAME);
	}

	@Test
	public void getValue() {
		String value = securityTokenHeader.getValue();
		assertThat(value).startsWith(VALUE_PREFIX);
	}
}

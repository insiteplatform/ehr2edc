package eu.ehr4cr.workbench.local.security.sts;

import java.io.InputStream;

import org.opensaml.core.config.InitializationService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.xml.BasicParserPool;

public class TestSTSTokenProvider implements STSTokenProvider {
	private static final String SAML_ASSERTION_PATH = "/ws/samlassertion.xml";

	@Override
	public Element getSecurityToken(String audience) throws Exception {
		try {
			InitializationService.initialize();
			InputStream in = TestSTSTokenProvider.class.getResourceAsStream(SAML_ASSERTION_PATH);
			BasicParserPool parser = new BasicParserPool();
			parser.initialize();
			Document doc = parser.parse(in);
			return doc.getDocumentElement();
		} catch (Exception e) {
			throw new IllegalStateException("Failed loading test SAML assertion");
		}
	}

}

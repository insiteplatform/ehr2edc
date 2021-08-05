package eu.ehr4cr.workbench.local.security.sts;

import eu.ehr4cr.workbench.local.exception.SystemException;
import eu.ehr4cr.workbench.local.web.HTTPHeader;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.zip.Deflater;

public class SecurityTokenHeader implements HTTPHeader {
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityTokenHeader.class);
	private static final String NAME = "x-securitytoken";
	private final STSTokenProvider stsTokenProvider;
	private final String audience;

	public SecurityTokenHeader(STSTokenProvider stsTokenProvider, String audience) {
		this.stsTokenProvider = stsTokenProvider;
		this.audience = audience;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getValue() {
		Element securityToken = getSecurityToken();
		return createHeaderValueFromToken(securityToken);
	}

	private Element getSecurityToken() {
		try {
			return stsTokenProvider.getSecurityToken(audience);
		} catch (Exception e) {
			throw new SystemException("Failed getting security token", e);
		}
	}

	private String createHeaderValueFromToken(Element token) {
		try {
			return doCreateHeaderValueFromToken(token);
		} catch (TransformerException e) {
			throw new SystemException("Could not create header value from token", e);
		}
	}

	private String doCreateHeaderValueFromToken(Element token) throws TransformerException {
		String tokenString = convertElement(token);
		LOGGER.debug("Creating header value from security token: {}", tokenString);
		return createHttpAuthzHeaderValue(tokenString);
	}

	private static String convertElement(Element el) throws TransformerException {
		StringWriter buffer = new StringWriter();
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		transformer.setOutputProperty("omit-xml-declaration", "yes");
		transformer.transform(new DOMSource(el), new StreamResult(buffer));
		return buffer.toString();
	}

	private static String createHttpAuthzHeaderValue(String samlToken) {
		try {
			byte[] compressed = compress(samlToken.getBytes("UTF-8"));
			String encoded = new String(Base64.encodeBase64(compressed));
			return "SAML auth=" + encoded;
		} catch (UnsupportedEncodingException var3) {
			throw new RuntimeException("Unsupported Encoding: UTF-8");
		}
	}

	private static byte[] compress(byte[] blob) {
		Deflater compresser = new Deflater();
		compresser.setInput(blob);
		compresser.finish();
		byte[] compressed = new byte[10000];
		int total = compresser.deflate(compressed);
		compresser.end();
		return ArrayUtils.subarray(compressed, 0, total);
	}
}

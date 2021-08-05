package eu.ehr4cr.workbench.local.security.rsa;

import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Optional;

import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.RSAKey;

import eu.ehr4cr.workbench.local.exception.feasibility.DomainException;

public class RsaKeyResolver {
	private final ObjectMapper objectMapper;
	private final Resource keystore;

	public RsaKeyResolver(ObjectMapper objectMapper, Resource keystore) {
		this.objectMapper = objectMapper;
		this.keystore = keystore;
	}

	public RSAKey resolve(String keyId) {
		try {
			JsonNode keystoreNode = readKeystore();
			System.out.println("resolve found keystoreNode " + keystoreNode);
			String key = findKey(keystoreNode, keyId).orElseThrow(this::keyNotFoundException);
			return RSAKey.parse(key);
		} catch (IOException | ParseException e) {
			throw new DomainException(String.format("Could not resolve the rsa key for %s", keyId), e);
		}
	}

	private JsonNode readKeystore() throws IOException {
		return objectMapper.readTree(keystore.getInputStream());
	}

	private DomainException keyNotFoundException() {
		return new DomainException("Could not retrieve key information");
	}

	private Optional<String> findKey(JsonNode keystore, String keyId) {
		Iterator<JsonNode> nodeIterator = keystore.withArray("keys").iterator();
		JsonNode key = null;
		while (nodeIterator.hasNext() && key == null) {
			JsonNode current = nodeIterator.next();
			if (keyIdMatches(keyId, current)) {
				key = current;
			}
		}
		return Optional.ofNullable(key)
				.map(JsonNode::toString);
	}

	private boolean keyIdMatches(String keyId, JsonNode keyJson) {
		return keyId.equals(keyJson.get("kid")
				.asText());
	}
}
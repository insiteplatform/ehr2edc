package com.custodix.insite.local.ehr2edc.security.oidc;

import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Optional;

import org.springframework.core.io.Resource;

import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.RSAKey;

class EHR2EDCRsaKeyResolver {
	private final ObjectMapper objectMapper;
	private final Resource keystore;

	EHR2EDCRsaKeyResolver(ObjectMapper objectMapper, Resource keystore) {
		this.objectMapper = objectMapper;
		this.keystore = keystore;
	}

	RSAKey resolve(String keyId) {
		try {
			JsonNode keystoreNode = readKeystore();
			String key = findKey(keystoreNode, keyId).orElseThrow(this::keyNotFoundException);
			return RSAKey.parse(key);
		} catch (IOException | ParseException e) {
			throw new SystemException(String.format("Could not resolve the rsa key for %s", keyId));
		}
	}

	private JsonNode readKeystore() throws IOException {
		return objectMapper.readTree(keystore.getInputStream());
	}

	private DomainException keyNotFoundException() {
		return new DomainException("Could not retrieve key information");
	}

	private Optional<String> findKey(JsonNode keystore, String keyId) {
		Iterator<JsonNode> nodeIterator = keystore.withArray("keys")
				.iterator();
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
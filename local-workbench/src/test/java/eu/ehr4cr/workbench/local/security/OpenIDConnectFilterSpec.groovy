package eu.ehr4cr.workbench.local.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.ImmutableMap
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.PlainHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.util.Base64URL
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import com.nimbusds.jwt.SignedJWT
import eu.ehr4cr.workbench.local.dao.SecurityDao
import eu.ehr4cr.workbench.local.exception.feasibility.DomainException
import eu.ehr4cr.workbench.local.security.rsa.RsaKeyResolver
import eu.ehr4cr.workbench.local.service.DomainTime
import eu.ehr4cr.workbench.local.service.TestTimeService
import org.mockito.Mockito
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.text.ParseException

import static com.nimbusds.jose.JWSAlgorithm.RS256
import static eu.ehr4cr.workbench.local.model.security.UserObjectMother.aDefaultUser
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class OpenIDConnectFilterSpec extends Specification {

    private static final String DEFAULT_URL = "/"
    private static final ObjectMapper OBJECTMAPPER = new ObjectMapper()
    private static final Resource KEYSTORE = new ClassPathResource("/keystore.jwks")
    private static final String RSA_KEY_FORMAT = "{\n" +
            "      \"alg\": \"RS256\",\n" +
            "      \"d\": \"PvBAngE3kkTnD3yDKo3wCvHJHm20kb9a0FVGLd0s2Y0E_3H2XnZC8-2zPhN6AQTjPhohSDCew20gzm76lyOvMqRiUP2Zpaopa1d2fGvNIQSdM07yKa6EivEYxqPQxa5esoZnexgnb9fom70I8n5OQRNQikwu-az26CsHX2zWMRodzSdN5CXHvb1PV09DmH8azTYwoMElPIqmcTfxiRw2Ov5ucmXXngKRFJgvfUgKd7v4ScBX7sQoQEjWEtt7ta0WvL3Ar5E1RAW4aHxuubZ6AtloxWCf17AAKw03dfP5RDm5TDmgm2B635ecJ7fTvneFmg8W_fdMTPRfBlCGNBp3wQ\",\n" +
            "      \"e\": \"AQAB\",\n" +
            "      \"n\": \"qt6yOiI_wCoCVlGO0MySsez0VkSqhPvDl3rfabOslx35mYEO-n4ABfIT5Gn2zN-CeIcOZ5ugAXvIIRWv5H55-tzjFazi5IKkOIMCiz5__MtsdxKCqGlZu2zt-BLpqTOAPiflNPpM3RUAlxKAhnYEqNha6-allPnFQupnW_eTYoyuzuedT7dSp90ry0ZcQDimntXWeaSbrYKCj9Rr9W1jn2uTowUuXaScKXTCjAmJVnsD75JNzQfa8DweklTyWQF-Y5Ky039I0VIu-0CIGhXY48GAFe2EFb8VpNhf07DP63p138RWQ1d3KPEM9mYJVpQC68j3wzDQYSljpLf9by7TGw\",\n" +
            "      \"kty\": \"RSA\",\n" +
            "      \"kid\": \"%s\"\n" +
            "    }"

    private SecurityDao securityDao
    private OAuth2RestTemplate restTemplate
    private OpenIDConnectFilter openIDConnectFilter

    void setup() {
        DomainTime.setTime(new TestTimeService())
        securityDao = mock(SecurityDao)
        def user = aDefaultUser()
        user.setId(254L)
        when(securityDao.getActiveUserByEmail(Mockito.any())).thenReturn(user)

        restTemplate = mock(OAuth2RestTemplate)
        RsaKeyResolver rsaKeyResolver = new RsaKeyResolver(OBJECTMAPPER, KEYSTORE);
        def userAuthFactory = new UserAuthenticationFactory(securityDao)
        openIDConnectFilter = new OpenIDConnectFilter(DEFAULT_URL, restTemplate, userAuthFactory, rsaKeyResolver)
    }

    def "Validating a correct token" () {
        given: "A valid encryption key in the keystore under kid 'insite-oidc-rsa1'"
        RSAKey key = makeRsaKey("insite-oidc-rsa1")
        and: "I sign a token with my private key"
        when(restTemplate.getAccessToken()).thenReturn(makeSignedToken("insite-oidc-rsa1", key))

        when: "I validate the token"
        def token = openIDConnectFilter.attemptAuthentication(mock(HttpServletRequest), mock(HttpServletResponse))

        then: "The validation succeeds"
        noExceptionThrown()
        and: "The token contains a valid user"
        token.authenticated
        token.name == "username"
        token.authorities.empty
        token.credentials == ""
    }

    def "Validating an incorrect token" () {
        given: "A valid encryption key in the keystore under kid 'insite-oidc-rsa1'"
        RSAKey key = makeRsaKey("insite-oidc-rsa1")
        and: "I sign a token with my private key, but the signature gets changed during the transmission"
        when(restTemplate.getAccessToken()).thenReturn(makeInvalidSignedToken("insite-oidc-rsa1", key))

        when: "I validate the token"
        openIDConnectFilter.attemptAuthentication(mock(HttpServletRequest), mock(HttpServletResponse))

        then: "The validation fails"
        BadCredentialsException exception = thrown(BadCredentialsException)
        exception.message == "Invalid token received"
    }

    def "Validating a correct token from an unknown key id" () {
        given: "A valid encryption key in the keystore under kid 'unknown'"
        RSAKey key = makeRsaKey("unknown")
        and: "I sign a token with my private key"
        when(restTemplate.getAccessToken()).thenReturn(makeSignedToken("unknown", key))

        when: "I validate the token"
        openIDConnectFilter.attemptAuthentication(mock(HttpServletRequest), mock(HttpServletResponse))

        then: "The validation fails"
        DomainException exception = thrown(DomainException)
        exception.message == "Could not retrieve key information"
    }

    def "Validating an unsigned token" () {
        given: "A token that is not signed"
        when(restTemplate.getAccessToken()).thenReturn(makeUnsignedToken())

        when: "I validate the token"
        openIDConnectFilter.attemptAuthentication(mock(HttpServletRequest), mock(HttpServletResponse))

        then: "The validation fails"
        BadCredentialsException exception = thrown(BadCredentialsException)
        exception.message == "Could not obtain user details from token"
    }

    def "Handling a failure to retrieve the token" () {
        given: "An exception occurs when retrieving the token"
        when(restTemplate.getAccessToken()).thenThrow(OAuth2Exception.class)

        when: "I validate the token"
        openIDConnectFilter.attemptAuthentication(mock(HttpServletRequest), mock(HttpServletResponse))

        then: "The validation fails"
        BadCredentialsException exception = thrown(BadCredentialsException)
        exception.message == "Could not obtain access token"
    }

    OAuth2AccessToken makeSignedToken(String keyId, RSAKey key) {
        def header = new JWSHeader.Builder(RS256)
                .keyID(keyId)
                .build()
        SignedJWT signedJWT = new SignedJWT(header, JWTClaimsSet.parse("{}"))
        signedJWT.sign(new RSASSASigner(key))
        def token = new DefaultOAuth2AccessToken(signedJWT.serialize())
        token.additionalInformation = ImmutableMap.of("id_token", makeUserIdToken())
        return token
    }

    OAuth2AccessToken makeInvalidSignedToken(String keyId, RSAKey key) {
        def header = new JWSHeader.Builder(RS256)
                .keyID(keyId)
                .build()
        SignedJWT signedJWT = new SignedJWT(header, JWTClaimsSet.parse("{}"))
        signedJWT.sign(new RSASSASigner(key))
        ReflectionTestUtils.setField(signedJWT, "signature", new Base64URL("invalid-value"))
        def token = new DefaultOAuth2AccessToken(signedJWT.serialize())
        token.additionalInformation = ImmutableMap.of("id_token", makeUserIdToken())
        return token
    }

    OAuth2AccessToken makeUnsignedToken() {
        PlainJWT jWT = new PlainJWT(new PlainHeader.Builder().build(), JWTClaimsSet.parse("{}"))
        def token = new DefaultOAuth2AccessToken(jWT.serialize())
        token.additionalInformation = ImmutableMap.of("id_token", makeUserIdToken())
        return token
    }

    String makeUserIdToken() {
        JWT jwt = new PlainJWT(JWTClaimsSet.parse("{ \"subject\": \"test@custodix.com\"}"))
        return jwt.serialize()
    }

    def makeRsaKey(String keyId) throws IOException, ParseException {
        String keyJson = String.format(RSA_KEY_FORMAT, keyId)
        return RSAKey.parse(keyJson)
    }
}
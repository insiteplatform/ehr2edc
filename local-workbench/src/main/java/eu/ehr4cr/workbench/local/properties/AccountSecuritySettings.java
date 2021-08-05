package eu.ehr4cr.workbench.local.properties;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import eu.ehr4cr.workbench.local.WebRoutes;

@Component
public class AccountSecuritySettings {
	private final PropertyProvider propertyProvider;
	private final Integer inviteExpireValue;
	private final TimeUnit inviteExpireUnit;

	@Autowired
	public AccountSecuritySettings(PropertyProvider propertyProvider,
			@Value("${user.invite.expireValue:7}") Integer inviteExpireValue,
			@Value("${user.invite.expireUnit:DAYS}") TimeUnit inviteExpireUnit) {
		this.propertyProvider = propertyProvider;
		this.inviteExpireValue = inviteExpireValue;
		this.inviteExpireUnit = inviteExpireUnit;
	}

	public Integer getInviteExpireValue() {
		return inviteExpireValue;
	}

	public TimeUnit getInviteExpireUnit() {
		return inviteExpireUnit;
	}

	public String getInvitationUri(Long userId, String password) {
		return UriComponentsBuilder.fromHttpUrl(propertyProvider.getBaseUrl() + WebRoutes.completeInvitation)
				.queryParam("userId", userId)
				.queryParam("password", password)
				.build()
				.toString();
	}

	public String getRecoveryUri(Long userId, String password) {
		return UriComponentsBuilder.fromHttpUrl(propertyProvider.getBaseUrl() + WebRoutes.completeRecovery)
				.queryParam("userId", userId)
				.queryParam("password", password)
				.build()
				.toString();
	}
}

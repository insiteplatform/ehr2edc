package com.custodix.workbench.local.mail;

import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.service.email.MailService;
import eu.ehr4cr.workbench.local.service.email.model.ImminentlyExpiringPasswordMailContent;
import eu.ehr4cr.workbench.local.service.email.model.ImmutableUserAcceptMailContent;
import eu.ehr4cr.workbench.local.service.email.model.ImmutableUserInviteMailContent;
import eu.ehr4cr.workbench.local.service.email.model.ImmutableUserRecoverMailContent;
import eu.ehr4cr.workbench.local.service.email.model.ImmutableUserRegistrationMailContent;
import eu.ehr4cr.workbench.local.service.email.model.UserAcceptMailContent;
import eu.ehr4cr.workbench.local.service.email.model.UserInviteMailContent;
import eu.ehr4cr.workbench.local.service.email.model.UserRecoverMailContent;
import eu.ehr4cr.workbench.local.service.email.model.UserRegistrationMailContent;
import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.custodix.workbench.local.mail.MailServiceImplTestConfiguration.ADMIN_ADDRESS;
import static com.custodix.workbench.local.mail.MailServiceImplTestConfiguration.SUPPORT_MAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MailServiceImplTestConfiguration.class)
@TestPropertySource(properties = "support.mail=support@devnull.com")
public class MailServiceImplTest {
	private static final String USER_ADDRESS = "user@devnull.com";
	private static final String USER_NAME = "Jane Doe";

	@Autowired
	private JavaMailSender javaMailSender;
	@Autowired
	private MailService mailService;

	@Before
	public void init() {
		resetJavaMailSenderSpy();
	}

	@Test
	public void sendUserInviteMail() throws Exception {
		UserInviteMailContent content = ImmutableUserInviteMailContent.builder()
				.inviteExpireValue(7)
				.inviteExpireUnit(TimeUnit.DAYS)
				.inviteAcceptUrl("http://test.domain.com/accept")
				.build();
		mailService.sendMail(content, USER_ADDRESS);
		validateUserInviteMail();
	}

	@Test
	public void sendUserRecoverMail() throws Exception {
		UserRecoverMailContent content = ImmutableUserRecoverMailContent.builder()
				.recoverExpireValue(7)
				.recoverExpireUnit(TimeUnit.DAYS)
				.recoverAcceptUrl("http://test.domain.com/accept")
				.build();
		mailService.sendMail(content, USER_ADDRESS);
		validateUserRecoverMail();
	}

	@Test
	public void sendUserAcceptMail() throws Exception {
		UserAcceptMailContent content = ImmutableUserAcceptMailContent.builder()
				.acceptExpireValue(7)
				.acceptExpireUnit(TimeUnit.DAYS)
				.acceptAcceptUrl("http://test.domain.com/accept")
				.build();
		mailService.sendMail(content, USER_ADDRESS);
		validateUserAcceptMail();
	}

	@Test
	public void sendUserRegistrationMail() throws Exception {
		UserRegistrationMailContent content = ImmutableUserRegistrationMailContent.builder()
				.username(USER_NAME)
				.acceptUrl("http://test.domain.com/members")
				.build();
		mailService.sendMail(content, GroupType.ADM);
		validateUserRegistrationMail();
	}

	@Test
	public void sendImminentlyExpiringPasswordMail() throws Exception {
		ImminentlyExpiringPasswordMailContent content = ImminentlyExpiringPasswordMailContent.newBuilder()
				.withExpirationDate(createTestDate())
				.withChangePasswordUrl("http://test.domain.com/account")
				.build();
		mailService.sendMail(content, USER_ADDRESS);
		validateImminentlyExpiringPasswordMail();
	}

	private void validateUserInviteMail() throws Exception {
		MimeMessageParser message = verifyMailSenderCall();
		validateMailContent(message, "userInviteMail.html");
		validateMetaAttributes(message, USER_ADDRESS);
	}

	private void validateUserRecoverMail() throws Exception {
		MimeMessageParser message = verifyMailSenderCall();
		validateMailContent(message, "userRecoverMail.html");
		validateMetaAttributes(message, USER_ADDRESS);
	}

	private void validateUserAcceptMail() throws Exception {
		MimeMessageParser message = verifyMailSenderCall();
		validateMailContent(message, "userAcceptMail.html");
		validateMetaAttributes(message, USER_ADDRESS);
	}

	private void validateUserRegistrationMail() throws Exception {
		MimeMessageParser message = verifyMailSenderCall();
		validateMailContent(message, "userRegistrationMail.html");
		validateMetaAttributes(message, ADMIN_ADDRESS);
	}

	private void validateImminentlyExpiringPasswordMail() throws Exception {
		MimeMessageParser message = verifyMailSenderCall();
		validateMailContent(message, "imminentlyExpiringPasswordMail.html");
		validateMetaAttributes(message, USER_ADDRESS);
	}

	private void validateMetaAttributes(MimeMessageParser message, String expectedRecipientAddress) throws Exception {
		assertThat(message.getFrom()).isEqualTo(SUPPORT_MAIL);
		assertThat(message.getTo()).isEmpty();
		assertThat(message.getCc()).isEmpty();
		assertThat(message.getBcc()).hasSize(1);
		assertThat(message.getBcc()
				.get(0)
				.toString()).isEqualTo(expectedRecipientAddress);
	}

	private void validateMailContent(MimeMessageParser message, String expectedMailLocation) throws IOException {
		String htmlContent = message.getHtmlContent();
		String expectedContent = getExpectedContent(expectedMailLocation);
		assertEquals("Mail content incorrect", expectedContent, htmlContent);
	}

	private MimeMessageParser verifyMailSenderCall() throws Exception {
		ArgumentCaptor<MimeMessage> arg = ArgumentCaptor.forClass(MimeMessage.class);
		verify(javaMailSender).send(arg.capture());
		MimeMessageParser parser = new MimeMessageParser(arg.getValue());
		parser.parse();
		return parser;
	}

	private String getExpectedContent(String template) throws IOException {
		String content = IOUtils.toString(this.getClass()
				.getResourceAsStream(template), "UTF-8");
		return content.replace("[currentYear]", Year.now()
				.toString());
	}

	private Date createTestDate() throws ParseException {
		String date = "2018-09-07 11:31:09";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return simpleDateFormat.parse(date);
	}

	private void resetJavaMailSenderSpy() {
		reset(javaMailSender);
		doNothing().when(javaMailSender)
				.send(any(MimeMessage.class));
	}

}
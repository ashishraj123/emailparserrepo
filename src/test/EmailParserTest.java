package test;

import javax.mail.Session;

import main.java.parser.coreParser.DaoConstants;
import main.java.parser.coreParser.EmailParser;
import main.java.parser.coreParser.SendEmail;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class EmailParserTest {

	@Test(expected = RuntimeException.class)
	@Ignore
	public final void whenNoEmailIdPassed() {
		SendEmail sendEmail = new SendEmail();
		sendEmail.sendParsedEmail("ashishrajauria2001@gmail.com",
				"ashishrich123", "", "hi...");
	}

	@Test
	@Ignore
	public void getEmailTextTest() {
		EmailParser emailParser = new EmailParser();
		Assert.assertEquals("email text", emailParser.getEmailText());
	}
	
	@Test
	public void testServerPropertiesAndSession() {
		EmailParser emailParser = new EmailParser();
		Assert.assertEquals(Session.class, emailParser
				.setServerPropertiesAndsession(DaoConstants.HOST,
						DaoConstants.PORT));
	}
}

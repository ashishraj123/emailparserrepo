package main.java.parser.coreParser;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

import main.java.interfaces.IEmailSender;

/**
 * sends e-mail messages containing to the particular emailID
 */
public class SendEmail implements IEmailSender{
	
	/**
	 * sends e-mail messages containing to the particular emailID
	 */
	@Override
	public void sendParsedEmail(final String fromEmail, final String pass, final String toEmail,
			final String emailText) {

		 String emailSubject = "Parsed Email Language Response";
		// Recipient's email ID needs to be mentioned.
		 String host = "smtp.gmail.com";
		final Session session = getMailConfigurationProperties(fromEmail, pass, host);
		try {
			final MimeMessage message = new MimeMessage(session);

			message.setFrom(new InternetAddress(fromEmail));

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					toEmail));
			message.setSubject(emailSubject);
			message.setText(emailText);
			final Transport transport = session.getTransport("smtps");
			transport.connect(host, fromEmail, pass);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();

		} catch (MessagingException messageException) {
			messageException.printStackTrace();
		}
	}
	
	@Override
	public Session getMailConfigurationProperties(final String from,final String pass,
			final String host) {
		// Get system properties
		final Properties properties = System.getProperties();
		// Setup mail server
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.user", from);
		properties.put("mail.smtp.password", pass);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.auth", "true");

		// Get the default Session object.
		final Session session = Session.getDefaultInstance(properties);
		return session;
	}
}
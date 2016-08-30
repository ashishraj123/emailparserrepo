package parser;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class SendEmail {
	public void sendParsedEmail(String fromEmail, String pass,
			String to, String emailText) {

		String emailSubject = "Parsed Email Language Response";
		// Recipient's email ID needs to be mentioned.
		String host = "smtp.gmail.com";
		Session session = getMailConfigurationProperties(fromEmail, pass, host);
		try {
			MimeMessage message = new MimeMessage(session);

			message.setFrom(new InternetAddress(fromEmail));

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));
			message.setSubject(emailSubject);
			message.setText(emailText);
			Transport transport = session.getTransport("smtps");
			transport.connect(host, fromEmail, pass);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();

		} catch (MessagingException messageException) {
			messageException.printStackTrace();
		}
	}

	private Session getMailConfigurationProperties(String from,
			String pass, String host) {
		// Get system properties
		Properties properties = System.getProperties();
		// Setup mail server
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.user", from);
		properties.put("mail.smtp.password", pass);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.auth", "true");

		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties);
		return session;
	}
}
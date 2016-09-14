package main.java.interfaces;

import javax.mail.Session;

/**
 * interface to send emails
 */
public interface IEmailSender {
	/**
	 * interface method to send emails
	 */
	public void sendParsedEmail(String fromEmail, String pass, String toEmail,String emailText);
	/**
	 * gets mail server config
	 */
	public Session getMailConfigurationProperties(String from, String pass,String host);
}

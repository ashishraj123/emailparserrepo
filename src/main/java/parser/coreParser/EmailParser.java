package main.java.parser.coreParser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import main.java.interfaces.IEmailParser;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

/**
 * Parses e-mail messages containing the specified keyword in Subject
 * field.
 */
public class EmailParser implements IEmailParser {

	private static final Logger logger = Logger
			.getLogger(EmailParser.class);
	/**
	 * Searches for e-mail messages containing the specified keyword in Subject
	 * field.
	 */
	@Override
	public Map<String, Integer> parseEmail(final String host, final String port,
			final String userName, final String password) {

		final Map<String, Integer> languageCountMap = new HashMap<String, Integer>();
		try {
			final Session session = setServerPropertiesAndsession(host, port);

			// connects to the message store
			final Store store = session.getStore("imap");
			store.connect(userName, password);
			// opens the inbox folder
			final Folder folderInbox = store.getFolder("INBOX");
			folderInbox.open(Folder.READ_ONLY);
			final Detector detector = createLanguageDetectorFactory();
			final Message[] messages = folderInbox.getMessages();
			for (Message message : messages) {
				 String messageBody = null;
				final MimeMultipart mmp = (MimeMultipart) message.getContent();
				for (int i = 0; i < mmp.getCount(); i++) {
					final BodyPart mbp = mmp.getBodyPart(i);
					messageBody = mbp.getContent() + "";
				}
				detector.append(messageBody);
				final String language = detector.detect();
				setLanguageCountMap(languageCountMap, detector, language);
			}
			// disconnect
			folderInbox.close(false);
			store.close();
		} catch (MessagingException ex) {
			logger.error("exceptionoccured while parsing email");
		} catch (LangDetectException e) {
			logger.error("exceptionoccured while parsing email");
		} catch (IOException e) {
			logger.error("exceptionoccured while parsing email");
		}
		return languageCountMap;
	}
	
	/**
     * This Method is used to get the email text
     * @return String
     */
	@Override
	public String getEmailText() {
		final StringBuilder stringBuilder = new StringBuilder();
		final Map<String, Integer> parsedCountMap = parseEmail(DaoConstants.HOST,
				DaoConstants.PORT, DaoConstants.USER_NAME,
				DaoConstants.PASSWORD);
		for (final String keyLanguage : parsedCountMap.keySet()) {
			stringBuilder.append(keyLanguage + "---"
					+ parsedCountMap.get(keyLanguage) + " ");
		}
		return stringBuilder.toString();

	}

	private void setLanguageCountMap(final Map<String, Integer> languageCountMap,
			final Detector detector, final String language) throws LangDetectException {
		if (languageCountMap.containsKey(language)) {
			int langCount = languageCountMap.get(detector.detect());
			langCount = langCount + 1;
			languageCountMap.put(detector.detect(), langCount);
		} else {
			languageCountMap.put(detector.detect(), 1);
		}
	}

	private Detector createLanguageDetectorFactory() throws LangDetectException {
		System.getProperty("user.dir");
		final File file = new File(System.getProperty("user.dir")
				+ "/src/main/java/parser/profiles");
		DetectorFactory.loadProfile(file);
		final Detector detector = DetectorFactory.create();
		return detector;

	}

	public Session setServerPropertiesAndsession(final String host, final String port) {
		final Properties properties = new Properties();
		// server setting
		properties.put("mail.imap.host", host);
		properties.put("mail.imap.port", port);

		// SSL setting
		properties.setProperty("mail.imap.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.imap.socketFactory.fallback", "false");
		properties.setProperty("mail.imap.socketFactory.port",
				String.valueOf(port));

		final Session session = Session.getDefaultInstance(properties);
		return session;
	}

	/**
	 * Test this program with a Gmail's account
	 */
	public static void main(final String[] args) {
		final String userName = DaoConstants.USER_NAME;
		final String password = DaoConstants.PASSWORD;
		final EmailParser searcher = new EmailParser();
		final String emailText = searcher.getEmailText();
		final SendEmail sendEmail = new SendEmail();
		sendEmail.sendParsedEmail(userName, password, DaoConstants.TO_EMAIL,
				emailText);
	}

}
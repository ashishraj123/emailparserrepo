package parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

public class EmailParser {

	/**
	 * Searches for e-mail messages containing the specified keyword in Subject
	 * field.
	 * 
	 * @param host
	 * @param port
	 * @param userName
	 * @param password
	 * @param keyword
	 */
	private Map<String, Integer> ParseEmail(String host, String port,
			String userName, String password) {

		Map<String, Integer> languageCountMap = new HashMap<String, Integer>();
		try {
			Session session = setServerPropertiesAndsession(host, port);

			// connects to the message store
			Store store = session.getStore("imap");
			store.connect(userName, password);
			// opens the inbox folder
			Folder folderInbox = store.getFolder("INBOX");
			folderInbox.open(Folder.READ_ONLY);
			Detector detector = createLanguageDetectorFactory();
			Message[] messages = folderInbox.getMessages();
			for (Message message : messages) {
				String messageBody = null;
				MimeMultipart mmp = (MimeMultipart) message.getContent();
				for (int i = 0; i < mmp.getCount(); i++) {
					BodyPart mbp = mmp.getBodyPart(i);
					messageBody = mbp.getContent() + "";
				}
				detector.append(messageBody);
				String language = detector.detect();
				setLanguageCountMap(languageCountMap, detector, language);
			}
			// disconnect
			folderInbox.close(false);
			store.close();
		} catch (MessagingException ex) {
			System.out.println("Could not connect to the message store.");
			ex.printStackTrace();
		} catch (LangDetectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return languageCountMap;
	}
	
	public String getEmailText(){
		StringBuilder stringBuilder=new StringBuilder();
		Map<String, Integer> parsedCountMap = ParseEmail(DaoConstants.HOST,
				DaoConstants.PORT, DaoConstants.USER_NAME,
				DaoConstants.PASSWORD);
		for(String keyLanguage:parsedCountMap.keySet()){
			stringBuilder.append(keyLanguage+"---"+parsedCountMap.get(keyLanguage)+" ");
		}
		return stringBuilder.toString();
		
	}

	private void setLanguageCountMap(Map<String, Integer> languageCountMap,
			Detector detector, String language) throws LangDetectException {
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
		File file = new File(System.getProperty("user.dir")+"/src/parser/profiles");
		DetectorFactory.loadProfile(file);
		Detector detector = DetectorFactory.create();
		return detector;
		
	}

	private Session setServerPropertiesAndsession(String host, String port) {
		Properties properties = new Properties();
		// server setting
		properties.put("mail.imap.host", host);
		properties.put("mail.imap.port", port);

		// SSL setting
		properties.setProperty("mail.imap.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.imap.socketFactory.fallback", "false");
		properties.setProperty("mail.imap.socketFactory.port",
				String.valueOf(port));

		Session session = Session.getDefaultInstance(properties);
		return session;
	}

	/**
	 * Test this program with a Gmail's account
	 */
	public static void main(String[] args) {
		String host = DaoConstants.HOST;
		String port = DaoConstants.PORT;
		String userName = DaoConstants.USER_NAME;
		String password = DaoConstants.PASSWORD;
		EmailParser searcher = new EmailParser();
		String emailText = searcher.getEmailText();
		SendEmail sendEmail=new SendEmail();
		sendEmail.sendParsedEmail(userName, password,
				DaoConstants.TO_EMAIL, emailText);
	}

}
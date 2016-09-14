package main.java.interfaces;

import java.util.Map;

/**used to parse the incoming emails
 */
public interface IEmailParser {
	
	/**
     * This Method is used to set the connection and parse the emails
     * @return Map(query,output)
     */
	public Map<String, Integer> parseEmail(String host, String port,String userName, String password);
	
	/**
     * This Method is used to get the email text
     * @return String
     */
	public String getEmailText();
	
	
}

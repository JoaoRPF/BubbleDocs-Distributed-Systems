package id.cli;

import id.cli.crypto.PasswordEncryption;
import id.cli.crypto.RequestXml;
import id.cli.crypto.SecureRandomNumber;

import java.io.ByteArrayOutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class Login {
	private String userId=null;
	private byte[] password = null;
	private byte[] encryptedPassword;
	private String randomNumber = null;
	
	public Login(String userId, byte[] password) throws Exception{
		this.setUserId(userId);
		this.setPassword(password);
		PasswordEncryption passwordEncryption = new PasswordEncryption(password);
		setEncryptedPassword(passwordEncryption.getDigestPassword());
		
	}
	
	public String getRandomNumber(){
		return this.randomNumber;
	}
	
	public void setRandomNumber(String rn){
		this.randomNumber = rn;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public byte[] getPassword() {
		return password;
	}

	public void setPassword(byte[] password) {
		this.password = password;
	}
	
	public byte[] getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(byte[] encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public byte[] prepareXml(String serviceName) throws Exception{
		Document xml = null;
		SecureRandomNumber secureRandomNumber = new SecureRandomNumber();
		int secureNumber = secureRandomNumber.getRandomNumber();
		setRandomNumber(secureNumber + "");
		
		//System.out.println("AAAAAAAAA::::"+ printHexBinary(secureRandomNumber.getRandomNumber()));
		RequestXml requestXml = new RequestXml(this.userId, serviceName, secureNumber + ""); //PERGUNTAR AO PROFESSOR SE E UM INT
		xml = requestXml.parseXML();
		requestXml.retrieveInformation();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(xml), new StreamResult(bos));
		byte [] array = bos.toByteArray();
		return array;
	}
}

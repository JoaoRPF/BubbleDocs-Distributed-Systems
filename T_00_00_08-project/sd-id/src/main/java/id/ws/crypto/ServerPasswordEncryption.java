package id.ws.crypto;

import static javax.xml.bind.DatatypeConverter.printHexBinary;
import id.ws.impl.UserManagement;

import java.security.MessageDigest;

public class ServerPasswordEncryption {

	private byte [] digestPassword = null;
	private byte[] userPassword = null;
	
	public ServerPasswordEncryption(UserManagement userManagement, String userId) throws Exception{
		byte[] pass = userManagement.getUserById(userId).getUserPass().getBytes("UTF-8");
		System.out.println("BYTES SERVER = "+pass);
		System.out.println("STRING = "+ new String(pass));
		this.setUserPassword(userManagement.getUserById(userId).getUserPass().getBytes("UTF-8"));
		this.generateKeyFromPassword(userPassword);
		System.out.println("KEY = "+getDigestPassword());
	}

	public byte [] getDigestPassword() {
		return digestPassword;
	}

	public void setDigestPassword(byte [] digestPassword) {
		this.digestPassword = digestPassword;
	}
	
	public byte[] getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(byte[] userPassword) {
		this.userPassword = userPassword;
	}
	
	private void generateKeyFromPassword(byte [] password) throws Exception {

		System.out.println("Bytes: ");
		System.out.println(printHexBinary(password));
		
		// get a message digest object using the MD5 algorithm
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		System.out.println(messageDigest.getProvider().getInfo());

		System.out.println("Computing digest ...");
		messageDigest.update(password);
		this.setDigestPassword(messageDigest.digest());

		System.out.println("Digest:");
		System.out.println(printHexBinary(this.getDigestPassword()));
	}
}

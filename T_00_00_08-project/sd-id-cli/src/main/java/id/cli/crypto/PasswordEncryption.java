package id.cli.crypto;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.security.MessageDigest;

public class PasswordEncryption {
	private byte [] digestPassword = null;
	
	public PasswordEncryption(byte [] password) throws Exception{
		this.generateKeyFromPassword(password);
	}

	public byte [] getDigestPassword() {
		return digestPassword;
	}

	public void setDigestPassword(byte [] digestPassword) {
		this.digestPassword = digestPassword;
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



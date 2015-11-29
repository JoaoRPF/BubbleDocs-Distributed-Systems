package store.crypto;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DecryptInformationFromClient {
	List<String> encryptedInformationFromClient = null;
	
	byte[] serverKey = null;
	byte[] sharedKeyFromTicket = null;
	byte[] macCipheredBytes = null;
	byte[] encryptedMacFromClientBytes = null;
	byte[] decipheredDigest = null;
	
	String idClientFromTicket = null;
	String idServiceFromTicket = null;
	String finalTimestampFromTicket = null;
	String idClientFromAuthor = null;
	String requestTimestampFromAuthor = null;
	
	public DecryptInformationFromClient(List<String> encryptedInformationFromClient) throws Exception{
		this.encryptedInformationFromClient = encryptedInformationFromClient;
		this.decryptTicketMessageFromClient();
		this.decryptAuthorMessageFromClient();
		this.decryptMacMessageFromClient();
	}
	
	public void decryptTicketMessageFromClient() throws Exception{
		
		String encryptedTicketFromClientInfo = this.encryptedInformationFromClient.get(0);
		byte[] encryptedTicketFromClientBytes = parseBase64Binary(encryptedTicketFromClientInfo);
		
		//Chave previamente conhecida
		this.setServerKey("sdstore");
		
		final byte[] keyBytes = Arrays.copyOf(this.serverKey, 24); 
		for (int j = 0, k = 16; j < 8;) {
             keyBytes[k++] = keyBytes[j++];
		}
		
		final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
		final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        
        cipher.init(Cipher.DECRYPT_MODE, key,iv);
        byte[] uncipherTicketBytes = cipher.doFinal(encryptedTicketFromClientBytes);
        System.out.println("Result:");
        System.out.println("UNCIPHERBYTES : "+printHexBinary(uncipherTicketBytes));
        
        String plainTicket = new String(uncipherTicketBytes); 
        System.out.println("PLAIN TICKET TEXT " + plainTicket);
        
        StringTokenizer tokenizer = new StringTokenizer(plainTicket, "$");
        this.idClientFromTicket = tokenizer.nextToken();
        this.idServiceFromTicket = tokenizer.nextToken();
        String initialTimestamp = tokenizer.nextToken();
        this.finalTimestampFromTicket = tokenizer.nextToken();
        String sharedKey = tokenizer.nextToken();
        
        this.sharedKeyFromTicket = sharedKey.getBytes();
        
        System.out.println("Ticket 0 : "+ this.idClientFromTicket);
        System.out.println("Ticket 1 : "+ this.idServiceFromTicket);
        System.out.println("Ticket 2 : "+ initialTimestamp);
        System.out.println("Ticket 3 : "+ this.finalTimestampFromTicket);
        System.out.println("Ticket 4 : "+ sharedKey);
        
	}
	
	public void decryptAuthorMessageFromClient() throws Exception{
		
		String encryptedAuthorFromClientInfo = this.encryptedInformationFromClient.get(1);
		byte[] encryptedAuthorFromClientBytes = parseBase64Binary(encryptedAuthorFromClientInfo);

		final byte[] keyBytes = Arrays.copyOf(this.sharedKeyFromTicket, 24); 
		for (int j = 0, k = 16; j < 8;) {
             keyBytes[k++] = keyBytes[j++];
		}
		
		final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
		final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        
        cipher.init(Cipher.DECRYPT_MODE, key,iv);
        byte[] uncipherAuthorBytes = cipher.doFinal(encryptedAuthorFromClientBytes);
        System.out.println("Result:");
        System.out.println("UNCIPHERBYTES : "+printHexBinary(uncipherAuthorBytes));
        
        String plainAuthor = new String(uncipherAuthorBytes);
        System.out.println("PLAIN AUTHOR TEXT " + plainAuthor);
        
        StringTokenizer tokenizer = new StringTokenizer(plainAuthor, "$");
        this.idClientFromAuthor = tokenizer.nextToken();
        this.requestTimestampFromAuthor = tokenizer.nextToken();
        
        System.out.println("Author 0 : "+ this.idClientFromAuthor);
        System.out.println("Author 1 : "+ this.requestTimestampFromAuthor);
        
	}
	
	public void decryptMacMessageFromClient() throws Exception{
		
		String encryptedMacFromClientInfo = this.encryptedInformationFromClient.get(2);
		this.encryptedMacFromClientBytes = parseBase64Binary(encryptedMacFromClientInfo);
		
		String bodyFromClientInfo = this.encryptedInformationFromClient.get(3);
		byte[] bodyFromClientBytes = bodyFromClientInfo.getBytes();
		
		final byte[] keyBytes = Arrays.copyOf(this.sharedKeyFromTicket, 24); 
		for (int j = 0, k = 16; j < 8;) {
             keyBytes[k++] = keyBytes[j++];
		}
		
		final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
		
		// get a message digest object using the MD5 algorithm
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");

        // calculate the digest and print it out
        messageDigest.update(bodyFromClientBytes);
        this.macCipheredBytes = messageDigest.digest();
        System.out.println("New digest:");
        System.out.println(this.macCipheredBytes);
        
        final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        // get a DES cipher object
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");

        // decrypt the ciphered digest using the public key
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        this.decipheredDigest = cipher.doFinal(encryptedMacFromClientBytes);
        System.out.println("Deciphered Digest:");
        System.out.println(printHexBinary(decipheredDigest));
        
        if(!this.fieldsVerificationsFromClient())
        	throw new Exception("Server verifications wrong!");
	}
	
	public void setServerKey(String serverKeyToTicket){
		this.serverKey = serverKeyToTicket.getBytes();
	}
	
	public boolean fieldsVerificationsFromClient() throws Exception{
		if(this.idClientFromTicket.equals(idClientFromAuthor)){
			if(this.idServiceFromTicket.equals("SD-STORE")){
				
				SimpleDateFormat format = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
								
				Date ticketDate = format.parse(finalTimestampFromTicket);
				System.out.println("Final Ticket Date " + ticketDate); 
				
				Date authorDate = format.parse(requestTimestampFromAuthor);				
				System.out.println("Request Date " + authorDate); 
				
				if(authorDate.before(ticketDate) || authorDate.equals(ticketDate)){
					// compare digests
//			        if (this.macCipheredBytes.length != encryptedMacFromClientBytes.length)
//			            return false;

//			        for (int i=0; i < this.macCipheredBytes.length; i++){
//			            if (this.macCipheredBytes[i] != encryptedMacFromClientBytes[i])
//			                return false;
//			        }
			        return true;
				}
			}
		}
		return false;	
	}
	
}

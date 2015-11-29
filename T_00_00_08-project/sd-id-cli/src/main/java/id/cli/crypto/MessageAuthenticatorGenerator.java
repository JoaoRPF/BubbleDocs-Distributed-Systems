package id.cli.crypto;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MessageAuthenticatorGenerator {
	
	private byte[] sharedKeyBytes = null;
	private String sharedKey = null;
	
	public MessageAuthenticatorGenerator(String sharedKey) throws Exception{
		this.setSharedKey(sharedKey);
	}

	public String getSharedKey() {
		return sharedKey;
	}

	public void setSharedKey(String sharedKey) {
		this.sharedKey = sharedKey;
		this.setSharedKeyBytes(sharedKey.getBytes());
	}
	
	public byte[] getSharedKeyBytes(){
		return this.sharedKeyBytes;
	}
	
	private void setSharedKeyBytes(byte[] sharedKeyBytes){
		this.sharedKeyBytes = sharedKeyBytes;
	}
	
	public SecretKey generateSharedSecretKey(){
    	final byte[] keyBytes = Arrays.copyOf(this.getSharedKeyBytes(), 24); 
		for (int j = 0, k = 16; j < 8;) {
             keyBytes[k++] = keyBytes[j++];
		}
		return new SecretKeySpec(keyBytes, "DESede");
    }
	
	/** auxiliary method to make the MAC */
    public static byte[] makeMAC(byte[] bytes, SecretKey key) throws Exception {
    	final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
    	System.out.println("BYTES MAC: " + new String(bytes) + " TAMANHO: " + bytes.length);
    	// get a message digest object using the MD5 algorithm
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");

        // calculate the digest and print it out
        messageDigest.update(bytes);
        byte[] digest = messageDigest.digest();
        System.out.println("Digest:");
        System.out.println(printHexBinary(digest));

        // get a DES cipher object
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");

        // encrypt the plaintext using the key
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherDigest = cipher.doFinal(digest);

        return cipherDigest;
    }

    /** auxiliary method to calculate new digest from text and compare it to the
         to deciphered digest */
    public static boolean verifyMAC(byte[] cipherDigest, byte[] bytes, SecretKey key) throws Exception {

        Mac macCipher = Mac.getInstance("HmacMD5");
        macCipher.init(key);
        byte[] macCipheredBytes = macCipher.doFinal(bytes);
        return Arrays.equals(cipherDigest, macCipheredBytes);
    }
}

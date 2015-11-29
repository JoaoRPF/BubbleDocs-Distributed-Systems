package id.cli.crypto;

import java.security.NoSuchAlgorithmException;
// provides helper methods to print byte[]
import java.security.SecureRandom;

public class SecureRandomNumber {

	SecureRandom randomNumber;
	
	public SecureRandomNumber(){
		
	}
	
	public int getRandomNumber() throws Exception{
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
//        final byte array[] = new byte[16];
//        random.nextBytes(array);
        return random.nextInt();
	}
}
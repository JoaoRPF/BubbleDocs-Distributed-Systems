package id.cli.crypto;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class AuthorXmlEncrypt {
	
	private Document authenticationXmlReadyToEncrypt = null;
	private String sharedKeyFromSdId = null;
	
	public AuthorXmlEncrypt(Document authenticationXmlReadyToEncrypt, String sharedKeyFromSdId){
		this.authenticationXmlReadyToEncrypt = authenticationXmlReadyToEncrypt;
		this.sharedKeyFromSdId = sharedKeyFromSdId;
	}
	
	public Document encryptAuhenticationField() throws Exception{
		final byte[] keyBytes = Arrays.copyOf(this.sharedKeyFromSdId.getBytes(), 24); 
		for (int j = 0, k = 16; j < 8;) {
             keyBytes[k++] = keyBytes[j++];
		}
		final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
		final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding"); 
      
        Node authorNode = authenticationXmlReadyToEncrypt.getDocumentElement().getFirstChild(); 
	    if (authorNode == null) {
	        throw new Exception("Author node not found!");
	    }
	    
	    String idClientText = authorNode.getFirstChild().getTextContent();
	    String timeRequestText = authorNode.getLastChild().getTextContent();
	    
	    String breaker = "$";
	    String authorInfo = idClientText + breaker + timeRequestText;
	    System.out.println("JJJJ = "+authorInfo);
	    byte[] authorBytes = authorInfo.getBytes();
	    
	    //remove credentials node
	    authenticationXmlReadyToEncrypt.getDocumentElement().removeChild(authorNode);
        
        System.out.println("Ciphering ...");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherBytes = cipher.doFinal(authorBytes);
        System.out.println("Result:");
        System.out.println(printHexBinary(cipherBytes));
        
        // create the element
        Element cipherAuthorElement = authenticationXmlReadyToEncrypt.createElement("cipherAuthor");
        Text text = authenticationXmlReadyToEncrypt.createTextNode(printBase64Binary(cipherBytes));
        cipherAuthorElement.appendChild(text);
        authenticationXmlReadyToEncrypt.getDocumentElement().appendChild(cipherAuthorElement);
                
        return authenticationXmlReadyToEncrypt;
	}
	
}

package id.ws.crypto;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.security.Key;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class SymCrypto {
	
	public SymCrypto(){}
	
	public Key generateSharedKey() throws Exception{
		System.out.println("Generating DES key ...");
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        keyGen.init(56);
        Key key = keyGen.generateKey();
        System.out.println("Key:");
        System.out.println(printHexBinary(key.getEncoded()));
        return key;
	}
	
	public Document encryptCredentials(Document xmlDocument, byte[] digestedPassword) throws Exception {
		
		final byte[] keyBytes = Arrays.copyOf(digestedPassword, 24); 
		for (int j = 0, k = 16; j < 8;) {
             keyBytes[k++] = keyBytes[j++];
		}
		final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
		final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding"); 
       
		Node credentialsNode = null;
        
        for(Node node = xmlDocument.getDocumentElement().getFirstChild(); node != null; node = node.getNextSibling() ) {
			if(node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("credentials")) {
				credentialsNode = node;
				break;
			}
	    }
	    if (credentialsNode == null) {
	        throw new Exception("Credentials node not found!");
	    }
	    
	    String breaker = "$";
	    String credentialsInfo = credentialsNode.getFirstChild().getTextContent() + breaker + credentialsNode.getLastChild().getTextContent();
	    byte[] credentialsBytes = credentialsInfo.getBytes();
//	    
//	    String sharedKeyInfo = sharedKeyNode.getTextContent();
//	    byte[] sharedKeyBytes = sharedKeyInfo.getBytes();
//	    
//	    String randomNumberInfo = randomNumberNode.getTextContent();
//	    byte[] randomNumberBytes = randomNumberInfo.getBytes();
	    
	    //remove credentials node
	    xmlDocument.getDocumentElement().removeChild(credentialsNode);
        
        System.out.println("Ciphering ...");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherBytes = cipher.doFinal(credentialsBytes);
        System.out.println("Result:");
        System.out.println(printHexBinary(cipherBytes));
        
        // create the element
        Element cipherCredentialsElement = xmlDocument.createElement("cipherCredentials");
        Text text = xmlDocument.createTextNode(printBase64Binary(cipherBytes));
        cipherCredentialsElement.appendChild(text);
        xmlDocument.getDocumentElement().appendChild(cipherCredentialsElement);
                
        return xmlDocument;
	}
	
	public Document encryptTicket(Document xmlDocument) throws Exception {
		
		//CHAVE SUPOSTAMENTE PARTILHADA ENTRE SDID E SDSTORE , USAR IGUAL DO OUTRO LADO !!
		String sdStoreKey  = "sdstore";
		byte[] sdStoreBytes = sdStoreKey.getBytes();
		
		final byte[] keyBytes = Arrays.copyOf(sdStoreBytes, 24); 
		for (int j = 0, k = 16; j < 8;) {
             keyBytes[k++] = keyBytes[j++];
		}
		final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
		final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding"); 
       
        Node ticketNode = null;
        
        for(Node node = xmlDocument.getDocumentElement().getFirstChild(); node != null; node = node.getNextSibling() ) {
			if(node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("ticket")) {
				ticketNode = node;
	        }
	    }
	    if (ticketNode == null) {
	        throw new Exception("Ticket node not found!");
	    }
	    
	    String breaker = "$";
	    String ticketInfo = null;
	    for(Node node = xmlDocument.getDocumentElement().getFirstChild().getFirstChild(); node!= null; node = node.getNextSibling()){
	    	if(ticketInfo == null)
	    		ticketInfo = node.getTextContent();
	    	else
	    		ticketInfo = ticketInfo + node.getTextContent();
	    	if(node.getNextSibling() != null){
	    		ticketInfo = ticketInfo + breaker;
	    	}
	    }
	    
	    byte[] ticketBytes = ticketInfo.getBytes();
	    
	    //remove ticket node
	    xmlDocument.getDocumentElement().removeChild(ticketNode);
        
        System.out.println("Ciphering ...");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherBytes = cipher.doFinal(ticketBytes);
        System.out.println("Result:");
        System.out.println(printHexBinary(cipherBytes));
        
        // create the element
        Element ticketBodyElement = xmlDocument.createElement("cipherTicket");
        Text text = xmlDocument.createTextNode(printBase64Binary(cipherBytes));
        ticketBodyElement.appendChild(text);
        // append nodes to document
        xmlDocument.getDocumentElement().appendChild(ticketBodyElement);
        
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        System.out.println("XML cipher contents:");
        transformer.transform(new DOMSource(xmlDocument), new StreamResult(System.out));
        System.out.println();
        
        return xmlDocument;
	}
}

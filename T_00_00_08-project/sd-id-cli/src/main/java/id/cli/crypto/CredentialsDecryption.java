package id.cli.crypto;

import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
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

public class CredentialsDecryption {
	
	private Document xmlDocument = null;
	private byte[] key = null;
	
	public CredentialsDecryption(Document xmlDocument, byte[] key) throws Exception{
		this.xmlDocument = xmlDocument;
		this.key = key;
		System.out.println("KEY === "+key);
		decrypt();
	}
	
	public Document getXmlDocument(){
		return xmlDocument;
	}
	
	private void decrypt() throws Exception{
		
		final byte[] keyBytes = Arrays.copyOf(key, 24); 
		for (int j = 0, k = 16; j < 8;) {
             keyBytes[k++] = keyBytes[j++];
		}
		
		final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
		final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		
		Node credentialsNode = null;
		
		for(Node node = xmlDocument.getDocumentElement().getFirstChild(); node != null; node = node.getNextSibling() ) {
			if(node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("cipherCredentials")) {
				credentialsNode = node;
				break;
			}
	    }
	    if (credentialsNode == null) {
	        throw new Exception("Credentials node not found!");
	    }
	    
	    String credentialsInfo = credentialsNode.getTextContent();
	    byte[] credentialsBytes = parseBase64Binary(credentialsInfo);
	    System.out.println("credentials encrypted ="+credentialsInfo);
	    System.out.println("credentials encrypted bytes = "+credentialsBytes);
		
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		Transformer transformer = TransformerFactory.newInstance().newTransformer();
//		transformer.transform(new DOMSource(this.xmlDocument), new StreamResult(bos));
//		byte [] xmlBytes = bos.toByteArray();
		
        
        cipher.init(Cipher.DECRYPT_MODE, key,iv);
        byte[] uncipherBytes = cipher.doFinal(credentialsBytes);
        System.out.println("Result:");
        System.out.println("UNCIPHERBYTES : "+printHexBinary(uncipherBytes));
        
        String newPlainText = new String(uncipherBytes); 
        System.out.println("PLAIN TEXT " + newPlainText);
        
        StringTokenizer tokenizer = new StringTokenizer(newPlainText, "$");
        String sharedKey = tokenizer.nextToken();
        String randomNumber = tokenizer.nextToken();
        System.out.println("CREDENTIALS 0 : "+sharedKey);
        System.out.println("CREDENTIALS 1 : "+randomNumber);
        
        // remove cipher body node
        xmlDocument.getDocumentElement().removeChild(credentialsNode);

        // create the element
        Element credentialsElement = xmlDocument.createElement("credentials");
        xmlDocument.getDocumentElement().appendChild(credentialsElement);
        
	    Element sharedKeyElement = xmlDocument.createElement("sharedKey");
	    Text sharedKeyText = xmlDocument.createTextNode(sharedKey);
	    sharedKeyElement.appendChild(sharedKeyText);
	    // append nodes to document
	    credentialsElement.appendChild(sharedKeyElement);
	      
	    Element randomNumberElement = xmlDocument.createElement("randomNumber");
	    Text randomNumberText = xmlDocument.createTextNode(randomNumber);
	    randomNumberElement.appendChild(randomNumberText);
	    // append nodes to document
	    credentialsElement.appendChild(randomNumberElement);
	      

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        
        System.out.println("XML document with deciphered body:");
        transformer.transform(new DOMSource(xmlDocument), new StreamResult(System.out));
        System.out.println();
	}
	
}

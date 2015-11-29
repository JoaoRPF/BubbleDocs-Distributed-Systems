package id.ws.crypto;

//helper methods to print byte[]

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.Key;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class ResponseXml {
	
	public static String XML = null;
	public byte[] digestedPassword = null;

	private Document xmlDocument = null;
	private Document xmlFromClient = null;
	private String globalSharedKey = null;
	private SymCrypto symCrypto = new SymCrypto();
		
	public ResponseXml(byte[] digestedPassword, Document xmlFromClient) throws Exception{
		this.digestedPassword = digestedPassword;
		this.xmlFromClient = xmlFromClient;
		ResponseXml.XML = "<message>" + 
						  	"<credentials>" + 
						  		"<sharedKey>" + "</sharedKey>" + 
								"<randomNumber>" + "</randomNumber>" +
						  	"</credentials>" + 
							"<ticket>" + 
								"<idClient>" + "</idClient>" + 
								"<idService>" + "</idService>" + 
								"<timestamp1>" + "</timestamp1>" + 
								"<timestamp2>" + "</timestamp2>" + 
								"<sharedKey>" + "</sharedKey>" + 
							"</ticket>" +
						   "</message>";
		
		xmlDocument = parseXmlToClient();
		fillCredentials();
		fillTicket();
		this.xmlDocument = symCrypto.encryptCredentials(xmlDocument, digestedPassword);
		this.xmlDocument = symCrypto.encryptTicket(xmlDocument);
	}
	
	public Document parseXmlToClient() throws Exception{
		InputStream xmlInputStream = new ByteArrayInputStream(XML.getBytes());

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        System.out.println("Parsing XML document from string bytes...");
        this.xmlDocument = documentBuilder.parse(xmlInputStream);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        System.out.println("XML document contents:");
        transformer.transform(new DOMSource(xmlDocument), new StreamResult(System.out));
        System.out.println();
        return this.xmlDocument;
	}
	
	private void fillCredentials() throws Exception{
		Key sharedKey = symCrypto.generateSharedKey();
		
		Node randomNumberNode = null;
		
		for(Node node = xmlFromClient.getDocumentElement().getFirstChild(); node != null; node = node.getNextSibling() ) {
			if(node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("randomNumber")) {
				randomNumberNode = node;
	            break;
	        }
	    }
	    if (randomNumberNode == null) {
	        throw new Exception("Body node not found!");
	    }
	    
	    String randomNumberInfo = randomNumberNode.getTextContent();
	    
	    Element keySharedElement = null;
	    Element randomNumberElement = null;
	    for (Node n = xmlDocument.getDocumentElement().getFirstChild().getFirstChild(); n != null; n = n.getNextSibling() ) {
	        if (n.getNodeType() == Node.ELEMENT_NODE) {
	        	if (n.getNodeName().equals("sharedKey")){
	        		keySharedElement = (Element) n;
	        	}
	        	if (n.getNodeName().equals("randomNumber"))
	        		randomNumberElement = (Element) n;
	        }
	    }

	    globalSharedKey = Base64.getEncoder().encodeToString(sharedKey.getEncoded()); //DIRETO OU PRIMEIRO PASSA A BYTES?
	    Text keySharedText = xmlDocument.createTextNode(globalSharedKey);
	    keySharedElement.appendChild(keySharedText);
	    
        Text randomNumberText = xmlDocument.createTextNode(randomNumberInfo);
        randomNumberElement.appendChild(randomNumberText);
		
	}
	
	public void fillTicket() throws Exception{
		
		Node idClientNode = null;
		Node idServiceNode = null;
		
		for(Node node = xmlFromClient.getDocumentElement().getFirstChild(); node != null; node = node.getNextSibling() ) {
			if(node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("idClient")) {
				idClientNode = node;
	        }
			if(node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("idService")) {
				idServiceNode = node;
				break;
			}
	    }
	    if (idClientNode == null || idServiceNode == null) {
	        throw new Exception("Body node not found!");
	    }
	    
	    String idClientInfo = idClientNode.getTextContent();
	    String idServiceInfo = idServiceNode.getTextContent();
	    
	    Element idClientElement = null;
	    Element idServiceElement = null;
	    Element timestamp1Element = null;
	    Element timestamp2Element = null;
	    Element sharedKeyElement = null;
	    for (Node n = xmlDocument.getDocumentElement().getLastChild().getFirstChild(); n != null; n = n.getNextSibling() ) {
	        if (n.getNodeType() == Node.ELEMENT_NODE) {
	        	System.out.println("NOME = "+n.getNodeName());
	        	if (n.getNodeName().equals("idClient")){
	        		idClientElement = (Element) n;
	        	}
	        	if (n.getNodeName().equals("idService")){
	        		idServiceElement = (Element) n;
	        	}
	        	if (n.getNodeName().equals("timestamp1")){
	        		timestamp1Element = (Element) n;
	        	}
	        	if (n.getNodeName().equals("timestamp2")){
	        		timestamp2Element = (Element) n;
	        	}
	        	if (n.getNodeName().equals("sharedKey")){
	        		sharedKeyElement = (Element) n;
	        	}
	        }
	    }
	    
	    Date date = new Date();
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.add(Calendar.MINUTE, 2);
	    Date newDate = cal.getTime();
	    
	    String timestampInitialInfo = date.toString();
	    String timestampFinalInfo = newDate.toString();
	    
	    Text idClientText = xmlDocument.createTextNode(idClientInfo);
	    idClientElement.appendChild(idClientText);
	    
        Text idServiceText = xmlDocument.createTextNode(idServiceInfo);
        idServiceElement.appendChild(idServiceText);
        
        Text timestampInitialText = xmlDocument.createTextNode(timestampInitialInfo);
        timestamp1Element.appendChild(timestampInitialText);
        
        Text timestampFinalText = xmlDocument.createTextNode(timestampFinalInfo);
        timestamp2Element.appendChild(timestampFinalText);
        
        Text keySharedText = xmlDocument.createTextNode(globalSharedKey);
	    sharedKeyElement.appendChild(keySharedText);
        
        System.out.println("XML document with cipher body:");
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        
        transformer.transform(new DOMSource(xmlDocument), new StreamResult(System.out));
        System.out.println();
	}
	
	
	public byte[] getEncryptionDocument() throws Exception{
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(this.xmlDocument), new StreamResult(bos));
		byte [] finalEncrypt = bos.toByteArray();
		
		return finalEncrypt;
	}
}
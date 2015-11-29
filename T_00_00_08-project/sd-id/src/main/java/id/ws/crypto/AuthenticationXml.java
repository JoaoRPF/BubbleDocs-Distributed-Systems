package id.ws.crypto;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class AuthenticationXml {

	//public static String XML = null;
	//public byte[] digestedPassword = null;
	private Document xmlDocument = null;
	
	public AuthenticationXml(){}
	
	public Document getDocument(){
		return this.xmlDocument;
	}
	
	public void createDocumentFromAuthentication(byte [] clientMessage) throws Exception{
	       		
		InputStream xmlClientInputStream  = new ByteArrayInputStream(clientMessage);
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        System.out.println("Parsing XML document from string bytes...");
        this.xmlDocument = documentBuilder.parse(xmlClientInputStream);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        System.out.println("XML document contents:");
        transformer.transform(new DOMSource(xmlDocument), new StreamResult(System.out));
        System.out.println();
	}
}

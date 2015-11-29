package id.cli.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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

public class ClientAuthorXml {
	
	public static String XML = null;
	private Document authenticationDocument = null;
	
	public ClientAuthorXml(String idClient){
		ClientAuthorXml.XML = "<message>" + 
								"<author>" + 
									"<idClient>" + idClient + "</idClient>" + 
									"<timeRequest>" + "</timeRequest>" + 
								"</author>" + 
							  "</message>";
	}
	
	public Document parseXML() throws Exception {

		InputStream xmlAuthenticationStream = new ByteArrayInputStream(XML.getBytes());

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		System.out.println("Parsing XML document from string bytes...");
		this.authenticationDocument = documentBuilder.parse(xmlAuthenticationStream);

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
		transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		System.out.println("XML Authenticaton Document contents:");
		transformer.transform(new DOMSource(authenticationDocument), new StreamResult(System.out));
		System.out.println();
		return this.authenticationDocument;
	}
	
	public void retrieveAuthenticationInformation() throws Exception{
		System.out.print("Body text: ");
	    Element timeRequestElement = null;
	    
	    for (Node node = authenticationDocument.getDocumentElement().getFirstChild().getFirstChild(); node != null; node = node.getNextSibling() ) {
	        if (node.getNodeType() == Node.ELEMENT_NODE) {
	        	System.out.println("NOME = " + node.getNodeName());
	        	if (node.getNodeName().equals("timeRequest")){
	        		timeRequestElement = (Element) node;
	        	}
	        }
	    }
        
        Date date = new Date();
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    
	    String timestampRequestInfo = date.toString();
	    
        Text idServiceText = authenticationDocument.createTextNode(timestampRequestInfo);
        timeRequestElement.appendChild(idServiceText);
        
        System.out.println("XML document with cipher body:");
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        
        transformer.transform(new DOMSource(authenticationDocument), new StreamResult(System.out));
        System.out.println();
	}
	
	public Document getAuthenticationDocument() throws Exception{
		return this.authenticationDocument;
	}

}

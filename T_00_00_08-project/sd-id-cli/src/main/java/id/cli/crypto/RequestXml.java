package id.cli.crypto;

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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


public class RequestXml  {
	
	public static String XML = null;
	private Document xmlDocument = null;

	public RequestXml(String idClient, String idService, String randomNumber){
		RequestXml.XML = "<message>" + "<idClient>" + idClient + "</idClient>" + 
				 "<idService>" + idService + "</idService>" + 
				 "<randomNumber>" + randomNumber + "</randomNumber>" + "</message>";
	}
	
	public RequestXml(){}
	
	
	public Document parseXML() throws Exception {
		
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
	
	public void retrieveInformation() throws Exception {
		
		System.out.print("Body text: ");
        Node idClientNode = null;
        Node idServiceNode = null;
        Node randomNumberNode = null;
        
        for( Node node = xmlDocument.getDocumentElement().getFirstChild();
             node != null;
             node = node.getNextSibling() ) {

            if( node.getNodeType() == Node.ELEMENT_NODE &&
                node.getNodeName().equals("idClient")) {
                idClientNode = node;
            }
            
            if( node.getNodeType() == Node.ELEMENT_NODE &&
                    node.getNodeName().equals("idService")) {
                    idServiceNode = node;
                }
            
            if( node.getNodeType() == Node.ELEMENT_NODE &&
                    node.getNodeName().equals("randomNumber")) {
                    randomNumberNode = node;
                    break;
                }
            
            }
        if (idClientNode == null || idServiceNode == null || randomNumberNode == null) {
            throw new Exception("Body node not found!");
        }

        String idClientInfo = idClientNode.getTextContent();
        String idServiceInfo = idServiceNode.getTextContent();
        String randomNumberInfo = randomNumberNode.getTextContent();
        
        Element clientElement = xmlDocument.createElement("idClient");
        Text clientText = xmlDocument.createTextNode(idClientInfo);
        clientElement.appendChild(clientText);
        
        Element serviceElement = xmlDocument.createElement("idService");
        Text serviceText = xmlDocument.createTextNode(idServiceInfo);
        serviceElement.appendChild(serviceText);
        
        Element randomNumberElement = xmlDocument.createElement("randomNumber");
        Text randomNumberText = xmlDocument.createTextNode(randomNumberInfo);
        randomNumberElement.appendChild(randomNumberText);
        
        System.out.println("XML document with cipher body:");
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        
        transformer.transform(new DOMSource(xmlDocument), new StreamResult(System.out));
        System.out.println();
	}
}

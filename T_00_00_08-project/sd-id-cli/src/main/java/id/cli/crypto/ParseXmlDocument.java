package id.cli.crypto;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ParseXmlDocument {
	
	private Document xmlDocument = null;
	
	public ParseXmlDocument(Document xmlDocument){
		this.xmlDocument = xmlDocument;
	}
	
	public String getCredentialsSharedKey(){
		Node credentialsNode = xmlDocument.getDocumentElement().getLastChild();	
		String credentialsSharedKey = credentialsNode.getFirstChild().getTextContent();		
		return credentialsSharedKey;
	}
	
	public String getCredentialsRandomNumber(){
		Node credentialsNode = xmlDocument.getDocumentElement().getLastChild();
		String credentialsRandomNumber = credentialsNode.getLastChild().getTextContent();		
		return credentialsRandomNumber;
	}
	
	public String getTicketTag(){
		Node ticketNode = xmlDocument.getDocumentElement().getFirstChild();
		String ticketContent = ticketNode.getTextContent();
		return ticketContent;
	}
}

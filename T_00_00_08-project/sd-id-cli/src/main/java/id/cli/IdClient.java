package id.cli;

import id.cli.crypto.AuthorXmlEncrypt;
import id.cli.crypto.ClientAuthorXml;
import id.cli.crypto.CredentialsDecryption;
import id.cli.crypto.ParseXmlDocument;
import id.cli.store.StoreClient;
import id.cli.store.StoreClientException;
import id.ws.uddi.UDDINaming;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.registry.JAXRException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.BindingProvider;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import pt.ulisboa.tecnico.sdis.id.ws.AuthReqFailed_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.EmailAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidEmail_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidUser_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.SDId;
import pt.ulisboa.tecnico.sdis.id.ws.SDId_Service;
import pt.ulisboa.tecnico.sdis.id.ws.UserAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserDoesNotExist_Exception;

public class IdClient implements SDId {
	
	/** WS service */
    SDId_Service service = null;

    /** WS port (interface) */
    SDId port = null;

    /** WS endpoint address */
    
    private String uddiURL = null;
    private String name = null;

    /** output option **/
    private boolean verbose = false;
    
    private String encryptedTicket = null;
    private String nameService = null;

	private String sharedKeyFromSdId = null;

	private String authorXmlEncryptedText; 

    public boolean isVerbose() {
        return verbose;
    }
    
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    public void setNameService(String nameService){
    	this.nameService = nameService;
    }
    
    public String getEncryptedTicket(){
		return this.encryptedTicket;
	}
    
    public String getSharedKeyFromSdId(){
    	return this.sharedKeyFromSdId;
    }
    
    public String getAuthorXmlEncryptedText() {
		return authorXmlEncryptedText;
	}

	public void setAuthorXmlEncryptedText(String authorXmlEncryptedText) {
		this.authorXmlEncryptedText = authorXmlEncryptedText;
	}

    /** constructor with provided web service URL 
     * @throws JAXRException */
    public IdClient(String uddiURL, String name) throws IdClientException, JAXRException {
        this.uddiURL = uddiURL;
    	this.name = name;
        createStub();
    }

    /** default constructor uses default endpoint address 
     * @throws JAXRException */
    public IdClient() throws IdClientException, JAXRException {
        createStub();
    }

    /** Stub creation and configuration 
     * @throws JAXRException */
    protected void createStub() throws JAXRException {

        System.out.printf("Contacting UDDI at %s%n", this.uddiURL);
        UDDINaming uddiNaming = new UDDINaming(this.uddiURL);

        System.out.printf("Looking for '%s'%n", this.name);
        String endpointAddress = uddiNaming.lookup(this.name);

        if (endpointAddress == null) {
            System.out.println("Not found!");
            return;
        } else {
            System.out.printf("Found %s%n", endpointAddress);
        }
    	
        if (verbose)
            System.out.println("Creating stub ...");
        service = new SDId_Service();
        port = service.getSDIdImplPort();

        if (verbose)
        	System.out.println("Setting endpoint address ...");
        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
    }
   
    //Implements SDId
    
	public void createUser(String userId, String emailAddress)
			throws EmailAlreadyExists_Exception, InvalidEmail_Exception,
			InvalidUser_Exception, UserAlreadyExists_Exception {
		port.createUser(userId, emailAddress);
		
	}
	public void renewPassword(String userId) throws UserDoesNotExist_Exception {
		port.renewPassword(userId);
		
	}
	public void removeUser(String userId) throws UserDoesNotExist_Exception {
		port.removeUser(userId);
	}
	public byte[] requestAuthentication(String userId, byte[] reserved)
			throws AuthReqFailed_Exception {
		System.out.println("BYTES CLIENT == "+reserved);
		Login login;
		byte[] returnFromServer = null;
		try {
			login = new Login(userId, reserved);
			byte[] xml = login.prepareXml("SD-STORE");
			byte[] key  = login.getEncryptedPassword();
			returnFromServer = port.requestAuthentication(userId, xml);
			firstRoundTrip(returnFromServer, login, key);
			secondRoundTrip(userId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnFromServer;
	}

	public void firstRoundTrip(byte[] returnFromServer, Login login, byte[] key) throws Exception{
		InputStream xmlServerInputStream  = new ByteArrayInputStream(returnFromServer);
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        System.out.println("Parsing XML document from string bytes...");
        Document xmlDocument = documentBuilder.parse(xmlServerInputStream);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        System.out.println("XML document contents:");
        transformer.transform(new DOMSource(xmlDocument), new StreamResult(System.out));
        System.out.println();
   
        CredentialsDecryption decryption = new CredentialsDecryption(xmlDocument, key);
        xmlDocument = decryption.getXmlDocument();
        
        ParseXmlDocument readyToParseDocument = new ParseXmlDocument(xmlDocument);
        this.encryptedTicket = readyToParseDocument.getTicketTag();
        this.sharedKeyFromSdId = readyToParseDocument.getCredentialsSharedKey();
        String arrivedRandomNumber = readyToParseDocument.getCredentialsRandomNumber();
        if (!arrivedRandomNumber.equals(login.getRandomNumber())){
        	throw new Exception("RANDOM NUMBERS DIFERENTES");
        }
	}
	
	private void secondRoundTrip(String userId) throws StoreClientException, JAXRException {
		prepareAuthorXmlToSend(userId, this.getSharedKeyFromSdId());		
		
		StoreClient storeClient = new StoreClient(this.uddiURL, this.nameService, this.getEncryptedTicket(), this.getAuthorXmlEncryptedText(), this.getSharedKeyFromSdId());
	
	}
	
	public void prepareAuthorXmlToSend(String userId, String sharedKeyFromSdId){
		ClientAuthorXml authorXml = new ClientAuthorXml(userId);
		
		try {
			authorXml.parseXML();
			authorXml.retrieveAuthenticationInformation();
			
			Document authenticationXmlReadyToEncrypt = authorXml.getAuthenticationDocument();
			
			AuthorXmlEncrypt authorEncrypt = new AuthorXmlEncrypt(authenticationXmlReadyToEncrypt, sharedKeyFromSdId);
			Document authorXmlEncrypted = authorEncrypt.encryptAuhenticationField();
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
			transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			System.out.println("XML Authenticaton Document contents:");
			transformer.transform(new DOMSource(authorXmlEncrypted), new StreamResult(System.out));
			System.out.println();
			
			this.setAuthorXmlEncryptedText(this.getStringFromXml(authorXmlEncrypted));
			
			System.out.println("Depois de encriptado " + this.getAuthorXmlEncryptedText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getStringFromXml(Document encryptedXmlDocument){
		Node encryptedAuthorNode = encryptedXmlDocument.getDocumentElement().getFirstChild();
		String encryptedAuthorContent = encryptedAuthorNode.getTextContent();
		
		return encryptedAuthorContent;
	}
}

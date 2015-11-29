package id.cli.store;

import id.cli.crypto.AuthorXmlEncrypt;
import id.cli.crypto.ClientAuthorXml;
import id.cli.handler.RelayClientHandler;
import id.ws.uddi.UDDINaming;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

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

import pt.ulisboa.tecnico.sdis.store.ws.CapacityExceeded_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore_Service;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;

public class StoreClient implements SDStore{
	/** WS service */
    SDStore_Service service = null;

    /** WS port (interface) */
    SDStore port = null;

    /** WS endpoint address */
    
    private String uddiURL = null;
    private String name = null;

    /** output option **/
    private boolean verbose = false;
    
    public static final String CLASS_NAME = StoreClient.class.getSimpleName();
    public static final String TOKEN = "client";
    
    private String encryptedTicketFromIdClient = null;
    
    private String encryptedAuthor = null;
    
    private String sharedKeyFromSdId = null;


    public boolean isVerbose() {
        return verbose;
    }
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /** constructor with provided web service URL 
     * @throws JAXRException */
    public StoreClient(String uddiURL, String name, String encryptedTicketFromIdClient, String encryptedAuthor, String sharedKeySdId) throws StoreClientException, JAXRException {
        this.uddiURL = uddiURL;
    	this.name = name;
    	this.encryptedTicketFromIdClient = encryptedTicketFromIdClient;
    	this.encryptedAuthor = encryptedAuthor;
    	this.sharedKeyFromSdId = sharedKeySdId;
        createStub();
    }

    /** default constructor uses default endpoint address 
     * @throws JAXRException */
    public StoreClient() throws StoreClientException, JAXRException {
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
        service = new SDStore_Service();
        port = service.getSDStoreImplPort();

        if (verbose)
        	System.out.println("Setting endpoint address ...");
        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        
        // *** #1 ***
        // put EncryptedTicket in request context
        System.out.printf("%s put token '%s' on request context%n", CLASS_NAME, this.encryptedTicketFromIdClient);
        requestContext.put(RelayClientHandler.REQUEST_TICKET_PROPERTY, this.encryptedTicketFromIdClient);
        requestContext.put(RelayClientHandler.REQUEST_AUTHOR_PROPERTY, this.encryptedAuthor);
        requestContext.put(RelayClientHandler.SHAREDKEY_PROPERTY, this.sharedKeyFromSdId);
        
        // make remote call
        System.out.printf("Remote call to %s ...%n", this.uddiURL);
        
        List<String> result;
		try {
			result = port.listDocs("alice");
			System.out.printf("Result: %s%n", result);
		} catch (UserDoesNotExist_Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
        
        // access response context
        Map<String, Object> responseContext = bindingProvider.getResponseContext();

        // *** #12 ***
        // get token from response context
        String finalValue = (String) responseContext.get(RelayClientHandler.RESPONSE_PROPERTY);
        System.out.printf("%s got token '%s' from response context%n", CLASS_NAME, finalValue);
    }
    
    //Implements SD-STORE Service
	public void createDoc(DocUserPair docUserPair)
			throws DocAlreadyExists_Exception {
		// TODO Auto-generated method stub
		
	}
	public List<String> listDocs(String userId)
			throws UserDoesNotExist_Exception {
		// TODO Auto-generated method stub
		return null;
	}
	public void store(DocUserPair docUserPair, byte[] contents)
			throws CapacityExceeded_Exception, DocDoesNotExist_Exception,
			UserDoesNotExist_Exception {
		// TODO Auto-generated method stub
		
	}
	public byte[] load(DocUserPair docUserPair)
			throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	//Auxiliar Methods
}

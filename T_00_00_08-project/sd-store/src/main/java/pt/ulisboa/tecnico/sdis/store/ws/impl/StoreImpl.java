package pt.ulisboa.tecnico.sdis.store.ws.impl;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import pt.ulisboa.tecnico.sdis.store.ws.CapacityExceeded_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;
import store.crypto.DecryptInformationFromClient;
import store.handler.RelayServerHandler;

@WebService(
    endpointInterface = "pt.ulisboa.tecnico.sdis.store.ws.SDStore", 
    wsdlLocation = "SD-STORE.1_1.wsdl", 
    name = "SDStore", 
    portName = "SDStoreImplPort", 
    targetNamespace = "urn:pt:ulisboa:tecnico:sdis:store:ws", 
    serviceName = "SDStore"
)
@HandlerChain(file="/handler-chain.xml")
public class StoreImpl implements SDStore {

    public static HashMap<String, DocumentRepository> userRepositories = new HashMap<String, DocumentRepository>();
    
    @Resource
    private WebServiceContext webServiceContext;
    
    public static final String CLASS_NAME = StoreImpl.class.getSimpleName();
    public static final String TOKEN = "SD-STORE";
    
    /*
     * From WSDL: <!-- Creates a new document in the provided user's repository.
     * In case this is the first operation on that user, a new repository is
     * created for the new user. Faults: a document already exists with the same
     * id -->
     */
    public void createDoc(DocUserPair docUserPair)
            throws DocAlreadyExists_Exception {

    	if(docUserPair.getUserId() != null || docUserPair.getUserId() != "" ||
    			docUserPair.getDocumentId() != null || docUserPair.getDocumentId() != "") {
    	
	        DocumentRepository rep = userRepositories.get(docUserPair.getUserId());
	
	        if (rep == null) {
	            rep = new DocumentRepository();
	            userRepositories.put(docUserPair.getUserId(), rep);
	        }
	
	        if (rep.addNewDocument(docUserPair.getDocumentId()) == false) {
	            DocAlreadyExists faultInfo = new DocAlreadyExists();
	            // fi.setMessage("Document already exists");
	            faultInfo.setDocId(docUserPair.getDocumentId());
	            throw new DocAlreadyExists_Exception("Document already exists", faultInfo);
	        }
    	}
    	this.getInformationFromStoreContext();
    }

    /*
     * From WSDL: <!-- Lists the document ids of the user's repository. Faults:
     * user does not exist -->
     */
    public List<String> listDocs(String userId)
            throws UserDoesNotExist_Exception {
    	
    	if(userId == null || userId.equalsIgnoreCase("") || userRepositories.get(userId) == null) {    	
            UserDoesNotExist faultInfo = new UserDoesNotExist();
            faultInfo.setUserId(userId);
            // fi.setMessage("User does not exist");
            throw new UserDoesNotExist_Exception("User does not exist **", faultInfo);    	
    	}
    	
        DocumentRepository rep = userRepositories.get(userId);
        this.getInformationFromStoreContext();
        return rep.listDocs(userId);
    }

    /*
     * From WSDL: <!-- Replaces the entire contents of the document by the
     * contents provided as argument. Faults: document does not exist, user does
     * not exist, repository capacity is exceeded. -->
     */
    public void store(DocUserPair docUserPair, byte[] newContents)
            throws CapacityExceeded_Exception, DocDoesNotExist_Exception,
            UserDoesNotExist_Exception {

        throw new UnsupportedOperationException("Not implemented in minimal version!");
    }

    /*
     * From WSDL: <!-- Returns the current contents of the document. Fault: user
     * or document do not exist -->
     */
    public byte[] load(DocUserPair docUserPair)
            throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {

        throw new UnsupportedOperationException("Not implemented in minimal version!");
    }

    // for testing
    static void reset() {
        userRepositories.clear();
        // as specified in:
        // http://disciplinas.tecnico.ulisboa.pt/leic-sod/2014-2015/labs/proj/test.html
        {
            DocumentRepository rep = new DocumentRepository();
            userRepositories.put("alice", rep);
        }
        {
            DocumentRepository rep = new DocumentRepository();
            userRepositories.put("bruno", rep);
        }
        {
            DocumentRepository rep = new DocumentRepository();
            userRepositories.put("carla", rep);
        }
        {
            DocumentRepository rep = new DocumentRepository();
            userRepositories.put("dimas", rep);
        }
    }
    
    public void getInformationFromStoreContext(){
    	// retrieve message context
        MessageContext messageContext = webServiceContext.getMessageContext();
        
        // *** #6 ***
        // get encryptedTicket token from message context
        String encryptedTicketValue = (String) messageContext.get(RelayServerHandler.REQUEST_TICKET_PROPERTY);
        System.out.printf("%s got token '%s' from response context%n", CLASS_NAME, encryptedTicketValue);
        
        String encryptedAuthorValue = (String) messageContext.get(RelayServerHandler.REQUEST_AUTHOR_PROPERTY);
        System.out.printf("%s got token '%s' from response context%n", CLASS_NAME, encryptedAuthorValue);
        
        String encryptedMacValue = (String) messageContext.get(RelayServerHandler.REQUEST_MAC_PROPERTY);
        System.out.printf("%s got token MAC '%s' from response context%n", CLASS_NAME, encryptedMacValue);
        
        String bodyValue = (String) messageContext.get(RelayServerHandler.REQUEST_BODY_PROPERTY);
        System.out.printf("%s got token Body '%s' from response context%n", CLASS_NAME, bodyValue);
        
        List<String> encryptedValuesFromClient = new ArrayList<String>();
        encryptedValuesFromClient.add(encryptedTicketValue);
        encryptedValuesFromClient.add(encryptedAuthorValue);
        encryptedValuesFromClient.add(encryptedMacValue);
        encryptedValuesFromClient.add(bodyValue);
        
        try {
			DecryptInformationFromClient decryptInformationFromClient = new DecryptInformationFromClient(encryptedValuesFromClient);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

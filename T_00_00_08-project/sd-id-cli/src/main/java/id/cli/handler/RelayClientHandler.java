package id.cli.handler;

import static javax.xml.bind.DatatypeConverter.printHexBinary;
import id.cli.crypto.MessageAuthenticatorGenerator;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class RelayClientHandler implements SOAPHandler<SOAPMessageContext>{
	
	public static final String REQUEST_TICKET_PROPERTY = "my.ticket.property";
	public static final String REQUEST_AUTHOR_PROPERTY = "my.author.property";
	public static final String REQUEST_MAC_PROPERTY = "my.mac.property";
	public static final String SHAREDKEY_PROPERTY = "my.sharedKey.property";
	
    public static final String RESPONSE_PROPERTY = "my.response.property";

    public static final String REQUEST_TICKET_HEADER = "myRequestTicketHeader";
    public static final String REQUEST_AUTHOR_HEADER = "myRequestAuthorHeader";
    public static final String REQUEST_MAC_HEADER = "myRequestMACHeader";
    public static final String REQUEST_NS = "urn:example";

    public static final String RESPONSE_HEADER = "myResponseHeader";
    public static final String RESPONSE_NS = REQUEST_NS;

    public static final String CLASS_NAME = RelayClientHandler.class.getSimpleName();
    public static final String TOKEN = "client-handler";
    
    private byte[] mac = null;


    public boolean handleMessage(SOAPMessageContext smc) {
    	
    	String service = smc.get(MessageContext.WSDL_SERVICE)+"";
        if(service.contains("SDId")){
        	return true;
        }
        
        Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outbound) {
            // outbound message

            // *** #2 ***
            // get token from request context
            String clientTicket = (String) smc.get(REQUEST_TICKET_PROPERTY);
            System.out.printf("%s received TicketInfo '%s'%n", CLASS_NAME, clientTicket);
            
            String clientAuthor = (String) smc.get(REQUEST_AUTHOR_PROPERTY);
            System.out.printf("%s received AuthorInfo '%s'%n", CLASS_NAME, clientAuthor);
            
            String sharedKeyFromSdId = (String) smc.get(SHAREDKEY_PROPERTY);
            System.out.printf("%s received sharedKeyInfo '%s'%n", CLASS_NAME, sharedKeyFromSdId);
            
            // put token in request SOAP header
            try {
                // get SOAP envelope
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();
                
                MessageAuthenticatorGenerator macGenerator = null;
                byte[] bodyBytes = se.getBody().getTextContent().getBytes();
                
                System.out.printf("%s received BODY '%s'%n", CLASS_NAME, se.getBody().getTextContent());
                
                try {
					macGenerator = new MessageAuthenticatorGenerator(sharedKeyFromSdId);
					this.mac = macGenerator.makeMAC(bodyBytes, macGenerator.generateSharedSecretKey());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                // add header
                SOAPHeader sh = se.getHeader();
                if (sh == null)
                    sh = se.addHeader();

                // add header element (name, namespace prefix, namespace)
                Name nameTicket = se.createName(REQUEST_TICKET_HEADER, "e", REQUEST_NS);
                SOAPHeaderElement elementTicket = sh.addHeaderElement(nameTicket);
                
                Name nameAuthor = se.createName(REQUEST_AUTHOR_HEADER, "e", REQUEST_NS);
                SOAPHeaderElement elementAuthor = sh.addHeaderElement(nameAuthor);
                
                Name nameMac = se.createName(REQUEST_MAC_HEADER, "e", REQUEST_NS);
                SOAPHeaderElement elementMac = sh.addHeaderElement(nameMac);
                
                System.out.println("flangoooo = " + this.mac);
                String macToSdStore = printHexBinary(this.mac);
                
                // *** #3 ***
                // add header element value
                elementTicket.addTextNode(clientTicket);
                elementAuthor.addTextNode(clientAuthor);
                elementMac.addTextNode(macToSdStore);
              
                System.out.printf("%s put tokenTicket '%s' on request message header%n", CLASS_NAME, clientTicket);
                System.out.printf("%s put tokenAuthor '%s' on request message header%n", CLASS_NAME, clientAuthor);
                System.out.printf("%s put tokenMAC '%s' on request message header%n", CLASS_NAME, macToSdStore);
                
                msg.writeTo(System.out);
                
            } catch (SOAPException e) {
                System.out.printf("Failed to add SOAP header because of %s%n", e);
            } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        } else {
            // inbound message

            // get token from response SOAP header
            try {
                // get SOAP envelope header
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();
                SOAPHeader sh = se.getHeader();

                // check header
                if (sh == null) {
                    System.out.println("Header not found.");
                    return true;
                }

                // get first header element
                Name name = se.createName(RESPONSE_HEADER, "e", RESPONSE_NS);
                Iterator it = sh.getChildElements(name);
                // check header element
                if (!it.hasNext()) {
                    System.out.printf("Header element %s not found.%n", RESPONSE_HEADER);
                    return true;
                }
                SOAPElement element = (SOAPElement) it.next();

                // *** #10 ***
                // get header element value
                String headerValue = element.getValue();
                System.out.printf("%s got '%s'%n", CLASS_NAME, headerValue);

                // *** #11 ***
                // put token in response context
                String newValue = headerValue + "," + TOKEN;
                System.out.printf("%s put token '%s' on response context%n", CLASS_NAME, TOKEN);
                smc.put(RESPONSE_PROPERTY, newValue);
                // set property scope to application so that client class can access property
                smc.setScope(RESPONSE_PROPERTY, Scope.APPLICATION);

            } catch (SOAPException e) {
                System.out.printf("Failed to get SOAP header because of %s%n", e);
            }

        }

        return true;
    }

    public boolean handleFault(SOAPMessageContext smc) {
        return true;
    }

    public Set<QName> getHeaders() {
        return null;
    }

    public void close(MessageContext messageContext) {
    }
}

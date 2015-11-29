package store.handler;

import java.io.IOException;
import java.util.*;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.*;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.*;


/**
 *  This is the handler server class of the Relay example.
 *
 *  #4 The server handler receives data from the client handler (via inbound SOAP message header).
 *  #5 The server handler passes data to the server (via message context).
 *
 *  *** GO TO server class to see what happens next! ***
 *
 *  #8 The server class receives data from the server (via message context).
 *  #9 The server handler passes data to the client handler (via outbound SOAP message header).
 *
 *  *** GO BACK TO client handler to see what happens next! ***
 */

public class RelayServerHandler implements SOAPHandler<SOAPMessageContext> {

	public static final String REQUEST_TICKET_PROPERTY = "my.ticket.property";
	public static final String REQUEST_AUTHOR_PROPERTY = "my.author.property";
	public static final String REQUEST_MAC_PROPERTY = "my.mac.property";
	public static final String REQUEST_BODY_PROPERTY = "my.body.property";
	
	
    public static final String RESPONSE_PROPERTY = "my.response.property";

    public static final String REQUEST_TICKET_HEADER = "myRequestTicketHeader";
    public static final String REQUEST_AUTHOR_HEADER = "myRequestAuthorHeader";
    public static final String REQUEST_MAC_HEADER = "myRequestMACHeader";
    public static final String REQUEST_NS = "urn:example";

    public static final String RESPONSE_HEADER = "myResponseHeader";
    public static final String RESPONSE_NS = REQUEST_NS;

    public static final String CLASS_NAME = RelayServerHandler.class.getSimpleName();
    public static final String TOKEN = "server-handler";


    public boolean handleMessage(SOAPMessageContext smc) {
        Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outbound) {
            // outbound message

            // *** #8 ***
            // get token from response context
            String propertyValue = (String) smc.get(RESPONSE_PROPERTY);
            System.out.printf("%s received '%s'%n", CLASS_NAME, propertyValue);

            // put token in response SOAP header
            try {
                // get SOAP envelope
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();

                // add header
                SOAPHeader sh = se.getHeader();
                if (sh == null)
                    sh = se.addHeader();

                // add header element (name, namespace prefix, namespace)
                Name name = se.createName(RESPONSE_HEADER, "e", RESPONSE_NS);
                SOAPHeaderElement element = sh.addHeaderElement(name);

                // *** #9 ***
                // add header element value
                String newValue = propertyValue + "," + TOKEN;
                element.addTextNode(newValue);

                System.out.printf("%s put token '%s' on response message header%n", CLASS_NAME, TOKEN);

            } catch (SOAPException e) {
                System.out.printf("Failed to add SOAP header because of %s%n", e);
            }


        } else {
            // inbound message

            // get token from request SOAP header
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

                // get Ticket header element
                Name nameTicket = se.createName(REQUEST_TICKET_HEADER, "e", REQUEST_NS);
                Iterator it = sh.getChildElements(nameTicket);
                // check header element
                if (!it.hasNext()) {
                    System.out.printf("Header element %s not found.%n", REQUEST_TICKET_HEADER);
                    return true;
                }
                SOAPElement elementTicket = (SOAPElement) it.next();

                // *** #4 ***
                // get header element value
                String headerTicketValue = elementTicket.getValue();
                System.out.printf("%s got '%s'%n", CLASS_NAME, headerTicketValue);

                // *** #5 ***
                // put Ticket token in request context
                System.out.printf("%s put token '%s' on request context%n", CLASS_NAME, headerTicketValue);
                smc.put(REQUEST_TICKET_PROPERTY, headerTicketValue);
                // set property scope to application so that server class can access property
                smc.setScope(REQUEST_TICKET_PROPERTY, Scope.APPLICATION);
                
                // get Author header element
                Name nameAuthor = se.createName(REQUEST_AUTHOR_HEADER, "e", REQUEST_NS);
                Iterator itAuthor = sh.getChildElements(nameAuthor);
                // check header element
                if (!itAuthor.hasNext()) {
                    System.out.printf("Header element %s not found.%n", REQUEST_AUTHOR_HEADER);
                    return true;
                }
                SOAPElement elementAuthor = (SOAPElement) itAuthor.next();

                // *** #4 ***
                // get Author header element value
                String headerAuthorValue = elementAuthor.getValue();
                System.out.printf("%s got '%s'%n", CLASS_NAME, headerAuthorValue);

                // *** #5 ***
                // put Author token in request context
                System.out.printf("%s put token '%s' on request context%n", CLASS_NAME, headerAuthorValue);
                smc.put(REQUEST_AUTHOR_PROPERTY, headerAuthorValue);
                // set property scope to application so that server class can access property
                smc.setScope(REQUEST_AUTHOR_PROPERTY, Scope.APPLICATION);
                
                //MAC
                // get MAC header element
                Name nameMac = se.createName(REQUEST_MAC_HEADER, "e", REQUEST_NS);
                Iterator itMac = sh.getChildElements(nameAuthor);
                // check header element
                if (!itMac.hasNext()) {
                    System.out.printf("Header element %s not found.%n", REQUEST_MAC_HEADER);
                    return true;
                }
                SOAPElement elementMac = (SOAPElement) itMac.next();

                // *** #4 ***
                // get MAC header element value
                String headerMacValue = elementMac.getTextContent();
                
                System.out.printf("%s got '%s'%n", CLASS_NAME, headerMacValue);

                // *** #5 ***
                // put MAC token in request context
                System.out.printf("%s put token '%s' on request context%n", CLASS_NAME, headerMacValue);
                smc.put(REQUEST_MAC_PROPERTY, headerMacValue);
                // set property scope to application so that server class can access property
                smc.setScope(REQUEST_MAC_PROPERTY, Scope.APPLICATION);
                
                
                String bodyValue = se.getBody().getTextContent();
                // *** #5 ***
                // put Body token in request context
                System.out.printf("%s put token '%s' on request context%n", CLASS_NAME, bodyValue);
                smc.put(REQUEST_BODY_PROPERTY, bodyValue);
                // set property scope to application so that server class can access property
                smc.setScope(REQUEST_BODY_PROPERTY, Scope.APPLICATION);
                
                msg.writeTo(System.out);

            } catch (SOAPException e) {
                System.out.printf("Failed to get SOAP header because of %s%n", e);
            } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

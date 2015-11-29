package pt.ulisboa.tecnico.sdis.store.ws.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.Endpoint;

import pt.ulisboa.tecnico.sdis.store.ws.uddi.UDDINaming;
import store.replication.FrontEnd;

public class StoreMain {
	
	private static StoreImpl impl;
	private static FrontEnd frontEnd;
	
	
    public static void main(String[] args) {
    	
    	List<String> wsNamesPublished = new ArrayList<String>();
    	
        // Check arguments
        if (args.length == 0 || args.length == 2) {
            System.err.println("Argument(s) missing!");
            System.err.println("Usage: java " + StoreMain.class.getName()
                    + " wsURL OR uddiURL wsName wsURL");
            return;
        }
        String uddiURL = null;
        String wsName = null;
        String wsURL = null;
        if (args.length == 1) {
            wsURL = args[0];
        } else if (args.length >= 3) {
            uddiURL = args[0];
            wsName = args[1];
            wsNamesPublished.add(wsName);
            wsURL = args[2];
        }
        String wsTestUrl = wsURL + "/test";

        Endpoint endpoint = null;
        Endpoint testEndpoint = null;

        UDDINaming uddiNaming = null;
        
        try {
        	//System.out.println("SD-STORE " + wsName);
        	impl = new StoreImpl();
        	if (System.getProperty("store-ws.test") != null) {
        		System.out.println("Populating test data...");
        	}
        	endpoint = Endpoint.create(impl);


        	// publish endpoint
        	System.out.printf("Starting %s%n", wsURL);
        	endpoint.publish(wsURL);

        	// publish to UDDI
        	if (uddiURL != null) {
        		System.out.printf("Publishing '%s' to UDDI at %s%n", wsName,
        				uddiURL);
        		uddiNaming = new UDDINaming(uddiURL);
        		uddiNaming.bind(wsName, wsURL);
        		System.out.println();
        		if(wsURL.contains("8090")){
        			frontEnd = new FrontEnd(wsNamesPublished, uddiNaming);
        		}
        	}

        	if ("true".equalsIgnoreCase(System.getProperty("ws.test"))) {
        		impl.reset();

        		System.out.printf("Starting %s%n", wsTestUrl);
        		testEndpoint = Endpoint.create(new TestControl());
        		testEndpoint.publish(wsTestUrl);
        	}

        	// wait
        	System.out.println("Awaiting connections");
        	System.out.println("Press enter to shutdown");
        	System.in.read();

        } catch (Exception e) {
        	System.out.printf("Caught exception: %s%n", e);
        	e.printStackTrace();

        } finally {
        	try {
        		if (endpoint != null) {
        			// stop endpoint
        			endpoint.stop();
        			System.out.printf("Stopped %s%n", wsURL);
        		}
        		if (testEndpoint != null) {
        			// stop test endpoint
        			testEndpoint.stop();
        			System.out.printf("Stopped %s%n", wsTestUrl);
        		}
        	} catch (Exception e) {
        		System.out.printf("Caught exception when stopping: %s%n", e);
        	}
        	try {
        		if (uddiNaming != null) {
        			// delete from UDDI
        			uddiNaming.unbind(wsName);
        			wsNamesPublished.remove(wsName);
        			System.out.printf("Deleted '%s' from UDDI%n", wsName);
        		}
        	} catch (Exception e) {
        		System.out.printf("Caught exception when deleting: %s%n", e);
        	}
        }
    }
	
//	public void setSecondarytoPrimary() throws DatatypeConfigurationException{
//		
//		System.out.println("PASSOU AQUI\n");
//		UDDINaming uddiNaming = null;
//	    try {
//	        // publish to UDDI
//	        System.out.printf("Publishing '%s' to UDDI at %s%n", "Secondary SD-STORE", "http://localhost:8081");
//	        uddiNaming = new UDDINaming("http://localhost:8081");
//	        uddiNaming.rebind("SD-STORE", "http://localhost:8091/SD-STORE/endpoint");
//	        System.out.println("PENSO QUE ASSUMI O PAPEL PRINCIPAL");
//		
//	        // wait
//	        System.out.println("Awaiting connections");
//	        			        
//	        System.out.println("Press enter to shutdown");
//	        System.in.read();
//	
//	    } catch(Exception e) {
//	        System.out.printf("Caught exception: %s%n", e);
//	        e.printStackTrace();
//	
//	    } 
//	    finally {
//
//	    	try {
//	    		if (uddiNaming != null) {
//	    			// delete from UDDI
//	    			uddiNaming.unbind("SD-STORE");
//	    			System.out.printf("Deleted '%s' from UDDI%n", "SD-STORE");
//	    		}
//	    	} catch(Exception e) {
//	    		System.out.printf("Caught exception when deleting: %s%n", e);
//	    	}
//	    }
//		
//	}

}

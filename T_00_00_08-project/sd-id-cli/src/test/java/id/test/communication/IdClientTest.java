package id.test.communication;

import java.util.*;

import org.junit.*;

import static org.junit.Assert.*;
import mockit.*;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import pt.ulisboa.tecnico.sdis.id.ws.*;
import id.cli.IdClient;
import id.ws.uddi.UDDINaming;

public class IdClientTest {
	
	//Variaveis
	private static String uddiURL = "http://localhost:8081";
	private String name = "SD-ID";
    
    /**
     *  In this test the server is mocked to
     *  simulate a communication exception.
     */
    @Test(expected=WebServiceException.class)
    public <P extends SDId & BindingProvider> void testMockServerException(@Mocked final SDId_Service service, @Mocked final P port) throws Exception {

        final Map<String,Object> map = new HashMap<String,Object>();
    	
        new Expectations() {{
            new SDId_Service();
            service.getSDIdImplPort(); result = port;
            port.getRequestContext(); result = map;
            port.createUser(anyString, anyString);
            result = new WebServiceException("Web Service nao disponivel");
        }};
        
        IdClient client = new IdClient(uddiURL, name);
        client.createUser("lexi","lexi@pt");
    }
    
    @Test
    public <P extends SDId & BindingProvider> void testMockServerExceptionOnSecondCall(@Mocked final SDId_Service service,@Mocked final P port)throws Exception {
    	
    	final Map<String,Object> map = new HashMap<String,Object>();
    	
    	new Expectations() {{
            new SDId_Service();
            service.getSDIdImplPort(); result = port;
            port.getRequestContext(); result = map;
            port.renewPassword("alice");
            //first call to renewPassword gives success
            result = null;
            //second call fails
            result = new WebServiceException("Web Service nao disponivel");
        }};


        // Unit under test is exercised.
        IdClient client = new IdClient(uddiURL, name);

        // first call to mocked server
        try {
            client.renewPassword("alice");

        } catch(WebServiceException e) {
            // exception is not expected
            fail();
        }

        // second call to mocked server
        try {
            client.renewPassword("alice");
            fail();
        
        } catch(WebServiceException e) {
            // exception is expected
            assertEquals("Web Service nao disponivel", e.getMessage());
        }
    }
    
    @Test 
    public <P extends SDId & BindingProvider> void testMockServer(@Mocked final SDId_Service service,@Mocked final P port)throws Exception {

    	final Map<String,Object> map = new HashMap<String,Object>();
    	
        new Expectations() {{
            new SDId_Service();
            service.getSDIdImplPort(); result = port;
            port.getRequestContext(); result = map;
            port.createUser(anyString, anyString);
            // first call to create user gives success
            result = null;
            // second call throws an exception
            result = new InvalidEmail_Exception("O email introduzido @pt tem um formato invalido", new InvalidEmail());
        }};


        // Unit under test is exercised.
        IdClient client = new IdClient(uddiURL, name);

        // first call to mocked server
        client.createUser("Abilio","abilio@pt");

        // second call to mocked server
        try {
            client.createUser("Bilio","@pt");
            fail();
        } catch(InvalidEmail_Exception e) {
            // exception is expected
            assertEquals("O email introduzido @pt tem um formato invalido", e.getMessage());
        }


        // a "verification block"
        // One or more invocations to mocked types, causing expectations to be verified.
        new Verifications() {{
            
            port.createUser(anyString, anyString); maxTimes = 2;
        }};
    } 
    
    @Test (expected = JAXRException.class)
    public void notAvailableWSTest(@Mocked final UDDINaming uddiNaming)throws Exception {
    	
        new Expectations() {{
        	new UDDINaming(anyString); result = new JAXRException("Servidor de nomes JUDDI nao esta a correr no endereco esperado");
        	//uddiNaming.lookup(name); 
        }};
        
        IdClient client = new IdClient("http://localhost:8082", name);
        client.createUser("lexi","lexi@pt");
    }
    
    
  @Test(expected = NullPointerException.class)
  public <P extends SDId & BindingProvider> void testMockServerNullPointerException(@Mocked final SDId_Service service,@Mocked final P port)throws Exception {
  	
  	final Map<String,Object> map = new HashMap<String,Object>();
  	
  	new Expectations() {{
          new SDId_Service();
          service.getSDIdImplPort(); result = port;
          port.getRequestContext(); result = map;
          port.removeUser("euclides");
          result = new NullPointerException(null);
      }};
      
      IdClient client = new IdClient(uddiURL, name);
      client.removeUser("euclides");
  }    
  
      
}

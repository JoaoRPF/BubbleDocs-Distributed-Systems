package id.test.contract;

import static org.junit.Assert.assertEquals;

import javax.xml.registry.JAXRException;

import id.cli.IdClient;
import id.cli.IdClientException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.sdis.id.ws.EmailAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidEmail_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidUser_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.SDId;
import pt.ulisboa.tecnico.sdis.id.ws.SDId_Service;
import pt.ulisboa.tecnico.sdis.id.ws.UserAlreadyExists_Exception;

public class CreateUserContractTest {
	
	private String url = "http://localhost:8081";
	private String wsName = "SD-ID";
	
	private IdClient port = null;
	
	@Before
	public void setUp(){
		try {
			port = new IdClient(url, wsName);
		} catch (IdClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@After
	public void tearDown(){
		port = null;
	}
	
	@Test(expected =  UserAlreadyExists_Exception.class)
	public void success() throws Exception { //Verificar se cria user com sucesso porque quando se insere um segundo com nome igual ja nao da
		port.createUser("Jackson", "jackson@hotmail.com");
		port.createUser("Jackson", "brahimi@hotmail.com"); //Para verificar que o utilizador Jackson foi criado com sucesso
	}
	
	@Test(expected = EmailAlreadyExists_Exception.class)
	public void emailExists() throws Exception {
		port.createUser("Tello", "jackson@pt");
		port.createUser("Martinez", "jackson@pt");
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void invalidEmail() throws Exception {
		port.createUser("Jonas", "jacksonEmail");  
	}

	@Test(expected = InvalidUser_Exception.class)
	public void invalidUsernameNull() throws Exception {
		port.createUser(null, "oliverTorres@ptt");  
	}
	 
	 @Test(expected = InvalidUser_Exception.class)
	   public void invalidUsername() throws Exception {

		 port.createUser("","mauricio@pt"); 
	   }
	 
	 @Test(expected =InvalidEmail_Exception.class)
	   public void invalidEmailLeft() throws Exception {
	      	port.createUser("juliano1","@juliano");
	   
	   }
	 
	 @Test(expected =InvalidEmail_Exception.class)
	   public void invalidEmailAt() throws Exception {
	      	port.createUser("juliano2","@");
	   
	   }
	 
	 @Test(expected =InvalidEmail_Exception.class)
	   public void invalidEmailName() throws Exception {
	      	port.createUser("juliano3","juliano");
	   
	   }
}
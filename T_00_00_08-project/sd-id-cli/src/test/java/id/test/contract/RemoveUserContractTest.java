package id.test.contract;

import id.cli.IdClient;
import id.cli.IdClientException;

import javax.xml.registry.JAXRException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.sdis.id.ws.UserDoesNotExist_Exception;

public class RemoveUserContractTest {
	
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
	
	@Test(expected =  UserDoesNotExist_Exception.class) 
	public void success() throws Exception {
		
		port.createUser("Chico", "chico@pt");

		port.removeUser("Chico");
		port.removeUser("Chico");
	}
	
	@Test(expected = UserDoesNotExist_Exception.class)
	public void userDoesNotExistTest() throws Exception{
		port.removeUser("Chico2");
	}
	
	@Test(expected = UserDoesNotExist_Exception.class)
	public void invalidUsernameNull() throws Exception {
		port.removeUser(null); 
	}
	
	@Test(expected = UserDoesNotExist_Exception.class)
	public void emptyUsername() throws Exception {
		port.removeUser(""); 
	}
	
}

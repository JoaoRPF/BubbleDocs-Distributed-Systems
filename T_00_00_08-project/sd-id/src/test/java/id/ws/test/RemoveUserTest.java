package id.ws.test;

import static org.junit.Assert.assertTrue;
import id.ws.impl.IdImpl;
import id.ws.impl.UserManagement;

import org.junit.Test;

import pt.ulisboa.tecnico.sdis.id.ws.InvalidUser_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserDoesNotExist_Exception;

public class RemoveUserTest {
	
	@Test 
	public void success() throws Exception {

		IdImpl port = new IdImpl();
		UserManagement userManagement = port.getUserManagement();
		boolean deleted = false;
		
		port.createUser("Chico", "chico@pt");

		port.removeUser("Chico");
		if(userManagement.getApplicationUsers().get("Chico") == null)
			deleted = true;
		
		assertTrue(deleted);
	}
	
	@Test(expected = UserDoesNotExist_Exception.class)
	public void userDoesNotExistTest() throws Exception{
		
		IdImpl port = new IdImpl();	
		port.removeUser("Chico");
	}
	
	@Test(expected = UserDoesNotExist_Exception.class)
	public void invalidUsernameNull() throws Exception {

		IdImpl port= new IdImpl();
		port.removeUser(null); 
	}
	
	@Test(expected = UserDoesNotExist_Exception.class)
	public void emptyUsername() throws Exception {

		IdImpl port= new IdImpl();
		port.removeUser(""); 
	}
}

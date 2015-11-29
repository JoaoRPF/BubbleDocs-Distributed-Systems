package id.ws.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import id.ws.impl.IdImpl;
import id.ws.impl.UserManagement;

import org.junit.Test;

import pt.ulisboa.tecnico.sdis.id.ws.UserDoesNotExist_Exception;

public class RenewPasswordTest {
	
	@Test 
	public void success() throws Exception {

		IdImpl port = new IdImpl();
		UserManagement userManagement = port.getUserManagement();
		
		port.createUser("Chico", "chico@pt");
		String oldPass = userManagement.getApplicationUsers().get("Chico").getUserPass();

		port.renewPassword("Chico");
		String newPass = userManagement.getApplicationUsers().get("Chico").getUserPass();
		
		assertThat(oldPass, not(equalTo(newPass)));
	}
	
	@Test(expected = UserDoesNotExist_Exception.class)
	public void userDoesNotExistTest() throws Exception{
		
		IdImpl port = new IdImpl();	
		port.renewPassword("Chico");
	}
	
	@Test(expected = UserDoesNotExist_Exception.class)
	public void invalidUsernameNull() throws Exception {

		IdImpl port= new IdImpl();
		port.renewPassword(null); 
	}
	
	@Test(expected = UserDoesNotExist_Exception.class)
	public void emptyUsername() throws Exception {

		IdImpl port= new IdImpl();
		port.renewPassword(""); 
	}
}
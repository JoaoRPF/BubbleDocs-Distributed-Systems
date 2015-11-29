package id.ws.test;

import static org.junit.Assert.assertEquals;
import id.ws.impl.IdImpl;
import id.ws.impl.UserManagement;

import org.junit.Test;

import pt.ulisboa.tecnico.sdis.id.ws.EmailAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidEmail_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidUser_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserAlreadyExists_Exception;

public class CreateUserTest {

	@Test 
	public void success() throws Exception {
		IdImpl port= new IdImpl();
		UserManagement userManagement = port.getUserManagement();

		port.createUser("Chica", "chica@pt");
		assertEquals("Chica", userManagement.getUserById("Chica").getUserId());
		assertEquals("chica@pt", userManagement.getUserById("Chica").getEmailAdress());
	}
	
	
	@Test(expected = UserAlreadyExists_Exception.class)
    public void usernameExists() throws Exception{
    	
		IdImpl port= new IdImpl();
		port.createUser("Chica", "ica@pt");
    	port.createUser("Chica", "chica@pt");    	
    }
	
	@Test(expected = EmailAlreadyExists_Exception.class)
	public void emailExists() throws Exception {
		IdImpl port= new IdImpl();
		port.createUser("Chica", "chica@pt");
		port.createUser("zezito", "chica@pt");
	}
	
	 @Test(expected =InvalidEmail_Exception.class)
	   public void invalidEmail() throws Exception {
		 	IdImpl port= new IdImpl();
	      	port.createUser("juliano","juliano@");
	   
	   }
	 
	 @Test(expected = InvalidUser_Exception.class)
	   public void invalidUsernameNull() throws Exception {

		 IdImpl port= new IdImpl();
		 port.createUser(null,"mauricio@pt"); 
	   }
	 
	 @Test(expected = InvalidUser_Exception.class)
	   public void invalidUsername() throws Exception {

		 IdImpl port= new IdImpl();
		 port.createUser("","mauricio@pt"); 
	   }
	 
	 @Test(expected =InvalidEmail_Exception.class)
	   public void invalidEmailLeft() throws Exception {
		 	IdImpl port= new IdImpl();
	      	port.createUser("juliano","@juliano");
	   
	   }
	 
	 @Test(expected =InvalidEmail_Exception.class)
	   public void invalidEmailAt() throws Exception {
		 	IdImpl port= new IdImpl();
	      	port.createUser("juliano","@");
	   
	   }
	 
	 @Test(expected =InvalidEmail_Exception.class)
	   public void invalidEmailName() throws Exception {
		 	IdImpl port= new IdImpl();
	      	port.createUser("juliano","juliano");
	   
	   }
}





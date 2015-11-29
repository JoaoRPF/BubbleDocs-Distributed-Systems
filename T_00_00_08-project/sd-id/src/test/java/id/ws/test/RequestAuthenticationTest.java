package id.ws.test;

import static org.junit.Assert.assertTrue;
import id.ws.impl.IdImpl;
import id.ws.impl.UserManagement;

import org.junit.Test;

import pt.ulisboa.tecnico.sdis.id.ws.AuthReqFailed_Exception;

public class RequestAuthenticationTest {
	
//	@Test 
//	public void success() throws Exception {
//
//		IdImpl port = new IdImpl();
//		UserManagement userManagement = port.getUserManagement();
//		String chicoPass;
//		byte[] chicoPassBytes;
//		
//		port.createUser("Chico", "chico@pt");
//		chicoPass = userManagement.getApplicationUsers().get("Chico").getUserPass();
//		chicoPassBytes = chicoPass.getBytes();
//		
//		byte[] authentication = port.requestAuthentication("Chico", chicoPassBytes);
//		boolean booleanAuthentication = authentication[0]!=0;
//		
//		assertTrue(booleanAuthentication);
//	}
//	
//	@Test(expected = AuthReqFailed_Exception.class)
//	public void userDoesNotExistTest() throws Exception {
//		
//		IdImpl port = new IdImpl();
//		byte[] chicoPassBytes = "pass".getBytes();
//		
//		port.requestAuthentication("Chico", chicoPassBytes);
//	}
//	
//	@Test(expected = AuthReqFailed_Exception.class)
//	public void InvalidPassTest() throws Exception {
//		
//		IdImpl port = new IdImpl();
//		byte[] chicoPassBytesWrong;
//		chicoPassBytesWrong = "aaabbbccc".getBytes();
//		
//		port.createUser("Chico","chico@pt");
//		port.requestAuthentication("Chico", chicoPassBytesWrong);	
//	}
}

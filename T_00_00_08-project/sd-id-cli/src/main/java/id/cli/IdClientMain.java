package id.cli;

import id.cli.store.StoreClient;
import pt.ulisboa.tecnico.sdis.id.ws.AuthReqFailed_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.EmailAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidEmail_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidUser_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserDoesNotExist_Exception;

public class IdClientMain {

	public static void main(String[] args) throws Exception {
		
		IdClient client = new IdClient(args[0], args[1]);
		client.setNameService(args[2]);
		
		/*
		try{
			System.out.println("Creating User Abilio...");
			client.createUser("abilio", "abilio@batata.pt");
		}catch(EmailAlreadyExists_Exception e){
			System.out.println("Caught email already exists exception. " + e.getMessage());
		}catch(InvalidEmail_Exception e){
			System.out.println("Caught invalid mail exception. " + e.getMessage());
		}catch(InvalidUser_Exception e){
			System.out.println("Caught invalid user exception. " + e.getMessage());
		}catch(UserAlreadyExists_Exception e){
			System.out.println("Caught user already exists exception. " + e.getMessage());
		}
		
		try{
			System.out.println("Renewing Password for Abilio...");
			client.renewPassword("abilio");
		}catch(UserDoesNotExist_Exception e){
			System.out.println("Caught user does not exists exception. " + e.getMessage());
		}
		
		try{
			System.out.println("Removing User Miguel...");
			client.removeUser("miguel");
		}catch(UserDoesNotExist_Exception e){
			System.out.println("Caught expected user does not exist exception. " + e.getMessage());
		}
		
		try{
			System.out.println("Removing User Abilio...");
			client.removeUser("abilio");
		}catch(UserDoesNotExist_Exception e){
			System.out.println("Caught user does not exist exception. " + e.getMessage());
		}*/
		
		try{
			byte[] pass = "Aaa1".getBytes("UTF-8");
			client.requestAuthentication("alice", pass);
			
		}catch(AuthReqFailed_Exception e){
			System.out.println("Request authentication for user eduardo failed. " + e.getMessage());
		}
	}

}

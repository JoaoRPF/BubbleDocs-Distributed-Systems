package id.ws.impl;

import id.ws.crypto.AuthenticationXml;
import id.ws.crypto.ResponseXml;
import id.ws.crypto.ServerPasswordEncryption;

import javax.jws.WebService;

import pt.ulisboa.tecnico.sdis.id.ws.AuthReqFailed_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.EmailAlreadyExists;
import pt.ulisboa.tecnico.sdis.id.ws.EmailAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidEmail;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidEmail_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidUser;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidUser_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.SDId;
import pt.ulisboa.tecnico.sdis.id.ws.UserAlreadyExists;
import pt.ulisboa.tecnico.sdis.id.ws.UserAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserDoesNotExist;
import pt.ulisboa.tecnico.sdis.id.ws.UserDoesNotExist_Exception;

@WebService(
	    endpointInterface="pt.ulisboa.tecnico.sdis.id.ws.SDId", 
	    wsdlLocation="SD-ID.wsdl",
	    name="SdId",
	    portName="SDIdImplPort",
	    targetNamespace="urn:pt:ulisboa:tecnico:sdis:id:ws",
	    serviceName="SDId"
	)
public class IdImpl implements SDId {

	private UserManagement userManagement = new UserManagement();
	
	public UserManagement getUserManagement(){
		return this.userManagement;
	}
	
	public void createUser(String userId, String emailAddress)
			throws EmailAlreadyExists_Exception, InvalidEmail_Exception,
			InvalidUser_Exception, UserAlreadyExists_Exception {
			
		if(userManagement.verifyCorrectEmail(emailAddress)){
			if(userManagement.verifyExistingEmail(emailAddress)){
				if(userManagement.verifyExistingUser(userId)){
					if(userId != null && !userId.isEmpty()){
						User createdUser = new User(userId, emailAddress);
						this.userManagement.addUserToApplication(createdUser);
					}
					else{
						InvalidUser invalidUser = new InvalidUser();
						throw new InvalidUser_Exception("O User com o ID " + userId + " e invalido", invalidUser);
					}
					
				}
				else{
					UserAlreadyExists userAlreadyExists = new UserAlreadyExists();
					throw new UserAlreadyExists_Exception("O User com o ID " + userId + " ja existe",userAlreadyExists);
				}
			}
			else{
				EmailAlreadyExists emailAlreadyExists = new EmailAlreadyExists();
				throw new EmailAlreadyExists_Exception("O email " + emailAddress + " ja existe",emailAlreadyExists);
			}
		}
		else{
			InvalidEmail invalidEmail = new InvalidEmail();
			throw new InvalidEmail_Exception("O email introduzido " + emailAddress + " tem um formato invalido",invalidEmail);
		}
						
	}

	public void renewPassword(String userId) throws UserDoesNotExist_Exception {
		boolean existingUser = false;
		if(userId != null && !userId.isEmpty()){
			if(!userManagement.verifyExistingUser(userId)){
				this.userManagement.getUserById(userId).setUserNewPass();
				existingUser = true;
			}
		}
		if (!existingUser){
			UserDoesNotExist userDoesNotExist = new UserDoesNotExist();
			throw new UserDoesNotExist_Exception("O User com o ID " + userId + " nao existe",userDoesNotExist);
		}
	}

	public void removeUser(String userId) throws UserDoesNotExist_Exception {
		boolean existingUser = false;
		if(userId != null && !userId.isEmpty()){
			if(!userManagement.verifyExistingUser(userId)){
				User userToRemove = userManagement.getUserById(userId);
				userManagement.removeUserFromApplication(userToRemove);
				existingUser = true;
			}
		}
		if (!existingUser){
			UserDoesNotExist userDoesNotExist = new UserDoesNotExist();
			throw new UserDoesNotExist_Exception("O User com o ID " + userId + " nao existe",userDoesNotExist);
		}

	}

	public byte[] requestAuthentication(String userId, byte[] reserved)
			throws AuthReqFailed_Exception {
		
		byte[] encryptionDocumentBytes = null;
		ResponseXml credentialsAuthentication = null;
	
		try {
			AuthenticationXml authXml = new AuthenticationXml();
			authXml.createDocumentFromAuthentication(reserved);
			ServerPasswordEncryption passEncryption = new ServerPasswordEncryption(this.userManagement, userId);
			byte[] digestedPassword = passEncryption.getDigestPassword();
		    credentialsAuthentication = new ResponseXml(digestedPassword, authXml.getDocument());
		    encryptionDocumentBytes = credentialsAuthentication.getEncryptionDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptionDocumentBytes;
	}
	
	public void init (){
		User alice = new User ("alice","Aaa1","alice@tecnico.pt");
		this.userManagement.addUserToApplication(alice);
		
		User bruno = new User ("bruno","Bbb2","bruno@tecnico.pt");
		this.userManagement.addUserToApplication(bruno);
		
		User carla = new User ("carla","Ccc3","carla@tecnico.pt");
		this.userManagement.addUserToApplication(carla);
		
		User duarte = new User ("duarte","Ddd4","duarte@tecnico.pt");
		this.userManagement.addUserToApplication(duarte);
		
		User eduardo = new User ("eduardo","Eee5","eduardo@tecnico.pt");
		this.userManagement.addUserToApplication(eduardo);
		
	}
}
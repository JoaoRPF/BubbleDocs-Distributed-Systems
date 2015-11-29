package id.ws.impl;

import java.util.HashMap;

public class UserManagement {
	
	private HashMap<String, User> applicationUsers = new HashMap<String, User>();
	
	public UserManagement(){
		this.applicationUsers.clear();
	}
	
	public HashMap<String, User> getApplicationUsers(){
		return this.applicationUsers;
	}
	
	public void addUserToApplication(User newUser){
		this.applicationUsers.put(newUser.getUserId(), newUser);
	}
	
	public void removeUserFromApplication(User userToRemove){
		this.applicationUsers.remove(userToRemove.getUserId());
	}
	
	public User getUserById(String userId){
		return this.applicationUsers.get(userId);
	}
	
	public boolean verifyCorrectEmail(String email){
		if(email.charAt((email.length()-1)) == '@' || email.charAt(0) == '@' || !email.contains("@"))
			return false;
		
		String[] result = email.split("@");
		
		if(!result[0].isEmpty() || result[0] != null || !result[1].isEmpty() || result[1] != null)
			return true;
		else return false;
	}
	
	public boolean verifyExistingEmail(String email){
		for(User user : this.applicationUsers.values()){
			if(user.getEmailAdress().equals(email))
				return false;
		}
		return true;
	}
	
	public boolean verifyExistingUser(String userId){
		for(User user : this.applicationUsers.values()){
			if(user.getUserId().equals(userId))
				return false;
		}
		return true;
	}
	
	public boolean verifyCorrectPassword(byte[] password){
		String stringPass = new String(password);
		for (User user : this.applicationUsers.values()){
			if(!(user.getUserPass().equals(stringPass)))
				return false;
		}
		return true;
	}
}

package id.ws.impl;

import java.util.Random;

public class User {
	private String id;
	private String pass;
	private String email;
	
	public User(String id, String pass, String email){
		this.id = id;
		this.pass = pass;
		this.email = email;
	}
	
	public User(String id, String email){
		this.id = id;
		this.email = email;
		this.pass= this.newUserPass();
	}
	

	public String getUserId(){
		return this.id;
	}
	
	public void setUserId(String id){
		this.id=id;
	}
	
	public String getUserPass(){
		return this.pass;
	}
	
	public void setUserPass(String pass){
		this.pass = pass;
	}
	
	public void setUserNewPass(){
		this.pass = this.newUserPass(); 
	}
	
	public String getEmailAdress(){
		return this.email;
	}
	
	public void setEmailAdress(String email){
		this.email=email;
	}
	
	public String newUserPass() {
		
		Random rand = new Random();
		String newPass = (rand.nextInt(100) + 0) + this.id + (rand.nextInt(100) + 0);
		return newPass;
	}
	
}
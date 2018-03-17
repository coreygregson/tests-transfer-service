/**
 * 
 */
package com.ingenico.transferservice.model;

/**
 * @author cgregson
 * Simple POJO representing the account Holder.
 */
public class AccountHolder {
	
	private String firstName;
	private String middleName;
	private String surname;
	
	/**
	 * Empty constructor required for marshalling / unmarshalling. 
	 */
	public AccountHolder() {}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getMiddleName() {
		return middleName;
	}
	
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}	
}

/**
 * 
 */
package com.ingenico.transferservice.model;

/**
 * @author cgregson
 * Simple POJO representing an account. 
 */
public class Account {

	private long accountNumber;
	private double balance;	
	private AccountHolder accountHolder;	
	
	/**
	 * Empty constructor required for marshalling / unmarshalling. 
	 */
	public Account() {}
	
	
	public Account(long accountNumber, double balance, AccountHolder accountHolder) {
		this.accountNumber = accountNumber;
		this.balance = balance;
		this.accountHolder = accountHolder;
	}
	
	public long getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public AccountHolder getAccountHolder() {
		return accountHolder;
	}

	public void setAccountHolder(AccountHolder accountHolder) {
		this.accountHolder = accountHolder;
	}

	
}

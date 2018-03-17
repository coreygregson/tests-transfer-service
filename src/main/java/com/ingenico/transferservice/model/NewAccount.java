/**
 * 
 */
package com.ingenico.transferservice.model;

/**
 * @author cgregson
 * Simple POJO representing the information to open a new account.
 */
public class NewAccount {

	private AccountHolder accountHolder;
	private double initialDeposit;
	
	/**
	 * Empty constructor required for marshalling / unmarshalling.
	 */
	public NewAccount() {}

	public AccountHolder getAccountHolder() {
		return accountHolder;
	}

	public void setAccountHolder(AccountHolder accountHolder) {
		this.accountHolder = accountHolder;
	}

	public double getInitialDeposit() {
		return initialDeposit;
	}

	public void setInitialDeposit(double initialDeposit) {
		this.initialDeposit = initialDeposit;
	}
}

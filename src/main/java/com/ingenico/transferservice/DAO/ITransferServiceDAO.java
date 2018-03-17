/**
 * 
 */
package com.ingenico.transferservice.DAO;

import com.ingenico.transferservice.model.AccountHolder;

/**
 * @author cgregson
 * Interface to encapsulate the DAO functionality
 */
public interface ITransferServiceDAO {
	
	/**
	 * Create and save a new account.
	 * @param accountNumber Account Number to use for the new account.
	 * @param initialBalance Initial balance of the account.
	 * @param accountHolder The holder of the account.
	 */
	public void openAccount(long accountNumber, double initialBalance, AccountHolder accountHolder);
	
	/**
	 * Return the current balance of an account.
	 * @param accountNumber Number of the account to return the balance for.
	 * @return The balance of the account.
	 * @throws IllegalArgumentException Thrown if the account does not exist.
	 */
	public double checkBalance(long accountNumber) throws IllegalArgumentException;
	
	/**
	 * Update the balance of the account by adding the new amount to its current balance.
	 * @param sourceAccountNumber Account to transfer the amount from. 
	 * @param targetAccountNumber Account to transfer the amount to.
	 * @param amount Must be positive.
	 * @throws IllegalArgumentException Thrown if the account does not exist, or if the source account would be overdrawn(negative balance);
	 */
	public void transferMoney(long sourceAccountNumber, long targetAccountNumber, double amount) throws IllegalArgumentException;
}

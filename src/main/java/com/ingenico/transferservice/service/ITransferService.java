/**
 * 
 */
package com.ingenico.transferservice.service;

import org.springframework.stereotype.Service;

import com.ingenico.transferservice.model.NewAccount;
import com.ingenico.transferservice.model.Transfer;

/**
 * @author cgregson
 * Interface for the Service Layer, where the business logic will be encapsulated.
 */
@Service
public interface ITransferService {

	/**
	 * Create and save a new account.
	 * @param newAccount The account to open.
	 * @return The account number of the newly created account.
	 * @throws IllegalArgumentException Thrown if the initial balance will be negative.
	 */
	public long openAccount(NewAccount newAccount) throws IllegalArgumentException;
	
	/**
	 * Transfer money between accounts.
	 * @param transfer The transfer info to execute.
	 * @throws IllegalArgumentException Thrown if an account does not exist, if the amount is less than zero or if either balance will become negative.
	 */
	public void transferMoney(Transfer transfer) throws IllegalArgumentException;
	
	/**
	 * Returns the current balance of an account.
	 * @param accountNumber The account to check the balance.
	 * @return The balance of the account.
	 * @throws IllegalArgumentException Thrown if the account does not exist.
	 */
	public double checkBalance(long accountNumber) throws IllegalArgumentException;
}

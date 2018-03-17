/**
 * 
 */
package com.ingenico.transferservice.DAO;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ingenico.transferservice.model.Account;
import com.ingenico.transferservice.model.AccountHolder;

/**
 * 
 * @author cgregson
 * Implementation of the Data Access Layer. Data is simply stored in a thread safe container.  Obviously in a real world scenario, this would not be acceptable and a 
 * real data store system should be used.
 */

@Repository
@Transactional(rollbackFor=Exception.class, propagation=Propagation.MANDATORY)
public class TransferServiceDAO implements ITransferServiceDAO {
	
	private ConcurrentHashMap<Long, Account> accounts;
	
	/**
	 * Constructor, initializes the collection.
	 */
	public TransferServiceDAO() {
		accounts = new ConcurrentHashMap<>();
	}
	
	@Override
	public void openAccount(long accountNumber, double initialBalance, AccountHolder accountHolder) {
		Account account = new Account(accountNumber, initialBalance, accountHolder);		
		setAccount(account, accountNumber);		
	}

	@Override
	public double checkBalance(long accountNumber) throws IllegalArgumentException {
		
		Account account = accounts.get(accountNumber);
		
		if(account == null ) {
			throw new IllegalArgumentException(MessageFormat.format("The account {0} could not be found.", accountNumber));
		}
		
		return account.getBalance();
	}
	
	@Override
	public void transferMoney(long sourceAccountNumber, long targetAccountNumber, double transactionValue)
			throws IllegalArgumentException {
		
		if(transactionValue <= 0) {
			throw new IllegalArgumentException(MessageFormat.format("The transaction cannot be processed as the transaction value {0} cannot be negative.", transactionValue));
		}
		
		Account sourceAccount = getAccount(sourceAccountNumber);		
		Account targetAccount = getAccount(targetAccountNumber);
				
		double updatedSourceBalance = sourceAccount.getBalance() - transactionValue;
		
		if(updatedSourceBalance < 0) {
			throw new IllegalArgumentException(MessageFormat.format("The transaction cannot be processed as the account {0} would become overdrawn.", sourceAccountNumber));
		} 
		
		sourceAccount.setBalance(updatedSourceBalance);
		targetAccount.setBalance(targetAccount.getBalance() + transactionValue);
		
		setAccount(sourceAccount, sourceAccountNumber);
		setAccount(targetAccount, targetAccountNumber);
				
	}
	
	/**
	 * Return the account / checks the account exists in the data store. 
	 * @param accountNumber The account to return.
	 * @return The account.
	 * @throws IllegalArgumentException Thrown if the account does not exist.
	 */
	private Account getAccount(long accountNumber) throws IllegalArgumentException {
		Account account = accounts.get(accountNumber);		
		
		if(account == null) {
			throw new IllegalArgumentException(MessageFormat.format("The account {0} could not be found.", accountNumber));
		}
		return account;
	}
	
	/**
	 * Update / Create an account in the data store.
	 * @param account Account to add.
	 * @param accountNumber Number for the account.
	 * @throws IllegalArgumentException Thrown when the account is null.
	 */
	private void setAccount(Account account, long accountNumber) throws IllegalArgumentException {
		
		if(account == null) {
			throw new IllegalArgumentException(MessageFormat.format("The account {0} could not be found.", accountNumber));
		}
		this.accounts.put(account.getAccountNumber(), account);
	}
}

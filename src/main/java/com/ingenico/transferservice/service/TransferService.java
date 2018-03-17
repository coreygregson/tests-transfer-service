/**
 * 
 */
package com.ingenico.transferservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ingenico.transferservice.AccountNumberGenerator;
import com.ingenico.transferservice.DAO.ITransferServiceDAO;

import com.ingenico.transferservice.model.NewAccount;
import com.ingenico.transferservice.model.Transfer;

/**
 * @author cgregson
 * Implementation of the Service Layer of the Transfer Service.
 */
@Service
@Transactional(rollbackFor=IllegalArgumentException.class)
public class TransferService implements ITransferService {

	@Autowired
	ITransferServiceDAO DAO;
		
	/**
	 * Default Constructor.
	 */
	public TransferService() {}
	
		
	@Override	
	@Transactional(rollbackFor=IllegalArgumentException.class)
	public long openAccount(NewAccount newAccount) { 
		
		if(newAccount.getInitialDeposit() < 0) {
			throw new IllegalArgumentException("New accounts cannot be opened with a negative balance.");
		}		
		else {
			long accountNumber = AccountNumberGenerator.getNextAccountNumber();
		
			DAO.openAccount(accountNumber, newAccount.getInitialDeposit(), newAccount.getAccountHolder());
			return accountNumber;
		}
	}

	@Override	
	@Transactional(rollbackFor=IllegalArgumentException.class)
	public void transferMoney(Transfer transfer) throws IllegalArgumentException {		
		DAO.transferMoney(transfer.getFromAccountNumber(), transfer.getToAccountNumber(), transfer.getTransactionValue());				
	}

	@Override
	public double checkBalance(long accountNumber) throws IllegalArgumentException {
		
		return DAO.checkBalance(accountNumber);
	}
}

package com.ingenico.transferservice;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ingenico.transferservice.model.NewAccount;
import com.ingenico.transferservice.model.Transfer;
import com.ingenico.transferservice.service.ITransferService;

@RestController
@RequestMapping("/transfer_service")
/**
 * 
 * @author cgregson
 * Controller class for the Service, contains all end points.
 */
public class TransferServiceController {
	
	@Autowired(required=true)	
	ITransferService TRANSFER_SERVICE;	
	
	@Async
    @RequestMapping(path="/open_account", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	/**
	 * Open a new account in the system.
	 * @param newAccount Account to open.
	 * @return The account number of the new account if it is opened successfully.
	 * @throws IllegalArgumentException Thrown if an account is attempted to be opened with a negative balance.
	 */
    public CompletableFuture<Long> openAccount(NewAccount newAccount) throws IllegalArgumentException {
		
		CompletableFuture<Long> fut = new CompletableFuture<Long>();
		
		try {
			fut = CompletableFuture.completedFuture(TRANSFER_SERVICE.openAccount(newAccount));			
		} catch (IllegalArgumentException ex) {
			fut.completeExceptionally(ex);
		} 
		
		return fut;
    }
    
	@Async
    @RequestMapping(path="/transfer_money", method=RequestMethod.PUT, consumes=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
	/**
	 * Transfer money from the fromAccount to the account in the 
	 * @param fromAccountNumber
	 * @param transferTo The details of the transfer recipient.
	 * @return Future void. Users will need to complete the execution to check for exceptions.
	 * @throws IllegalArgumentException Thrown if either account does not exist, or if the account will be overdrawn.
	 */
    public CompletableFuture<Void> transferMoney(Transfer transfer) throws IllegalArgumentException {
		
		return CompletableFuture.runAsync(() -> {    
		    try {
		    	TRANSFER_SERVICE.transferMoney(transfer);
		    } catch (IllegalArgumentException ex) {		    	
		    	throw ex;
		    }		    
		});				
    }
    
    @Async
    @RequestMapping(path="/accounts/{accountNumber}/check_balance", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    /**
     * Find the current balance of an account.
     * @param accountNumber Account Number to check the balance.
     * @return A future containing the balance of the account. 
     * @throws IllegalArgumentException Thrown if an account number does not exist,
     */
    public CompletableFuture<Double> checkBalance(@PathVariable("accountNumber") long accountNumber) throws IllegalArgumentException {    	

    	CompletableFuture<Double> fut = new CompletableFuture<Double>();
    	
		try {
			fut = CompletableFuture.completedFuture(TRANSFER_SERVICE.checkBalance(accountNumber));
			
		} catch (IllegalArgumentException ex) {
			fut.completeExceptionally(ex);
		} 
		return fut;
    	        
    }
}
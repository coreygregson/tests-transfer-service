/**
 * 
 */
package com.ingenico.transferservice;

import static org.junit.Assert.*;

import java.text.MessageFormat;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.annotation.Repeat;

import com.ingenico.transferservice.model.AccountHolder;
import com.ingenico.transferservice.model.NewAccount;
import com.ingenico.transferservice.model.Transfer;
import com.ingenico.transferservice.service.TransferService;



/**
 * @author cgregson
 *
 */
public class TransferService_ServiceUnitTests {
	
	 public ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
	 public TransferService tsService = context.getBean(TransferService.class);
	
	@Test
	@Repeat(value = 10)
	/**
	 * Check valid accounts are opened correctly.
	 */
	public void testAccountOpeningValid() {
		
    	double initialDepositAccount1 = 1500.00;
    	
    	NewAccount testAccount1 = getNewAccount("Test", "Account", "One", initialDepositAccount1);
    	    	    	
    	try {
    		long testAccount1Number = tsService.openAccount(testAccount1);

    		assertTrue(testAccount1Number > 0);

    		assertTrue(tsService.checkBalance(testAccount1Number) == initialDepositAccount1);
    		
    	}catch (Exception e) {
    		fail(e.getMessage());
		} 
	}
	
	@Test
	@Repeat(value = 10)
	/**
	 * Test accounts cannot be opened with negative balances
	 */
	public void testAccountOpeningInvalid() {
		    	
    	double initialDepositAccount1 = -1500.00;
    	
    	NewAccount testAccount1 = getNewAccount("Test", "Account", "One", initialDepositAccount1);
    	    	    	
    	try {

    		tsService.openAccount(testAccount1);
    	
    		fail("Should have throw an AccountOverDrawnException when account opened with a negative balance.");    		
    		
    	} catch (IllegalArgumentException e) {
    		//correct
		} catch (Exception e) {
    		fail(e.getMessage());
		} 
	}
	
	@Test
	/**
	 * Test new accounts are created with new account numbers.
	 */
	public void testAccountNumbersIncrement() {		
    	
    	double initialDepositAccount1 = 1500.00;
    	double initialDepositAccount2 = 1000.00;
    	
    	NewAccount testAccount1 = getNewAccount("Test", "Account", "One", initialDepositAccount1);
    	NewAccount testAccount2 = getNewAccount("Test", "Account", "Two", initialDepositAccount2);
    	    	    	
    	try {
    		long accountnumber1 = tsService.openAccount(testAccount1);
    		long accountnumber2 = tsService.openAccount(testAccount2);
    	
    		assertTrue(accountnumber1 + 1 == accountnumber2);    		
    		    		
    	}catch (Exception e) {
    		fail(e.getMessage());
		} 
	}
	
	@Test
	/**
	 * Test valid transfers work correctly, including decimal numbers.
	 */
	public void testTransferValid() {		
    	
    	double initialDepositAccount1 = 1500.00;
    	double initialDepositAccount2 = 0;
    	
    	double transfer1Amount = 950.50;
    	double transfer2Amount = 40.40;
    	
    	NewAccount testAccount1 = getNewAccount("Test", "Account", "One", initialDepositAccount1);
    	NewAccount testAccount2 = getNewAccount("Test", "Account", "Two", initialDepositAccount2);
    	    	    	
    	try {
    		long accountNumber1 = tsService.openAccount(testAccount1);
    		long accountNumber2 = tsService.openAccount(testAccount2);
    		
    		//Check initial deposits were recorded
    		assertTrue(tsService.checkBalance(accountNumber1) == initialDepositAccount1);    		
    		assertTrue(tsService.checkBalance(accountNumber2) == initialDepositAccount2);
    		
    		//Check both balances are correct after a transfer.
    		Transfer testTranfer1 = getTransfer(accountNumber1, accountNumber2, transfer1Amount);    		
    		tsService.transferMoney(testTranfer1);
    		
    		assertTrue(tsService.checkBalance(accountNumber1) == initialDepositAccount1 - transfer1Amount);    		
    		assertTrue(tsService.checkBalance(accountNumber2) == initialDepositAccount2 + transfer1Amount);
    		
    		//Check balances are correct after a second transfer.
    		Transfer testTranfer2 = getTransfer(accountNumber1, accountNumber2, transfer2Amount);    		
    		tsService.transferMoney(testTranfer2);
    		
    		assertTrue(tsService.checkBalance(accountNumber1) == initialDepositAccount1 - transfer1Amount - transfer2Amount);    		
    		assertTrue(tsService.checkBalance(accountNumber2) == initialDepositAccount2 + transfer1Amount + transfer2Amount);
    		    		
    		    		
    	}catch (Exception e) {
    		fail(e.getMessage());
		} 
	}
	
	@Test
	@Repeat(value = 10)
	/**
	 * Check transactions which will result in an overdrawn account fail correctly.
	 */
	public void testTransferOverdrawnSourceAccount() {
		    	
    	double initialDepositAccount1 = 1500.00;
    	double initialDepositAccount2 = 0;
    	
    	double transfer1Amount = 2000.00;    	
    	
    	NewAccount testAccount1 = getNewAccount("Test", "Account", "One", initialDepositAccount1);
    	NewAccount testAccount2 = getNewAccount("Test", "Account", "Two", initialDepositAccount2);
    	    	    	
    	try {
    		long accountNumber1 = tsService.openAccount(testAccount1);
    		long accountNumber2 = tsService.openAccount(testAccount2);
    		
    		//Check initial deposits were recorded
    		assertTrue(tsService.checkBalance(accountNumber1) == initialDepositAccount1);    		
    		assertTrue(tsService.checkBalance(accountNumber2) == initialDepositAccount2);
    		
    		//Check both balances are correct after a transfer.
    		Transfer testTranfer1 = getTransfer(accountNumber1, accountNumber2, transfer1Amount);    		
    		tsService.transferMoney(testTranfer1);
			
			fail("Should have throw an IllegalArgument when a transfer would results in a negative balance.");    		    		
    		    		
    	} catch (IllegalArgumentException e) {
    		//correct
		}
    	catch (Exception e) {
    		fail(e.getMessage());
		} 
	}
	
	@Test
	@Repeat(value = 10)
	/**
	 * Check transactions with invalid source account number work.
	 */
	public void testTransferInvalidSourceAccountNumber() {		
    	
    	double initialDepositAccount1 = 1500.00;    	
    	
    	double transfer1Amount = 100.00;    	
    	
    	NewAccount testAccount1 = getNewAccount("Test", "Account", "One", initialDepositAccount1);    	
    	    	    	
    	try {
    		long accountNumber1 = tsService.openAccount(testAccount1);    		
    		
    		try {
    			long invalidAccountNumber = 100;
    			Transfer testTranfer1 = getTransfer(invalidAccountNumber, accountNumber1, transfer1Amount); 
    			tsService.transferMoney(testTranfer1);
    			fail("Should have throw an IllegalArgumentException");
    		} catch (IllegalArgumentException e) {
        		//correct
    		} catch(Exception ex) {
    			fail(MessageFormat.format("Unexpected exception occured: {0}", ex.getMessage()));
    		}    		
    		    		
    	}catch (Exception e) {
    		fail(e.getMessage());
		} 
	}
	
	@Test
	@Repeat(value = 10)
	/**
	 * Check transactions with invalid target account number work.
	 */
	public void testTransferInvalidTargetAccountNumber() {
		    	
    	double initialDepositAccount1 = 1500.00;    	
    	
    	double transfer1Amount = 100.00;    	
    	
    	NewAccount testAccount1 = getNewAccount("Test", "Account", "One", initialDepositAccount1);    	
    	    	    	
    	try {
    		long accountNumber1 = tsService.openAccount(testAccount1);    		
    		
    		try {
    			long invalidAccountNumber = 100;
    			Transfer testTranfer1 = getTransfer(accountNumber1, invalidAccountNumber, transfer1Amount); 
    			tsService.transferMoney(testTranfer1);
    			fail("Should have throw an InvalidAccountNumberExcetion");
    		} catch (IllegalArgumentException e) {
        		//correct
    		}catch(Exception ex) {
    			fail(MessageFormat.format("Unexpected exception occured: {0}", ex.getMessage()));
    		}    		
    		    		
    	}catch (Exception e) {
    		fail(e.getMessage());
		} 
	}
	
	/**
	 * Generate a Transfer Object
	 * @param fromAccountNumber
	 * @param toAccountNumber
	 * @param transferAmoun
	 * @return 
	 */
	protected static Transfer getTransfer(long fromAccountNumber, long toAccountNumber, double transferAmount) {
		Transfer transfer = new Transfer();
		transfer.setTransactionValue(transferAmount);
		transfer.setToAccountNumber(toAccountNumber);
		transfer.setFromAccountNumber(fromAccountNumber);
	    	
		return transfer;
	}
	    
	/**
	 * Generate a NewAccount Object
	 * @param firstName
	 * @param middleName
	 * @param surname
	 * @param initialDeposit
	 * @return
	 */
	protected static NewAccount getNewAccount(String firstName, String middleName, String surname, double initialDeposit) {
		AccountHolder newAccountHolder = new AccountHolder();
		newAccountHolder.setFirstName(firstName);
		newAccountHolder.setMiddleName(middleName);
		newAccountHolder.setSurname(surname);    	
	    	
		NewAccount newAccount = new NewAccount();    	
		newAccount.setInitialDeposit(initialDeposit);
		newAccount.setAccountHolder(newAccountHolder);
	    	
		return newAccount;
	}
} 
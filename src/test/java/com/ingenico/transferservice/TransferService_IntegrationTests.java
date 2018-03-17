/**
 * 
 */
package com.ingenico.transferservice;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.junit4.SpringRunner;

import com.ingenico.transferservice.model.NewAccount;
import com.ingenico.transferservice.model.Transfer;
import com.ingenico.transferservice.App;
import com.ingenico.transferservice.AppConfig;


/**
 * @author cgregson
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {App.class, TransferServiceController.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
/**
 * Integration (Full stack) Testing class.  This should cover all valid and invalid usage scenarios. 
 * @author cgregson
 *
 */
public class TransferService_IntegrationTests {
    
    @Value("${local.server.port}")
	int port;
   
    public ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
	public TransferServiceController ts = context.getBean(TransferServiceController.class);
    
    @Test
    @Repeat(value = 10)
    /**
     * Test that an account can be opened, with a positive balance.  Check the balance matches the expected result.
     */
    public void testOpenAccounValid() {
    	
    	double initialDeposit = 1000.00;
        NewAccount newAccount = TransferService_ServiceUnitTests.getNewAccount("Test", "Initial", "Deposit", initialDeposit);
        
        Future<Long> accountNumber;
		try {
			accountNumber = ts.openAccount(newAccount);
			double balance = ts.checkBalance(accountNumber.get()).get();
			
			assertTrue(balance == initialDeposit);
			
		} catch (Exception e) {
			fail(e.getMessage());
		} 
    }
    
    @Test
    @Repeat(value = 10)
    /**
     * Test opening an Invalid Account (Negative opening balance).
     */
    public void testOpenAccountInvalid() { 
    	
    	double initialDeposit = -1000.00;
        NewAccount newAccount = TransferService_ServiceUnitTests.getNewAccount("Test", "Initial", "Deposit", initialDeposit);
        
		try {
			ts.openAccount(newAccount).get();
		} catch (IllegalArgumentException e) {			
			fail(e.getMessage());
		} catch (ExecutionException e) {			
			String name = e.getCause().getClass().getName();
			assertTrue(name.equalsIgnoreCase(IllegalArgumentException.class.getName()));			
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}  
    }
    
    @Test
    @Repeat(value = 10)
    /**
     * Test that valid transfers are executed correctly.  Both balances should be correct after the transaction.
     */
    public void testTransferValid() {
    	
    	try {
	    	double initialDeposit1 = 1000.00;
	    	double initialDeposit2 = 0.00;
	        NewAccount newAccount1 = TransferService_ServiceUnitTests.getNewAccount("Test", "Account", "One", initialDeposit1);
	        NewAccount newAccount2 = TransferService_ServiceUnitTests.getNewAccount("Test", "Account", "Two", initialDeposit2);
	        
	        Future<Long> accountNumber1;
	        Future<Long> accountNumber2;
			accountNumber1 = ts.openAccount(newAccount1);
			accountNumber2 = ts.openAccount(newAccount2);
			long accountnumber1Val = accountNumber1.get();
			long accountnumber2Val = accountNumber2.get();
				
			double transferAmount = 600.00;
				
			Transfer transfer = TransferService_ServiceUnitTests.getTransfer(accountnumber1Val, accountnumber2Val, transferAmount);
				
			ts.transferMoney(transfer).get();
				
			Double account1Balance = ts.checkBalance(accountnumber1Val).get();
			Double account2Balance = ts.checkBalance(accountnumber2Val).get();
			assertTrue(account1Balance == initialDeposit1 - transferAmount);
			assertTrue(account2Balance == initialDeposit2 + transferAmount);
    	} catch(Exception ex) {
    		fail(ex.getMessage());
    	}
    }    
    
    @Test
    @Repeat(value = 10)
    /**
     * Test that valid transfers are executed correctly.  Both balances should be correct after the transaction.
     */
    public void testTransferOverdrawnSource() { 
    	double initialDeposit1 = 1000.00;
    	double initialDeposit2 = 0.00;
        NewAccount newAccount1 = TransferService_ServiceUnitTests.getNewAccount("Test", "Account", "One", initialDeposit1);
        NewAccount newAccount2 = TransferService_ServiceUnitTests.getNewAccount("Test", "Account", "Two", initialDeposit2);
        
        Future<Long> accountNumber1;
        Future<Long> accountNumber2;
		try {
			accountNumber1 = ts.openAccount(newAccount1);
			accountNumber2 = ts.openAccount(newAccount2);
			long accountnumber1Val = accountNumber1.get();
			long accountnumber2Val = accountNumber2.get();
			
			double transferAmount = initialDeposit1 + 1;
			
			Transfer transfer = TransferService_ServiceUnitTests.getTransfer(accountnumber1Val, accountnumber2Val, transferAmount);
			
			try {
				ts.transferMoney(transfer).get();
				fail("Should not get here");
			} catch (IllegalArgumentException e) {
				fail(e.getMessage());
			} catch (ExecutionException e) {			
				String name = e.getCause().getClass().getName();
				assertTrue(name.equalsIgnoreCase(IllegalArgumentException.class.getName()));
				
				//Ensure that NEITHER balances change! 
				assertTrue(initialDeposit1 == ts.checkBalance(accountnumber1Val).get());
				assertTrue(initialDeposit2 == ts.checkBalance(accountnumber2Val).get());
			}  			
			
		} catch (ExecutionException e) {
			assertTrue(e.getCause().getClass().getName().equals(IllegalArgumentException.class.getName()));
			fail(e.getCause().getMessage());
		} catch (Exception e1) {
			fail(e1.getMessage());
		}		
    } 
    
    @Test
    @Repeat(value = 10)
    /**
     * Test that a transfer from an invalid account number does not work.  The to account's balance must not have changed.
     */
    public void testTransferInvalidSourceAccount() { 
    	double initialDeposit1 = 1000.00;    	
        NewAccount newAccount1 = TransferService_ServiceUnitTests.getNewAccount("Test", "Account", "One", initialDeposit1);        
        
        Future<Long> accountNumber1;        
		try {
			accountNumber1 = ts.openAccount(newAccount1);			
			long accountnumber1Val = accountNumber1.get();
			long invalidAccountNumber = -1000;
			
			double transferAmount = initialDeposit1 + 1;
			
			Transfer transfer = TransferService_ServiceUnitTests.getTransfer(invalidAccountNumber, accountnumber1Val, transferAmount);
			
			try {
				ts.transferMoney(transfer).get();
				fail("Should not get here");
			} catch (IllegalArgumentException e) {
				fail(e.getMessage());
			} catch (ExecutionException e) {			
				String name = e.getCause().getClass().getName();
				assertTrue(name.equalsIgnoreCase(IllegalArgumentException.class.getName()));
				
				//Ensure that the valid account's balance does not change 
				assertTrue(initialDeposit1 == ts.checkBalance(accountnumber1Val).get());
			}  			
			
		} catch (ExecutionException e) {
			assertTrue(e.getCause().getClass().getName().equals(IllegalArgumentException.class.getName()));
			fail(e.getCause().getMessage());
		} catch (Exception e1) {
			fail(e1.getMessage());
		}		
    }
    
    @Test
    @Repeat(value = 10)
    /**
     * Test that a transfer to an invalid account number does not work.  The from account's balance must not have changed.
     */
    public void testTransferInvalidTargetAccount() { 
    	double initialDeposit1 = 1000.00;    	
        NewAccount newAccount1 = TransferService_ServiceUnitTests.getNewAccount("Test", "Account", "One", initialDeposit1);        
        
        Future<Long> accountNumber1;        
		try {
			accountNumber1 = ts.openAccount(newAccount1);			
			long accountnumber1Val = accountNumber1.get();
			long invalidAccountNumber = -100;
			
			double transferAmount = 500;
			
			Transfer transfer = TransferService_ServiceUnitTests.getTransfer(accountnumber1Val, invalidAccountNumber, transferAmount);
			
			try {
				ts.transferMoney(transfer).get();
				fail("Should not get here");								
			} catch (IllegalArgumentException e) {
				fail(e.getMessage());				
			} catch (ExecutionException e) {			
				String name = e.getCause().getClass().getName();
				assertTrue(name.equalsIgnoreCase(IllegalArgumentException.class.getName()));
				
				//Ensure that the valid account's balance does not change 
				assertTrue(initialDeposit1 == ts.checkBalance(accountnumber1Val).get());
			}  						
		} catch (ExecutionException e) {
			assertTrue(e.getCause().getClass().getName().equals(IllegalArgumentException.class.getName()));
			fail(e.getCause().getMessage());
		} catch (Exception e1) {
			fail(e1.getMessage());
		}		
    }   

}
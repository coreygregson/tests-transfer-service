/**
 * 
 */
package com.ingenico.transferservice;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author cgregson
 * Singleton thread safe class to generate new account numbers.
 */
public class AccountNumberGenerator {
	
	private static final AtomicLong accountNumbers = new AtomicLong(5000);
		
	private AccountNumberGenerator() {}
	
	public static long getNextAccountNumber() {
		return accountNumbers.incrementAndGet();
	}
}

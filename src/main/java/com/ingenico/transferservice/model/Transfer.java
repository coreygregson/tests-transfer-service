/**
 * 
 */
package com.ingenico.transferservice.model;

/**
 * @author cgregson
 * Simple POJO for representing all the information required for a transfer. 
 */
public class Transfer {

	private long toAccountNumber;
	private long fromAccountNumber;
	
	private double transactionValue;

	/**
	 * Empty constructor required for marshalling / unmarshalling.
	 */
	public Transfer() {}
		
	public long getToAccountNumber() {
		return toAccountNumber;
	}

	public void setToAccountNumber(long toAccountNumber) {
		this.toAccountNumber = toAccountNumber;
	}

	public double getTransactionValue() {
		return transactionValue;
	}

	public void setTransactionValue(double transactionValue) {
		this.transactionValue = transactionValue;
	}

	public long getFromAccountNumber() {
		return fromAccountNumber;
	}

	public void setFromAccountNumber(long fromAccountNumber) {
		this.fromAccountNumber = fromAccountNumber;
	}
}

package ca.mcgill.ecse321.eventregistration.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Bitcoin {
	private String bitCoinUserId;
	@Id
	public String getBitCoinUserId() {
		return bitCoinUserId;
	}

	public void setBitCoinUserId(String bitCoinUserId) {
		this.bitCoinUserId = bitCoinUserId;
	}
	
	private int amount;
	
	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

}

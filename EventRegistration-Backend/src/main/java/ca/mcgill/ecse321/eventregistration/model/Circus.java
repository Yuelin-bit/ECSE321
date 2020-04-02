package ca.mcgill.ecse321.eventregistration.model;

import javax.persistence.Entity;

@Entity
public class Circus extends Event {
	String company;

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
	
}

package ca.mcgill.ecse321.eventregistration.dto;

import java.sql.Date;
import java.sql.Time;

public class CircusDto extends EventDto {
	private String company;
	
	public String getCompany() {
		return company;
	}

	public CircusDto(String name, Date date, Time startTime, Time endTime, String company) {
		super(name, date, startTime, endTime);
		this.company = company;
	}
}

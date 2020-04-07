package ca.mcgill.ecse321.eventregistration.dto;

import java.sql.Date;
import java.sql.Time;

public class CircusDto extends EventDto {
	private String name;
	private Date date;
	private Time startTime;
	private Time endTime;
	private String company;


	public CircusDto() {
	}

	public CircusDto(String name) {
		this(name, Date.valueOf("1971-01-01"), Time.valueOf("00:00:00"), Time.valueOf("23:59:59"), "null");
	}

	public CircusDto(String name, Date date, Time startTime, Time endTime, String company) {
		this.name = name;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
		this.company = company;
	}

	public String getName() {
		return name;
	}

	public Date getDate() {
		return date;
	}

	public Time getStartTime() {
		return startTime;
	}

	public Time getEndTime() {
		return endTime;
	}
	
	public String getCompany() {
		return company;
	}
}

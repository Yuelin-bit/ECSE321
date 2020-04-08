package ca.mcgill.ecse321.eventregistration.dto;

public class RegistrationDto {

	private PersonDto person;
	private EventDto event;
	private BitcoinDto bitcoin;
	private int amount;
	public int getAmount() {
		return amount;
	}

	public String getUserID() {
		return userID;
	}

	private String userID;

	public BitcoinDto getBitcoin() {
		return bitcoin;
	}

	public RegistrationDto() {
	}
	
	public RegistrationDto(BitcoinDto bitcoin) {
		this.bitcoin = bitcoin;
	}
	
	public RegistrationDto(BitcoinDto bitcoin, int amount, String userID) {
		this.bitcoin = bitcoin;
		this.amount = amount;
		this.userID = userID;
	}
	
	public RegistrationDto(int amount, String userID) {
		this.amount = amount;
		this.userID = userID;
	}
	
	public RegistrationDto(PersonDto person, EventDto event) {
		this.person = person;
		this.event = event;
	}

	public RegistrationDto(PersonDto person, EventDto event, BitcoinDto bitcoin) {
		this.person = person;
		this.event = event;
		this.bitcoin = bitcoin;
	}

	public RegistrationDto(PersonDto person, EventDto event, int amount, String userID) {
		this.amount = amount;
		this.userID = userID;
		this.person = person;
		this.event = event;
	}

	public EventDto getEvent() {
		return event;
	}

	public void setEvent(EventDto event) {
		this.event = event;
	}

	public PersonDto getPerson() {
		return person;
	}

	public void setPerson(PersonDto person) {
		this.person = person;
	}

}

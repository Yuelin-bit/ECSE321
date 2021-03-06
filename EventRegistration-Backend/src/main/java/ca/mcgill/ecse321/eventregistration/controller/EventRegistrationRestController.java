package ca.mcgill.ecse321.eventregistration.controller;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ca.mcgill.ecse321.eventregistration.model.*;
import ca.mcgill.ecse321.eventregistration.dto.*;
import ca.mcgill.ecse321.eventregistration.service.EventRegistrationService;

@CrossOrigin(origins = "*")
@RestController
public class EventRegistrationRestController {

	@Autowired
	private EventRegistrationService service;

	// POST Mappings

	// @formatter:off
	// Turning off formatter here to ease comprehension of the sample code by
	// keeping the linebreaks
	// Example REST call:
	// http://localhost:8088/persons/John
	@PostMapping(value = { "/persons/{name}", "/persons/{name}/" })
	public PersonDto createPerson(@PathVariable("name") String name) throws IllegalArgumentException {
		// @formatter:on
		Person person = service.createPerson(name);
		return convertToDto(person);
	}
	
	// @formatter:off
	// Turning off formatter here to ease comprehension of the sample code by
	// keeping the linebreaks
	// Example REST call:
	// http://localhost:8088/persons/John
	@PostMapping(value = { "/promoters/{name}", "/promoters/{name}/" })
	public PromoterDto createPromoter(@PathVariable("name") String name) throws IllegalArgumentException {
		// @formatter:on
		Promoter promoter = service.createPromoter(name);
		return convertToDto(promoter);
	}

	// @formatter:off
	// Example REST call:
	// http://localhost:8080/events/testevent?date=2013-10-23&startTime=00:00&endTime=23:59
	@PostMapping(value = { "/events/{name}", "/events/{name}/" })
	public EventDto createEvent(@PathVariable("name") String name, @RequestParam Date date,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME, pattern = "HH:mm") LocalTime startTime,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME, pattern = "HH:mm") LocalTime endTime)
			throws IllegalArgumentException {
		// @formatter:on
		Event event = service.createEvent(name, date, Time.valueOf(startTime), Time.valueOf(endTime));
		return convertToDto(event);
	}
	
	// @formatter:off
	// Example REST call:
	// http://localhost:8080/events/testevent?date=2013-10-23&startTime=00:00&endTime=23:59
	@PostMapping(value = { "/circus/{name}", "/circus/{name}/" })
	public CircusDto createCircus(@PathVariable("name") String name, @RequestParam Date date,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME, pattern = "HH:mm") LocalTime startTime,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME, pattern = "HH:mm") LocalTime endTime,
			@RequestParam String company)
			throws IllegalArgumentException {
		// @formatter:on
		Circus circus = service.createCircus(name, date, Time.valueOf(startTime), Time.valueOf(endTime), company);
		return convertToDto(circus);
	}

	// @formatter:off
	@PostMapping(value = { "/register", "/register/" })
	public RegistrationDto registerPersonForEvent(@RequestParam(name = "person") PersonDto pDto,
			@RequestParam(name = "event") EventDto eDto) throws IllegalArgumentException {
		// @formatter:on

		// Both the person and the event are identified by their names
		Person p = service.getPerson(pDto.getName());
		Event e = service.getEvent(eDto.getName());

		Registration r = service.register(p, e);
		return convertToDto(r, p, e);
	}
	
	@PostMapping(value = { "/assign", "/assign/" })
	public void assign(@RequestParam(name = "promoter") PromoterDto pDto,
			@RequestParam(name = "event") EventDto eDto) throws IllegalArgumentException {
		Promoter p = service.getPromoter(pDto.getName());
		Event e = service.getEvent(eDto.getName());
		service.promotesEvent(p, e);
	}
	
	@PostMapping(value = { "/pay/", "/pay" })
	public void pay(@RequestParam PersonDto person,@RequestParam EventDto event,
		@RequestParam String deviceId,@RequestParam int amount)throws IllegalArgumentException {
			Person p = service.getPerson(person.getName());
			Event e = service.getEvent(event.getName());
			Registration r = service.getRegistrationByPersonAndEvent(p, e);
			if(r == null) {
				throw new IllegalArgumentException("Registration does not exsit");
			}
			Bitcoin b = service.createBitcoinPay(deviceId, amount);
			service.pay(r, b);
	}

	// GET Mappings

	@GetMapping(value = { "/events", "/events/" })
	public List<EventDto> getAllEvents() {
		List<EventDto> eventDtos = new ArrayList<>();
		for (Event event : service.getAllEvents()) {
			if(event.getClass().equals(Circus.class)) {
				eventDtos.add(convertToDto((Circus)event));
			}else
				eventDtos.add(convertToDto(event));
		}
		return eventDtos;
	}

	// Example REST call:
	// http://localhost:8088/events/person/JohnDoe
	@GetMapping(value = { "/events/person/{name}", "/events/person/{name}/" })
	public List<EventDto> getEventsOfPerson(@PathVariable("name") PersonDto pDto) {
		Person p = convertToDomainObject(pDto);
		return createAttendedEventDtosForPerson(p);
	}

	@GetMapping(value = { "/persons/{name}", "/persons/{name}/" })
	public PersonDto getPersonByName(@PathVariable("name") String name) throws IllegalArgumentException {
		return convertToDto(service.getPerson(name));
	}
	
	@GetMapping(value = { "/promoters/{name}", "/promoters/{name}/" })
	public PromoterDto getPromotersByName(@PathVariable("name") String name) throws IllegalArgumentException {
		return convertToDto(service.getPromoter(name));
	}

	@GetMapping(value = { "/registrations", "/registrations/" })
	public RegistrationDto getRegistration(@RequestParam(name = "person") PersonDto pDto,
			@RequestParam(name = "event") EventDto eDto) throws IllegalArgumentException {
		// Both the person and the event are identified by their names
		Person p = service.getPerson(pDto.getName());
		Event e = service.getEvent(eDto.getName());

		Registration r = service.getRegistrationByPersonAndEvent(p, e);
		return convertToDtoWithoutPerson(r);
	}

	@GetMapping(value = { "/registrations/person/{name}", "/registrations/person/{name}/" })
	public List<RegistrationDto> getRegistrationsForPerson(@PathVariable("name") PersonDto pDto)
			throws IllegalArgumentException {
		// Both the person and the event are identified by their names
		Person p = service.getPerson(pDto.getName());

		return createRegistrationDtosForPerson(p);
	}
	
	@GetMapping(value = { "/registrations/bitcoin/{name}", "/registrations/person/{name}/" })
	public List<RegistrationDto> getRegistrationsForBitcoin(@PathVariable("name") PersonDto pDto)
			throws IllegalArgumentException {
		// Both the person and the event are identified by their names
		Person p = service.getPerson(pDto.getName());

		return createRegistrationDtosForBitcoin(p);
	}
	
	
	@GetMapping(value = { "/bitcoins", "/bitcoins/" })
	public BitcoinDto getBitCoin(@RequestParam(name = "person") PersonDto pDto,
			@RequestParam(name = "event") EventDto eDto) throws IllegalArgumentException {
		Person p = service.getPerson(pDto.getName());
		Event e = service.getEvent(eDto.getName());

		Registration r = service.getRegistrationByPersonAndEvent(p, e);
		Bitcoin b = service.getBitcoin(r);
		return convertToDto(b);
	}


	@GetMapping(value = { "/persons", "/persons/" })
	public List<PersonDto> getAllPersons() {
		List<PersonDto> persons = new ArrayList<>();
		for (Person person : service.getAllPersons()) {
			persons.add(convertToDto(person));
		}
		return persons;
	}
	
	@GetMapping(value = { "/promoters", "/promoters/" })
	public List<PromoterDto> getAllPromoters() {
		List<PromoterDto> promoters = new ArrayList<>();
		for (Promoter promoter : service.getAllPromoters()) {
			promoters.add(convertToDto(promoter));
		}
		return promoters;
	}

	@GetMapping(value = { "/events/{name}", "/events/{name}/" })
	public EventDto getEventByName(@PathVariable("name") String name) throws IllegalArgumentException {
		return convertToDto(service.getEvent(name));
	}

	// Model - DTO conversion methods (not part of the API)

	private EventDto convertToDto(Event e) {
		if (e == null) {
			throw new IllegalArgumentException("There is no such Event!");
		}
		EventDto eventDto = new EventDto(e.getName(), e.getDate(), e.getStartTime(), e.getEndTime());
		return eventDto;
	}
	
	private CircusDto convertToDto(Circus e) {
		if (e == null) {
			throw new IllegalArgumentException("There is no such Circus!");
		}
		CircusDto circusDto = new CircusDto(e.getName(), e.getDate(), e.getStartTime(), e.getEndTime(),e.getCompany());
		return circusDto;
	}

	private PersonDto convertToDto(Person p) {
		if (p == null) {
			throw new IllegalArgumentException("There is no such Person!");
		}
		PersonDto personDto = new PersonDto(p.getName());
		personDto.setEventsAttended(createAttendedEventDtosForPerson(p));
		return personDto;
	}
	
	private PromoterDto convertToDto(Promoter p) {
		if (p == null) {
			throw new IllegalArgumentException("There is no such Promoter!");
		}
		PromoterDto promoterDto = new PromoterDto(p.getName());
		promoterDto.setEventsAttended(createAttendedEventDtosForPerson(p));
		promoterDto.setPromotes(createPromoteDtosForPromoter(p));
		return promoterDto;
	}

	// DTOs for registrations
	private RegistrationDto convertToDto(Registration r, Person p, Event e) {
		EventDto eDto = convertToDto(e);
		PersonDto pDto = convertToDto(p);
		return new RegistrationDto(pDto, eDto);
	}
	
	private BitcoinDto convertToDto(Bitcoin b) {
		// TODO Auto-generated method stub
		return new BitcoinDto(b.getUserID(), b.getAmount());
	}
	

	private RegistrationDto convertToDto(Registration r) {
		EventDto eDto = convertToDto(r.getEvent());
		PersonDto pDto = convertToDto(r.getPerson());
		RegistrationDto rDto;
		if(r.getBitcoin()!=null)
			rDto = new RegistrationDto(pDto, eDto, r.getBitcoin().getAmount(), r.getBitcoin().getUserID());
		else
			rDto = new RegistrationDto(pDto, eDto);
		return rDto;
	}

	// return registration dto without peron object so that we are not repeating
	// data
	private RegistrationDto convertToDtoWithoutPerson(Registration r) {
		RegistrationDto rDto = convertToDto(r);
		rDto.setPerson(null);
		return rDto;
	}
	
	private RegistrationDto convertToDtoOnlyBitcoin(Registration r) {
		BitcoinDto b = convertToDto(r.getBitcoin());
		
		return new RegistrationDto(b);
	}


	private Person convertToDomainObject(PersonDto pDto) {
		List<Person> allPersons = service.getAllPersons();
		for (Person person : allPersons) {
			if (person.getName().equals(pDto.getName())) {
				return person;
			}
		}
		return null;
	}

	// Other extracted methods (not part of the API)

	private List<EventDto> createAttendedEventDtosForPerson(Person p) {
		List<Event> eventsForPerson = service.getEventsAttendedByPerson(p);
		List<EventDto> events = new ArrayList<>();
		for (Event event : eventsForPerson) {
			events.add(convertToDto(event));
		}
		return events;
	}
	
	private List<EventDto> createPromoteDtosForPromoter(Promoter p) {
		Set<Event> es = p.getPromotes();
		List<EventDto> events = new ArrayList<>();
		for (Event e : es) {
			events.add(convertToDto(e));
		}
		return events;
	}
	
//	private List<EventDto> createPromotesDtosForPromoter(Promoter p) {
//		List<Event> eventsForPromoter = service.get.getEventsAttendedByPerson(p);
//		List<EventDto> events = new ArrayList<>();
//		for (Event event : eventsForPerson) {
//			events.add(convertToDto(event));
//		}
//		return events;
//	}

	private List<RegistrationDto> createRegistrationDtosForPerson(Person p) {
		List<Registration> registrationsForPerson = service.getRegistrationsForPerson(p);
		List<RegistrationDto> registrations = new ArrayList<RegistrationDto>();
		for (Registration r : registrationsForPerson) {
			registrations.add(convertToDtoWithoutPerson(r));
		}
		return registrations;
	}
	
	private List<RegistrationDto> createRegistrationDtosForBitcoin(Person p) {
		List<Registration> registrationsForPerson = service.getRegistrationsForPerson(p);
		List<RegistrationDto> registrations = new ArrayList<RegistrationDto>();
		for (Registration r : registrationsForPerson) {
			registrations.add(convertToDtoOnlyBitcoin(r));
		}
		return registrations;
	}


}

package ca.mcgill.ecse321.eventregistration.service;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.mcgill.ecse321.eventregistration.dao.*;
import ca.mcgill.ecse321.eventregistration.model.*;

@Service
public class EventRegistrationService {

	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private PersonRepository personRepository;
	@Autowired
	private RegistrationRepository registrationRepository;
	@Autowired
	private PromoterRepository promoterRepository;
	@Autowired
	private CircusRepository circusRepository;
	@Autowired
	private BitcoinRepository bitcoinRepository;
	
	

	@Transactional
	public Person createPerson(String name) {
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("Person name cannot be empty!");
		} else if (personRepository.existsById(name)) {
			throw new IllegalArgumentException("Person has already been created!");
		}
		Person person = new Person();
		person.setName(name);
		personRepository.save(person);
		return person;
	}


	@Transactional
	public Person getPerson(String name) {
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("Person name cannot be empty!");
		}
		Person person = personRepository.findByName(name);
		return person;
	}

	@Transactional
	public List<Person> getAllPersons() {
		return toList(personRepository.findAll());
	}

	@Transactional
	public Event buildEvent(Event event, String name, Date date, Time startTime, Time endTime) {
		// Input validation
		String error = "";
		if (name == null || name.trim().length() == 0) {
			error = error + "Event name cannot be empty! ";
		} else if (eventRepository.existsById(name)) {
			throw new IllegalArgumentException("Event has already been created!");
		}
		if (date == null) {
			error = error + "Event date cannot be empty! ";
		}
		if (startTime == null) {
			error = error + "Event start time cannot be empty! ";
		}
		if (endTime == null) {
			error = error + "Event end time cannot be empty! ";
		}
		if (endTime != null && startTime != null && endTime.before(startTime)) {
			error = error + "Event end time cannot be before event start time!";
		}
		error = error.trim();
		if (error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		event.setName(name);
		event.setDate(date);
		event.setStartTime(startTime);
		event.setEndTime(endTime);
		return event;
	}

	@Transactional
	public Event createEvent(String name, Date date, Time startTime, Time endTime) {
		Event event = new Event();
		buildEvent(event, name, date, startTime, endTime);
		eventRepository.save(event);
		return event;
	}

	@Transactional
	public Event getEvent(String name) {
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("Event name cannot be empty!");
		}
		Event event = eventRepository.findByName(name);
		return event;
	}

	// This returns all objects of instance "Event" (Subclasses are filtered out)
	@Transactional
	public List<Event> getAllEvents() {
		//return toList(eventRepository.findAll()).stream().filter(e -> e.getClass().equals(Event.class)).collect(Collectors.toList());
		return toList(eventRepository.findAll()).stream().collect(Collectors.toList());
	}

	@Transactional
	public Registration register(Person person, Event event) {
		String error = "";
		if (person == null) {
			error = error + "Person needs to be selected for registration! ";
		} else if (!personRepository.existsById(person.getName())) {
			error = error + "Person does not exist! ";
		}
		if (event == null) {
			error = error + "Event needs to be selected for registration!";
		} else if (!eventRepository.existsById(event.getName())) {
			error = error + "Event does not exist!";
		}
		if (registrationRepository.existsByPersonAndEvent(person, event)) {
			error = error + "Person is already registered to this event!";
		}

		error = error.trim();

		if (error.length() > 0) {
			throw new IllegalArgumentException(error);
		}

		Registration registration = new Registration();
		registration.setId(person.getName().hashCode() * event.getName().hashCode());
		registration.setPerson(person);
		registration.setEvent(event);

		registrationRepository.save(registration);

		return registration;
	}

	@Transactional
	public List<Registration> getAllRegistrations() {
		return toList(registrationRepository.findAll());
	}

	@Transactional
	public Registration getRegistrationByPersonAndEvent(Person person, Event event) {
		if (person == null || event == null) {
			throw new IllegalArgumentException("Person or Event cannot be null!");
		}

		return registrationRepository.findByPersonAndEvent(person, event);
	}
	@Transactional
	public List<Registration> getRegistrationsForPerson(Person person){
		if(person == null) {
			throw new IllegalArgumentException("Person cannot be null!");
		}
		return registrationRepository.findByPerson(person);
	}

	@Transactional
	public List<Registration> getRegistrationsByPerson(Person person) {
		return toList(registrationRepository.findByPerson(person));
	}

	@Transactional
	public List<Event> getEventsAttendedByPerson(Person person) {
		if (person == null) {
			throw new IllegalArgumentException("Person cannot be null!");
		}
		List<Event> eventsAttendedByPerson = new ArrayList<>();
		for (Registration r : registrationRepository.findByPerson(person)) {
			eventsAttendedByPerson.add(r.getEvent());
		}
		return eventsAttendedByPerson;
	}
	
	@Transactional
	public Promoter createPromoter(String name) {
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("Promoter name cannot be empty!");
		}else if (promoterRepository.existsById(name)) {
			throw new IllegalArgumentException("Promoter has already been created!");
		}
		Promoter promoter = new Promoter();
		promoter.setName(name);
		promoterRepository.save(promoter);
		return promoter;
	}
	
	@Transactional
	public Promoter getPromoter(String name) {
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("Person name cannot be empty!");
		}
		Promoter promoter = promoterRepository.findPromoterByName(name);
		return promoter;
	}
	
	@Transactional
	public List<Promoter> getAllPromoters() {
		return toList(promoterRepository.findAll());
	}
	
	@Transactional
	public void promotesEvent(Promoter promoter, Event event) {
		String error = "";
		if(promoter == null || !promoter.getClass().equals(Promoter.class)) {
			error = error + "Promoter needs to be selected for promotes!";
		}else if(!promoterRepository.existsById(promoter.getName())){
			error = error + "Promoter does not exist!";
		}
		if(event == null) {
			error = error + "Event cannot be empty!";
		}else if(! eventRepository.existsById(event.getName())){
			error = error + "Event does not exist!";
		}
		error = error.trim();
		if (error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		event.setPromoter(promoter);
		Set<Event> temp = promoter.getPromotes();
		if(temp == null) {
			temp = new HashSet<Event>();
		}
		temp.add(event);	
		promoter.setPromotes(temp);	
		promoterRepository.save(promoter);
		eventRepository.save(event);
	}
	
	@Transactional
	public Circus createCircus(String name, Date circusDate, Time valueOf, Time valueOf2, String company) {
		String error = "";
		if (name == null || name.trim().length() == 0) {
			error = error + "Event name cannot be empty! ";
		} else if (eventRepository.existsById(name)) {
			error = error + "Event has already been created!";
		}
		if (circusDate == null) {
			error = error + "Event date cannot be empty!";
		}
		if (valueOf == null) {
			error = error + "Event start time cannot be empty!";
		}
		if (valueOf2 == null) {
			error = error + "Event end time cannot be empty!";
		}
		if (valueOf2 != null && valueOf != null && valueOf2.before(valueOf)) {
			error = error + "Event end time cannot be before event start time!";
		}
		if (company == null || company.trim().length() == 0) {
			error = error + "Circus company cannot be empty!";
		}
		error = error.trim();
		if (error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		Circus circus = new Circus();
		circus.setName(name);
		circus.setDate(circusDate);
		circus.setStartTime(valueOf);
		circus.setEndTime(valueOf2);
		circus.setCompany(company);
		circusRepository.save(circus);
		return circus;
	}
	
	@Transactional
	public Circus getCircus(String name) {
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("Circus name cannot be empty!");
		}
		Circus circus = circusRepository.findCircusByName(name);
		return circus;
	}
	
	@Transactional
	public List<Circus> getAllCircuses() {
		return toList(circusRepository.findAll());
	}
	



	public Bitcoin createBitcoinPay(String id, int amount) {
		String error = "";
		boolean format = true;
		if(id == null || id.trim().length()!=9 ||  id.length()!=9) {
			format = false;
		}else {
			if(id.charAt(4)!='-') format = false;
			String prev = id.substring(0, 4);
			String after = id.substring(5);
			if(this.is_alpha(prev) == false) format = false;
			if(this.isInteger(after) == false) format = false;

		}
		if(id == null || format == false) {
			error = error + "User id is null or has wrong format!";
		}
		if(amount < 0) {
			error = error + "Payment amount cannot be negative!";
		}
		if (error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		Bitcoin bitcoin = new Bitcoin();	
		bitcoin.setUserID(id);
		bitcoin.setAmount(amount);
		bitcoinRepository.save(bitcoin);
		return bitcoin;
	}
	
	public Bitcoin getBitcoin(Registration registration) {
		String error = "";
		if(registration == null) {
			error = error + "Registration is null!";
		}
		if (error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		return registration.getBitcoin();
	}

	public void pay(Registration r, Bitcoin ap) {
		String error = "";
		if(r == null || ap == null) {
			error = error + "Registration and payment cannot be null!";
		}
		if (error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		r.setBitcoin(ap);
		registrationRepository.save(r);
	}

	private <T> List<T> toList(Iterable<T> iterable) {
		List<T> resultList = new ArrayList<T>();
		for (T t : iterable) {
			resultList.add(t);
		}
		return resultList;
	}
		
	/**
	 * tell whether string is integer
	 * @param str is string
	 * @return true if string are integer, otherwise false
	 */
	 private boolean isInteger(String str){
         Pattern pattern=Pattern.compile("^[-\\+]?[\\d]*$");
         return pattern.matcher(str).matches();
     }
	/**
	 * tell whether string is alpha
	 * ignore lowerCase or upperCase
	 * @param str is string
	 * @return true if string are alpha, otherwise false
	 */
    private boolean is_alpha(String str) {
        if(str==null) return false;
        return str.matches("[a-zA-Z]+");
    }
	/**
	 * tell whether string is alpha or integer
	 * ignore lowerCase or upperCase
	 * @param str is string
	 * @return true if string are alpha or integer, otherwise false
	 */
    private boolean isLetterDigit(String str) {
        String regex = "^[a-z0-9A-Z]+$";
        return str.matches(regex);
    }


}

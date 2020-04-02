package ca.mcgill.ecse321.eventregistration.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;


@Entity
public class Promoter extends Person {
	private Set<Event> events;
	
	@OneToMany(mappedBy="managingEvents" )
	public Set<Event> getEvents(){
	     return this.events;
	}
	
	public void setEvents(Set<Event> events) {
		  this.events = events;
	}
}

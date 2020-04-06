package ca.mcgill.ecse321.eventregistration.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;


@Entity
public class Promoter extends Person {
	private Set<Event> promotes;
	
	public Promoter() {
		promotes = new HashSet<Event>();
	}
	
	@OneToMany(mappedBy="promoter", cascade = { CascadeType.ALL })
	public Set<Event> getPromotes(){
	     return this.promotes;
	}
	
	public void setPromotes(Set<Event> promotes) {
		  this.promotes = promotes;
	}
}

package ca.mcgill.ecse321.eventregistration.dao;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.eventregistration.model.Event;
import ca.mcgill.ecse321.eventregistration.model.Promoter;

public interface EventRepository extends CrudRepository<Event, String> {

	Event findByName(String name);
	Set<Event> findByPromoter (Promoter promoter);
}

package ca.mcgill.ecse321.eventregistration.dao;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.eventregistration.model.Bitcoin;
import ca.mcgill.ecse321.eventregistration.model.Registration;

public interface BitcoinRepository extends CrudRepository<Bitcoin, String> {
	Bitcoin findBitcoinByUserID(String userID);

}

package edu.sjsu.cmpe275.lab2.respository;

import edu.sjsu.cmpe275.lab2.entity.Reservation;
import org.springframework.data.repository.CrudRepository;

public interface ReservationRepository extends CrudRepository<Reservation,String>{
}

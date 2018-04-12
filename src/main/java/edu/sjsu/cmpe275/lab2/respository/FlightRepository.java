package edu.sjsu.cmpe275.lab2.respository;

import edu.sjsu.cmpe275.lab2.entity.Flight;

import org.springframework.data.repository.CrudRepository;

public interface FlightRepository extends CrudRepository<Flight,String>{
}

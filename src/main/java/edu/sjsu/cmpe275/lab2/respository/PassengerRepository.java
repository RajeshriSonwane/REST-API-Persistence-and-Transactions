package edu.sjsu.cmpe275.lab2.respository;

import edu.sjsu.cmpe275.lab2.entity.Passenger;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

@Transactional(Transactional.TxType.REQUIRED)
public interface PassengerRepository extends CrudRepository<Passenger, String> {
    @Query(value = "SELECT DISTINCT * FROM reservationsystem.passenger p, reservationsystem.reservation r WHERE p.id = r.passenger_id AND r.reservation_number = :reservationNumber",
            nativeQuery = true)
    Passenger getPassengerByOrderNo(@Param("reservationNumber") String reservationNumber);
}

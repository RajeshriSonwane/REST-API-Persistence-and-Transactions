package edu.sjsu.cmpe275.lab2.respository;

import edu.sjsu.cmpe275.lab2.entity.Reservation;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Transactional(Transactional.TxType.REQUIRED)
public interface ReservationRepository extends CrudRepository<Reservation, String> {
    /**
     * Search for reservations list.
     *
     * @param passengerId  the passenger id
     * @param dest_from    the dest from
     * @param dest_to      the dest to
     * @param flightNumber the flight number
     * @return the list
     */
    @Query(value = "SELECT DISTINCT * FROM reservationsystem.passenger p, reservationsystem.reservation r, reservationsystem.reservation_flights fr, reservationsystem.flight f " +
            "WHERE p.id = r.passenger_id AND r.reservation_number = fr.reservations_reservation_number AND fr.flights_flight_number = f.flight_number "+
            "AND r.passenger_id = COALESCE(:passengerId,r.passenger_id) AND f.flight_number = COALESCE(:flightNumber,f.flight_number) " +
            "AND f.origin = COALESCE(:dest_from,f.origin) and f.destination = COALESCE(:dest_to,f.destination)",
            nativeQuery = true)
    List<Reservation> searchForReservations(@Param("passengerId") String passengerId, @Param("dest_from") String dest_from,
                                            @Param("dest_to") String dest_to, @Param("flightNumber") String flightNumber);
}
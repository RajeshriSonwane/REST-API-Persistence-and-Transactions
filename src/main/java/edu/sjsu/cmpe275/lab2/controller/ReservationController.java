package edu.sjsu.cmpe275.lab2.controller;

import edu.sjsu.cmpe275.lab2.entity.*;
import edu.sjsu.cmpe275.lab2.respository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private FlightRepository flightRepository;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getReservation(@PathVariable("id") String number) {
        //query db
        Reservation reservation = reservationRepository.findOne(number);

        if (reservation == null) {
            return new ResponseEntity<>(new BadRequestController(new BadRequest(404, "Sorry, the requested reservation with id " +
                    number + " does not exist")), HttpStatus.NOT_FOUND);
        } else {
            //Passenger passenger = passengerRepository.getPassengerByOrderNo(reservation.getOrderNumber());
            //reservation.setPassenger(new PassengerLtdInfo(passenger));
            return new ResponseEntity<>(reservation, HttpStatus.OK);
        }
    }

    private Reservation removePassenger(Reservation reservation) {
        Set<Flight> flights = reservation.getFlights();
        for (Flight f : flights) {
            f.setPassengers(null);
        }
        reservation.setFlights(flights);
        return reservation;
    }

    /**
     * (9) Search for reservations.
     *
     * @param passengerId  the passenger id
     * @param origin       the from
     * @param to           the to
     * @param flightNumber the flight number
     * @return the response entity
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> searchReservation(@RequestParam(value = "passengerId", required = false) String passengerId,
                                               @RequestParam(value = "origin", required = false) String origin,
                                               @RequestParam(value = "to", required = false) String to,
                                               @RequestParam(value = "flightNumber", required = false) String flightNumber) {

        System.out.println(passengerId + "-----------" + origin + "-----------" + to + "--------" + flightNumber);
        if (passengerId == null && origin == null && to == null && flightNumber == null) {
            return new ResponseEntity<>(new BadRequest(new Response(400, "Atleast one parameter must be present!")),
                    HttpStatus.BAD_REQUEST);
        }

        List<Reservation> reservations = reservationRepository.searchForReservations(passengerId, origin, to, flightNumber);

        System.out.println("Search repository" + reservations.get(0).getReservationNumber());

        // hack: printing passenger at the app layer to avoid infinite recursion
        for (Reservation reservation : reservations) {

            Passenger passenger = passengerRepository.getPassengerByOrderNo(reservation.getReservationNumber());
            System.out.println(passenger.getFirstname() + passenger.getId() + passenger.getGender());
            //  reservation.setPassenger(new PassengerLtdInfo(passenger));
            //  reservation.setPassenger(new Passenger(passenger));
        }

        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    /**
     * (10) Cancel a reservation.
     *
     * @param number the number
     * @return the response entity
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @RequestMapping(value = "/{number}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteReservation(@PathVariable("number") String number) {
        // query db
        Reservation reservation = reservationRepository.findOne(number);
        if (reservation == null) {
            return new ResponseEntity<>(new Response(404, "Reservation with number " + number + " does not exist"),
                    HttpStatus.NOT_FOUND);
        } else {
            // cancel a reservation for a passenger
            Passenger passenger = reservation.getPassenger();
            System.out.println("Deleted Passenger" + passenger.getFirstname());
            passenger.getReservations().remove(reservation);
            passengerRepository.save(passenger);

            // update the number of available seats for the involved flight
            Set<Flight> flights = reservation.getFlights();
            for (Flight flight : flights) {
                flight.incrementSeatsLeftByOne();
            }
            // batch query for improved performance
            flightRepository.save(flights);

            // write to db
            reservationRepository.delete(number);
            return new ResponseEntity<>(new Response(200, "Reservation with number " + number +
                    " is deleted successfully"), HttpStatus.OK);
        }
    }
}

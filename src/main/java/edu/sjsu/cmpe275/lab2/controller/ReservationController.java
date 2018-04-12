package edu.sjsu.cmpe275.lab2.controller;


import edu.sjsu.cmpe275.lab2.entity.*;
import edu.sjsu.cmpe275.lab2.respository.PassengerRepository;
import edu.sjsu.cmpe275.lab2.respository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.sjsu.cmpe275.lab2.respository.PassengerRepository;

@RestController
@RequestMapping(value = "/reservation")
public class ReservationController {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @RequestMapping(value = "/{reservationNumber}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getReservationJson(@PathVariable("reservationNumber") String reservationNumber) {
        return getReservation(reservationNumber);
    }

    @RequestMapping(value = "/{reservationNumber}", method = RequestMethod.GET, params = "xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> getPersonXml(@PathVariable("reservationNumber") String reservationNumber, @RequestParam(value = "xml") String isXml) {
        if (isXml.equals("true")) {
            return getReservation(reservationNumber);

        } else {
            return new ResponseEntity<>(new BadRequestController(new BadRequest(400, "Xml param; found in invalid state!")),
                    HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<?> getReservation(String reservationNumber) {
        //query db
        Reservation reservation = reservationRepository.findOne(reservationNumber);

        if (reservation == null) {
            System.out.println("Not FOund");
            return new ResponseEntity<>(new BadRequestController(new BadRequest(404, "Sorry, the requested reservation with id " +
                    reservationNumber + " does not exist")), HttpStatus.NOT_FOUND);
        } else {
            Passenger passenger = passengerRepository.getPassengerByOrderNo(reservation.getReservationNumber());
            reservation.setPassenger(new PassengerDetails(passenger));
            System.out.println("Found Passenger");
            return new ResponseEntity<>(reservation, HttpStatus.OK);
        }
    }
}

package edu.sjsu.cmpe275.lab2.controller;

import edu.sjsu.cmpe275.lab2.entity.*;
import edu.sjsu.cmpe275.lab2.respository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/flight")
public class FlightController {

    @Autowired
    private FlightRepository flightRepository;

    /**
     * (1) Get a flight back as JSON.
     *
     * @param flightNumber the flight number
     * @return the flight json
     */
    @RequestMapping(value = "/{flightNumber}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getFlightJson(@PathVariable("flightNumber") String flightNumber) {

        return getFlight(flightNumber);

    }

    /**
     * (2) Get a flight back as XML.
     *
     * @param flightNumber the flight number
     * @param isXml        the is xml
     * @return the flight xml
     */
    @RequestMapping(value = "/{flightNumber}", method = RequestMethod.GET, params = "xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> getFlightXml(@PathVariable("flightNumber") String flightNumber,
                                          @RequestParam(value = "xml") String isXml) {
        if (isXml.equals("true")) {
            return getFlight(flightNumber);

        } else {
            return new ResponseEntity<>(new BadRequestController(new BadRequest(400, "Xml param; found in invalid state!")),
                    HttpStatus.BAD_REQUEST);
        }

    }

    private ResponseEntity<?> getFlight(String flightNumber) {
        //query db
        Flight flight = flightRepository.findOne(flightNumber);

        if (flight == null) {
            return new ResponseEntity<>(new BadRequestController(new BadRequest(404, "Sorry, the requested flight with number "
                    + flightNumber + " does not exist")), HttpStatus.NOT_FOUND);

        } else {
            /*
            List<PassengerLtdInfo> passengers = new ArrayList<>();
            for (Reservation reservation : flight.getReservations()) {
                passengers.add(new PassengerLtdInfo(reservation.getPassengerFKey()));
            }
            flight.setPassengers(passengers);*/

            return new ResponseEntity<>(flight, HttpStatus.OK);
        }

    }

}

package edu.sjsu.cmpe275.lab2.controller;

import edu.sjsu.cmpe275.lab2.entity.*;
import edu.sjsu.cmpe275.lab2.respository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.sjsu.cmpe275.lab2.service.IntervalStartComparator;
import org.joda.time.Interval;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

            /*List<PassengerLtdInfo> passengers = new ArrayList<>();
            for (Reservation reservation : flight.getReservations()) {
                passengers.add(new PassengerLtdInfo(reservation.getPassengerFKey()));
            }
            flight.setPassengers(passengers);*/

            return new ResponseEntity<>(flight, HttpStatus.OK);
        }

    }

    /**
     * (13) Create or update a flight.
     *
     * @param flightNumber      the flight number
     * @param price             the price
     * @param from              the from
     * @param to                the to
     * @param departureTime     the departure time
     * @param arrivalTime       the arrival time
     * @param description       the description
     * @param capacity          the capacity
     * @param model             the model
     * @param manufacturer      the manufacturer
     * @param yearOfManufacture the year of manufacture
     * @return the response entity
     */
    @RequestMapping(value = "/{flightNumber}", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> UpdateFlight(@PathVariable("flightNumber") String flightNumber,
                                                  @RequestParam(value = "price") int price,
                                                  @RequestParam(value = "from") String from,
                                                  @RequestParam(value = "to") String to,
                                                  @RequestParam(value = "departureTime") String departureTime,
                                                  @RequestParam(value = "arrivalTime") String arrivalTime,
                                                  @RequestParam(value = "description") String description,
                                                  @RequestParam(value = "capacity") int capacity,
                                                  @RequestParam(value = "model") String model,
                                                  @RequestParam(value = "manufacturer") String manufacturer,
                                                  @RequestParam(value = "yearOfManufacture") int yearOfManufacture) {

        System.out.println("IN create flight");
       // to update the seatsLeft when capacity is modified
        Flight flight = flightRepository.findOne(flightNumber);

        System.out.println(flight + "-" + flightNumber + "-" + price + "-" + from + "-" + to + "-" + departureTime + "-" + arrivalTime + "-" + description + "-" + capacity + "-" + model + "-" + manufacturer + "-" + yearOfManufacture);
        int seatsLeft = capacity;

        if (arrivalTime.equals(departureTime)) {
            return new ResponseEntity<>(new BadRequest(new Response(400, "Arrival and Departure Time can't be same!")),
                    HttpStatus.BAD_REQUEST);
        }

        if (capacity < 0) {
            return new ResponseEntity<>(new BadRequest(new Response(400, "Capacity can't be negative value!")),
                    HttpStatus.BAD_REQUEST);
        }

        if (flight != null && capacity < flight.getReservations().size()) {
            return new ResponseEntity<>(new BadRequest(new Response(400, "New capacity can't be less than " +
                    "the reservations for that flight!")),
                    HttpStatus.BAD_REQUEST);
        }

        if (flight != null) {
            // set flight capacity
            seatsLeft = capacity - flight.getReservations().size();
            // placeholder
            List<Interval> intervals = new ArrayList<>();

            for (Reservation reservation : flight.getReservations()) {

                Passenger passenger = reservation.getPassenger();
                List<Reservation> passengerReservations = passenger.getReservations();

                for (Reservation passengerReservation : passengerReservations) {
                    Set<Flight> flights = passengerReservation.getFlights();

                    for (Flight passengerFlight : flights) {
                        // compute interval
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh", Locale.US);

                        try {
                            Date departureDateTime = simpleDateFormat.parse(String.valueOf(passengerFlight.getDepartureTime()));
                            Date arrivalDateTime = simpleDateFormat.parse(String.valueOf(passengerFlight.getArrivalTime()));

                            intervals.add(new Interval(departureDateTime.getTime(), arrivalDateTime.getTime()));

                        } catch (ParseException e) {
                            e.printStackTrace();
                            return new ResponseEntity<>(new BadRequest(new Response(500, "Invalid Date Format!")),
                                    HttpStatus.BAD_REQUEST);

                        }

                    }
                }
            }

            Collections.sort(intervals, new IntervalStartComparator());

            // check Time-Overlap for a certain passenger
            if (isOverlapping(intervals)) {
                return new ResponseEntity<>(new BadRequest(new Response(400, "Passenger will have overlapping flight time!")),
                        HttpStatus.BAD_REQUEST);
            }

        }

        // save to db
        Flight flightObjFromDb = flightRepository.save(new Flight(flightNumber, price, from, to, departureTime,
                arrivalTime, description, seatsLeft, new Plane(capacity, model, manufacturer, yearOfManufacture)));

        return new ResponseEntity<>(flightObjFromDb, HttpStatus.OK);

    }

    public static boolean isOverlapping(List<Interval> sortedIntervals) {
        for (int i = 0, n = sortedIntervals.size(); i < n - 1; i++) {
            if (sortedIntervals.get(i).overlaps(sortedIntervals.get(i + 1))) {
                return true;
            }
        }
        return false;
    }

    /**
     * (14) Delete a flight.
     *
     * @param flightNumber the flight number
     * @return the response entity
     */
    @RequestMapping(value = "/airline/{flightNumber}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteFlight(@PathVariable("flightNumber") String flightNumber) {

        // query db
        Flight flight = flightRepository.findOne(flightNumber);
        System.out.println("In delete flight" + flight);
        if (flight == null) {
            return new ResponseEntity<>(new Response(404, "Flight with number " +
                    flightNumber + " does not exist"), HttpStatus.NOT_FOUND);

        } else if (!flight.getReservations().isEmpty()) {
            return new ResponseEntity<>(new BadRequest(new Response(400, "Flight with number " + flightNumber
                    + " has one or more reservation")), HttpStatus.BAD_REQUEST);

        } else {
            // remove from db
            flightRepository.delete(flightNumber);
            return new ResponseEntity<>(new Response(200, "Flight with number " + flightNumber +
                    " is deleted successfully"), HttpStatus.OK);
        }
    }
}

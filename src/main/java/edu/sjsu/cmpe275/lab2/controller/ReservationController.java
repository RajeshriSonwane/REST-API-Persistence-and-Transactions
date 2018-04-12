package edu.sjsu.cmpe275.lab2.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import edu.sjsu.cmpe275.lab2.entity.*;
import edu.sjsu.cmpe275.lab2.respository.*;
import edu.sjsu.cmpe275.lab2.util.IntervalStartComparator;
import edu.sjsu.cmpe275.lab2.util.View;
import org.json.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

import org.joda.time.Interval;


@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private FlightRepository flightRepository;


    @JsonView(View.ReservationView.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getReservation(@PathVariable("id") String number) {
        //query db
        Reservation reservation = reservationRepository.findOne(number);

        if (reservation == null) {
            return new ResponseEntity<>(new BadRequest(404, "Sorry, the requested reservation with id " +
                    number + " does not exist"), HttpStatus.NOT_FOUND);

        } else {
            System.out.println("reservation" + reservation);
            return new ResponseEntity<>(reservation, HttpStatus.OK);
        }

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = "xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> getReservationXml(@PathVariable("id") String number, @RequestParam(value = "xml") String isXml) {
        if (isXml.equals("true")) {
            Reservation reservation = reservationRepository.findOne(number);
            return new ResponseEntity<>(reservation, HttpStatus.OK);

        } else {
            return new ResponseEntity<>(new BadRequest(400, "Xml param; found in invalid state!"),
                    HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * (7) Make a reservation.
     *
     * @param passengerId   the passenger id
     * @param flightNumbers the flight numbers
     * @return the response entity
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @RequestMapping(method = RequestMethod.POST, params = {"passengerId", "flightLists"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createReservation(@RequestParam(value = "passengerId") String passengerId,
                                               @RequestParam(value = "flightLists") List<String> flightNumbers) {

        Passenger passenger = passengerRepository.findOne(passengerId);

        if (passenger == null) {
            return new ResponseEntity<>(new BadRequest(400, "Passenger with passenger with id " +
                    passengerId + " does not exist"), HttpStatus.BAD_REQUEST);
        }

        if (flightNumbers.size() == 0) {
            return new ResponseEntity<>(new BadRequest(400, "Flight list is empty"), HttpStatus.BAD_REQUEST);
        }

        List<Flight> flights = new ArrayList<>(); // placeholder for flights
        List<Interval> intervals = new ArrayList<>(); //to check overlapping between the intervals of flights

        Iterable<Flight> itrFlights = flightRepository.findAll(flightNumbers);
        int countFlights = 0;
        double price = 0;
        for (Flight flight : itrFlights) {
            price += flight.getPrice();

            Date departureDateTime = (flight.getDepartureTime());
            Date arrivalDateTime = (flight.getArrivalTime());

            intervals.add(new Interval(departureDateTime.getTime(), arrivalDateTime.getTime()));

            // The total amount of passengers can not exceed the capacity of the reserved plane.
            if (flight.decrementSeatsLeftByOne()) {
                return new ResponseEntity<>(new BadRequest(400, "The total amount of passengers can not exceed the capacity of the reserved plane."), HttpStatus.BAD_REQUEST);
            }

            List<Passenger> passengers = flight.getPassengers();
            if (passengers.contains(passenger)) {
                return new ResponseEntity<>(new BadRequest(400, "You already in one flight of the reservation"), HttpStatus.BAD_REQUEST);

            } else {
                int seat = flight.getSeatsLeft();
                if (seat == 0) {
                    return new ResponseEntity<>(new BadRequest(400, "Some flight in your reservation is full, cannot reservate for you"), HttpStatus.BAD_REQUEST);
                } else {
                    seat--;
                }
                flight.setSeatsLeft(seat);
                passengers.add(passenger);
            }
            flight.setPassengers(passengers);
            flights.add(flight);
            countFlights++;
        }

        if (countFlights != flightNumbers.size()) {
            return new ResponseEntity<>(new BadRequest(404, "Some flights from the list does not exist"), HttpStatus.NOT_FOUND);
        }

        List<Flight> passengerPreviousFlights = new ArrayList<>();
        // query for passenger's existing reservation information
        for (Reservation reservation : passenger.getReservations()) {
            passengerPreviousFlights.addAll(reservation.getFlights());
        }

        Collections.sort(intervals, new IntervalStartComparator());

        if (isOverlapping(intervals)) {
            return new ResponseEntity<>(new BadRequest(400, "Time-Overlap is not allowed!"), HttpStatus.BAD_REQUEST);
        }

        Reservation reservation = reservationRepository.save(new Reservation(passenger, price, flights));
        // update flights db
        flightRepository.save(flights);


        if (reservation == null) {
            return new ResponseEntity<>(new BadRequest(500, "Internal error"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }


    /**
     * (8) Update a reservation.
     *
     * @param number         the number
     * @param flightsAdded   the flights added
     * @param flightsRemoved the flights removed
     * @return the response entity
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @RequestMapping(value = "/{number}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateReservation(@PathVariable("number") String number,
                                               @RequestParam(value = "flightsAdded", required = false) List<String> flightsAdded,
                                               @RequestParam(value = "flightsRemoved", required = false) List<String> flightsRemoved) {

        Reservation reservation = reservationRepository.findOne(number);

        if (reservation == null) {
            return new ResponseEntity<>(new BadRequest(404, "Reserveration with number " + number + " does not exist"), HttpStatus.NOT_FOUND);
        }

        // If flightsAdded (or flightsRemoved) param exists, then its list of values cannot be empty.
        if (flightsAdded != null && flightsAdded.isEmpty()) {
            return new ResponseEntity<>(new BadRequest(404, "flightsAdded list cannot be empty, if param exists"), HttpStatus.NOT_FOUND);
        }

        if (flightsRemoved != null && flightsRemoved.isEmpty()) {
            return new ResponseEntity<>(new BadRequest(404, "flightsRemoved list cannot be empty, if param exists"), HttpStatus.NOT_FOUND);

        }

        List<Flight> flights = reservation.getFlights();
        List<Flight> removedFlights = new ArrayList<>();
        List<Flight> addedFlights = new ArrayList<>();

        double price = reservation.getPrice();

        if (flightsRemoved != null) {
            for (String flightNumber : flightsRemoved) {
                for (Flight flight : flights) {

                    if (flightNumber.equals(flight.getFlightNumber())) {
                        // compute modified reservation price; part one
                        price -= flight.getPrice();
                        flights.remove(flight);

                        // to update flights db
                        flight.incrementSeatsLeftByOne();
                        removedFlights.add(flight);
                        break;
                    }
                }// end of inner loop
            }// end of outer loop
            // update flights db
            flightRepository.save(removedFlights);
        }

        int loopCount = 0;

        if (flightsAdded != null) {
            // place holder to compute intervals: duration between arrival and departure for each flight
            List<Interval> intervals = new ArrayList<>();

            // query db for flight details
            Iterable<Flight> iFlights = flightRepository.findAll(flightsAdded);

            for (Flight flight : iFlights) {
                // compute modified reservation price; part two
                price += flight.getPrice();

                Date departureDateTime = (flight.getDepartureTime());
                Date arrivalDateTime = (flight.getArrivalTime());

                intervals.add(new Interval(departureDateTime.getTime(), arrivalDateTime.getTime()));
                // The total amount of passengers can not exceed the capacity of the reserved plane.
                if (flight.decrementSeatsLeftByOne()) {
                    return new ResponseEntity<>(new BadRequest(400, "The total amount of passengers can not exceed the capacity of the reserved plane."), HttpStatus.BAD_REQUEST);

                }

                // to update flights table
                addedFlights.add(flight);
                loopCount++;
            }

            if (loopCount != flightsAdded.size()) {
                return new ResponseEntity<>(new BadRequest(404, "Some flights from the list does not exist"), HttpStatus.NOT_FOUND);
            }

            List<Flight> passengerPreviousFlights = new ArrayList<>();
            // query passenger's previous reservation information
            for (Reservation eachReservation : reservation.getPassenger().getReservations()) {
                passengerPreviousFlights.addAll(eachReservation.getFlights());
            }

            for (Flight flight : passengerPreviousFlights) {
                // compute interval
                Date departureDateTime = (flight.getDepartureTime());
                Date arrivalDateTime = (flight.getArrivalTime());

                intervals.add(new Interval(departureDateTime.getTime(), arrivalDateTime.getTime()));
            }

            Collections.sort(intervals, new IntervalStartComparator());

            if (isOverlapping(intervals)) {
                return new ResponseEntity<>(new BadRequest(400, "Time-Overlap is not allowed!"), HttpStatus.BAD_REQUEST);

            }

            flights.addAll(addedFlights);
            // update flights db
            flightRepository.save(addedFlights);
        }

        //reservation.setFlights(flights);
        reservation.setPrice(price);
        Reservation reservationObjFromDb = reservationRepository.save(reservation);

        //Passenger passenger = passengerRepository.getPassengerByOrderNo(reservationObjFromDb.getReservationNumber());
        //reservationObjFromDb.setPassengerInfo(new PassengerInformation(passenger));

        return new ResponseEntity<>(reservationObjFromDb, HttpStatus.OK);
    }

    /**
     * Is overlapping boolean.
     * Checks for overlapping in the flights
     *
     * @param sortedIntervals the sorted intervals
     * @return the boolean
     */
    public static boolean isOverlapping(List<Interval> sortedIntervals) {
        for (int i = 0, n = sortedIntervals.size(); i < n - 1; i++) {
            if (sortedIntervals.get(i).overlaps(sortedIntervals.get(i + 1))) {
                return true;
            }
        }
        return false;
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
            return new ResponseEntity<>(new BadRequest(404, "Reservation with number " + number + " does not exist"),
                    HttpStatus.NOT_FOUND);
        } else {
            // cancel a reservation for a passenger
            Passenger passenger = reservation.getPassenger();
            System.out.println("Deleted Passenger" + passenger.getFirstname());
            passenger.getReservations().remove(reservation);
            passengerRepository.save(passenger);

            // update the number of available seats for the involved flight
            List<Flight> flights = reservation.getFlights();
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
            return new ResponseEntity<>(new BadRequest(400, "Atleast one parameter must be present!"), HttpStatus.BAD_REQUEST);
        }

        List<Reservation> reservations = reservationRepository.searchForReservations(passengerId, origin, to, flightNumber);

        JSONObject passengerJSON = new JSONObject();
        JSONObject reservationJSON = new JSONObject();
        JSONObject flightJSON = new JSONObject();
        JSONObject finalJSON = new JSONObject();
        JSONArray flightJSONArray = new JSONArray();

        finalJSON.put("reservation", reservationJSON);
        for (Reservation reservation : reservations) {
            reservationJSON.put("reservationNumber", reservation.getReservationNumber());
            reservationJSON.put("price", reservation.getPrice());
            reservationJSON.put("passenger", passengerJSON);
            reservationJSON.put("flights", flightJSON);

            passengerJSON.put("id", reservation.getPassenger().getId());
            passengerJSON.put("firstname", reservation.getPassenger().getFirstname());
            passengerJSON.put("lastname", reservation.getPassenger().getLastname());
            passengerJSON.put("age", reservation.getPassenger().getAge());
            passengerJSON.put("gender", reservation.getPassenger().getGender());
            passengerJSON.put("phone", reservation.getPassenger().getPhone());

            flightJSON.put("flight", flightJSONArray);
            for (Flight f : reservation.getFlights()) {
                JSONObject fJSON = new JSONObject();

                fJSON.put("number", f.getFlightNumber());
                fJSON.put("price", f.getPrice());
                fJSON.put("origin", f.getOrigin());
                fJSON.put("to", f.getDescription());
                fJSON.put("departureTime", f.getDepartureTime());
                fJSON.put("arrivalTime", f.getArrivalTime());
                fJSON.put("seatsLeft", f.getSeatsLeft());
                fJSON.put("description", f.getDescription());
                //get plane object in JSON
                JSONObject planeJson = new JSONObject();
                planeJson.put("capacity", f.getPlane().getCapacity());
                planeJson.put("model", f.getPlane().getModel());
                planeJson.put("manufacturer", f.getPlane().getManufacturer());
                planeJson.put("year", f.getPlane().getYear());
                fJSON.put("plane", planeJson);
                flightJSONArray.put(fJSON);
            }
        }
        System.out.println(finalJSON);
        return new ResponseEntity<>(XML.toString(finalJSON), HttpStatus.OK);
    }
}

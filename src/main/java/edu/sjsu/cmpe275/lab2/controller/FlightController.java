package edu.sjsu.cmpe275.lab2.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.sjsu.cmpe275.lab2.entity.*;
import edu.sjsu.cmpe275.lab2.respository.FlightRepository;
import edu.sjsu.cmpe275.lab2.util.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

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

    @JsonView(View.FlightView.class)
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
    public ResponseEntity<?> createOrUpdateFlight(@PathVariable("flightNumber") String flightNumber,
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
        try{

            if(from==null||to==null||from==""||to==""||departureTime==null||arrivalTime==null ||departureTime==""||arrivalTime==""||description==null||
                    description==""||model==null||model==""||manufacturer==null||manufacturer==""||price==0||capacity==0||yearOfManufacture==0){
                    return new ResponseEntity<>(new BadRequestController(new BadRequest(400, "all parameters are not filled")), HttpStatus.BAD_REQUEST);
            }
            return null;

        }catch (DataIntegrityViolationException e){
            return new ResponseEntity<>(new BadRequestController(new BadRequest(400, "another passenger with the same number already exists.")), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * (14) Delete a flight.
     *
     * @param flightNumber the flight number
     * @return the response entity
     */
    @RequestMapping(value = "/{flightNumber}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteFlight(@PathVariable("flightNumber") String flightNumber) {

        // query db
        Flight flight = flightRepository.findOne(flightNumber);
        System.out.println("In delete flight" + flight);
        if (flight == null) {
            return new ResponseEntity<>(new BadRequestController(new BadRequest(404, "Flight with number " + flightNumber + " does not exist")), HttpStatus.NOT_FOUND);

            //return new ResponseEntity<>(new Response(404, "Flight with number " + flightNumber + " does not exist"), HttpStatus.NOT_FOUND);

        } else if (!flight.getReservations().isEmpty()) {
            return new ResponseEntity<>(new BadRequestController(new BadRequest(400, "Flight with number " + flightNumber + " has one or more reservation")), HttpStatus.BAD_REQUEST);

            //return new ResponseEntity<>(new BadRequest(new Response(400, "Flight with number " + flightNumber + " has one or more reservation")), HttpStatus.BAD_REQUEST);

        } else {
            // remove from db
            flightRepository.delete(flightNumber);
            return new ResponseEntity<>(new BadRequestController(new BadRequest(200, "Flight with number " + flightNumber +  " is deleted successfully")), HttpStatus.OK);

            //return new ResponseEntity<>(new Response(200, "Flight with number " + flightNumber +   " is deleted successfully"), HttpStatus.OK);
        }
    }

}

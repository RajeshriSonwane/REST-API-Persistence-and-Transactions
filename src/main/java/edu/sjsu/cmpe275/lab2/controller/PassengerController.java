package edu.sjsu.cmpe275.lab2.controller;

import edu.sjsu.cmpe275.lab2.entity.*;

import edu.sjsu.cmpe275.lab2.respository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping(value = "/passenger")
public class PassengerController {

    @Autowired
    private PassengerRepository passengerRepository;

    /**
     * (1) Get a passenger back as JSON
     *
     * @param id the id
     * @return the passenger json
     */
    @RequestMapping(value = "/{id}",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPassengerJson(@PathVariable("id") String id) {

        return getPassenger(id);

    }

    /**
     * (2) Get a passenger back as XML.
     *
     * @param id    the id
     * @param isXml the is xml
     * @return the person xml
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = "xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> getPersonXml(@PathVariable("id") String id, @RequestParam(value = "xml") String isXml) {
        if (isXml.equals("true")) {
            return getPassenger(id);

        } else {
            return new ResponseEntity<>(new BadRequestController(new BadRequest(400, "Xml param; found in invalid state!")),
                    HttpStatus.BAD_REQUEST);
        }

    }


    private ResponseEntity<?> getPassenger(String id) {
        //query db
        Passenger passenger = passengerRepository.findOne(id);

        if (passenger == null) {
            System.out.println("Not FOund");
            return new ResponseEntity<>(new BadRequestController(new BadRequest(404, "Sorry, the requested passenger with id " +
                    id + " does not exist")), HttpStatus.NOT_FOUND);
        } else {
            System.out.println("Found Passenger");
            return new ResponseEntity<>(passenger, HttpStatus.OK);

        }

    }




}

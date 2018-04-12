package edu.sjsu.cmpe275.lab2.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.sjsu.cmpe275.lab2.entity.*;

import edu.sjsu.cmpe275.lab2.respository.PassengerRepository;
import edu.sjsu.cmpe275.lab2.util.View;
import org.hibernate.Session;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.json.XML;



import java.awt.print.Book;

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
    @JsonView(View.PassengerView.class)
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
    @JsonView(View.PassengerView.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = "xml")
    public ResponseEntity<?> getPersonXml(@PathVariable("id") String id, @RequestParam(value = "xml") String isXml) {
        if (isXml.equals("true")) {
            return getPassengerJson(id);

        } else {
            return new ResponseEntity<>(new BadRequestController(new BadRequest(400, "Xml param; found in invalid state!")),
                    HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * (3) Get a passenger
     *
     * @param id    the id
     * @param isxml
     * @return the person xml
     */
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

    /**
     * (4) Create a passenger.
     *
     * @param firstname the firstname
     * @param lastname  the lastname
     * @param age       the age
     * @param gender    the gender
     * @param phone     the phone
     * @return the response entity
     */
    @RequestMapping(method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPassenger(@RequestParam(value="firstname") String firstname,
                                            @RequestParam(value="lastname") String lastname,
                                            @RequestParam(value="age") int age,
                                            @RequestParam(value="gender") String gender,
                                            @RequestParam(value="phone") String phone ){
        try{
            if(firstname==null||lastname==null||firstname==""||lastname==""||age== 0||gender==null||gender==""||phone==null||phone==""){
                return new ResponseEntity<>(new BadRequestController(new BadRequest(400, "all parameters are not filled")), HttpStatus.BAD_REQUEST);
            }else
            {
                Passenger passenger = passengerRepository.save(new Passenger(firstname,lastname,age,gender,phone));
                return new ResponseEntity<>(passenger, HttpStatus.OK);
            }
        }catch (DataIntegrityViolationException e){
            return new ResponseEntity<>(new BadRequestController(new BadRequest(400, "another passenger with the same number already exists.")), HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * (5) Update a passenger.
     *
     * @param id        the id
     * @param firstname the firstname
     * @param lastname  the lastname
     * @param age       the age
     * @param gender    the gender
     * @param phone     the phone
     * @return the response entity
     */
    @RequestMapping(method= RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePassenger(@RequestParam(value="id") String id,
                                             @RequestParam(value="firstname") String firstname,
                                             @RequestParam(value="lastname") String lastname,
                                             @RequestParam(value="age") int age,
                                             @RequestParam(value="gender") String gender,
                                             @RequestParam(value="phone") String phone ){
        try {
            if(id== null||id==""){
                return new ResponseEntity<>(new BadRequestController(new BadRequest(400, "Passenger id is missing")), HttpStatus.BAD_REQUEST);
            }else
            {
                Passenger passenger = passengerRepository.findOne(id);
                if(passenger == null)
                    return new ResponseEntity<>(new BadRequestController(new BadRequest(400, "Passenger id doesnt exist")), HttpStatus.BAD_REQUEST);
                else
                {
                    passenger.setFirstname(firstname);
                    passenger.setLastname(lastname);
                    passenger.setAge(age);
                    passenger.setPhone(phone);
                    passenger.setGender(gender);
                    Passenger passengerUpdated = passengerRepository.save(passenger);
                    return new ResponseEntity<>(passengerUpdated, HttpStatus.OK);

                }
            }
        }catch (DataIntegrityViolationException e){
            return new ResponseEntity<>(new BadRequestController(new BadRequest(400, "another passenger with the same number already exists.")), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * (6) Delete a passenger.
     *
     * @param id the id
     * @return the response entity
     */

    //@javax.transaction.Transactional(Transactional.TxType.REQUIRED)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deletePassenger(@PathVariable("id") String id) {
        Passenger passenger = passengerRepository.findOne(id);
        if (passenger == null) {
            return new ResponseEntity<>(new Response(404, "Passenger with id " + id + " does not exist"),
                    HttpStatus.NOT_FOUND);

        }else{


            passengerRepository.delete(id);
            return new ResponseEntity<>(new Response(200, "Passenger with id " + id + " is deleted successfully"),
                    HttpStatus.OK);
        }
    }

}

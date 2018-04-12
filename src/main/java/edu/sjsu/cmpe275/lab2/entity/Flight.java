package edu.sjsu.cmpe275.lab2.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

@XmlRootElement
@Entity
@Table(name = "flight")
public class Flight {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String flightNumber; // Each flight has a unique flight number.
    private double price;
    private String origin;
    private String destination;
    private Date departureTime;
    private Date arrivalTime;
    private int seatsLeft;
    private String description;

    @Embedded
    private Plane plane;  // Embedded

    @Transient
    private List<Passenger> passengers;

    public Flight(){

    }

    public Flight(String flightNumber, double price, String origin, String destination, Date departureTime, Date arrivalTime, int seatsLeft, String description, Plane plane, List<Passenger> passengers) {
        this.flightNumber = flightNumber;
        this.price = price;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.seatsLeft = seatsLeft;
        this.description = description;
        this.plane = plane;
        this.passengers = passengers;


    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getSeatsLeft() {
        return seatsLeft;
    }

    public void setSeatsLeft(int seatsLeft) {
        this.seatsLeft = seatsLeft;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Plane getPlane() {
        return plane;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }
}

package edu.sjsu.cmpe275.lab2.entity;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

@XmlRootElement
@Entity
@Table(name = "reservation")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="passenger")
public class Reservation {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String reservationNumber;

    @ManyToOne(targetEntity = Passenger.class, fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private Passenger passenger;

    private double price;

    @ManyToMany(targetEntity = Flight.class, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Set<Flight> flights = new HashSet<Flight>();


    public Reservation(){

    }

    public Reservation(Passenger passenger, double price, Set<Flight> flights) {
        this.passenger = passenger;
        this.price = price;
        this.flights = flights;
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(String reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Set<Flight> getFlights() {
        return flights;
    }

    public void setFlights(Set<Flight> flights) {
        this.flights = flights;
    }
}

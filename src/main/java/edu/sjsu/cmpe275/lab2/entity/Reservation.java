package edu.sjsu.cmpe275.lab2.entity;

import com.fasterxml.jackson.annotation.*;
import edu.sjsu.cmpe275.lab2.util.View;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

@XmlRootElement
@Entity
@Table(name = "reservation")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("reservation")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT ,use = JsonTypeInfo.Id.NAME)
public class Reservation {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @JsonView({View.PassengerView.class, View.ReservationView.class})
    private String reservationNumber;

    @JsonView({View.PassengerView.class, View.ReservationView.class})
    private double price;


    @JsonView(View.ReservationView.class)
    @ManyToOne(targetEntity = Passenger.class, fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private Passenger passenger;

    @JsonView({View.PassengerView.class, View.ReservationView.class})
    @ManyToMany(targetEntity = Flight.class, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<Flight> flights;

    //@XmlElement
    //@Transient
    //@XmlTransient
    //private PassengerInformation passengerInfo;

    public Reservation(){

    }

    public Reservation(Passenger passenger, double price, List<Flight> flights) {
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

    @XmlTransient
    public Passenger getPassenger() {
        return passenger;
    }

    //@XmlTransient
    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @XmlTransient
    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

   /* public PassengerInformation getPassengerInfo() {
        return passengerInfo;
    }

    public void setPassengerInfo(PassengerInformation passengerInfo) {
        this.passengerInfo = passengerInfo;
    }*/
}

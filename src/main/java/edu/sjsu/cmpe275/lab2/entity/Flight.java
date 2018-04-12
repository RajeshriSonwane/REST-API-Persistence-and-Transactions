package edu.sjsu.cmpe275.lab2.entity;

import com.fasterxml.jackson.annotation.*;
import edu.sjsu.cmpe275.lab2.util.View;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@XmlRootElement
@Entity
@Table(name = "flight")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("flight")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT ,use = JsonTypeInfo.Id.NAME)
public class Flight {

    @Id
   // @GeneratedValue(generator = "uuid")
   // @GenericGenerator(name = "uuid", strategy = "uuid2")
    @JsonView({View.PassengerView.class, View.ReservationView.class, View.FlightView.class})
    private String flightNumber; // Each flight has a unique flight number.

    @JsonView({View.PassengerView.class, View.ReservationView.class, View.FlightView.class})
    private double price;

    @JsonView({View.PassengerView.class, View.ReservationView.class, View.FlightView.class})
    private String origin;

    @JsonView({View.PassengerView.class, View.ReservationView.class, View.FlightView.class})
    private String destination;

    @JsonFormat(pattern="yyyy-MM-dd-hh")
    @JsonView({View.PassengerView.class, View.ReservationView.class, View.FlightView.class})
    private Date departureTime;

    @JsonFormat(pattern="yyyy-MM-dd-hh")
    @JsonView({View.PassengerView.class, View.ReservationView.class, View.FlightView.class})
    private Date arrivalTime;

    @JsonView({View.PassengerView.class, View.ReservationView.class, View.FlightView.class})
    private int seatsLeft;

    @JsonView({View.PassengerView.class, View.ReservationView.class, View.FlightView.class})
    private String description;

    @Embedded
    @JsonView({View.PassengerView.class, View.ReservationView.class, View.FlightView.class})
    private Plane plane;  // Embedded

    @JsonView({View.FlightView.class})
    @ManyToMany(targetEntity = Passenger.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Passenger> passengers ;

    @ManyToMany(mappedBy = "flights")
    private List<Reservation> reservations;

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

    @JsonView({View.FlightView.class})
    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }

    /**
     * Increment seats left by one.
     */
    public void incrementSeatsLeftByOne() {
        seatsLeft += 1;
    }

    /**
     * Decrement seats left by one boolean.
     *
     * @return the boolean
     */
    public boolean decrementSeatsLeftByOne() {

        return --seatsLeft == -1;
    }

    /**
     * Gets reservations.
     *
     * @return the reservations
     */
    @JsonIgnore
    public List<Reservation> getReservations() {
        return reservations;
    }

}

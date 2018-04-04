package edu.sjsu.cmpe275.lab2.Reservation;

import edu.sjsu.cmpe275.lab2.Flight.Flight;
import edu.sjsu.cmpe275.lab2.Passenger.Passenger;

import java.util.List;

public class Reservation {
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

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    private String reservationNumber;
    private Passenger passenger;
    private double price; // sum of each flight’s price.
    private List<Flight> flights;

}

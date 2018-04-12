package edu.sjsu.cmpe275.lab2.entity;

import com.fasterxml.jackson.annotation.JsonView;
import edu.sjsu.cmpe275.lab2.util.View;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlType;


@Embeddable
@XmlType(propOrder={"capacity", "model", "manufacturer", "year" })
public class Plane {

    @Column
    @JsonView({View.PassengerView.class, View.FlightView.class, View.ReservationView.class})
    private int capacity;

    @Column
    @JsonView({View.PassengerView.class, View.FlightView.class, View.ReservationView.class})
    private String model;

    @Column
    @JsonView({View.PassengerView.class, View.FlightView.class, View.ReservationView.class})
    private String manufacturer;

    @Column
    @JsonView({View.PassengerView.class, View.FlightView.class, View.ReservationView.class})
    private int year;

    public Plane(){

    }

    public Plane(int capacity, String model, String manufacturer, int year) {
        this.capacity = capacity;
        this.model = model;
        this.manufacturer = manufacturer;
        this.year = year;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}

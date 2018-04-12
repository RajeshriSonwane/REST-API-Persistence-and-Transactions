package edu.sjsu.cmpe275.lab2.entity;

import edu.sjsu.cmpe275.lab2.entity.Reservation;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@Entity
@Table(name = "passenger")
public class Passenger {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String firstname;
    private String lastname;
    private int age;
    private String gender;
    private String phone;

    // reservation made by the passenger should also be deleted.
    @OneToMany(mappedBy = "passengerID", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations;

    public Passenger(){

    }
    public Passenger(String firstname, String lastname, int age, String gender, String phone, List<Reservation> reservations) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.reservations = reservations;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}
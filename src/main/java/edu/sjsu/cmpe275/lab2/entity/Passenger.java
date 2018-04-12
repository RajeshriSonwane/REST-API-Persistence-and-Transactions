package edu.sjsu.cmpe275.lab2.entity;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import edu.sjsu.cmpe275.lab2.util.View;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

@XmlRootElement
@Entity
@Table(name = "passenger")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Passenger {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @JsonView({View.PassengerView.class, View.ReservationView.class, View.FlightView.class})
    private String id;

    @Column(name= "firstname")
    @JsonView({View.PassengerView.class, View.ReservationView.class, View.FlightView.class})
    private String firstname;

    @Column(name= "lastname")
    @JsonView({View.PassengerView.class, View.ReservationView.class, View.FlightView.class})
    private String lastname;

    @Column(name= "age")
    @JsonView({View.PassengerView.class, View.ReservationView.class, View.FlightView.class})
    private int age;

    @Column(name= "gender")
    @JsonView({View.PassengerView.class, View.ReservationView.class, View.FlightView.class})
    private String gender;

    @Column(name = "phone", nullable = false, unique = true)
    @JsonView({View.PassengerView.class, View.ReservationView.class, View.FlightView.class})
    private String phone;

    // reservation made by the passenger should also be deleted.
    @OneToMany(mappedBy = "passenger", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView(View.PassengerView.class)
    private List<Reservation> reservations;

    public Passenger(){

    }
    public Passenger(String firstname, String lastname, int age, String gender, String phone) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
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

    //@XmlTransient
    public List<Reservation> getReservations() {        return reservations;    }

    //@XmlTransient
    public void setReservations(List<Reservation> reservations) {        this.reservations = reservations;    }
}

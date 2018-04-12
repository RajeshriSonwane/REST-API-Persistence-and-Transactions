package edu.sjsu.cmpe275.lab2.entity;


import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PassengerInformation {
    private String id;

    private String firstname;

    private String lastname;

    private int age;

    private String gender;

    private String phone;

    public PassengerInformation() {
    }

    public PassengerInformation(Passenger passenger){
        this.id = passenger.getId();
        this.firstname = passenger.getFirstname();
        this.lastname = passenger.getLastname();
        this.age = passenger.getAge();
        this.gender = passenger.getGender();
        this.phone = passenger.getPhone();
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
}

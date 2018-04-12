package edu.sjsu.cmpe275.lab2.entity;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import edu.sjsu.cmpe275.lab2.util.View;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonTypeName("BadRequest")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT ,use = JsonTypeInfo.Id.NAME)
public class BadRequest {

    @JsonView({View.PassengerView.class, View.ReservationView.class, View.FlightView.class})
    private int code;

    @JsonView({View.PassengerView.class, View.ReservationView.class, View.FlightView.class})
    private String msg;

    public BadRequest(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public BadRequest() {

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /*private Response BadRequest;

        public BadRequest() {
        }

        public BadRequest(Response BadRequest) {
            this.BadRequest = BadRequest;
        }

        public Response getResponse() {
            return BadRequest;
        }

        public void setResponse(Response BadRequest) {
            this.BadRequest = BadRequest;
        }*/


}
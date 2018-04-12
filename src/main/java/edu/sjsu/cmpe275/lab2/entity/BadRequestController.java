package edu.sjsu.cmpe275.lab2.entity;

public class BadRequestController {
    private BadRequest BadRequestMsg;

    public BadRequestController(edu.sjsu.cmpe275.lab2.entity.BadRequest BadRequest) {
        BadRequestMsg = BadRequest;
    }

    public edu.sjsu.cmpe275.lab2.entity.BadRequest getBadRequest() {
        return BadRequestMsg;
    }

    public void setBadRequest(edu.sjsu.cmpe275.lab2.entity.BadRequest BadRequest) {
        BadRequestMsg = BadRequest;
    }
}

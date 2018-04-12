package edu.sjsu.cmpe275.lab2.entity;

public class BadRequest {

    private int code;
    private String msg;

    public BadRequest(int code, String msg) {
        this.code = code;
        this.msg = msg;
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
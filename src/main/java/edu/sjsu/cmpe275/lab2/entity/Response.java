package edu.sjsu.cmpe275.lab2.entity;

public class Response {
    private int code1;
    private String msg;

    public Response(int code1, String msg) {
        this.code1 = code1;
        this.msg = msg;
    }

    public int getCode() {
        return code1;
    }

    public void setCode(int code) {
        this.code1 = code;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}

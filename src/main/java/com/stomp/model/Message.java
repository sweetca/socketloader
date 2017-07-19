package com.stomp.model;

public class Message {
    private String status;
    private String message;
    private String data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Message(String status, String message, String data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public String toJson(){
        return null;
    }

    @Override
    public String toString(){
        return "{'status':'" + this.status + "','message':'" + this.message + "','data':" + this.data + "}";
    }
}

package com.stomp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponseDto implements Serializable{
    private String status;
    private String message;
    private ResponseMap data = new ResponseMap();

    public LoginResponseDto() {

    }

    public void setData(ResponseMap data) {
        this.data = data;
    }

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

    public ResponseMap getData() {
        return data;
    }




}

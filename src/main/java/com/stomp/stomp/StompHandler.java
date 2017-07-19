package com.stomp.stomp;

import com.stomp.model.User;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import java.lang.reflect.Type;

public class StompHandler implements StompSessionHandler {
    private static final Logger LOGGER = LogManager.getLogger(StompHandler.class);
    private User user;

    public StompHandler(User user){
        this.user = user;
    }

    @Override
    public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
        LOGGER.info("USER CONNECTED " + this.user.getLogin());
        this.user.setConnected(true);
    }

    @Override
    public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {
        LOGGER.error("USER "+this.user.getLogin()+" ERROR handleException "+throwable.getMessage());
        this.user.setConnected(false);
    }

    @Override
    public void handleTransportError(StompSession stompSession, Throwable throwable) {
        LOGGER.error("USER "+this.user.getLogin()+" ERROR handleTransportError "+throwable.getMessage());
        this.user.setConnected(false);
    }

    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
        return String.class;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object o) {
        LOGGER.error("Frame received " + o);
    }
}

package com.stomp.stomp;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

public class FrameHandler implements StompFrameHandler {
    private static Logger logger = LogManager.getLogger(FrameHandler.class);
    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
        return String.class;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object o) {
        logger.debug(o);
    }
}

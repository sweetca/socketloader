package com.stomp.stomp;

import org.apache.log4j.LogManager;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.apache.log4j.Logger;

public class SocketHandler implements WebSocketHandler {
    private static Logger logger = LogManager.getLogger(SocketHandler.class);
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        logger.debug("Connected");
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        logger.info("Handle message");
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        logger.error(throwable.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        logger.debug("Closed connection with status " + closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}

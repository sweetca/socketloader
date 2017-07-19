package com.stomp.model;

import com.stomp.Constants;
import com.stomp.dto.CourierStep;
import com.stomp.dto.CourierStepList;
import com.stomp.dto.LoginResponseDto;
import com.stomp.dto.ResponseMap;
import com.stomp.stomp.SocketHandler;
import com.stomp.stomp.StompHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.DefaultStompSession;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class User {
    private static final Logger LOGGER = LogManager.getLogger(User.class);
    private String login;
    private String password;
    private String jSessionId;
    private String csrf;
    private String rememberme;
    private long companyId;
    private DefaultStompSession stompSession;
    private boolean connected = false;
    private double lon = 30.527112;
    private double lat = 50.440753;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }
    public User(String login, String password, double lat, double lon) {
        this.login = login;
        this.password = password;
        this.lat = lat;
        this.lon = lon;
    }
    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getjSessionId() {
        return jSessionId;
    }
    public void setjSessionId(String jSessionId) {
        this.jSessionId = jSessionId;
    }
    public String getCsrf() {
        return csrf;
    }
    public void setCsrf(String csrf) {
        this.csrf = csrf;
    }
    public String getRememberme() {
        return rememberme;
    }
    public void setRememberme(String rememberme) {
        this.rememberme = rememberme;
    }
    public long getCompanyId() {
        return companyId;
    }
    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }
    public DefaultStompSession getStompSession() {
        return stompSession;
    }
    public void setStompSession(DefaultStompSession stompSession) {
        stompSession.setMessageConverter(new MappingJackson2MessageConverter());
        this.stompSession = stompSession;
    }
    public boolean isConnected() {
        return connected;
    }
    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    public double getLat() {
        this.lat = lat + 1 / 10000.0;
        return lat;
    }
    public double getLon() {
        this.lon = lon +  1 / 10000.0;
        return lon;
    }

    public boolean doLogin() throws Exception{
        boolean success = true;
        try (CloseableHttpClient client = HttpClients.createDefault()){
            HttpGet get = new HttpGet(Constants.HTTP_PROTOCOL + Constants.HOST + "/mobile/login?l=" + this.login + "&p=" + this.password);
            CloseableHttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                String entityString = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
                this.setCredentials(entityString);
                success = this.connectToStomp();
            } else {
                success = false;
            }
        } catch (Exception e){
            e.printStackTrace();
            success = false;
        }

        if (!success){
            throw new LoginException("Login error user : " + this.login);
        }
        return success;
    }
    public void doLogout() {
        if (this.connected){
            try {
                stompSession.disconnect();
            } catch (Exception e){
                e.printStackTrace();
            }

            try {
                boolean success = this.logout();
                if (success){
                    LOGGER.info("user logout = " + this.login);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private boolean logout() throws Exception{
        boolean success = true;
        try (CloseableHttpClient client = HttpClients.createDefault()){
            HttpGet get = new HttpGet(Constants.HTTP_PROTOCOL + Constants.HOST + "/mobile/logout");
            List<String> cookieList = new LinkedList<>();
            String cookieString = "JSESSIONID=" + this.getjSessionId() +
                    "; remember-me=" + this.getRememberme();
            cookieList.add(cookieString);
            get.addHeader("cookie", cookieString);
            CloseableHttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
                success = true;
            } else {
                success = false;
            }
        } catch (Exception e){
            e.printStackTrace();
            success = false;
        }

        if (!success){
            throw new LoginException("Login error user : " + this.login);
        }
        return success;
    }
    public boolean sendCoordinates(int jobFlag) throws Exception {
        if (this.connected){
            CourierStep courierStep = new CourierStep();
            courierStep.setLatitude(this.getLat());
            courierStep.setLongitude(this.getLon());
            CourierStepList list = new CourierStepList();
            list.add(courierStep);
            stompSession.send("/port/message/courier/position." + this.companyId + "." + jobFlag, list);
            return true;
        } else {
            throw new LoginException("USER WAS DISCONNECTED");
        }
    }
    public boolean sendCoordinates(int jobFlag,double lat,double lon) throws Exception {
        if (this.connected){
            CourierStep courierStep = new CourierStep();
            courierStep.setLatitude(lat);
            courierStep.setLongitude(lon);
            CourierStepList list = new CourierStepList();
            list.add(courierStep);
            stompSession.send("/port/message/courier/position." + this.companyId + "." + jobFlag, list);
            return true;
        } else {
            throw new LoginException("USER WAS DISCONNECTED");
        }
    }


    private void setCredentials(String data) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        LoginResponseDto dto = objectMapper.readValue(data, LoginResponseDto.class);
        if (dto.getStatus().contains("ERROR")) {
            throw new LoginException("Login error");
        }
        ResponseMap responseMap = dto.getData();
        this.companyId = Long.parseLong(responseMap.get(Constants.COMPANY_COOKIE));
        this.csrf = responseMap.get(Constants.CSRF_COOKIE);
        this.rememberme = responseMap.get(Constants.REMEMBER_ME_COOKIE);
        this.jSessionId = responseMap.get(Constants.JSESSION_ID_COOKIE);
    }
    private boolean connectToStomp() throws Exception {
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(webSocketClient));

        String stompUrl = Constants.WEBSOCKET_PROTOCOL + Constants.HOST + Constants.WEBSOCKET_ENDPOINT;
        SockJsClient sockJsClient = new SockJsClient(transports);
        sockJsClient.doHandshake(new SocketHandler(), stompUrl);

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.afterPropertiesSet();

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new StringMessageConverter());
        stompClient.setTaskScheduler(taskScheduler);
        stompClient.setDefaultHeartbeat(new long[]{0, 0});

        WebSocketHttpHeaders httpHeaders = new WebSocketHttpHeaders();
        StompHeaders stompHeaders = new StompHeaders();

        List<String> cookieList = new LinkedList<>();
        String cookieString = "JSESSIONID=" + this.getjSessionId() +
                ";remember-me=" + this.getRememberme();
        cookieList.add(cookieString);

        httpHeaders.put("cookie", cookieList);
        stompHeaders.put("cookie", cookieList);

        StompSessionHandler handler = new StompHandler(this);

        RememberMeAuthenticationToken rememberMeToken = new RememberMeAuthenticationToken("user", this, null);
        UsernamePasswordAuthenticationToken usernameToken = new UsernamePasswordAuthenticationToken(this, login);

        stompClient.setTaskScheduler(taskScheduler);
        ListenableFuture<StompSession> future = stompClient.connect(stompUrl, httpHeaders, stompHeaders,
                handler, rememberMeToken, usernameToken);

        DefaultStompSession stompSession = (DefaultStompSession)future.get();
        this.setStompSession(stompSession);
        return true;
    }

    @Override
    public String toString() {
        return "Login=" + this.login + " password=" + this.password + " company=" + this.companyId;
    }
}

package com.stomp.loadtest;

import com.stomp.model.User;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.ArrayList;

@RunWith(JUnit4.class)
public class AllUsersTest {
    private static final Logger LOGGER = LogManager.getLogger(AllUsersTest.class);
    
    public static final String USER_LOGIN_PREFIX = "courierTest";
    public static final int COMPANY_PREFIX = 1;
    public static final int USER_START_PREFIX = 1;
    public static final int USER_END_PREFIX = 1000;
    public static final String USER_PASSWORD = "00000000";
    
    private ArrayList<User> successSessions;
    private HttpClient client;
    
    @Before
    public void init() throws Exception {
        successSessions = new ArrayList<>();
        client = new HttpClient();
        client.setMaxConnectionsPerDestination(2000);
        client.setExecutor(new QueuedThreadPool(2000));
        client.start();
    }

    @Test
    public void allUsers() throws Exception {
        double lon = 30.527112;
        double lat = 50.440753 + (USER_START_PREFIX * 0.001);

        for (int i = USER_START_PREFIX; i < USER_END_PREFIX; i++) {
            lon = lon + 1/1000.0;

            User user = new User(USER_LOGIN_PREFIX + COMPANY_PREFIX + i, USER_PASSWORD, lat, lon);

            boolean success = user.doLogin();
            Assert.assertTrue(success);

            successSessions.add(user);
            LOGGER.warn("user login = " + user);
        }

        int jobFlag = 1;
        int steps = 5;

        for (int i= 0; i <= steps ; i++){
            if (i == 1){
                jobFlag = 2;
            } else if (i == steps){
                jobFlag = 3;
            }


            for (User user : successSessions){
                Assert.assertTrue(user.sendCoordinates(jobFlag));
            }

            if (i == steps){
                for (User u: successSessions){
                    u.doLogout();
                    LOGGER.warn("user logout = " + u.getLogin());
                }
            }
        }
    }

    @After
    public void finish(){
        client.destroy();
    }
}
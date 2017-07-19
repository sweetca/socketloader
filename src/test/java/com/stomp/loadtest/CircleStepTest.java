package com.stomp.loadtest;

import com.stomp.model.User;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Alex Skorokhod alexey.skorohod@gmail.com
 */
@RunWith(JUnit4.class)
public class CircleStepTest {
    public static final String USER_LOGIN_PREFIX = "courierTest";
    public static final int COMPANY_PREFIX = 1;
    public static final String USER_PASSWORD = "00000000";

    private static double[] LONGS = {30.569064,30.568914,30.569332,30.570319,30.569525,30.567358,30.567460,30.568565,30.568951};
    private static double[] LATS =  {50.421544,50.422132,50.422795,50.423731,50.424544,50.424421,50.423171,50.422245,50.421221};

    private HttpClient client;

    @Before
    public void init() throws Exception {
        client = new HttpClient();
        client.setMaxConnectionsPerDestination(2000);
        client.setExecutor(new QueuedThreadPool(2000));
        client.start();
    }

    @Ignore
    @Test
    public void circleData() throws Exception {
        User user = new User(USER_LOGIN_PREFIX + COMPANY_PREFIX + "1", USER_PASSWORD, LATS[0], LONGS[0]);
        boolean success = user.doLogin();
        Assert.assertTrue(success);
        for (int i=0; i < LATS.length ; i++){
            if (i == 0){
                user.sendCoordinates(1,LATS[i],LONGS[i]);
            } else if (i > 0 && i < LATS.length - 1){
                user.sendCoordinates(2,LATS[i],LONGS[i]);
            } else if (i == LATS.length - 1){
                user.sendCoordinates(3,LATS[i],LONGS[i]);
            }
            Thread.sleep(500);
        }
        user.doLogout();
    }
    @After
    public void finish(){
        client.destroy();
    }
}

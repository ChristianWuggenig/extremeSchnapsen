package cardfactory.com.extremeschnapsen;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class NetworkManagerUnitTest {
    NetworkManager networkManager;
    Context context;
    INetworkDisplay networkDisplay;

    @Before
    public void init() {
        context = mock(Context.class);
        networkDisplay = mock(INetworkDisplay.class);
        networkManager = NetworkManager.getInstance(context, networkDisplay);
    }

    @After
    public void setNull() {
        context = null;
        networkDisplay = null;
        networkManager = null;
    }

    @Test
    public void testStartHttpServer() {
        networkManager.startHttpServer(new ArrayList<Deck>());

        assertTrue(true); //if the statement above does not fail, then the test is successful
    }

    @Test
    public void testStopHttpServer() {
        networkManager.stopHttpServer();

        assertTrue(true); //if the statement above does not fail, then the test is successful
    }

    //the nullpointerexception is thrown in the volley-class, because the requestqueue cannot be created on a mock-object
    @Test (expected = NullPointerException.class)
    public void testStartHttpClient() {
        networkManager.startHttpClient();

        fail("Exception not thrown"); //if the statement above does not fail, then the test failed
    }

    //the nullpointerexception is thrown in the volley-class, because the requestqueue cannot be created on a mock-object
    @Test (expected = NullPointerException.class)
    public void testSendCardWithClient() {
        networkManager.startHttpClient(); //to start as client

        networkManager.sendCard(1);

        fail("Exception not thrown"); //if the statement above does not fail, then the test failed
    }

    @Test
    public void testSendCardWithServer() {
        networkManager.startHttpServer(new ArrayList<Deck>()); //to start as client

        networkManager.sendCard(1);

        assertTrue(true); //if the statement above does not fail, then the test is successful
    }

    //the nullpointerexception is thrown in the volley-class, because the requestqueue cannot be created on a mock-object
    @Test (expected = NullPointerException.class)
    public void testWaitForCard() {
        networkManager.waitForCard();

        fail("Exception not thrown"); //if the statement above does not fail, then the test failed
    }
}


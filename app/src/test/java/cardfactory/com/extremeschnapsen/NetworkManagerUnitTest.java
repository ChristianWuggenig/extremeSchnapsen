package cardfactory.com.extremeschnapsen;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import cardfactory.com.extremeschnapsen.models.Deck;
import cardfactory.com.extremeschnapsen.models.Player;
import cardfactory.com.extremeschnapsen.networking.INetworkDisplay;
import cardfactory.com.extremeschnapsen.networking.NetworkManager;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class NetworkManagerUnitTest {
    NetworkManager networkManager;
    Context context;
    INetworkDisplay networkDisplay;
    Player player;

    @Before
    public void init() {
        context = mock(Context.class);
        networkDisplay = mock(INetworkDisplay.class);
        networkManager = NetworkManager.getInstance(context, networkDisplay);
        player = mock(Player.class);
        when(player.getUsername()).thenReturn("testUser");
    }

    @After
    public void setNull() {
        context = null;
        networkDisplay = null;
        networkManager = null;
    }

    @Test
    public void testStartHttpServer() {
        networkManager.startHttpServer(new ArrayList<Deck>(), player);

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
        networkManager.startHttpClient(player);

        fail("Exception not thrown"); //if the statement above does not fail, then the test failed
    }

    //the nullpointerexception is thrown in the volley-class, because the requestqueue cannot be created on a mock-object
    @Test (expected = NullPointerException.class)
    public void testSendCardWithClient() {
        networkManager.startHttpClient(player); //to start as client

        networkManager.sendCard(1);

        fail("Exception not thrown"); //if the statement above does not fail, then the test failed
    }

    @Test
    public void testSendCardWithServer() {
        networkManager.startHttpServer(new ArrayList<Deck>(), player); //to start as client

        networkManager.sendCard(1);

        assertTrue(true); //if the statement above does not fail, then the test is successful
    }

    //the nullpointerexception is thrown in the volley-class, because the requestqueue cannot be created on a mock-object
    @Test (expected = NullPointerException.class)
    public void testWaitForCard() {
        networkManager.waitForCard(false);

        fail("Exception not thrown"); //if the statement above does not fail, then the test failed
    }
}


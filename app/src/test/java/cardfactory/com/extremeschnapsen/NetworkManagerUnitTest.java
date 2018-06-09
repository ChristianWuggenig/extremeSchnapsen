package cardfactory.com.extremeschnapsen;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import cardfactory.com.extremeschnapsen.gui.GameActivity;
import cardfactory.com.extremeschnapsen.gui.StartGameActivity;
import cardfactory.com.extremeschnapsen.models.Deck;
import cardfactory.com.extremeschnapsen.models.Player;
import cardfactory.com.extremeschnapsen.networking.INetworkDisplay;
import cardfactory.com.extremeschnapsen.networking.IStartGame;
import cardfactory.com.extremeschnapsen.networking.NetworkManager;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class NetworkManagerUnitTest {
    NetworkManager networkManager;
    Context context;
    INetworkDisplay networkDisplay;
    Player player;
    String mode;

    @Before
    public void init() {
        context = mock(StartGameActivity.class);
        networkDisplay = mock(INetworkDisplay.class);
        networkManager = NetworkManager.getInstance(context);
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
    public void testStartHttpServerWithDeckPlayerNetworkDisplay() {
        networkManager.startHttpServer(new ArrayList<Deck>(), player, networkDisplay);

        assertTrue(true); //if the statement above does not fail, then the test is successful
    }

    @Test
    public void testStartHttpServerWithMode() {
        networkManager.startHttpServer(mode);

        assertTrue(true); //if the statement above does not fail, then the test is successful
    }

    @Test
    public void testStopHttpServer() {
        networkManager.stopHttpServer();

        assertTrue(true); //if the statement above does not fail, then the test is successful
    }

    //the nullpointerexception is thrown in the volley-class, because the requestqueue cannot be created on a mock-object
    @Test (expected = NullPointerException.class)
    public void testStartHttpClientWithNetworkDisplayPlayer() {
        networkManager.startHttpClient(networkDisplay, player);

        fail("Exception not thrown"); //if the statement above does not fail, then the test failed
    }

    //the nullpointerexception is thrown in the volley-class, because the requestqueue cannot be created on a mock-object
    @Test (expected = NullPointerException.class)
    public void testStartHttpClientWithMode() {
        networkManager.startHttpClient(mode);

        fail("Exception not thrown"); //if the statement above does not fail, then the test failed
    }

    //the nullpointerexception is thrown in the volley-class, because the requestqueue cannot be created on a mock-object
    @Test (expected = NullPointerException.class)
    public void testSendCardWithClient() {
        networkManager.startHttpClient(networkDisplay, player); //to start as client

        networkManager.sendCard(1);

        fail("Exception not thrown"); //if the statement above does not fail, then the test failed
    }

    @Test
    public void testSendCardWithServer() {
        networkManager.startHttpServer(mode); //has to be done first in order to initialize the http-server-object
        networkManager.startHttpServer(new ArrayList<Deck>(), player, networkDisplay); //to start as client

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


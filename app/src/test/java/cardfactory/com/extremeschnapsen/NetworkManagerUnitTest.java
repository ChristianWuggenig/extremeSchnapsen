package cardfactory.com.extremeschnapsen;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

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
        networkManager.setNull(); //necessary to create a "clean" environment for every test case
        networkManager = null;
    }

    @Test
    public void testStartHttpServerWithDeckPlayerNetworkDisplay() {
        networkManager.startHttpServer(mode); //necessary to create the http-server-object inside the networkmanager
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
        networkManager.startHttpServer(mode); //start first, before stopping it
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
        networkManager.startHttpClient(); //start the client first
        networkManager.sendCard(1);

        fail("Exception not thrown"); //if the statement above does not fail, then the test failed
    }

    @Test
    public void testSendCardWithServer() {
        networkManager.startHttpServer(mode); //has to be done first in order to initialize the http-server-object
        networkManager.sendCard(1);

        assertTrue(true); //if the statement above does not fail, then the test is successful
    }

    //the nullpointerexception is thrown in the volley-class, because the requestqueue cannot be created on a mock-object
    @Test (expected = NullPointerException.class)
    public void testWaitForCard() {
        networkManager.startHttpClient(); //start the client first
        networkManager.waitForCard(false);

        fail("Exception not thrown"); //if the statement above does not fail, then the test failed
    }

    @Test
    public void testSendTrumpExchangedWithServer() {
        networkManager.startHttpServer(mode);
        networkManager.sendTrumpExchanged();

        assertTrue(true); //if the statement above does not fail, then the test is successful
    }

    //the exception is thrown, because the httpclient-object is not initialized, which is not possible due to the missing context for volley
    @Test (expected = NullPointerException.class)
    public void testSendTrumpExchangedWithClient() {
        networkManager.startHttpClient(); //start the client first
        networkManager.sendTrumpExchanged();

        fail("Exception not thrown"); //if the statement above does not fail, then the test failed
    }

    @Test
    public void testSendTurnWithServer() {
        networkManager.startHttpServer(mode);
        networkManager.sendTurn();

        assertTrue(true); //if the statement above does not fail, then the test is successful
    }

    //the exception is thrown, because the httpclient-object is not initialized, which is not possible due to the missing context for volley
    @Test (expected = NullPointerException.class)
    public void testSendTurnWithClient() {
        networkManager.startHttpClient(); //start the client first
        networkManager.sendTurn();

        fail("Exception not thrown"); //if the statement above does not fail, then the test failed
    }

    @Test
    public void testSend2040WithServer() {
        networkManager.startHttpServer(mode);
        networkManager.send2040("pik");

        assertTrue(true); //if the statement above does not fail, then the test is successful
    }

    //the exception is thrown, because the httpclient-object is not initialized, which is not possible due to the missing context for volley
    @Test (expected = NullPointerException.class)
    public void testSend2040WithClient() {
        networkManager.startHttpClient(); //start the client first
        networkManager.send2040("pik");

        fail("Exception not thrown"); //if the statement above does not fail, then the test failed
    }

    @Test
    public void testSendSightJokerWithServer() {
        networkManager.startHttpServer(mode);
        networkManager.sendSightJoker();

        assertTrue(true); //if the statement above does not fail, then the test is successful
    }

    //the exception is thrown, because the httpclient-object is not initialized, which is not possible due to the missing context for volley
    @Test (expected = NullPointerException.class)
    public void testSendSightJokerWithClient() {
        networkManager.startHttpClient(); //start the client first
        networkManager.sendSightJoker();

        fail("Exception not thrown"); //if the statement above does not fail, then the test failed
    }

    @Test
    public void testSendParrySightJokerWithServer() {
        networkManager.startHttpServer(mode);
        networkManager.sendParrySightJoker();

        assertTrue(true); //if the statement above does not fail, then the test is successful
    }

    //the exception is thrown, because the httpclient-object is not initialized, which is not possible due to the missing context for volley
    @Test (expected = NullPointerException.class)
    public void testSendParrySightJokerWithClient() {
        networkManager.startHttpClient(); //start the client first
        networkManager.sendParrySightJoker();

        fail("Exception not thrown"); //if the statement above does not fail, then the test failed
    }
}


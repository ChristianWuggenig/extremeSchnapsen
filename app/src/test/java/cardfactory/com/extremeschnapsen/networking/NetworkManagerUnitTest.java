package cardfactory.com.extremeschnapsen.networking;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import cardfactory.com.extremeschnapsen.gui.GameActivity;
import cardfactory.com.extremeschnapsen.gui.MessageHelper;
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
    HTTPClient httpClient;
    HTTPServer httpServer;

    @Before
    public void init() {
        context = mock(StartGameActivity.class);
        networkDisplay = mock(INetworkDisplay.class);
        networkManager = NetworkManager.getInstance(context);
        player = mock(Player.class);
        when(player.getUsername()).thenReturn("testUser");

        httpClient = mock(HTTPClient.class);
        httpServer = mock(HTTPServer.class);

        networkManager.setHttpClient(httpClient);
        networkManager.setHttpServer(httpServer);
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
        List<Deck> currentDeck = new ArrayList<>();
        currentDeck.add(mock(Deck.class));

        networkManager.startHttpServer(currentDeck, player, networkDisplay);

        verify(httpServer).setCurrentDeck(currentDeck);
        verify(httpServer).setPlayer(player);
        verify(httpServer).setNetworkDisplay(networkDisplay);
    }

    @Test
    public void testStartHttpServerWithMode() {
        networkManager.startHttpServer(mode);

        assertTrue(true); //if the statement above does not fail, then the test is successful
    }

    @Test
    public void testStopHttpServer() {
        networkManager.stopHttpServer();

        verify(httpServer).stopHTTPServer();
    }

    @Test
    public void testStartHttpClientWithNetworkDisplayPlayer() {
        networkManager.startHttpClient(networkDisplay, player);

        verify(httpClient).setNetworkDisplay(networkDisplay);
        verify(httpClient).setPlayer(player);
        verify(httpClient).setAlreadyReceived(false);
        verify(httpClient).getDeck();
    }

    //the nullpointerexception is thrown in the volley-class, because the requestqueue cannot be created on a mock-object
    @Test (expected = NullPointerException.class)
    public void testStartHttpClientWithMode() {
        networkManager.startHttpClient(mode);

        fail("Exception not thrown"); //if the statement above does not fail, then the test failed
    }

    @Test
    public void testSendCardWithClient() {
        networkManager.sendCard(1);

        verify(httpClient).sendAction(NetworkHelper.ID, String.valueOf(1));
    }

    @Test
    public void testSendCardWithServer() {
        networkManager.startHttpServer(null, null, null); //start the server first
        networkManager.sendCard(1);

        verify(httpServer).setCardPlayed(1);
    }

    @Test
    public void testWaitForCard() {
        networkManager.waitForCard(false);

        verify(httpClient).setAlreadyReceived(false);
        verify(httpClient).getServerInformation();
    }

    @Test
    public void testSendTrumpExchangedWithServer() {
        networkManager.startHttpServer(null, null, null);
        networkManager.sendTrumpExchanged();

        verify(httpServer).setTrumpExchanged(true);

    }

    @Test
    public void testSendTrumpExchangedWithClient() {
        networkManager.sendTrumpExchanged();

        verify(httpClient).sendAction(NetworkHelper.TRUMP, "true");
    }

    @Test
    public void testSendTurnWithServer() {
        networkManager.startHttpServer(null, null, null);
        networkManager.sendTurn();

        verify(httpServer).setTurn(true);
    }

    @Test
    public void testSendTurnWithClient() {
        networkManager.sendTurn();

        verify(httpClient).sendAction(NetworkHelper.TURN, "true");
    }

    @Test
    public void testSend2040WithServer() {
        networkManager.startHttpServer(null, null, null);
        networkManager.send2040("pik");

        verify(httpServer).setTwentyForty("pik");
    }

    @Test
    public void testSend2040WithClient() {
        networkManager.send2040("pik");

        verify(httpClient).sendAction(NetworkHelper.TWENTYFORTY, "pik");
    }

    @Test
    public void testSendSightJokerWithServer() {
        networkManager.startHttpServer(null, null, null);
        networkManager.sendSightJoker();

        verify(httpServer).setSightJoker(true);
    }

    @Test
    public void testSendSightJokerWithClient() {
        networkManager.sendSightJoker();

        verify(httpClient).sendAction(NetworkHelper.SIGHTJOKER, "true");
    }

    @Test
    public void testSendParrySightJokerWithServer() {
        networkManager.startHttpServer(null, null, null);
        networkManager.sendParrySightJoker();

        verify(httpServer).setParrySightJoker(true);
    }

    @Test
    public void testSendParrySightJokerWithClient() {
        networkManager.sendParrySightJoker();

        verify(httpClient).sendAction(NetworkHelper.PARRYSIGHTJOKER, "true");
    }

    @Test
    public void testSendCardExchangeWithServer() {
        networkManager.startHttpServer(null, null, null);
        networkManager.sendCardExchange(1, 2);

        verify(httpServer).setCardExchange("1;2");
    }

    @Test
    public void testSendCardExchangeWithClient() {
        networkManager.sendCardExchange(1, 2);

        verify(httpClient).sendAction(NetworkHelper.CARD_EXCHANGE, "1;2");
    }
}


package cardfactory.com.extremeschnapsen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cardfactory.com.extremeschnapsen.models.Deck;
import cardfactory.com.extremeschnapsen.models.Player;
import cardfactory.com.extremeschnapsen.networking.HTTPServer;
import cardfactory.com.extremeschnapsen.networking.INetworkDisplay;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class HTTPServerUnitTest {
    HTTPServer httpServer;

    INetworkDisplay networkDisplay;
    List<Deck> currentDeck;
    Player player;

    @Before
    public void init() {
        networkDisplay = mock(INetworkDisplay.class);
        currentDeck = new ArrayList<>();

        for (int count = 1; count <= 20; count++) {
            Deck deck = mock(Deck.class);
            when(deck.getCardID()).thenReturn((long)count);
            currentDeck.add(deck);
        }

        player = mock(Player.class);
        when(player.getUsername()).thenReturn("testUser");

        httpServer = new HTTPServer(currentDeck, networkDisplay, player);
    }

    @After
    public void setNull() {
        networkDisplay = null;
        currentDeck = null;
        httpServer = null;
    }

    @Test
    public void testStartServer() {
        httpServer.startServer();

        //if the statement above does not throw an exception, then the test is successful
        assertTrue(true);
    }

    @Test
    public void testGetSetCardPlayed() {
        int playedCard = 2;
        httpServer.setCardPlayed(playedCard);
        assertEquals(2, httpServer.getCardPlayed());
    }

    @Test
    public void testStopHTTPServer() {
        httpServer.startServer(); //needs to be started first
        httpServer.stopHTTPServer();

        //if the statement above does not throw an exception, then the test is successful
        assertTrue(true);
    }

    @Test
    public void testSendAllDeck() {
        Map<String, List<String>> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("testName");
        map.put("Name", list);
        String response = httpServer.sendAllDeck(map);

        try {
            JSONObject jsonObject = new JSONArray(response).getJSONObject(0);
            assertEquals(1, jsonObject.getInt("ID"));
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testSendCurrentlyPlayedCard() {
        httpServer.setCardPlayed(1);
        String response = httpServer.sendCurrentlyPlayedCard();

        try {
            JSONObject jsonObject = new JSONObject(response);
            assertEquals(1, jsonObject.getInt("ID"));
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testGetCurrentlyPlayedCard() {
        Map<String, String> params = new HashMap<>();
        params.put("postData", "{\"ID\": 1}");

        String response = httpServer.getCurrentlyPlayedCard(params);

        try {
            JSONObject jsonObject = new JSONObject(response);
            assertEquals(1, jsonObject.getInt("ID"));
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testGetCurrentlyPlayedCardWithJsonException() {
        Map<String, String> params = new HashMap<>();
        params.put("postData", "dummyData");

        String response = httpServer.getCurrentlyPlayedCard(params);

        assertEquals(null, response);
    }
}

package cardfactory.com.extremeschnapsen;

import android.content.Context;

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

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class HTTPServerUnitTest {
    HTTPServer httpServer;

    INetworkDisplay networkDisplay;
    List<Deck> currentDeck;

    @Before
    public void init() {
        networkDisplay = mock(INetworkDisplay.class);
        currentDeck = new ArrayList<>();

        for (int count = 1; count <= 20; count++) {
            Deck deck = mock(Deck.class);
            when(deck.getCardID()).thenReturn((long)count);
            currentDeck.add(deck);
        }

        httpServer = new HTTPServer(currentDeck, networkDisplay);
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
        httpServer.stopHTTPServer();

        //if the statement above does not throw an exception, then the test is successful
        assertTrue(true);
    }

    @Test
    public void testSendAllDeck() {
        String response = httpServer.sendAllDeck();

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

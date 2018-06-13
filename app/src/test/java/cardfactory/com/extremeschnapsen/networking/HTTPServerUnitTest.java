package cardfactory.com.extremeschnapsen.networking;

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

import cardfactory.com.extremeschnapsen.gui.MessageHelper;
import cardfactory.com.extremeschnapsen.models.Deck;
import cardfactory.com.extremeschnapsen.models.Player;
import cardfactory.com.extremeschnapsen.networking.HTTPServer;
import cardfactory.com.extremeschnapsen.networking.INetworkDisplay;
import cardfactory.com.extremeschnapsen.networking.IStartGame;
import cardfactory.com.extremeschnapsen.networking.NetworkHelper;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class HTTPServerUnitTest {
    HTTPServer httpServer;

    INetworkDisplay networkDisplay;
    IStartGame startGame;
    List<Deck> currentDeck;
    Player player;
    String mode;

    @Before
    public void init() {
        networkDisplay = mock(INetworkDisplay.class);
        startGame = mock(IStartGame.class);
        currentDeck = new ArrayList<>();

        for (int count = 1; count <= 20; count++) {
            Deck deck = mock(Deck.class);
            when(deck.getCardID()).thenReturn((long)count);
            currentDeck.add(deck);
        }

        player = mock(Player.class);
        when(player.getUsername()).thenReturn("testUser");

        httpServer = new HTTPServer(startGame, mode);
    }

    @After
    public void setNull() {
        networkDisplay = null;
        startGame = null;
        mode = null;
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
        httpServer.setCurrentDeck(currentDeck);
        httpServer.setPlayer(player);
        httpServer.setNetworkDisplay(networkDisplay);
        Map<String, List<String>> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("testName");
        map.put(NetworkHelper.NAME, list);
        String response = httpServer.sendAllDeck(map);

        try {
            JSONObject jsonObject = new JSONArray(response).getJSONObject(0);
            assertEquals(1, jsonObject.getInt(NetworkHelper.ID));
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testSendCurrentlyPlayedCard() {
        httpServer.setCardPlayed(1);
        JSONObject response = httpServer.sendCurrentlyPlayedCard();

        try {
            assertEquals(1, response.getInt(NetworkHelper.ID));
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }

    /*@Test
    public void testGetCurrentlyPlayedCard() {
        httpServer.setNetworkDisplay(networkDisplay);
        Map<String, String> params = new HashMap<>();
        params.put("postData", "{\"" + NetworkHelper.ID + "\": 1}");

        String response = httpServer.getCurrentlyPlayedCard(params);

        try {
            JSONObject jsonObject = new JSONObject(response);
            assertEquals(1, jsonObject.getInt(NetworkHelper.ID));
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
    }*/

    @Test
    public void testSendParrySightJokerWithParry() {
        httpServer.setParrySightJoker(true);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.PARRYSIGHTJOKER, "true");
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }

        try {
            assertEquals(jsonObject.get(NetworkHelper.PARRYSIGHTJOKER), httpServer.sendParrySightJoker().get(NetworkHelper.PARRYSIGHTJOKER));

        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testSendParrySightJokerWithoutParry() {
        httpServer.setParrySightJoker(false);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.PARRYSIGHTJOKER, "false");
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }

        try {
            assertEquals(jsonObject.get(NetworkHelper.PARRYSIGHTJOKER), httpServer.sendParrySightJoker().get(NetworkHelper.PARRYSIGHTJOKER));

        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testSendSightJokerWithSight() {
        httpServer.setSightJoker(true);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.SIGHTJOKER, "true");
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }

        try {
            assertEquals(jsonObject.get(NetworkHelper.SIGHTJOKER), httpServer.sendSightJoker().get(NetworkHelper.SIGHTJOKER));

        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testSendSightJokerWithoutSight() {
        httpServer.setSightJoker(false);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.SIGHTJOKER, "false");
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }

        try {
            assertEquals(jsonObject.get(NetworkHelper.SIGHTJOKER), httpServer.sendSightJoker().get(NetworkHelper.SIGHTJOKER));

        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testSend2040WithSuit() {
        httpServer.setTwentyForty("pik");

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.TWENTYFORTY, "pik");
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }

        try {
            assertEquals(jsonObject.get(NetworkHelper.TWENTYFORTY), httpServer.send2040().get(NetworkHelper.TWENTYFORTY));

        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testSend2040WithoutSuit() {
        httpServer.setTwentyForty("");

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.TWENTYFORTY, "");
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }

        try {
            assertEquals(jsonObject.get(NetworkHelper.TWENTYFORTY), httpServer.send2040().get(NetworkHelper.TWENTYFORTY));

        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testSendCardExchangeWithCards() {
        httpServer.setCardExchange("1;2");

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.CARD_EXCHANGE, "1;2");
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }

        try {
            assertEquals(jsonObject.get(NetworkHelper.CARD_EXCHANGE), httpServer.sendCardExchange().get(NetworkHelper.CARD_EXCHANGE));

        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testSendCardExchangeWithoutCards() {
        httpServer.setCardExchange("");

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.CARD_EXCHANGE, "");
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }

        try {
            assertEquals(jsonObject.get(NetworkHelper.CARD_EXCHANGE), httpServer.sendCardExchange().get(NetworkHelper.CARD_EXCHANGE));

        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testSendTurnWithTurn() {
        httpServer.setTurn(true);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.TURN, "true");
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }

        try {
            assertEquals(jsonObject.get(NetworkHelper.TURN), httpServer.sendTurn().get(NetworkHelper.TURN));

        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testSendTurnWithoutTurn() {
        httpServer.setTurn(false);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.TURN, "false");
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }

        try {
            assertEquals(jsonObject.get(NetworkHelper.TURN), httpServer.sendTurn().get(NetworkHelper.TURN));

        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testSendTrumpExchangedWithTrump() {
        httpServer.setTrumpExchanged(true);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.TRUMP, "true");
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }

        try {
            assertEquals(jsonObject.get(NetworkHelper.TRUMP), httpServer.sendTrumpExchanged().get(NetworkHelper.TRUMP));

        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testSendTrumpExchangedWithoutTrump() {
        httpServer.setTrumpExchanged(false);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.TRUMP, "false");
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }

        try {
            assertEquals(jsonObject.get(NetworkHelper.TRUMP), httpServer.sendTrumpExchanged().get(NetworkHelper.TRUMP));

        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testSendClientModeWithBothNormal() {
        httpServer = new HTTPServer(startGame, NetworkHelper.MODE_NORMAL); //initialize server again with mode normal
        Map<String, List<String>> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add(NetworkHelper.MODE_NORMAL);
        map.put(NetworkHelper.MODE, list);

        try {
            JSONObject jsonObject = new JSONObject(httpServer.sendClientMode(map));
            assertEquals(NetworkHelper.MODE_NORMAL, jsonObject.get(NetworkHelper.MODE));
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testSendClientModeWithBothExtreme() {
        httpServer = new HTTPServer(startGame, NetworkHelper.MODE_EXTREME); //initialize server again with mode normal
        Map<String, List<String>> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add(NetworkHelper.MODE_EXTREME);
        map.put(NetworkHelper.MODE, list);

        try {
            JSONObject jsonObject = new JSONObject(httpServer.sendClientMode(map));
            assertEquals(NetworkHelper.MODE_EXTREME, jsonObject.get(NetworkHelper.MODE));
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testSendClientModeWithServerNormalClientExtreme() {
        httpServer = new HTTPServer(startGame, NetworkHelper.MODE_NORMAL); //initialize server again with mode normal
        Map<String, List<String>> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add(NetworkHelper.MODE_EXTREME);
        map.put(NetworkHelper.MODE, list);

        try {
            JSONObject jsonObject = new JSONObject(httpServer.sendClientMode(map));
            assertEquals(NetworkHelper.MODE_EXTREME, jsonObject.get(NetworkHelper.MODE));
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testSendClientModeWithServerExtremeClientNormal() {
        httpServer = new HTTPServer(startGame, NetworkHelper.MODE_EXTREME); //initialize server again with mode normal
        Map<String, List<String>> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add(NetworkHelper.MODE_NORMAL);
        map.put(NetworkHelper.MODE, list);

        try {
            JSONObject jsonObject = new JSONObject(httpServer.sendClientMode(map));
            assertEquals(NetworkHelper.MODE_EXTREME, jsonObject.get(NetworkHelper.MODE));
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testGetClientInformationWithID() {
        httpServer.setNetworkDisplay(networkDisplay);
        Map<String, String> params = new HashMap<>();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.ID, 1);
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }

        params.put("postData", jsonObject.toString());

        assertEquals(jsonObject.toString(), httpServer.getClientInformation(params));
    }

    @Test
    public void testGetClientInformationWithTrump() {
        httpServer.setNetworkDisplay(networkDisplay);
        Map<String, String> params = new HashMap<>();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.TRUMP, true);
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }

        params.put("postData", jsonObject.toString());

        assertEquals(jsonObject.toString(), httpServer.getClientInformation(params));
    }

    @Test
    public void testGetClientInformationWithTurn() {
        httpServer.setNetworkDisplay(networkDisplay);
        Map<String, String> params = new HashMap<>();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.TURN, true);
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }

        params.put("postData", jsonObject.toString());

        assertEquals(jsonObject.toString(), httpServer.getClientInformation(params));
    }

    @Test
    public void testGetClientInformationWithTwentyForty() {
        httpServer.setNetworkDisplay(networkDisplay);
        Map<String, String> params = new HashMap<>();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.TWENTYFORTY, "pik");
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }

        params.put("postData", jsonObject.toString());

        assertEquals(jsonObject.toString(), httpServer.getClientInformation(params));
    }

    @Test
    public void testGetClientInformationWithSightJoker() {
        httpServer.setNetworkDisplay(networkDisplay);
        Map<String, String> params = new HashMap<>();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.SIGHTJOKER, true);
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }

        params.put("postData", jsonObject.toString());

        assertEquals(jsonObject.toString(), httpServer.getClientInformation(params));
    }

    @Test
    public void testGetClientInformationWithParrySightJoker() {
        httpServer.setNetworkDisplay(networkDisplay);
        Map<String, String> params = new HashMap<>();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.PARRYSIGHTJOKER, true);
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }

        params.put("postData", jsonObject.toString());

        assertEquals(jsonObject.toString(), httpServer.getClientInformation(params));
    }

    @Test
    public void testSendClientInformation() {
        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(NetworkHelper.ID, 1);
            jsonArray.put(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put(NetworkHelper.TRUMP, "true");
            jsonArray.put(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put(NetworkHelper.TURN, "true");
            jsonArray.put(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put(NetworkHelper.TWENTYFORTY, "pik");
            jsonArray.put(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put(NetworkHelper.SIGHTJOKER, "true");
            jsonArray.put(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put(NetworkHelper.PARRYSIGHTJOKER, "true");
            jsonArray.put(jsonObject);

            httpServer.setNetworkDisplay(networkDisplay);
            httpServer.setCardPlayed(1);
            httpServer.setTrumpExchanged(true);
            httpServer.setTurn(true);
            httpServer.setTwentyForty("pik");
            httpServer.setSightJoker(true);
            httpServer.setParrySightJoker(true);

            JSONArray jsonArrayResponse = new JSONArray(httpServer.sendClientInformation());

            assertEquals(jsonArray.getJSONObject(0).get(NetworkHelper.ID), jsonArrayResponse.getJSONObject(0).get(NetworkHelper.ID));
            assertEquals(jsonArray.getJSONObject(1).get(NetworkHelper.TRUMP), jsonArrayResponse.getJSONObject(1).get(NetworkHelper.TRUMP));
            assertEquals(jsonArray.getJSONObject(2).get(NetworkHelper.TURN), jsonArrayResponse.getJSONObject(2).get(NetworkHelper.TURN));
            assertEquals(jsonArray.getJSONObject(3).get(NetworkHelper.TWENTYFORTY), jsonArrayResponse.getJSONObject(3).get(NetworkHelper.TWENTYFORTY));
            assertEquals(jsonArray.getJSONObject(4).get(NetworkHelper.SIGHTJOKER), jsonArrayResponse.getJSONObject(4).get(NetworkHelper.SIGHTJOKER));
            assertEquals(jsonArray.getJSONObject(5).get(NetworkHelper.PARRYSIGHTJOKER), jsonArrayResponse.getJSONObject(5).get(NetworkHelper.PARRYSIGHTJOKER));
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }
    }


}

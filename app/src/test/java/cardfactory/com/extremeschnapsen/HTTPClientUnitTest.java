package cardfactory.com.extremeschnapsen;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import cardfactory.com.extremeschnapsen.gui.StartGameActivity;
import cardfactory.com.extremeschnapsen.models.Player;
import cardfactory.com.extremeschnapsen.networking.HTTPClient;
import cardfactory.com.extremeschnapsen.networking.INetworkDisplay;
import cardfactory.com.extremeschnapsen.networking.NetworkHelper;
import cardfactory.com.extremeschnapsen.networking.NetworkManager;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class HTTPClientUnitTest {
    HTTPClient httpClient;
    Player player;
    INetworkDisplay networkDisplay;

    @Before
    public void init() {
        httpClient = new HTTPClient();
        player = mock(Player.class);
        when(player.getUsername()).thenReturn("testUser");
        networkDisplay = mock(INetworkDisplay.class);

        httpClient.setNetworkDisplay(networkDisplay);
        httpClient.setPlayer(player);
    }

    @After
    public void setNull() {
        httpClient = null;
        player = null;
        networkDisplay = null;
    }

    @Test
    public void testReceiveShuffledDeck() {
        JSONArray jsonArray = new JSONArray();

        try {
            for (int count = 0; count < 20; count++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(NetworkHelper.ID, count);
                jsonArray.put(jsonObject);
            }
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }

        httpClient.receiveShuffledDeck(jsonArray);
    }

    @Test
    public void testProcessServerInformation() {
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
        } catch (JSONException ex) {
            fail(ex.getMessage());
        }

        httpClient.processServerInformation(jsonArray);
    }
}

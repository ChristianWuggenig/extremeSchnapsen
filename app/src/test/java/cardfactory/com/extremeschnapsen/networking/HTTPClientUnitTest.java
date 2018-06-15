package cardfactory.com.extremeschnapsen.networking;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import cardfactory.com.extremeschnapsen.gui.MessageHelper;
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

        verify(networkDisplay).displayUserInformation(MessageHelper.YOURTURN);
        verify(networkDisplay).dismissDialog();
    }

    @Test
    public void testProcessServerInformationWithCardID() {
        JSONArray jsonArray = createJsonArray(1);

        httpClient.processServerInformation(jsonArray);

        assertTrue(true); //if the statement above does not fail, the test is successful
    }

    @Test
    public void testProcessServerInformationWithoutCardID() {
        JSONArray jsonArray = createJsonArray(0);

        httpClient.processServerInformation(jsonArray);

        assertTrue(true); //if the statement above does not fail, the test is successful
    }

    private JSONArray createJsonArray(int cardID) {
        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(NetworkHelper.ID, cardID);
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

        return jsonArray;
    }
}

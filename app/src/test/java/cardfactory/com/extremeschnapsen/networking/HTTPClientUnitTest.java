package cardfactory.com.extremeschnapsen.networking;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

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
    RequestQueue requestQueue;
    IStartGame startGame;

    @Before
    public void init() {
        httpClient = new HTTPClient();
        player = mock(Player.class);
        when(player.getUsername()).thenReturn("testUser");
        networkDisplay = mock(INetworkDisplay.class);
        requestQueue = mock(RequestQueue.class);
        startGame = mock(IStartGame.class);

        httpClient.setNetworkDisplay(networkDisplay);
        httpClient.setPlayer(player);
        httpClient.setRequestQueue(requestQueue);
    }

    @After
    public void setNull() {
        httpClient = null;
        player = null;
        networkDisplay = null;
    }

    @Test
    public void testGetGameMode() {
        String queueTag = "extremeSchnapsen";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "http://192.168.49.1:8080/" + "?" + NetworkHelper.MODE + "=" + "normal", new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", error.getMessage());
            }
        });

        DefaultRetryPolicy defaultRetryPolicy = new DefaultRetryPolicy(20 * 100, 10, 1.0f);
        request.setRetryPolicy(defaultRetryPolicy);
        request.setTag(queueTag);
        requestQueue.add(request);

        httpClient.getGameMode("normal");

        verify(requestQueue).add(request);
    }

    @Test
    public void testGetDeck() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "http://192.168.49.1:8080/" + "?" + NetworkHelper.NAME + "=" + player.getUsername(), new JSONArray(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("HTTPError", error.getMessage());

            }
        });

        requestQueue.add(request);

        httpClient.getDeck();

        verify(requestQueue).add(request);
    }

    @Test
    public void testSendActionWithCard() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.ID, 1);
        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://192.168.49.1:8080/", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", error.getMessage());
            }
        });

        requestQueue.add(request);

        httpClient.sendAction(NetworkHelper.ID, String.valueOf(1));

        verify(requestQueue).add(request);
    }

    @Test
    public void testSendActionWithTrumpExchanged() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.TRUMP, "true");
        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://192.168.49.1:8080/", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", error.getMessage());
            }
        });

        requestQueue.add(request);

        httpClient.sendAction(NetworkHelper.TRUMP, "true");

        verify(requestQueue).add(request);
    }

    @Test
    public void testGetServerInformation() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "http://192.168.49.1:8080/" + "?" + NetworkHelper.ID + "=1", new JSONArray(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", error.getMessage());
            }
        });

        requestQueue.add(request);

        httpClient.setAlreadyReceived(false);
        httpClient.getServerInformation();

        verify(requestQueue).add(request);
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

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(NetworkHelper.NAME, "testUser");
            jsonArray.put(jsonObject);

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

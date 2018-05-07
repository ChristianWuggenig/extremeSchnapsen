package cardfactory.com.extremeschnapsen;

import android.app.DownloadManager;
import android.content.Context;
import android.util.Log;

import com.android.volley.*;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StartHTTPClient {

    private  INetworkDisplay networkDisplay;
    private RequestQueue requestQueue;

    private String oppositeIP;

    private static final String queueTag = "extremeSchnapsen";

    public StartHTTPClient(Context context, INetworkDisplay networkDisplay) {
        this.networkDisplay = networkDisplay;
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        oppositeIP = "http://192.168.49.1:8080/";
    }

    public void getDeck() {

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, oppositeIP, new JSONArray(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //List<Deck> currentDeck = new ArrayList<>();
                int[] shuffledDeckIDs = new int[20];

                try {
                    for (int count = 0; count < 20; count++) {
                        JSONObject jsonObject = response.getJSONObject(count);
                        shuffledDeckIDs[count] = jsonObject.getInt("ID");
                        /*for (int innerCount = 0; innerCount < wrongDeck.size(); innerCount++) {
                            if (jsonObject.getLong("ID") == wrongDeck.get(innerCount).getCardID()) {
                                currentDeck.add(wrongDeck.get(innerCount));
                            }
                        }*/
                    }
                } catch (Exception ex) {
                    Log.d("JSONError", ex.getMessage());
                }

                networkDisplay.displayShuffledDeck(shuffledDeckIDs);
                networkDisplay.displayStatus("Deck received, first card has id: " + String.valueOf(shuffledDeckIDs[0]));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("HTTPError", error.getMessage());
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 10, 1.0f));

        request.setTag(queueTag);
        requestQueue.add(request);
    }

    public void sendCard(final int cardID) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("ID", cardID);
        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, oppositeIP, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                networkDisplay.displayStatus("Card " + cardID + " successfully sent");
                StartHTTPClient.this.getPlayedCard();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", error.getMessage());
            }
        });

        requestQueue.add(request);
    }

    public void getPlayedCard() {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, oppositeIP + "?ID=1", new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if((int)response.get("ID") != 0) {
                        networkDisplay.displayStatus("Server played card " + response.get("ID"));
                        networkDisplay.setMyTurn(true, (int)response.getInt("ID"));
                    } else {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            Log.d("ThreadError", ex.getMessage());
                        }

                        getPlayedCard();
                    }

                } catch (JSONException ex) {
                    Log.d("JSONError", ex.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", error.getMessage());
            }
        });

        requestQueue.add(request);

    }
}

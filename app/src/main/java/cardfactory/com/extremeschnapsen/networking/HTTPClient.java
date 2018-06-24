package cardfactory.com.extremeschnapsen.networking;

import android.content.Context;
import android.util.Log;

import com.android.volley.*;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cardfactory.com.extremeschnapsen.gui.MessageHelper;
import cardfactory.com.extremeschnapsen.models.Player;

/**
 * This class represents the http-client and is responsible for client-server-exchange of game relevant data.
 * Actions are received via the network manager, responses are displayed on the network display interface object
 */
public class HTTPClient {

    private INetworkDisplay networkDisplay; //the network display interface object
    private IStartGame startGame; //the start game display interface object to show information in the startgame-activity

    private RequestQueue requestQueue; //the volley request queue object

    private static final String SERVER_IP = "http://192.168.49.1:8080/"; //is static, because WifiP2P always assigns this ip address;

    private static final String QUEUE_TAG = "extremeSchnapsen"; //contains the queueTag for volley

    private Player player; //contains the current player

    private boolean alreadyReceived; //true, if the client already received the played card from the server

    /**
     * the constructor
     * @param context contains the activity context
     */
    public HTTPClient(Context context) {
        this.startGame = (IStartGame)context;
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    /**
     * only used for testing, because a volley instance cannot be created on a mock object
     */
    public HTTPClient() {

    }

    /**
     * set, if the client already received the currently played card from server
     * @param alreadyReceived true, if received
     */
    public void setAlreadyReceived(boolean alreadyReceived) {
        this.alreadyReceived = alreadyReceived;
    }

    /**
     * set the current network display for displaying information on the gui
     * @param networkDisplay the current network display
     */
    public void setNetworkDisplay(INetworkDisplay networkDisplay) {
        this.networkDisplay = networkDisplay;
    }

    /**
     * set the current player (client)
     * @param player the current player
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * set the request queue (only for testing)
     * @param requestQueue the mock object
     */
    public void setRequestQueue(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    /**
     * get the game mode from the server and also send the own mode
     * @param mode the mode the client has set in the game settings
     */
    public void getGameMode(String mode) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, SERVER_IP + "?" + NetworkHelper.MODE + "=" + mode, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    startGame.setGameMode(response.getString(NetworkHelper.MODE));
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

        //set initial timeout and retry policy for volley
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 10, 1.0f));

        request.setTag(QUEUE_TAG);
        requestQueue.add(request);
    }

    /**
     * initiates a request to get the complete deck from the opposite player
     * does not return anything, because it updates the network display interface variable
     */
    public void getDeck() {

        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, SERVER_IP + "?" + NetworkHelper.NAME + "=" + player.getUsername(), new JSONArray(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                receiveShuffledDeck(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("HTTPError", error.getMessage());
                getDeck();
            }
        });

        requestQueue.add(request);
    }

    /**
     * send an action to the server, no matter if its turn, trump or anything else
     * @param name the name of the action sent (e.g. turn, trump)
     * @param value the value of the action sent (e.g. true, pik, kreuz)
     */
    public void sendAction(String name, String value) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(name, value);
        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SERVER_IP, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                alreadyReceived = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", error.getMessage());
            }
        });

        requestQueue.add(request);
    }

    /**
     * get information from server (played card, trump exchanged, turned (zugedreht), jokers, etc.)
     */
    public void getServerInformation() {

        if(!alreadyReceived) {
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, SERVER_IP + "?" + NetworkHelper.ID + "=1", new JSONArray(), new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    processServerInformation(response);
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

    /**
     * gathers the ids of the shuffled deck from the jsonarray and puts it into an integer array to pass it to the gui for further processing
     * @param response the json array with all json objects with the ids
     */
    public void receiveShuffledDeck(JSONArray response) {
        int[] shuffledDeckIDs = new int[20];
        String playerName = "";

        try {
            //converts the ID-String into an int-array
            for (int count = 0; count < 20; count++) {
                JSONObject jsonObject = response.getJSONObject(count);
                shuffledDeckIDs[count] = jsonObject.getInt(NetworkHelper.ID);
            }

            JSONObject jsonObject = response.getJSONObject(20);
            playerName = jsonObject.getString(NetworkHelper.NAME);

        } catch (Exception ex) {
            Log.d("JSONError", ex.getMessage());
        }

        networkDisplay.displayShuffledDeck(shuffledDeckIDs, playerName); //display the updated deck and the opposite players name
        networkDisplay.displayUserInformation(MessageHelper.YOURTURN);
        networkDisplay.dismissDialog();
    }

    /**
     * processes all information gathered from the server (trump, card played, etc)
     * @param response jsonarray with jsonobjects containing the desired information
     */
    public void processServerInformation(JSONArray response) {
        if (response.length() != 0) {
            try {
                for (int count = 0; count < response.length(); count++) {
                    JSONObject jsonObject = response.getJSONObject(count);
                    String key = jsonObject.keys().next();

                    if (!key.equals(NetworkHelper.ID))
                        networkDisplay.receiveAction(key, jsonObject.getString(key));
                    else {
                        if (jsonObject.getInt(NetworkHelper.ID) != 0) {
                            networkDisplay.receiveAction(key, jsonObject.getString(NetworkHelper.ID));
                            alreadyReceived = true;
                        } else {
                            try {
                                Thread.sleep(700);
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                                Log.d("ThreadError", ex.getMessage());
                            }

                            networkDisplay.waitForCard();
                            Log.d("Waiting", "waiting for card");

                        }
                    }
                }
            } catch (JSONException ex) {
                Log.d("JSONError", ex.getMessage());
            }
        }
    }
}

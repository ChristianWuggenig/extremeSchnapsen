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

public class HTTPClient {

    private INetworkDisplay networkDisplay; //the network display interface object
    private IStartGame startGame;

    private RequestQueue requestQueue; //the volley request queue object

    private static final String serverIP = "http://192.168.49.1:8080/"; //is static, because WifiP2P always assigns this ip address;

    private static final String queueTag = "extremeSchnapsen"; //contains the queueTag for volley

    private Player player; //contains the current player

    private boolean alreadyReceived; //true, if the client already received the played card from the server

    /*public HTTPClient(Context context, INetworkDisplay networkDisplay, Player player) {
        this.networkDisplay = networkDisplay;
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        this.player = player;
        alreadyReceived = false;
    }*/

    public HTTPClient(Context context) {
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        this.startGame = (IStartGame)context;
    }

    public void setAlreadyReceived(boolean alreadyReceived) {
        this.alreadyReceived = alreadyReceived;
    }

    public void setNetworkDisplay(INetworkDisplay networkDisplay) {
        this.networkDisplay = networkDisplay;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void getGameMode(String mode) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, serverIP + "?" + NetworkHelper.MODE + "=" + mode, new JSONObject(), new Response.Listener<JSONObject>() {
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

        request.setTag(queueTag);
        requestQueue.add(request);
    }

    /**
     * initiates a request to get the complete deck from the opposite player
     * does not return anything, because it updates the network display interface variable
     */
    public void getDeck() {

        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, serverIP + "?" + NetworkHelper.NAME + "=" + player.getUsername(), new JSONArray(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

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
     * sends a played card to the server with a given cardID
     * @param cardID contains the id of the played card
     */
    public void sendCard(final int cardID) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.ID, cardID);
        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, serverIP, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //networkDisplay.displayUserInformation("Card " + cardID + " successfully sent");
                alreadyReceived = false;
                //networkDisplay.waitForCard(); //start the listener for a played card
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", error.getMessage());
            }
        });

        requestQueue.add(request);
    }

    public void sendAction(String name, String value) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(name, value);
        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, serverIP, jsonObject, new Response.Listener<JSONObject>() {
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
     * get information from server (played card, trump exchanged, turned (zugedreht)
     */
    public void getServerInformation() {

        if(!alreadyReceived) {
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, serverIP + "?" + NetworkHelper.ID + "=1", new JSONArray(), new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        //check if the returned id is 0, if yes, try again in 500ms, if no, display the played card
                        JSONObject id = response.getJSONObject(0);
                        JSONObject trump = response.getJSONObject(1);
                        JSONObject turn = response.getJSONObject(2);
                        JSONObject twentyForty = response.getJSONObject(3);
                        JSONObject sightJoker = response.getJSONObject(4);
                        JSONObject parrySightJoker = response.getJSONObject(5);
                        JSONObject cardExchange = response.getJSONObject(6);

                        if (trump.getBoolean(NetworkHelper.TRUMP)) {
                            networkDisplay.receiveAction(NetworkHelper.TRUMP, "true");
                        }

                        if (turn.getBoolean(NetworkHelper.TURN)) {
                            networkDisplay.receiveAction(NetworkHelper.TURN, "true");
                        }

                        if (twentyForty.getString(NetworkHelper.TWENTYFORTY) != "") {
                            networkDisplay.receiveAction(NetworkHelper.TWENTYFORTY, twentyForty.getString(NetworkHelper.TWENTYFORTY));
                        }

                        if (sightJoker.getBoolean(NetworkHelper.SIGHTJOKER)) {
                            networkDisplay.receiveAction(NetworkHelper.SIGHTJOKER, "true");
                        }

                        if (parrySightJoker.getBoolean(NetworkHelper.PARRYSIGHTJOKER)) {
                            networkDisplay.receiveAction(NetworkHelper.PARRYSIGHTJOKER, "true");
                        }

                        if (cardExchange.getString(NetworkHelper.CARD_EXCHANGE) != "") {
                            networkDisplay.receiveAction(NetworkHelper.CARD_EXCHANGE, cardExchange.getString(NetworkHelper.CARD_EXCHANGE));
                        }

                        if(id.getInt(NetworkHelper.ID) != 0) {
                            networkDisplay.displayUserInformation(MessageHelper.YOURTURN);
                            networkDisplay.setMyTurn(id.getInt(NetworkHelper.ID));

                            alreadyReceived = true;
                        } else {
                            try {
                                Thread.sleep(700);
                            } catch (InterruptedException ex) {
                                Log.d("ThreadError", ex.getMessage());
                            }

                            networkDisplay.waitForCard();
                            Log.d("Waiting", "waiting for card");
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
}

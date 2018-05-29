package cardfactory.com.extremeschnapsen.networking;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cardfactory.com.extremeschnapsen.models.Deck;
import cardfactory.com.extremeschnapsen.models.Player;
import fi.iki.elonen.NanoHTTPD;

public class HTTPServer {
    private int cardPlayed; //contains the played card

    private List<Deck> currentDeck; //contains the current deck

    private INetworkDisplay networkDisplay; //contains the network display interface object

    private NanoHTTPD nanoHTTPD; //contains the nanohttpd-server

    private Player player; //contains the current player

    private boolean trumpExchanged; //true, if trump was exchanged, else false

    public HTTPServer(List<Deck> currentDeck, INetworkDisplay networkDisplay, Player player) {
        cardPlayed = 0;
        this.currentDeck = currentDeck;
        this.networkDisplay = networkDisplay;
        this.player = player;
        trumpExchanged = false;
    }

    /**
     * start the http-server and also implement the response-methods
     */
    public void startServer() {
        try {
            nanoHTTPD = new NanoHTTPD(8080) {
                @Override
                public Response serve(IHTTPSession session) {
                    String message = "";

                    //check if the request-method is GET
                    if (session.getMethod() == Method.GET) {
                        Map<String, List<String>> params = session.getParameters();

                        //check what the client wants to have
                        if (params.size() != 0) {
                            if (params.get("Name") != null) {
                                message = sendAllDeck(params);
                            } else if (params.get("ID") != null) {
                                message = sendClientInformation();
                            }
                        }
                    }
                    //check if the request-method is POST
                    else if(session.getMethod() == Method.POST) {
                        Map<String, String> params = new HashMap<>();
                        try {
                            session.parseBody(params); //get the parameters from the session
                        } catch (Exception ex) {
                            Log.d("HTTPError", ex.getMessage());
                        }

                        message = getClientInformation(params);
                    }

                    return newFixedLengthResponse(message); //return a string message with the appropriate response
                }
            };

            nanoHTTPD.start(); //start the http-server
        } catch (IOException ex) {
            Log.d("HTTPError", ex.getMessage());
        }
    }

    /**
     * set the currently played card
     * @param cardPlayed contains the cardID of the played card
     */
    public void setCardPlayed(int cardPlayed) {
        this.cardPlayed = cardPlayed;
    }

    /**
     * get the currently played Card
     * @return returns the played card
     */
    public int getCardPlayed() {
        return cardPlayed;
    }

    public void setTrumpExchanged(boolean trumpExchanged) {
        this.trumpExchanged = trumpExchanged;
    }

    /**
     * stop the http-server
     */
    public void stopHTTPServer() {
        nanoHTTPD.stop();
    }

    /**
     * send the shuffled deck ids to the client
     * @return returns the response string with the ids in json format
     */
    public String sendAllDeck(Map<String, List<String>> params) {
        JSONArray jsonArray = new JSONArray();

        //create json-objects to insert the cardIDs
        try {
            for (Deck deck : currentDeck) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("ID", deck.getCardID());
                jsonArray.put(jsonObject);
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Name", player.getUsername());
            jsonArray.put(jsonObject);

        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }

        //networkDisplay.displayStatus(currentDeck.get(0).toString());
        networkDisplay.displayStatus("waiting");
        networkDisplay.displayPlayer(params.get("Name").get(0));
        networkDisplay.dismissDialog();

        return jsonArray.toString(); //convert the jsonArray of cardIDs to a string message for the response
    }

    public String sendClientInformation() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(sendCurrentlyPlayedCard());
        jsonArray.put(sendTrumpExchanged());
        return jsonArray.toString();
    }

    /**
     * sends the currently played card to the client (waits for the client to ask for it)
     * @return returns the response with the id of the played card in json format
     */
    public JSONObject sendCurrentlyPlayedCard() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("ID", cardPlayed);
        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }

        cardPlayed = 0;

        return jsonObject; //convert the jsonArray of cardIDs to a string message for the response
    }

    public String getClientInformation(Map<String, String> params) {
        try {
            JSONObject jsonObject = new JSONObject(params.get("postData"));

            if (jsonObject.has("ID")) {
                int cardID = jsonObject.getInt("ID");
                networkDisplay.setMyTurn(Integer.parseInt(String.valueOf(cardID)));
            } else if (jsonObject.has("Trump")) {
                networkDisplay.exchangeTrump();
            }

            return jsonObject.toString(); //convert the jsonArray of cardIDs to a string message for the response
        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }
        return null;
    }

    /**
     * get the currently played card from the client
     * @param params used to get the parameters from the http-headers
     * @return returns a response with the same id (not needed, but a response message is necessary)
     */
    public String getCurrentlyPlayedCard(Map<String, String> params) {
        try {
            JSONObject jsonObject = new JSONObject(params.get("postData"));
            int cardID = jsonObject.getInt("ID");

            networkDisplay.setMyTurn(Integer.parseInt(String.valueOf(cardID)));

            return jsonObject.toString(); //convert the jsonArray of cardIDs to a string message for the response
        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }
        return null;
    }

    public JSONObject sendTrumpExchanged() {
        JSONObject jsonObject = new JSONObject();

        try {
            if (trumpExchanged) {
                jsonObject.put("Trump", "true");
                trumpExchanged = false;
            }
            else
                jsonObject.put("Trump", "false");
        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }

        return jsonObject; //convert the jsonArray of cardIDs to a string message for the response
    }
}

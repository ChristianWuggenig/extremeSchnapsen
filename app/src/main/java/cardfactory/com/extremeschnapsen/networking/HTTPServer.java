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

    public HTTPServer(List<Deck> currentDeck, INetworkDisplay networkDisplay, Player player) {
        this.cardPlayed = 0;
        this.currentDeck = currentDeck;
        this.networkDisplay = networkDisplay;
        this.player = player;
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

                        //if the parameter list is empty, then the client wants to get the current deck
                        if (params.size() != 0) {
                            if (params.get("Name") != null) {
                                message = sendAllDeck(params);
                            } else if (params.get("ID") != null) {
                                message = sendCurrentlyPlayedCard();
                            }
                        }
                        //if the parameter list is not 0, then the client wants to get the currently played card from the server
                        else {

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

                        message = getCurrentlyPlayedCard(params);
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

        networkDisplay.displayStatus(currentDeck.get(0).toString());
        networkDisplay.displayPlayer(params.get("Name").get(0));
        networkDisplay.dismissDialog();

        return jsonArray.toString(); //convert the jsonArray of cardIDs to a string message for the response
    }

    /**
     * sends the currently played card to the client (waits for the client to ask for it)
     * @return returns the response with the id of the played card in json format
     */
    public String sendCurrentlyPlayedCard() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("ID", cardPlayed);
        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }

        cardPlayed = 0;

        return jsonObject.toString(); //convert the jsonArray of cardIDs to a string message for the response
    }

    /**
     * get the currently played card from the client
     * @param params used to get the parameters from the http-headers
     * @return returns a response with the same id (not needed, but a response message is necessary)
     */
    public String getCurrentlyPlayedCard(Map<String, String> params) {
        try {
            JSONObject jsonObject = new JSONObject(params.get("postData"));
            /*String cardIDString = jsonObject.getString("ID"); //get the id from the played card

            networkDisplay.displayStatus("opposite player played card " + cardIDString);*/
            int cardID = jsonObject.getInt("ID");
            networkDisplay.displayStatus("opposite player played card " + String.valueOf(cardID));
            //networkDisplay.setMyTurn(Integer.parseInt(cardIDString));
            networkDisplay.setMyTurn(Integer.parseInt(String.valueOf(cardID)));

            return jsonObject.toString(); //convert the jsonArray of cardIDs to a string message for the response
        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }
        return null;
    }
}

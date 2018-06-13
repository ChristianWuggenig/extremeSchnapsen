package cardfactory.com.extremeschnapsen.networking;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cardfactory.com.extremeschnapsen.gui.MessageHelper;
import cardfactory.com.extremeschnapsen.models.Deck;
import cardfactory.com.extremeschnapsen.models.Player;
import fi.iki.elonen.NanoHTTPD;

public class HTTPServer {
    private int cardPlayed; //contains the played card

    private List<Deck> currentDeck; //contains the current deck

    private INetworkDisplay networkDisplay; //contains the network display interface object used for displaying information on the gui
    private IStartGame startGame; //contains the start game interface object used for displaying information on the gui

    private NanoHTTPD nanoHTTPD; //contains the nanohttpd-server

    private Player player; //contains the current player

    private boolean trumpExchanged; //true, if trump was exchanged, else false
    private boolean turn; //true, if the server turned (zudrehen)
    private String twentyForty; //true, if the server played 20 or 40
    private boolean sightJoker; //true, if the sight joker was used
    private boolean parrySightJoker; //true, if the parry sight joker was used
    private String cardExchange; //contains the two cards to be exchanged

    private String mode; //contains the game mode (extreme or normal)

    public HTTPServer(IStartGame startGame, String mode) {
        cardPlayed = 0;
        trumpExchanged = false;
        twentyForty = "";
        turn = false;
        this.mode = mode;
        this.startGame = startGame;
        sightJoker = false;
        parrySightJoker = false;
        cardExchange = "";
    }

    /**
     * set the current deck
     * @param currentDeck contains the current deck
     */
    public void setCurrentDeck(List<Deck> currentDeck) {
        this.currentDeck = currentDeck;
    }

    /**
     * set the network display
     * @param networkDisplay contains the current network display for showing information in the gui
     */
    public void setNetworkDisplay(INetworkDisplay networkDisplay) {
        this.networkDisplay = networkDisplay;
    }

    /**
     * set the current player
     * @param player contains the current player
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * set if the round was turned (zugedreht)
     * @param turn true, if turned
     */
    public void setTurn(boolean turn) {
        this.turn = turn;
    }

    /**
     * set if 20 or 40 was played
     * @param twentyForty contains the suit of the 20/40 played
     */
    public void setTwentyForty(String twentyForty) {
        this.twentyForty = twentyForty;
    }

    /**
     * set if the sight joker was used
     * @param sightJoker true, if used
     */
    public void setSightJoker(boolean sightJoker) {
        this.sightJoker = sightJoker;
    }

    /**
     * set if the parry sight joker was used
     * @param parrySightJoker true, if used
     */
    public void setParrySightJoker(boolean parrySightJoker) {
        this.parrySightJoker = parrySightJoker;
    }

    public void setCardExchange(String cardExchange) {
        this.cardExchange = cardExchange;
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
     * set if trump was exchanged
     * @param trumpExchanged true, if exchanged
     */
    public void setTrumpExchanged(boolean trumpExchanged) {
        this.trumpExchanged = trumpExchanged;
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
                            if (params.get(NetworkHelper.NAME) != null) {
                                message = sendAllDeck(params);
                            } else if (params.get(NetworkHelper.ID) != null) {
                                message = sendClientInformation();
                            } else if (params.get(NetworkHelper.MODE) != null) {
                                message = sendClientMode(params);
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
     * stop the http-server
     */
    public void stopHTTPServer() {
        nanoHTTPD.stop();
    }

    /**
     * send the shuffled deck ids to the client including the name of the player (server)
     * @return returns the response string with the ids in json format
     */
    public String sendAllDeck(Map<String, List<String>> params) {
        JSONArray jsonArray = new JSONArray();

        //create json-objects to insert the cardIDs
        try {
            for (Deck deck : currentDeck) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(NetworkHelper.ID, deck.getCardID());
                jsonArray.put(jsonObject);
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(NetworkHelper.NAME, player.getUsername());
            jsonArray.put(jsonObject);

        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }

        networkDisplay.displayUserInformation(MessageHelper.WAITING);
        networkDisplay.displayPlayer(params.get(NetworkHelper.NAME).get(0));
        networkDisplay.dismissDialog();

        return jsonArray.toString(); //convert the jsonArray of cardIDs to a string message for the response
    }

    /**
     * call the necessary methods to send information to the client
     * @return jsonarray converted to a string for the response
     */
    public String sendClientInformation() {
        JSONArray jsonArray = new JSONArray();

        jsonArray.put(sendCurrentlyPlayedCard());

        if (trumpExchanged)
            jsonArray.put(sendTrumpExchanged());

        if (turn)
            jsonArray.put(sendTurn());

        if (!twentyForty.equals(""))
            jsonArray.put(send2040());

        if (sightJoker)
            jsonArray.put(sendSightJoker());

        if (parrySightJoker)
            jsonArray.put(sendParrySightJoker());

        if (!cardExchange.equals(""))
            jsonArray.put(sendCardExchange());

        return jsonArray.toString();
    }

    /**
     * sends the game mode to the client (also decides, which mode is chosen in comparison with the own mode)
     * @param params the mode the client has chosen
     * @return the mode that will actually be used then in json format converted to string
     */
    public String sendClientMode(Map<String, List<String>> params) {
        String mode = params.get(NetworkHelper.MODE).get(0);

        try {
            JSONObject jsonObject = new JSONObject();

            if (mode.equals(NetworkHelper.MODE_EXTREME)) {
                jsonObject.put(NetworkHelper.MODE, NetworkHelper.MODE_EXTREME);
                startGame.setGameMode(NetworkHelper.MODE_EXTREME);
            } else {
                if (this.mode.equals(NetworkHelper.MODE_EXTREME)) {
                    jsonObject.put(NetworkHelper.MODE, NetworkHelper.MODE_EXTREME);
                    startGame.setGameMode(NetworkHelper.MODE_EXTREME);
                } else {
                    jsonObject.put(NetworkHelper.MODE, NetworkHelper.MODE_NORMAL);
                    startGame.setGameMode(NetworkHelper.MODE_NORMAL);
                }
            }

            return jsonObject.toString(); //convert the jsonArray of cardIDs to a string message for the response
        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }
        return null;
    }

    /**
     * sends the currently played card to the client (waits for the client to ask for it)
     * @return returns the response with the id of the played card in json format
     */
    public JSONObject sendCurrentlyPlayedCard() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NetworkHelper.ID, cardPlayed);
        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }

        cardPlayed = 0;

        return jsonObject; //convert the jsonArray of cardIDs to a string message for the response
    }

    /**
     * sends the information of trump exchanged or not to the client
     * @return returns a jsonobject with the required information
     */
    public JSONObject sendTrumpExchanged() {
        JSONObject jsonObject = new JSONObject();

        try {
            if (trumpExchanged) {
                jsonObject.put(NetworkHelper.TRUMP, "true");
                trumpExchanged = false;
            }
            else
                jsonObject.put(NetworkHelper.TRUMP, "false");
        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }

        return jsonObject;
    }

    /**
     * sends the information of turned (zugedreht) or not to the client
     * @return returns a jsonobject with the required information
     */
    public JSONObject sendTurn() {
        JSONObject jsonObject = new JSONObject();

        try {
            if (turn) {
                jsonObject.put(NetworkHelper.TURN, "true");
                turn = false;
            }
            else
                jsonObject.put(NetworkHelper.TURN, "false");
        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }

        return jsonObject;
    }

    /**
     * sends the information of 20 or 40 with the chosen suit to the client
     * @return returns a jsonobject with the required information
     */
    public JSONObject send2040() {
        JSONObject jsonObject = new JSONObject();

        try {
            if (twentyForty != "") {
                jsonObject.put(NetworkHelper.TWENTYFORTY, twentyForty);
                twentyForty = "";
            }
            else
                jsonObject.put(NetworkHelper.TWENTYFORTY, twentyForty);
        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }

        return jsonObject;
    }

    /**
     * sends the information of sight joker used or not to the client
     * @return returns a jsonobject with the required information
     */
    public JSONObject sendSightJoker() {
        JSONObject jsonObject = new JSONObject();

        try {
            if (sightJoker) {
                jsonObject.put(NetworkHelper.SIGHTJOKER, "true");
                sightJoker = false;
            }
            else
                jsonObject.put(NetworkHelper.SIGHTJOKER, "false");
        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }

        return jsonObject;
    }

    /**
     * sends the information of parry sight joker used or not to the client
     * @return returns a jsonobject with the required information
     */
    public JSONObject sendParrySightJoker() {
        JSONObject jsonObject = new JSONObject();

        try {
            if (parrySightJoker) {
                jsonObject.put(NetworkHelper.PARRYSIGHTJOKER, "true");
                parrySightJoker = false;
            }
            else
                jsonObject.put(NetworkHelper.PARRYSIGHTJOKER, "false");
        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }

        return jsonObject;
    }

    /**
     * sends the information of card exchange with a string containing the exchanged cards in format CardA;CardB
     * @return returns a jsonobject with the required information
     */
    public JSONObject sendCardExchange() {
        JSONObject jsonObject = new JSONObject();

        try {
            if (cardExchange != "") {
                jsonObject.put(NetworkHelper.CARD_EXCHANGE, cardExchange);
                cardExchange = "";
            }
            else
                jsonObject.put(NetworkHelper.CARD_EXCHANGE, cardExchange);
        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }

        return jsonObject;
    }

    /**
     * get all information from the client, if send (called when a post-request was detected)
     * @param params contains all params, depending on what the client sent
     * @return returns the same information as receives, in order to check at the client if the information was transmitted successfully
     */
    public String getClientInformation(Map<String, String> params) {
        try {
            JSONObject jsonObject = new JSONObject(params.get("postData"));

            String key = jsonObject.keys().next();
            networkDisplay.receiveAction(key, String.valueOf(jsonObject.get(key)));

            return jsonObject.toString(); //convert the jsonArray of cardIDs to a string message for the response
        } catch (JSONException ex) {
            Log.d("JSONError", ex.getMessage());
        }
        return null;
    }
}

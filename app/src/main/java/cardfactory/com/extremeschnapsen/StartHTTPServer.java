package cardfactory.com.extremeschnapsen;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class StartHTTPServer {
    int cardPlayed;
    List<Deck> currentDeck;
    INetworkDisplay networkDisplay;

    public StartHTTPServer(List<Deck> currentDeck, INetworkDisplay networkDisplay) {
        this.cardPlayed = 0;
        this.currentDeck = currentDeck;
        this.networkDisplay = networkDisplay;
    }

    public void startServer() {
        try {
            NanoHTTPD nanoHTTPD = new NanoHTTPD(8080) {
                @Override
                public Response serve(IHTTPSession session) {
                    String message = "";

                    if (session.getMethod() == Method.GET) {
                        Map<String, List<String>> params = session.getParameters();

                        if (params.size() == 0) {

                            JSONArray jsonArray = new JSONArray();

                            try {
                                for (Deck deck : currentDeck) {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("ID", deck.getCardID());
                                    jsonArray.put(jsonObject);
                                }
                            } catch (JSONException ex) {
                                Log.d("JSONError", ex.getMessage());
                            }

                            message = jsonArray.toString();

                            networkDisplay.displayStatus(currentDeck.get(0).toString());
                        } else {
                            JSONObject jsonObject = new JSONObject();

                            try {
                                jsonObject.put("ID", cardPlayed);
                            } catch (JSONException ex) {
                                Log.d("JSONError", ex.getMessage());
                            }

                            cardPlayed = 0;

                            message = jsonObject.toString();

                        }
                    } else if(session.getMethod() == Method.POST) {
                        try {
                            Map<String, String> params = new HashMap<>();
                            session.parseBody(params);
                            JSONObject jsonObject = new JSONObject(params.get("postData"));
                            String cardIDString = jsonObject.getString("ID");
                            networkDisplay.displayStatus("opposite player played card " + cardIDString);
                            networkDisplay.setMyTurn(true);
                            message = jsonObject.toString();
                        } catch (Exception ex) {
                            Log.d("HTTPError", ex.getMessage());
                        }

                    }

                    return newFixedLengthResponse(message);
                }
            };

            nanoHTTPD.start();
        } catch (IOException ex) {
            Log.d("HTTPError", ex.getMessage());
        }
    }

    public void setCardPlayed(int cardPlayed) {
        this.cardPlayed = cardPlayed;
    }
}

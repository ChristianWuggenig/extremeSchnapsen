package cardfactory.com.extremeschnapsen;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public abstract class StartHTTPServerAsyncTask extends AsyncTask {
    NanoHTTPD nanoHTTPD;
    @Override
    protected Object doInBackground(final Object[] objects) {

        Log.d("AsyncTask", "Starting AsyncTask");

        try {
            nanoHTTPD = new NanoHTTPD(8080) {
                @Override
                public Response serve(IHTTPSession session) {
                    String message = "";

                    if (session.getMethod() == Method.GET) {
                        Map<String, List<String>> params = session.getParameters();

                        if (params.size() == 0) {
                            List<Deck> currentDeck = (List<Deck>) objects[0];

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

                            updateDeck();
                        } else {
                            if (objects.length > 1) {
                                int cardPlayed = (int)objects[1];

                                while (cardPlayed == 0) {
                                    try {
                                        Thread.sleep(500);
                                        Log.d("Waiting", "Waiting for player to tap card");
                                    } catch (InterruptedException ex) {
                                        Log.d("ThreadException", ex.getMessage());
                                    }
                                }
                                JSONObject jsonObject = new JSONObject();

                                try {
                                    jsonObject.put("ID", cardPlayed);
                                } catch (JSONException ex) {
                                    Log.d("JSONError", ex.getMessage());
                                }

                                //cardPlayed = 0;

                                message = jsonObject.toString();
                            }
                        }
                    } else if(session.getMethod() == Method.POST) {
                        try {
                            Map<String, String> params = new HashMap<>();
                            session.parseBody(params);
                            JSONObject jsonObject = new JSONObject(params.get("postData"));
                            String cardIDString = jsonObject.getString("ID");
                            updateCardPlayed(Integer.parseInt(cardIDString));
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


        return null;
    }

    public abstract void updateCardPlayed(int cardID);

    public abstract void updateDeck();

    public void stopSelf() {
        nanoHTTPD.stop();
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        nanoHTTPD.stop();
        super.onCancelled();
    }
}

package cardfactory.com.extremeschnapsen;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class NetworkManager  {

    private static NetworkManager networkManager;

    private Socket socket;

    private INetworkDisplay networkDisplay;
    private Context context;

    private String serverIP;

    private boolean isServer;

    StartHTTPClient httpClient;
    StartHTTPServerAsyncTask asyncTask;
    HTTPServerService serverService;

    NanoHTTPD nanoHTTPD;

    private int cardPlayed;

    private NetworkManager (Context context, INetworkDisplay networkDisplay) {
        this.networkDisplay = networkDisplay;
        this.context = context;
        serverIP = "192.168.49.1";
        cardPlayed = 0;
    }

    public static NetworkManager getInstance(Context context, INetworkDisplay networkDisplay) {
        if (networkManager == null) {
            networkManager = new NetworkManager(context, networkDisplay);
        }
        return networkManager;
    }

    public void startServer(final List<Deck> currentDeck) {
        isServer = true;

        StartServerAsyncTask asyncTask = new StartServerAsyncTask() {
            @Override
            public void getClientSocket(Socket clientSocket) {
                socket = clientSocket;
                startServerReceiveDeck(currentDeck);
            }
        };

        asyncTask.execute();
    }

    private void startServerReceiveDeck(List<Deck> currentDeck) {
        ReceiveMessageServerAsyncTask asyncTaskReceive = new ReceiveMessageServerAsyncTask() {
            @Override
            public void updateDecks(List<Deck> currentDeckFromClient) {
                networkDisplay.displayStatus(currentDeckFromClient.get(0).toString());
            }

            @Override
            public void updateCardPlayed(int cardID) {

            }
        };

        asyncTaskReceive.execute(socket, currentDeck);
    }

    public void startClient(final List<Deck> currentDeck) {
        isServer = false;

        StartClientAsyncTask asyncTask = new StartClientAsyncTask() {
            @Override
            public void getServerSocket(Socket serverSocket) {
                socket = serverSocket;
                if(currentDeck != null)
                    startClientSendDeck(currentDeck);
                else
                    NetworkManager.this.waitForCard();
            }
        };

        asyncTask.execute(serverIP);
    }

    private void startClientSendDeck(final List<Deck> currentDeck) {
            SendMessageClientAsyncTask asyncTaskSend = new SendMessageClientAsyncTask() {
                @Override
                public void waitForCard() {
                    NetworkManager.this.startClient(null);
                    networkDisplay.displayStatus(currentDeck.get(0).toString());
                }
            };

            asyncTaskSend.execute(socket, currentDeck);

    }

    public void playCard(int cardID) {
        if (isServer) {
            SendMessageServerAsyncTask asyncTask = new SendMessageServerAsyncTask();
            asyncTask.execute(socket, String.valueOf(cardID));
        } else {
            SendMessageClientAsyncTask asyncTask = new SendMessageClientAsyncTask() {
                @Override
                public void waitForCard() {

                }
            };
            asyncTask.execute(socket, String.valueOf(cardID));
        }
    }

    public void waitForCard() {
        if (isServer) {
            ReceiveMessageServerAsyncTask asyncTask = new ReceiveMessageServerAsyncTask() {
                @Override
                public void updateDecks(List<Deck> currentDeckFromClient) {

                }

                @Override
                public void updateCardPlayed(int cardID) {
                    networkDisplay.displayStatus("card " + String.valueOf(cardID) + " played");
                    networkDisplay.setMyTurn(true);
                }
            };

            asyncTask.execute(socket);
        } else {
            ReceiveMessageClientAsyncTask asyncTask = new ReceiveMessageClientAsyncTask() {
                @Override
                public void updateCardPlayed(int cardID) {
                    networkDisplay.displayStatus("card " + String.valueOf(cardID) + " played");
                    networkDisplay.setMyTurn(true);
                }
            };

            asyncTask.execute(socket);
        }
    }

    public void startHttpServer(final List<Deck> currentDeck) {
        isServer = true;

        /*serverService = new HTTPServerService() {
            @Override
            public void updateDeck() {
                networkDisplay.displayStatus(currentDeck.get(0).toString());
            }

            @Override
            public void updateCardPlayed(int cardID) {
                networkDisplay.displayStatus("opposite player played card " + cardID);
                networkDisplay.setMyTurn(true);
            }
        };

        Intent i = new Intent(context.getApplicationContext(), HTTPServerService.class);
        context.startService(i);*/

        /*asyncTask = new StartHTTPServerAsyncTask() {
            @Override
            public void updateCardPlayed(int cardID) {
                networkDisplay.displayStatus("opposite player played card " + cardID);
                networkDisplay.setMyTurn(true);
            }

            @Override
            public void updateDeck() {
                networkDisplay.displayStatus(currentDeck.get(0).toString());
            }
        };
        asyncTask.execute(currentDeck);*/


        try {
            nanoHTTPD = new NanoHTTPD(8080) {
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

    public void startHttpClient(List<Deck> wrongDeck) {
        isServer = false;

        httpClient = new StartHTTPClient(context, networkDisplay);
        httpClient.getDeck(wrongDeck);
    }

    public void sendCard(int cardID) {
        if (isServer) {
            /*asyncTask.cancel(true);
            asyncTask = new StartHTTPServerAsyncTask() {
                @Override
                public void updateCardPlayed(int cardID) {
                    networkDisplay.displayStatus("opposite player played card " + cardID);
                    networkDisplay.setMyTurn(true);
                }

                @Override
                public void updateDeck() {

                }
            };

            asyncTask.execute(new ArrayList<>(), cardID);*/

            this.cardPlayed = cardID;
        }
        else
            httpClient.sendCard(cardID);
    }
}

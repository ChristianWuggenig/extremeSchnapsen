package cardfactory.com.extremeschnapsen.networking;

import android.content.Context;

import java.util.List;

import cardfactory.com.extremeschnapsen.models.Deck;
import cardfactory.com.extremeschnapsen.models.Player;

public class NetworkManager {

    private static NetworkManager networkManager; //create a static network manager

    private Context context; //contains the GameActivity-Context

    private boolean isServer; //shows if the current phone is the server or the client

    private HTTPClient httpClient; //used for the http-client
    private HTTPServer httpServer; //used for the http-server

    /**
     * the private constructor necessary for a singleton
     * @param context the activity context
     */
    private NetworkManager (Context context) {
        this.context = context;
    }

    /**
     * implements a singleton, which returns a networkmanager-object
     * @param context contains the GameActivity-Context
     * @return
     */
    public static NetworkManager getInstance(Context context) {
        if (networkManager == null) {
            networkManager = new NetworkManager(context);
        }
        return networkManager;
    }

    /**
     * used ONLY for the unit test in order to create a "clean" environment for every method test
     */
    public void setNull() {
        networkManager = null;
    }

    /**
     * set the http client object (only for unit tests)
     * @param httpClient the mock object
     */
    public void setHttpClient(HTTPClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * set the http server object (only for unit tests)
     * @param httpServer the mock object
     */
    public void setHttpServer(HTTPServer httpServer) {
        this.httpServer = httpServer;
    }

    /**
     * starts the http-server and holds the current deck for the client
     * @param currentDeck the current, shuffled deck
     * @param player the current player
     * @param networkDisplay the network display for showing information on the gui
     */
    public void startHttpServer(List<Deck> currentDeck, Player player, INetworkDisplay networkDisplay) {
        isServer = true;

        httpServer.setCurrentDeck(currentDeck);
        httpServer.setPlayer(player);
        httpServer.setNetworkDisplay(networkDisplay);
    }

    /**
     * starts the http-server and holds the current deck for the client
     */
    public void startHttpServer(String mode) {
        isServer = true;

        httpServer = new HTTPServer((IStartGame)context, mode);
        httpServer.startServer();
    }

    /**
     * stop the http-server
     */
    public void stopHttpServer() {
        httpServer.stopHTTPServer();
    }

    /**
     * start the http-client
     */
    public void startHttpClient(INetworkDisplay networkDisplay, Player player) {
        isServer = false;

        httpClient.setNetworkDisplay(networkDisplay);
        httpClient.setPlayer(player);
        httpClient.setAlreadyReceived(false);
        httpClient.getDeck(); //get the current deck from server
    }

    /**
     * start the http-client
     */
    public void startHttpClient(String mode) {
        isServer = false;

        httpClient = new HTTPClient(context);
        httpClient.getGameMode(mode);
    }

    /**
     * send a card to the opposite phone
     * @param cardID contains the id of the played card
     */
    public void sendCard(int cardID) {

        if (isServer) {
            httpServer.setCardPlayed(cardID);
        }
        else {
            httpClient.sendCard(cardID);
        }
    }

    /**
     * called when the client has to wait for a played card from the server
     */
    public void waitForCard(boolean alreadyReceived) {
        httpClient.setAlreadyReceived(alreadyReceived);
        httpClient.getServerInformation();
    }

    /**
     * called when the client or the server wants to send the information that the trump was exchanged
     */
    public void sendTrumpExchanged() {
        if (isServer) {
            httpServer.setTrumpExchanged(true);
        } else {
            httpClient.sendAction(NetworkHelper.TRUMP, "true");
        }
    }

    /**
     * called when the client or the server wants to send the information that the round was turned (zugedreht)
     */
    public void sendTurn() {
        if (isServer) {
            httpServer.setTurn(true);
        } else {
            httpClient.sendAction(NetworkHelper.TURN, "true");
        }
    }

    /**
     * called when the client or the server wants to send the information that 20 or 40 was played
     * @param suit the suit of the 20/40 played
     */
    public void send2040(String suit) {
        if (isServer) {
            httpServer.setTwentyForty(suit);
        } else {
            httpClient.sendAction(NetworkHelper.TWENTYFORTY, suit);
        }
    }

    /**
     * called when the client or the server wants to send the information that the sight joker was used
     */
    public void sendSightJoker() {
        if (isServer) {
            httpServer.setSightJoker(true);
        } else {
            httpClient.sendAction(NetworkHelper.SIGHTJOKER, "true");
        }
    }

    /**
     * called when the client or the server wants to send the information that the parry sight joker was used
     */
    public void sendParrySightJoker() {
        if (isServer) {
            httpServer.setParrySightJoker(true);
        } else {
            httpClient.sendAction(NetworkHelper.PARRYSIGHTJOKER, "true");
        }
    }

    /**
     * called when the client or the server wants to send the information that a card was exchanged
     */
    public void sendCardExchange(int cardA, int cardB) {
        String cardString = String.valueOf(cardA) + ";" + String.valueOf(cardB);

        if (isServer) {
            httpServer.setCardExchange(cardString);
        } else {
            httpClient.sendAction(NetworkHelper.CARD_EXCHANGE, cardString);
        }
    }
}

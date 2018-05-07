package cardfactory.com.extremeschnapsen;

import android.content.Context;
import java.util.List;

public class NetworkManager  {

    private static NetworkManager networkManager;

    private INetworkDisplay networkDisplay;
    private Context context;

    private boolean isServer;

    StartHTTPClient httpClient;
    StartHTTPServer httpServer;

    private NetworkManager (Context context, INetworkDisplay networkDisplay) {
        this.networkDisplay = networkDisplay;
        this.context = context;
    }

    public static NetworkManager getInstance(Context context, INetworkDisplay networkDisplay) {
        if (networkManager == null) {
            networkManager = new NetworkManager(context, networkDisplay);
        }
        return networkManager;
    }

    public void startHttpServer(List<Deck> currentDeck) {
        isServer = true;

        httpServer = new StartHTTPServer(currentDeck, networkDisplay);
        httpServer.startServer();
    }

    public void startHttpClient() {
        isServer = false;

        httpClient = new StartHTTPClient(context, networkDisplay);
        httpClient.getDeck();
    }

    public void sendCard(int cardID) {

        if (isServer) {
            httpServer.setCardPlayed(cardID);
        }
        else
            httpClient.sendCard(cardID);

    }
}

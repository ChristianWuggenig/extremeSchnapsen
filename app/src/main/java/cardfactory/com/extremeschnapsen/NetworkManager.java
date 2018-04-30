package cardfactory.com.extremeschnapsen;

import java.util.List;

public class NetworkManager  {

    private static NetworkManager networkManager;

    private StartServerAsyncTask startServerAsyncTask;
    private ClientAsyncTask clientAsyncTask;

    private INetworkDisplay networkDisplay;

    private String oppositeIP;

    private NetworkManager (INetworkDisplay networkDisplay) {
        this.networkDisplay = networkDisplay;
    }

    public static NetworkManager getInstance(INetworkDisplay networkDisplay) {
        if (networkManager == null) {
            networkManager = new NetworkManager(networkDisplay);
        }
        return networkManager;
    }

    public void startServer(List<Deck> currentDeck) {
        startServerAsyncTask = new StartServerAsyncTask() {
            @Override
            public void updateDecks(List<Deck> currentDeckFromClient) {
                currentDeck = currentDeckFromClient;

                networkDisplay.displayStatus(currentDeck.get(0).toString());

            }

            @Override
            public void updateCardPlayed(int cardID) {
                networkDisplay.displayStatus("card " + String.valueOf(cardID) + " played");
            }

            @Override
            public void updateIP(String ip) {
                oppositeIP = ip;
            }
        };

        startServerAsyncTask.execute(currentDeck);
    }

    public void startClient(List<Deck> currentDeck) {
        clientAsyncTask = new ClientAsyncTask() {
            @Override
            public void updateDecks(List<Deck> currentDeck) {
                networkDisplay.displayStatus(currentDeck.get(0).toString());
            }

            @Override
            public void updateCardPlayed(int cardID) {
                networkDisplay.displayStatus("card " + String.valueOf(cardID) + " played");
            }

            @Override
            public void startServer() {

            }
        };

        oppositeIP = "192.168.49.1";

        clientAsyncTask.execute(oppositeIP, currentDeck);
    }

    public void playCard(int cardID) {
        clientAsyncTask = new ClientAsyncTask() {
            @Override
            public void updateDecks(List<Deck> currentDeck) {
                networkDisplay.displayStatus(currentDeck.get(0).toString());
            }

            @Override
            public void updateCardPlayed(int cardID) {
                networkDisplay.displayStatus("card " + String.valueOf(cardID) + " played");
            }

            @Override
            public void startServer() {
                networkDisplay.changeClientToServer();
            }
        };

        clientAsyncTask.execute(oppositeIP, cardID);
    }

    public void waitForCard() {
        startServerAsyncTask = new StartServerAsyncTask() {
            @Override
            public void updateDecks(List<Deck> currentDeckFromClient) {
                currentDeck = currentDeckFromClient;

                networkDisplay.displayStatus(currentDeck.get(0).toString());

            }

            @Override
            public void updateCardPlayed(int cardID) {
                networkDisplay.displayStatus("card " + String.valueOf(cardID) + " played");
            }

            @Override
            public void updateIP(String ip) {

            }
        };

        startServerAsyncTask.execute();
    }
}

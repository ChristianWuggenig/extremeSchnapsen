package cardfactory.com.extremeschnapsen;

import java.util.List;

public class NetworkManager  {

    private static NetworkManager networkManager;

    private ServerAsyncTask serverAsyncTask;
    private ClientAsyncTask clientAsyncTask;

    private INetworkDisplay networkDisplay;

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
        serverAsyncTask = new ServerAsyncTask() {
            @Override
            public void updateDecks(List<Deck> currentDeckFromClient) {
                currentDeck = currentDeckFromClient;

                networkDisplay.displayStatus(currentDeck.get(0).toString());

            }

            @Override
            public void updateCardPlayed(int cardID) {
                networkDisplay.displayStatus("card " + String.valueOf(cardID) + " played");
            }
        };

        serverAsyncTask.execute(currentDeck);
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
        };

        clientAsyncTask.execute(currentDeck);
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
        };

        clientAsyncTask.execute(cardID);
    }

    public void waitForCard() {
        serverAsyncTask = new ServerAsyncTask() {
            @Override
            public void updateDecks(List<Deck> currentDeckFromClient) {
                currentDeck = currentDeckFromClient;

                networkDisplay.displayStatus(currentDeck.get(0).toString());

            }

            @Override
            public void updateCardPlayed(int cardID) {
                networkDisplay.displayStatus("card " + String.valueOf(cardID) + " played");
            }
        };

        serverAsyncTask.execute();
    }
}

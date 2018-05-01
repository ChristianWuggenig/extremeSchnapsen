package cardfactory.com.extremeschnapsen;

import java.net.Socket;
import java.util.List;

public class NetworkManager  {

    private static NetworkManager networkManager;

    private Socket socket;

    private INetworkDisplay networkDisplay;

    private String serverIP;

    private boolean isServer;
    private boolean deckExchanged;

    private NetworkManager (INetworkDisplay networkDisplay) {
        this.networkDisplay = networkDisplay;
        serverIP = "192.168.49.1";
        deckExchanged = false;
    }

    public static NetworkManager getInstance(INetworkDisplay networkDisplay) {
        if (networkManager == null) {
            networkManager = new NetworkManager(networkDisplay);
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
                deckExchanged = true;
            }
        };

        asyncTask.execute();
    }

    private void startServerReceiveDeck(List<Deck> currentDeck) {
        if (!deckExchanged) {
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
                deckExchanged = true;
            }
        };

        asyncTask.execute(serverIP);
    }

    private void startClientSendDeck(final List<Deck> currentDeck) {
        if(!deckExchanged) {
            SendMessageClientAsyncTask asyncTaskSend = new SendMessageClientAsyncTask() {
                @Override
                public void waitForCard() {
                    NetworkManager.this.startClient(null);
                    networkDisplay.displayStatus(currentDeck.get(0).toString());
                }
            };

            asyncTaskSend.execute(socket, currentDeck);
        }
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
}

package cardfactory.com.extremeschnapsen;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Round {

    private Context context;
    private List<Card> allCards;
    private List<Deck> currentDeck;
    private DeckDataSource deckDataSource;
    private CardDataSource cardDataSource;

    private NetworkManager networkManager;

    private boolean myTurn;

    public Round(Context context) {
        this.context = context;
        deckDataSource = new DeckDataSource(context);
        cardDataSource = new CardDataSource(context);
        cardDataSource.open();
        deckDataSource.open();
        allCards = new ArrayList<>();
        currentDeck = new ArrayList<>();

        networkManager = NetworkManager.getInstance((INetworkDisplay)context);
    }

    public List<Deck> initializeRound() {
        allCards = cardDataSource.getAllCards();
        currentDeck = deckDataSource.shuffelDeck(allCards);
        return currentDeck;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public void startServer() {
        networkManager.startServer(currentDeck);
    }

    public void startClient() {
        networkManager.startClient(currentDeck);
    }

    public boolean playCard(int cardID) {
        if (myTurn) {
            networkManager.playCard(cardID);
            myTurn = false;
            networkManager.waitForCard();
            return true;
        }
        return false;
    }

    public void waitForCard() {
        networkManager.waitForCard();
    }
}

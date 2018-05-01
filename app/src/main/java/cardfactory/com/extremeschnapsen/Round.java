package cardfactory.com.extremeschnapsen;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Round {

    private List<Card> allCards;
    private List<Deck> currentDeck;
    private DeckDataSource deckDataSource;
    private CardDataSource cardDataSource;

    private NetworkManager networkManager;

    private boolean myTurn;

    private int moves;

    public Round(Context context) {
        deckDataSource = new DeckDataSource(context);
        cardDataSource = new CardDataSource(context);
        cardDataSource.open();
        deckDataSource.open();
        allCards = new ArrayList<>();
        currentDeck = new ArrayList<>();

        moves = 1;

        networkManager = NetworkManager.getInstance(context, (INetworkDisplay)context);
    }

    //die Karten auf der Hand zurückbekommen
    public List<Deck> getCardsOnHand(boolean player1){
        List<Deck> deckonhands = null;

        if (player1) {
            for (Deck deck : this.currentDeck) {
                if (deck.getDeckStatus() == 1) {
                    deckonhands.add(deck);
                }
            }
        }
        else {
            for (Deck deck : this.currentDeck) {
                if (deck.getDeckStatus() == 2) {
                    deckonhands.add(deck);
                }
            }
        }
        return deckonhands;

    }

    //offene Karte aus dem Deck erhalten
    public Deck getOpenCard(){

        Deck opencard = null;
        for (Deck deck : this.currentDeck) {
            if (deck.getDeckStatus() == 3) {
                opencard = deck;
            }
        }
        return opencard;
    }

    public List<Deck> initializeRound() {
        allCards = cardDataSource.getAllCards();
        currentDeck = deckDataSource.shuffelDeck(allCards);
        return currentDeck;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public void increaseMoves() {
        moves++;
    }

    public void startServer() {
        networkManager.startHttpServer(currentDeck);
    }

    public void startClient() {
        networkManager.startHttpClient(currentDeck);
    }

    public boolean playCard(int cardID) {
        if (myTurn && moves <= 10) {
            networkManager.sendCard(cardID);
            myTurn = false;
            moves++;
            return true;
        }
        return false;
    }
}

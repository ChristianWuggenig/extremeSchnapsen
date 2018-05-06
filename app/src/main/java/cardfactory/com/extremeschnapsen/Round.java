package cardfactory.com.extremeschnapsen;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Round {

    private List<Card> allCards;
    private List<Deck> currentDeck;
    private List<RoundPoints> allRoundPoints;
    private DeckDataSource deckDataSource;
    private CardDataSource cardDataSource;
    private RoundPointsDataSource roundPointsDataSource;


    private NetworkManager networkManager;

    private boolean myTurn;
    private boolean isGroupOwner;

    private int moves;

    public Round(Context context) {
        deckDataSource = new DeckDataSource(context);
        cardDataSource = new CardDataSource(context);
        roundPointsDataSource = new RoundPointsDataSource(context);
        cardDataSource.open();
        deckDataSource.open();
        roundPointsDataSource.open();
        allCards = new ArrayList<>();
        currentDeck = new ArrayList<>();
        allRoundPoints = new ArrayList<>();

        moves = 1;

        deckDataSource.deleteDeckTable();

        networkManager = NetworkManager.getInstance(context, (INetworkDisplay) context);
    }

    //die Karten auf der Hand zurückbekommen
    public List<Deck> getCardsOnHand() {
        List<Deck> deckonhands = new ArrayList<>();

        if (isGroupOwner) {
            for (Deck deck : this.currentDeck) {
                if (deck.getDeckStatus() == 1) {
                    deckonhands.add(deck);
                }
            }
        } else {
            for (Deck deck : this.currentDeck) {
                if (deck.getDeckStatus() == 2) {
                    deckonhands.add(deck);
                }
            }
        }
        return deckonhands;

    }

    public List<Deck> getAllCards() {
        return deckDataSource.getAllDeck();
    }

    //offene Karte aus dem Deck erhalten
    public Deck getOpenCard() {
        Deck opencard = null;
        for (Deck deck : this.currentDeck) {
            if (deck.getDeckStatus() == 3) {
                opencard = deck;
            }
        }
        return opencard;
    }

    //playedCard Player 1
    public Deck getPlayedCardPlayer1() {
        Deck playedplayer1 = null;
        for (Deck deck : this.currentDeck) {
            if (deck.getDeckStatus() == 5) {
                playedplayer1 = deck;
            }
        }
        return playedplayer1;
    }

    //playedCard Player 2
    public Deck getPlayedCardPlayer2() {
        Deck playedplayer2 = null;
        for (Deck deck : this.currentDeck) {
            if (deck.getDeckStatus() == 6) {
                playedplayer2 = deck;
            }
        }
        return playedplayer2;
    }


    public List<Deck> getPlayedCards() {
        List<Deck> playedcards = new ArrayList<>();

        for (Deck deck : this.currentDeck) {
            if (deck.getDeckStatus() == 6 && isGroupOwner) {
                playedcards.add(deck);
            } else if (deck.getDeckStatus() == 5 && isGroupOwner) {
                playedcards.add(deck);
            }
            if (deck.getDeckStatus() == 5 && !isGroupOwner) {
                playedcards.add(deck);
            } else if (deck.getDeckStatus() == 6 && !isGroupOwner) {
                playedcards.add(deck);
            }
        }
        return playedcards;
    }

    public void initializeRound() {
        allCards = cardDataSource.getAllCards();
        currentDeck = deckDataSource.shuffelDeck(allCards);
        isGroupOwner = true;
    }

    public void getShuffledDeck(int[] shuffledDeckIDs) {
        allCards = cardDataSource.getAllCards();
        currentDeck = deckDataSource.receiveShuffeldDeck(shuffledDeckIDs, allCards);
        isGroupOwner = false;
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
        networkManager.startHttpClient();
    }

    public boolean playCard(int cardID) {
        if (myTurn && moves <= 10) {
            for (Deck deck : currentDeck) {
                if (deck.getCardID() == cardID) {
                    if (isGroupOwner) {
                        deck.setDeckStatus(5); //wie bekomme ich das in die GUI?
                        deckDataSource.updateDeckStatus(deck.getCardID(), 5);
                    } else {
                        deck.setDeckStatus(6);
                        deckDataSource.updateDeckStatus(deck.getCardID(), 6);
                    }
                }
            }

            networkManager.sendCard(cardID);
            myTurn = false;
            this.increaseMoves();

            return true;
        }

        return false;
    }

    public void updateCard(int cardID, int status) {
        deckDataSource.updateDeckStatus(cardID, status);

        for (Deck deck : currentDeck) {
            if (deck.getCardID() == cardID) {
                deck.setDeckStatus(status);
            }
        }
    }



    //TODO: für daniel: nach dem stich muss noch der status der karte upgedated werden (auf 7 oder 8)

    public void compareCards(Deck cardPlayer1, Deck cardPlayer2, Deck trump, RoundPoints pointsplayer1, RoundPoints pointsplayer2) {
        //both cards of round are set, continue to compare those

        //init roundpoints for each player, get old round points from db


        if (cardPlayer1 != null && cardPlayer2 != null && trump != null) {

            if (cardPlayer1.getCardSuit() == getOpenCard().getCardSuit() && cardPlayer2.getCardSuit() != getOpenCard().getCardSuit()) {
                pointsplayer1.updatePlayer1Points(cardPlayer1.getCardValue() + cardPlayer2.getCardValue());
            } else if (cardPlayer1.getCardSuit() != getOpenCard().getCardSuit() && cardPlayer2.getCardSuit() == getOpenCard().getCardSuit()) {
                pointsplayer2.updatePlayer2Points(cardPlayer1.getCardValue() + cardPlayer2.getCardValue());
            } else if (cardPlayer1.getCardSuit() == getOpenCard().getCardSuit() && cardPlayer2.getCardSuit() == getOpenCard().getCardSuit()) {
                if (cardPlayer1.getCardValue() > cardPlayer2.getCardValue()) {
                    //das muss dann in die Datenbamk gespeichert werden, also das Objekt pointsPlayer1
                    pointsplayer1.updatePlayer1Points(cardPlayer1.getCardValue() + cardPlayer2.getCardValue());
                } else {
                    pointsplayer2.updatePlayer2Points(cardPlayer1.getCardValue() + cardPlayer2.getCardValue());
                }
                //bitte schauen wie das dritte else if gemacht werden muss wenn die Kartenwerte gleich sind
                // welcher spieler dann die Punkte erhält bin mir da nicht sicher ob man das so machen kann danke !!
                if (cardPlayer1.getCardValue() > cardPlayer2.getCardValue()) {
                    pointsplayer1.updatePlayer1Points(cardPlayer1.getCardValue() + cardPlayer2.getCardValue());
                } else if (cardPlayer2.getCardValue() > cardPlayer1.getCardValue()) {
                    pointsplayer2.updatePlayer2Points(cardPlayer1.getCardValue() + cardPlayer2.getCardValue());
                } else if (cardPlayer1.getCardValue() == cardPlayer2.getCardValue()) {
                    pointsplayer1.updatePlayer1Points(cardPlayer1.getCardValue() + cardPlayer2.getCardValue());
                }

            }

            roundPointsDataSource.saveRoundPoints(pointsplayer1);
            roundPointsDataSource.saveRoundPoints(pointsplayer2);
        }

    }
}

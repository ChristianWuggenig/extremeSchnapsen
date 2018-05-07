package cardfactory.com.extremeschnapsen;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Round {

    private List<Card> allCards;
    private List<Deck> currentDeck;
    private List<RoundPoints> allRoundPoints;
    private DeckDataSource deckDataSource;
    private CardDataSource cardDataSource;
    private RoundPointsDataSource roundPointsDataSource;
    
    private RoundPoints points;


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
        roundPointsDataSource.createRoundPoints(1, 0, 0, 0);
        allCards = new ArrayList<>();
        currentDeck = new ArrayList<>();
        allRoundPoints = new ArrayList<>();

        points = new RoundPoints(1L, 0, 0, 0);

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
                        deck.setDeckStatus(5);
                        deckDataSource.updateDeckStatus(deck.getCardID(), 5);
                    } else {
                        deck.setDeckStatus(6);
                        deckDataSource.updateDeckStatus(deck.getCardID(), 6);
                    }
                }
            }

            networkManager.sendCard(cardID);
            myTurn = false;
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

    public void getNextFreeCard(int playerID) {
        for (Deck deck : currentDeck) {
            if (deck.getDeckStatus() == 4) {
                deck.setDeckStatus(playerID);
                break;
            }
        }
    }

    public void compareCards() {
        //both cards of round are set, continue to compare those

        Deck cardPlayer1 = getPlayedCardPlayer1();
        Deck cardPlayer2 = getPlayedCardPlayer2();
        boolean player1Won = false;
        boolean player2Won = false;

        if (cardPlayer1 != null && cardPlayer2 != null) {

            //spieler 1 hat trumpf, spieler 2 nicht
            if (cardPlayer1.getCardSuit().equals(getOpenCard().getCardSuit()) && !cardPlayer2.getCardSuit().equals(getOpenCard().getCardSuit())) {
                points.updatePlayer1Points(cardPlayer1.getCardValue() + cardPlayer2.getCardValue());
                player1Won = true;

            }
            //spieler 2 hat trumpf, spieler 1 nicht
            else if (!cardPlayer1.getCardSuit().equals(getOpenCard().getCardSuit()) && cardPlayer2.getCardSuit().equals(getOpenCard().getCardSuit())) {
                points.updatePlayer2Points(cardPlayer1.getCardValue() + cardPlayer2.getCardValue());
                player2Won = true;

            }
            //beide haben einen trumpf
            else if (cardPlayer1.getCardSuit().equals(getOpenCard().getCardSuit()) && cardPlayer2.getCardSuit().equals(getOpenCard().getCardSuit())) {

                if (cardPlayer1.getCardValue() > cardPlayer2.getCardValue()) {
                    points.updatePlayer1Points(cardPlayer1.getCardValue() + cardPlayer2.getCardValue());
                    player1Won = true;

                } else {
                    points.updatePlayer2Points(cardPlayer1.getCardValue() + cardPlayer2.getCardValue());
                    player2Won = true;

                }

            }
            //keiner von beiden hat einen trumpf
            else if(!cardPlayer1.getCardSuit().equals(getOpenCard().getCardSuit()) && !cardPlayer2.getCardSuit().equals(getOpenCard().getCardSuit())) {

                if (cardPlayer1.getCardValue() > cardPlayer2.getCardValue()) {
                    points.updatePlayer1Points(cardPlayer1.getCardValue() + cardPlayer2.getCardValue());
                    player1Won = true;

                } else if (cardPlayer2.getCardValue() > cardPlayer1.getCardValue()) {
                    points.updatePlayer2Points(cardPlayer1.getCardValue() + cardPlayer2.getCardValue());
                    player2Won = true;

                } else if (cardPlayer1.getCardValue() == cardPlayer2.getCardValue()) {
                    if (isGroupOwner) {
                        points.updatePlayer1Points(cardPlayer1.getCardValue() + cardPlayer2.getCardValue());
                        player1Won = true;
                    }
                    else {
                        points.updatePlayer2Points(cardPlayer1.getCardValue() + cardPlayer2.getCardValue());
                        player2Won = true;
                    }
                }

            }

            roundPointsDataSource.saveRoundPoints(points);

            cardPlayer1.setDeckStatus(7);
            cardPlayer2.setDeckStatus(8);
            deckDataSource.updateDeckStatus(cardPlayer1.getCardID(), 7); //updaten des status der karte
            deckDataSource.updateDeckStatus(cardPlayer2.getCardID(), 8);

            if (player1Won && isGroupOwner) {
                myTurn = true;
                getNextFreeCard(1);
                getNextFreeCard(2);
            } else if (player2Won && !isGroupOwner) {
                myTurn = true;
                getNextFreeCard(2);
                getNextFreeCard(1);
            } else if (player1Won && !isGroupOwner) {
                myTurn = false;
                networkManager.waitForCard();
                getNextFreeCard(1);
                getNextFreeCard(2);
            } else if (player2Won && isGroupOwner) {
                myTurn = false;
                getNextFreeCard(2);
                getNextFreeCard(1);
            }

            increaseMoves();
        }
    }
}

package cardfactory.com.extremeschnapsen;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Round {

    private List<Card> allCards; //contains a list of all cards
    private List<Deck> currentDeck; //contains the current deck
    private DeckDataSource deckDataSource; //contains a reference to the deck-datasource
    private CardDataSource cardDataSource; //contains a reference to the card-datasource
    private RoundPointsDataSource roundPointsDataSource; //contains a reference to the roundpoints-datasource

    private NetworkManager networkManager; //the network manager object (singleton)

    private boolean myTurn; //always set after a turn
    private boolean isGroupOwner; //shows if the current devices is groupowner (= server) or not (= client)

    private int moves; //contains the number of the current moves

    private String trump; //contains the trump card suit (e.g. Kreuz)

    public Round(Context context) {
        deckDataSource = new DeckDataSource(context);
        cardDataSource = new CardDataSource(context);
        roundPointsDataSource = new RoundPointsDataSource(context);
        cardDataSource.open();
        deckDataSource.open();
        roundPointsDataSource.open();
        roundPointsDataSource.deleteRoundPointsTable();
        roundPointsDataSource.createRoundPoints(1, 1, 0, 0);
        allCards = new ArrayList<>();
        currentDeck = new ArrayList<>();

        moves = 1;

        deckDataSource.deleteDeckTable(); //delete the deck from the database before creating a new one

        networkManager = NetworkManager.getInstance(context, (INetworkDisplay) context); //get the singleton object from the network-manager
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

    /**
     * returns a list of all deck cards
     * @return list of deck items
     */
    public List<Deck> getAllDecks() {
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

    /**
     * get the played cards from both players
     * @return list with the played cards from both players
     */
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

    /**
     * initialize a new round and shuffle the deck (server only!)
     */
    public void initializeRound() {
        allCards = cardDataSource.getAllCards();
        currentDeck = deckDataSource.shuffelDeck(allCards);
        isGroupOwner = true;
    }

    /**
     * initialize a new round with an already shuffled the deck (client only!)
     */
    public void getShuffledDeck(int[] shuffledDeckIDs) {
        allCards = cardDataSource.getAllCards();
        currentDeck = deckDataSource.receiveShuffeldDeck(shuffledDeckIDs, allCards);
        isGroupOwner = false;
    }

    /**
     * get or set if it is my turn or not
     * @param myTurn is it my turn or not
     */
    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    /**
     * increase the number of moves
     */
    public void increaseMoves() {
        moves++;
    }

    /**
     * start the http-server
     */
    public void startServer() {
        networkManager.startHttpServer(currentDeck);
    }

    /**
     * start the http-client
     */
    public void startClient() {
        networkManager.startHttpClient();
    }

    /**
     * play a card (send it to the opposite player and save it to the database)
     * @param cardID contains the id of the played card
     * @return true if successful, false if it was not my turn or the end of the round has already been reached
     */
    public boolean playCard(int cardID) {
        if (myTurn && moves <= 100) {
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

            return true;
        }

        return false;
    }

    /**
     * update the status of a given card
     * @param cardID contains the id of the desired card
     * @param status contains the new status
     */
    public void updateCard(int cardID, int status) {
        deckDataSource.updateDeckStatus(cardID, status);

        for (Deck deck : currentDeck) {
            if (deck.getCardID() == cardID) {
                deck.setDeckStatus(status);
            }
        }
    }

    /**
     * get the next free card from the deck
     * @param playerID used to identify which player gets the free card
     */
    public void getNextFreeCard(int playerID) {
        boolean found = false;

        for (Deck deck : currentDeck) {
            if (deck.getDeckStatus() == 4) {
                deck.setDeckStatus(playerID);
                deckDataSource.updateDeckStatus(deck.getCardID(), playerID);
                found = true;
                break;
            }
        }

        if (!found) {
            for (Deck deck : currentDeck) {
                if (deck.getDeckStatus() == 3) {
                    deck.setDeckStatus(playerID);
                    deckDataSource.updateDeckStatus(deck.getCardID(), playerID);
                }
            }
        }
    }

    /**
     * compares the two played cards and decides which player gets the appropriate round points
     * @return
     */
    public boolean compareCards() {
        //both cards of round are set, continue to compare those


        Deck cardPlayer1 = getPlayedCardPlayer1();
        Deck cardPlayer2 = getPlayedCardPlayer2();
        boolean player1Won = false;
        boolean player2Won = false;
        trump = deckDataSource.getTrump();



        if (cardPlayer1 != null && cardPlayer2 != null) {
            int sum_draw_points = cardPlayer1.getCardValue() + cardPlayer2.getCardValue();
            RoundPoints rp = new RoundPoints(1,1,0,0);

            //spieler 1 hat trumpf, spieler 2 nicht
            if (cardPlayer1.getCardSuit().equals(trump) && !cardPlayer2.getCardSuit().equals(trump)) {
                rp.setPointsplayer1(sum_draw_points);
                player1Won = true;

            }
            //spieler 2 hat trumpf, spieler 1 nicht
            else if (!cardPlayer1.getCardSuit().equals(trump) && cardPlayer2.getCardSuit().equals(trump)) {
                rp.setPointsplayer2(sum_draw_points);
                player2Won = true;

            }
            //beide haben einen trumpf
            else if (cardPlayer1.getCardSuit().equals(trump) && cardPlayer2.getCardSuit().equals(trump)) {

                if (cardPlayer1.getCardValue() > cardPlayer2.getCardValue()) {
                    rp.setPointsplayer1(sum_draw_points);
                    player1Won = true;

                } else {
                    rp.setPointsplayer2(sum_draw_points);
                    player2Won = true;

                }

            }
            //keiner von beiden hat einen trumpf
            else if(!cardPlayer1.getCardSuit().equals(trump) && !cardPlayer2.getCardSuit().equals(trump)) {
                //nicht die selbe Farbe
                if (!cardPlayer1.getCardSuit().equals(cardPlayer2.getCardSuit())){
                    if (myTurn && isGroupOwner) {
                        rp.setPointsplayer1(sum_draw_points);
                        player1Won = true;
                    }
                    else if (!myTurn && isGroupOwner){
                        rp.setPointsplayer2(sum_draw_points);
                        player2Won = true;
                    }
                    else if (myTurn && !isGroupOwner){
                        rp.setPointsplayer2(sum_draw_points);
                        player2Won = true;
                    }
                    else if (!myTurn && !isGroupOwner){
                        rp.setPointsplayer1(sum_draw_points);
                        player1Won = true;
                    }

                }
                //selbe Farbe
                else {
                    if (cardPlayer1.getCardValue() > cardPlayer2.getCardValue()) {
                        rp.setPointsplayer1(sum_draw_points);
                        player1Won = true;

                    } else if (cardPlayer2.getCardValue() > cardPlayer1.getCardValue()) {
                        rp.setPointsplayer2(sum_draw_points);
                        player2Won = true;

                    }
                }
            }


            roundPointsDataSource.saveRoundPoints(rp);

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

            deckDataSource.getAllDeck();

            RoundPoints rp2 = roundPointsDataSource.getCurrentRoundPointsObject();
    // wenn 20 und 40 geht hier weiter machen für Spielgewinn.   
            if (rp2.getPointsplayer1()>=66){

                if (rp2.getPointsplayer2() >= 33){
                    // 1 Punkt
                }
                else if (rp2.getPointsplayer2() >0 && rp2.getPointsplayer2() <33){
                    // 2 Punkte
                }
                else {
                    // 3 Punkte
                }
                if (isGroupOwner) {
                    try {
                        Thread.sleep(2000);
                    }
                    catch (InterruptedException e){

                    }
                    networkManager.stopHttpServer();
                }
                else {
                    try {
                        Thread.sleep(5000);
                    }
                    catch (InterruptedException e){

                    }

                }

                return true;

            }
            else if (rp2.getPointsplayer2()>=66){
                if (rp2.getPointsplayer1() >= 33){
                    // 1 Punkt
                }
                else if (rp2.getPointsplayer1() >0 && rp2.getPointsplayer1() <33){
                    // 2 Punkte
                }
                else {
                    // 3 Punkte
                }
                if (isGroupOwner) {
                    try {
                        Thread.sleep(2000);
                    }
                    catch (InterruptedException e){

                    }
                    networkManager.stopHttpServer();
                }
                else {
                    try {
                        Thread.sleep(5000);
                    }
                    catch (InterruptedException e){

                    }

                }

                return true;
            }
            return false;
        }
        return false;
    }

    public boolean checkFor20(List<Deck> cardsOnHand, List<CardImageView> cardImageViews){

        boolean result = false;

        //check only every second step
        if(this.moves % 2 == 0) {
            //check if player has more than one card of a color
            int countHerz = 0;
            int countPink = 0;
            int countKaro = 0;
            int countKreuz = 0;

            long hasHerzKoenig = 0;
            long hasHerzDame = 0;

            long hasKaroKoenig = 0;
            long hasKaroDame = 0;

            long hasKreuzKoenig = 0;
            long hasKreuzDame = 0;

            long hasPikKoenig = 0;
            long hasPikDame = 0;

            Collections.sort(cardsOnHand, new Comparator<Deck>() {
                @Override
                public int compare(Deck d1, Deck d2) {
                    return d1.getCardSuit().compareToIgnoreCase(d2.getCardSuit());
                }
            });

            for(CardImageView civ : cardImageViews){
                Log.e("CARD_IMAGE_VIEW", civ.getCardId() + "");
            }

            for (Deck d : cardsOnHand) {
                switch (d.getCardSuit()){
                    case "karo":
                        countKaro++;
                        break;
                    case "kreuz":
                        countKreuz++;
                        break;
                    case "pik":
                        countPink++;
                        break;
                    case "herz":
                        countHerz++;
                        break;
                }

                if(countHerz > 1 ){
                    if(d.getCardValue() == 3){
                        hasHerzDame = d.getCardID();
                    }else if(d.getCardValue() == 4){
                        hasHerzKoenig = d.getCardID();
                    }
                }else if(countKaro > 1){
                    if(d.getCardValue() == 3){
                        hasKaroDame = d.getCardID();
                    }else if(d.getCardValue() == 4){
                        hasKaroKoenig = d.getCardID();
                    }
                }else if(countKreuz > 1){
                    if(d.getCardValue() == 3){
                        hasKreuzDame = d.getCardID();
                    }else if(d.getCardValue() == 4){
                        hasKreuzKoenig = d.getCardID();
                    }
                }else if(countPink > 1){
                    if(d.getCardValue() == 3){
                        hasPikDame = d.getCardID();
                    }else if(d.getCardValue() == 4){
                        hasPikKoenig = d.getCardID();
                    }
                }

                if(hasHerzDame != 0 && hasHerzKoenig != 0){
                    //find cards in image view and enable more points
                    for(CardImageView civ : cardImageViews){
                        if(civ.getCardId() == hasHerzDame){
                            //update style and updated enable 20 strike
                            civ.setImageAlpha(25);
                            civ.setEnable_20_strike(true);
                        }else if(civ.getCardId() == hasHerzKoenig){
                            civ.setImageAlpha(25);
                            civ.setEnable_20_strike(true);
                        }
                    }
                }
                if(hasKaroDame != 0 && hasKaroKoenig != 0){
                    for(CardImageView civ : cardImageViews){
                        if(civ.getCardId() == hasKaroDame){
                            civ.setImageAlpha(25);
                            civ.setEnable_20_strike(true);
                        }else if(civ.getCardId() == hasKaroKoenig){
                            civ.setImageAlpha(25);
                            civ.setEnable_20_strike(true);
                        }
                    }
                }
                if(hasKreuzDame != 0 && hasKreuzKoenig != 0){
                    for(CardImageView civ : cardImageViews){
                        if(civ.getCardId() == hasKreuzDame){
                            civ.setImageAlpha(25);
                            civ.setEnable_20_strike(true);
                        }else if(civ.getCardId() == hasKreuzDame){
                            civ.setImageAlpha(25);
                            civ.setEnable_20_strike(true);
                        }
                    }
                }
                if(hasPikDame != 0 && hasPikKoenig != 0){
                    for(CardImageView civ : cardImageViews){
                        if(civ.getCardId() == hasPikDame){
                            civ.setImageAlpha(25);
                            civ.setEnable_20_strike(true);
                        }else if(civ.getCardId() == hasPikKoenig){
                            civ.setImageAlpha(25);
                            civ.setEnable_20_strike(true);
                        }
                    }
                }
            }
        }

        return result;
    }

}

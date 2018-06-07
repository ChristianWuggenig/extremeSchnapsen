package cardfactory.com.extremeschnapsen.gameengine;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cardfactory.com.extremeschnapsen.database.CardDataSource;
import cardfactory.com.extremeschnapsen.database.DeckDataSource;
import cardfactory.com.extremeschnapsen.database.PlayerDataSource;
import cardfactory.com.extremeschnapsen.database.RoundPointsDataSource;
import cardfactory.com.extremeschnapsen.gui.MessageHelper;
import cardfactory.com.extremeschnapsen.models.Card;
import cardfactory.com.extremeschnapsen.models.CardImageView;
import cardfactory.com.extremeschnapsen.models.Deck;
import cardfactory.com.extremeschnapsen.models.Player;
import cardfactory.com.extremeschnapsen.models.RoundPoints;
import cardfactory.com.extremeschnapsen.networking.INetworkDisplay;
import cardfactory.com.extremeschnapsen.networking.NetworkManager;

public class Round {

    private Game game_round; //Game Deklaration -> Instanz unten

    private List<Card> allCards; //contains a list of all cards
    private List<Deck> currentDeck; //contains the current deck
    private DeckDataSource deckDataSource; //contains a reference to the deck-datasource
    private CardDataSource cardDataSource; //contains a reference to the card-datasource
    private RoundPointsDataSource roundPointsDataSource; //contains a reference to the roundpoints-datasource
    private PlayerDataSource playerDataSource; //contains a reference to the player-datasource

    private NetworkManager networkManager; //the network manager object (singleton)

    private boolean myTurn; //always set after a turn
    private boolean isGroupOwner; //shows if the current devices is groupowner (= server) or not (= client)

    //private int moves; ist jetzt in RoundPointsDataSource
    //contains the number of the current moves

    private String trump; //contains the trump card suit (e.g. Kreuz)

    private Player player; //contains the current player

    private INetworkDisplay networkDisplay; //contains the networkDisplay-object for displaying status messages

    private RoundPoints points; //contains the round points

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

        points = roundPointsDataSource.getCurrentRoundPointsObject();


        game_round = new Game (context, true);


        deckDataSource.deleteDeckTable(); //delete the deck from the database before creating a new one

        networkDisplay = (INetworkDisplay)context;

        networkManager = NetworkManager.getInstance(context); //get the singleton object from the network-manager

        playerDataSource = new PlayerDataSource(context);
        playerDataSource.open();

        List<Player> players = playerDataSource.getAllPlayers();
        player = players.get(0);
    }

    public void openDatabases() {
        deckDataSource.open();
        cardDataSource.open();
        roundPointsDataSource.open();
        playerDataSource.open();
    }

    public void closeDatabases() {
        deckDataSource.close();
        cardDataSource.close();
        roundPointsDataSource.close();
        playerDataSource.close();
    }

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

    //die Karten auf der Hand zurückbekommen
    public List<Deck> getCardsOnHand(int player) {
        List<Deck> onHand = new ArrayList<>();

        for (Deck deck : this.currentDeck) {
            if (deck.getDeckStatus() == player) {
                onHand.add(deck);
            }
        }

        return onHand;
    }

    /**
     * get the name of the current user
     * @return the name of the current user
     */
    public String getUsername() {
        return player.getUsername();
    }

    public void exchangeTrump(){
        RoundPoints rp = new RoundPoints(1,0,0,0);
        rp = roundPointsDataSource.getCurrentRoundPointsObject();

        if (this.points.getMoves() < 4){
            if (myTurn && rp.getTrumpExchanged() == 0) {
                for (Deck deck : this.getCardsOnHand()) {
                    if (deck.getCardValue() == 2 && deck.getDeckTrump() == 1) {
                        rp.setTrumpExchanged(1);
                        if (isGroupOwner) {
                            this.deckDataSource.updateDeckStatus(this.getOpenCard().getCardID(), 1);
                            this.currentDeck = this.deckDataSource.getAllDeck();
                            this.roundPointsDataSource.updtateTrumpExchanged(rp);
                        } else {
                            //this.getOpenCard().setDeckStatus(2);
                            deckDataSource.updateDeckStatus(this.getOpenCard().getCardID(), 2);
                            this.currentDeck = this.deckDataSource.getAllDeck();
                            this.roundPointsDataSource.updtateTrumpExchanged(rp);
                        }

                        networkManager.sendTrumpExchanged();

                        this.deckDataSource.updateDeckStatus(deck.getCardID(), 3);
                        this.currentDeck = this.deckDataSource.getAllDeck();
                        break;
                    }
                }
            }
        }
    }

    public void receiveExchangeTrump(){
        RoundPoints rp = new RoundPoints(1,0,0,0);
        rp = roundPointsDataSource.getCurrentRoundPointsObject();
        //if (myTurn && rp.getTrumpExchanged() == 0) {
            for (Deck deck : this.getAllDecks()) {
                if (deck.getCardValue() == 2 && deck.getDeckTrump() == 1) {
                    rp.setTrumpExchanged(1);
                    this.deckDataSource.updateDeckStatus(this.getOpenCard().getCardID(), deck.getDeckStatus());
                    this.roundPointsDataSource.updtateTrumpExchanged(rp);
                    this.deckDataSource.updateDeckStatus(deck.getCardID(), 3);
                    this.currentDeck = this.deckDataSource.getAllDeck();
                    break;
                }
            }
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
        this.currentDeck = getAllDecks();
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
        this.currentDeck = getAllDecks();
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

        //nicht this.currentDeck sondern aktuelles Deck in der Datenbank
        for (Deck deck : this.getAllDecks()) {
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
        this.roundPointsDataSource.increaseMoves();
    }

    /**
     * start the http-server
     */
    public void startServer() {
        networkManager.startHttpServer(currentDeck, player, networkDisplay);
    }

    /**
     * start the http-client
     */
    public void startClient() {
        networkManager.startHttpClient(networkDisplay, player);
    }

    /**
     * play a card (send it to the opposite player and save it to the database)
     * @param cardID contains the id of the played card
     * @return true if successful, false if it was not my turn or the end of the round has already been reached
     */
    public boolean playCard(int cardID) {

        //ich bin dran und und schlussphase
        points = roundPointsDataSource.getCurrentRoundPointsObject();
        Deck wanttoplaycard = new Deck();

        if (points.getMoves()<5) {
            if (myTurn) {

                for (Deck deck : this.getAllDecks()) {
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
        }
        if (points.getMoves()>=5) {
            if (myTurn) {
                List<Deck> playedcards = getPlayedCards();
                if (playedcards.size() !=1 ) {

                    for (Deck deck : this.getAllDecks()) {
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
                else {
                    for (Deck deck : this.getAllDecks()){
                        if (deck.getCardID() == cardID){
                            wanttoplaycard = deck;
                            break;
                        }
                    }
                    if (checkForFarbStuchzwang(playedcards.get(0), wanttoplaycard)) {
                        for (Deck deck : this.getAllDecks()) {
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
                    else {
                        return false;
                    }
                }
            }
        }
        return false;

    }

    public boolean checkForFarbStuchzwang (Deck playedcard, Deck wanttoplay){

        boolean farbe = false;
        boolean highercard = false;
        boolean trumpOnHand = false;
        List<Deck> tempcardsToPlay = new ArrayList<>();
        List<Deck> cardsToPlay = new ArrayList<>();

        //check ob Karte von gleicher Farbe, wenn ja wird es der List cardsToPlay hinzugefÃ¼gt
        for (Deck deck : this.getCardsOnHand()) {
            if (deck.getCardSuit().equals(playedcard.getCardSuit())) {
                farbe = true;
                tempcardsToPlay.add(deck);
            }
        }


        //check wenn gleiche Farbe vorhanden auf hÃ¶here Karte
        if (farbe){
            for (Deck deck : tempcardsToPlay){
                if (deck.getCardValue() > playedcard.getCardValue()){
                    highercard = true;
                    break;
                }
            }
        }
        //wenn gleiche Farbe und HÃ¶hre Karte vorhanden lÃ¶sche niedrigere Karten
        if (highercard){
            for (Deck deck : tempcardsToPlay){
                if (deck.getCardValue() > playedcard.getCardValue()){
                    cardsToPlay.add(deck);
                }
            }
        }

        if (farbe && !highercard){
            for (Deck deck : tempcardsToPlay){
                cardsToPlay.add(deck);
            }

        }


        //check wenn nicht die gleich Farbe, aber Trumpf
        if (!farbe) {

            for (Deck deck : this.getCardsOnHand()) {
                if (deck.getDeckTrump() == 1) {
                    trumpOnHand = true;
                    cardsToPlay.add(deck);
                }
            }
        }
        //schauen, ob wantToplay Karte in Liste vorkommt
        if (farbe || trumpOnHand) {
            for (Deck deck : cardsToPlay) {
                if (deck.getCardID() == wanttoplay.getCardID()) {
                    return true;
                }

            }
            return false;
        }
        else {
            return true;
        }

    }

    /**
     * tell the volley-client to wait for a card from the server
     */
    public void waitForCard() {
        networkManager.waitForCard(false);
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
     * @return true if the round is finished, else false
     */
    public boolean compareCards() {
        //both cards of round are set, continue to compare those

        Deck cardPlayer1 = getPlayedCardPlayer1();
        Deck cardPlayer2 = getPlayedCardPlayer2();
        boolean player1Won = false;
        boolean player2Won = false;
        int player1 = 0;
        int player2 = 0;
        trump = deckDataSource.getTrump();

        networkDisplay.updateDeck();

        if (cardPlayer1 != null && cardPlayer2 != null) {
            int sum_draw_points = cardPlayer1.getCardValue() + cardPlayer2.getCardValue();
            RoundPoints rp = new RoundPoints(1,1,0,0);

            //spieler 1 hat trumpf, spieler 2 nicht
            if (cardPlayer1.getCardSuit().equals(trump) && !cardPlayer2.getCardSuit().equals(trump)) {
                rp.setPointsplayer1(sum_draw_points);
                player1Won = true;
                player1 = 7;
                player2 = 7;
            }
            //spieler 2 hat trumpf, spieler 1 nicht
            else if (!cardPlayer1.getCardSuit().equals(trump) && cardPlayer2.getCardSuit().equals(trump)) {
                rp.setPointsplayer2(sum_draw_points);
                player2Won = true;
                player1 = 8;
                player2 = 8;
            }
            //beide haben einen trumpf
            else if (cardPlayer1.getCardSuit().equals(trump) && cardPlayer2.getCardSuit().equals(trump)) {

                if (cardPlayer1.getCardValue() > cardPlayer2.getCardValue()) {
                    rp.setPointsplayer1(sum_draw_points);
                    player1Won = true;
                    player1 = 7;
                    player2 = 7;

                } else {
                    rp.setPointsplayer2(sum_draw_points);
                    player2Won = true;
                    player1 = 8;
                    player2 = 8;
                }

            }
            //keiner von beiden hat einen trumpf
            else if(!cardPlayer1.getCardSuit().equals(trump) && !cardPlayer2.getCardSuit().equals(trump)) {
                //nicht die selbe Farbe
                if (!cardPlayer1.getCardSuit().equals(cardPlayer2.getCardSuit())){
                    if (myTurn && isGroupOwner) {
                        rp.setPointsplayer1(sum_draw_points);
                        player1Won = true;
                        player1 = 7;
                        player2 = 7;
                    }
                    else if (!myTurn && isGroupOwner){
                        rp.setPointsplayer2(sum_draw_points);
                        player2Won = true;
                        player1 = 8;
                        player2 = 8;
                    }
                    else if (myTurn && !isGroupOwner){
                        rp.setPointsplayer2(sum_draw_points);
                        player2Won = true;
                        player1 = 8;
                        player2 = 8;
                    }
                    else if (!myTurn && !isGroupOwner){
                        rp.setPointsplayer1(sum_draw_points);
                        player1Won = true;
                        player1 = 7;
                        player2 = 7;
                    }

                }
                //selbe Farbe
                else {
                    if (cardPlayer1.getCardValue() > cardPlayer2.getCardValue()) {
                        rp.setPointsplayer1(sum_draw_points);
                        player1Won = true;
                        player1 = 7;
                        player2 = 7;

                    } else if (cardPlayer2.getCardValue() > cardPlayer1.getCardValue()) {
                        rp.setPointsplayer2(sum_draw_points);
                        player2Won = true;
                        player1 = 8;
                        player2 = 8;
                    }
                }
            }


            roundPointsDataSource.saveRoundPoints(rp);
            //wenn noch keinen Stuch und 20/40 angesagt; und jetzt ein Stuch -> so werden
            //die "versteckten Punkte" jetzt gutgeschrieben
            HiddenPointsToRealPoints();

            if (player1Won && isGroupOwner) {
                myTurn = true;
                getNextFreeCard(1);
                getNextFreeCard(2);
                networkDisplay.displayUserInformation(MessageHelper.WON);
            } else if (player2Won && !isGroupOwner) {
                myTurn = true;
                getNextFreeCard(2);
                getNextFreeCard(1);
                networkDisplay.displayUserInformation(MessageHelper.WON);
            } else if (player1Won && !isGroupOwner) {
                myTurn = false;
                networkManager.waitForCard(false);
                getNextFreeCard(1);
                getNextFreeCard(2);
                networkDisplay.displayUserInformation(MessageHelper.LOST);
            } else if (player2Won && isGroupOwner) {
                myTurn = false;
                getNextFreeCard(2);
                getNextFreeCard(1);
                networkDisplay.displayUserInformation(MessageHelper.LOST);
            }

            networkDisplay.updateDeck();

            cardPlayer1.setDeckStatus(player1);
            cardPlayer2.setDeckStatus(player2);
            deckDataSource.updateDeckStatus(cardPlayer1.getCardID(), player1); //updaten des status der karte
            deckDataSource.updateDeckStatus(cardPlayer2.getCardID(), player2);

            increaseMoves();

            deckDataSource.getAllDeck();

            RoundPoints rp2 = roundPointsDataSource.getCurrentRoundPointsObject();

            if (rp2.getPointsplayer1()>=66){

                if (rp2.getPointsplayer2() >= 33){
                    // 1 Punkt
                    game_round.updateGamePoints(1,0);
                }
                else if (rp2.getPointsplayer2() >0 && rp2.getPointsplayer2() <33){
                    // 2 Punkte
                    game_round.updateGamePoints(2,0);
                }
                else {
                    // 3 Punkte
                    game_round.updateGamePoints(3,0);
                }

                return true;

            }
            else if (rp2.getPointsplayer2()>=66){
                if (rp2.getPointsplayer1() >= 33){
                    // 1 Punkt
                    game_round.updateGamePoints(0,1);
                }
                else if (rp2.getPointsplayer1() >0 && rp2.getPointsplayer1() <33){
                    // 2 Punkte
                    game_round.updateGamePoints(0,2);
                }
                else {
                    // 3 Punkte
                    game_round.updateGamePoints(0,3);
                }

                return true;
            }
            //letzter Stich
            if (rp2.getMoves() == 10) {
                if (player1Won){
                    game_round.updateGamePoints(1,0);

                }
                else {
                    game_round.updateGamePoints(0,1);
                }

                return true;
            }

            return false;
        } else if (cardPlayer1 == null && cardPlayer2 != null && !isGroupOwner) {
            networkManager.waitForCard(false);
            networkDisplay.displayUserInformation(MessageHelper.WAITING);
            networkDisplay.updateDeck();
        } else if (cardPlayer1 != null && cardPlayer2 == null && !isGroupOwner) {
            networkDisplay.displayUserInformation(MessageHelper.YOURTURN);
            networkDisplay.updateDeck();
        } else if (cardPlayer1 == null && cardPlayer2 != null && isGroupOwner) {
            networkDisplay.displayUserInformation(MessageHelper.YOURTURN);
            networkDisplay.updateDeck();
        } else if (cardPlayer1 != null && cardPlayer2 == null && isGroupOwner) {
            networkDisplay.displayUserInformation(MessageHelper.WAITING);
            networkDisplay.updateDeck();
        } else {
            networkDisplay.displayUserInformation(MessageHelper.WAITING);
            networkDisplay.updateDeck();
        }
        return false;
    }

    //ob Stuch bereits vorhanden
    public boolean checkForStuch(){
        boolean checkStuch = false;
        this.points = this.roundPointsDataSource.getCurrentRoundPointsObject();

        if(isGroupOwner && points.getPointsplayer1()>0){
            checkStuch = true;
        }
        if(!isGroupOwner && points.getPointsplayer2()>0){
            checkStuch = true;
        }
        return checkStuch;
    }

    //nach Message, wenn 20/40 ausgespielt wurde -> Information, welche Farbe wird benötigt
    public void receiveCheck2040(String farbe){
        int i = 0;
        RoundPoints rp = new RoundPoints(1,1,0,0);

        this.trump = deckDataSource.getTrump();

        if (trump == farbe)
            i=4;
        else if (!farbe.equals(""))
            i=2;

        //20er 40er
        if (i == 2 || i ==4) {
            if (isGroupOwner) {
                rp.setPointsplayer2(10 * i);
            }
            else{
                rp.setPointsplayer1(10 * i);
            }

            this.roundPointsDataSource.saveRoundPoints(rp);
        }

        if (i == 2) {
            networkDisplay.displayUserInformation(MessageHelper.TWENTYRECEIVED);
        } else if (i == 4) {
            networkDisplay.displayUserInformation(MessageHelper.FORTYRECEIVED);
        }
    }

    //Der Button Herz, Pik, Karo oder Kreuz wird gedrückt um 20/40 anzusagen
    public String check2040(String farbe) {
        if (myTurn) {
            int i = 0;
            String check2040 = "";
            RoundPoints rp = new RoundPoints(1,1,0,0);

            for (Deck deck : this.getCardsOnHand()){
                if (farbe.equals(deck.getCardSuit()) && deck.getCardValue() <=4 && deck.getCardValue() >2){
                    if(deck.getDeckTrump() == 1)
                        i = 2 + i;
                    else
                        i++;
                }
            }
            //20er 40er
            if (i == 2 || i ==4) {
                check2040 = farbe;
                if (checkForStuch()){
                    if (isGroupOwner) {
                        rp.setPointsplayer1(10 * i);
                    }
                    else{
                        rp.setPointsplayer2(10 * i);
                    }
                }
                else{
                    if (isGroupOwner){
                        rp.setHiddenpointsplayer1(10*i);
                    }
                    else{
                        rp.setHiddenpointsplayer2(10*i);

                    }
                }
                this.roundPointsDataSource.saveRoundPoints(rp);
            }

            if (i == 2) {
                networkDisplay.displayUserInformation(MessageHelper.TWENTYPLAYED);
                networkManager.send2040(farbe);
            } else if (i == 4) {
                networkDisplay.displayUserInformation(MessageHelper.FORTYPLAYED);
                networkManager.send2040(farbe);
            }

            //als Rückgabeparameter für Message an anderen Spieler
            return check2040;
        } else {
            return "";
        }

    }

    //Checkt, ob jetzt Stuch verhanden ist, um Punkte gutzuschreiben
    public void HiddenPointsToRealPoints(){
        this.points = this.roundPointsDataSource.getCurrentRoundPointsObject();

        if (points.getPointsplayer1() > 0 && points.getHiddenpointsplayer1() > 0) {
            points.setPointsplayer1(points.getPointsplayer1() + points.getHiddenpointsplayer1());
            points.setHiddenpointsplayer1(0);
            this.roundPointsDataSource.updateRoundPoints(points);
        }
        else if (points.getPointsplayer2() > 0 && points.getHiddenpointsplayer2() > 0) {
                points.setPointsplayer2(points.getPointsplayer2() + points.getHiddenpointsplayer2());
                points.setHiddenpointsplayer2(0);
                this.roundPointsDataSource.updateRoundPoints(points);
        }

    }


    public boolean checkFor20(List<Deck> cardsOnHand, List<CardImageView> cardImageViews){

        boolean result = false;

        //check only every second step
        if(this.roundPointsDataSource.getCurrentRoundPointsObject().getMoves() % 2 == 0) {
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
                //Log.e("CARD_IMAGE_VIEW", civ.getCardId() + "");
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

    public String getRoundPointsPlayer1() {
        RoundPoints roundPoints = roundPointsDataSource.getCurrentRoundPointsObject();
        return String.valueOf(roundPoints.getPointsplayer1());
    }

    public String getRoundPointsPlayer2() {
        RoundPoints roundPoints = roundPointsDataSource.getCurrentRoundPointsObject();
        return String.valueOf(roundPoints.getPointsplayer2());
    }

    public String getGamePointsPlayer1(){
       return String.valueOf(game_round.getGamePointsPlayer1());
    }

    public String getGamePointsPlayer2(){
        return String.valueOf(game_round.getGamePointsPlayer2());
    }

    public List<Deck> getAlreadyPlayedCards() {
        List<Deck> deckPlayed = new ArrayList<>();


        if (isGroupOwner) {
            for (Deck deck : deckDataSource.getAllDeck()) {
                if (deck.getDeckStatus() == 7) {
                    deckPlayed.add(deck);
                }
            }
        } else {
            for (Deck deck : deckDataSource.getAllDeck()) {
                if (deck.getDeckStatus() == 8) {
                    deckPlayed.add(deck);
                }
            }
        }
        return deckPlayed;
    }

    public void sightJokerUsed() {
        roundPointsDataSource.open();
        RoundPoints roundPoints = roundPointsDataSource.getCurrentRoundPointsObject();

        if (isGroupOwner) {
            roundPoints.setSightJokerPlayer1(1);
        } else {
            roundPoints.setSightJokerPlayer2(1);
        }

        roundPointsDataSource.saveRoundPoints(roundPoints);

        networkManager.sendSightJoker();
    }

    public void sightJokerReceived() {
        roundPointsDataSource.open();
        RoundPoints roundPoints = roundPointsDataSource.getCurrentRoundPointsObject();

        if (isGroupOwner) {
            roundPoints.setSightJokerPlayer2(1);
        } else {
            roundPoints.setSightJokerPlayer1(1);
        }

        roundPointsDataSource.saveRoundPoints(roundPoints);
    }

    public void sightJokerParryUsed() {
        roundPointsDataSource.open();
        RoundPoints roundPoints = roundPointsDataSource.getCurrentRoundPointsObject();

        if (isGroupOwner) {
            roundPoints.setParrySightJokerPlayer1(1);
        } else {
            roundPoints.setParrySightJokerPlayer2(1);
        }

        roundPointsDataSource.saveRoundPoints(roundPoints);

        networkManager.sendParrySightJoker();
    }

    public void sightJokerParryReceived() {
        roundPointsDataSource.open();
        RoundPoints roundPoints = roundPointsDataSource.getCurrentRoundPointsObject();

        if (isGroupOwner) {
            roundPoints.setParrySightJokerPlayer2(1);
        } else {
            roundPoints.setParrySightJokerPlayer1(1);
        }

        roundPointsDataSource.saveRoundPoints(roundPoints);
    }
}

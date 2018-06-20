package cardfactory.com.extremeschnapsen.gameengine;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cardfactory.com.extremeschnapsen.database.CardDataSource;
import cardfactory.com.extremeschnapsen.database.DeckDataSource;
import cardfactory.com.extremeschnapsen.database.PlayerDataSource;
import cardfactory.com.extremeschnapsen.database.RoundPointsDataSource;
import cardfactory.com.extremeschnapsen.gui.MessageHelper;
import cardfactory.com.extremeschnapsen.models.Card;
import cardfactory.com.extremeschnapsen.models.Deck;
import cardfactory.com.extremeschnapsen.models.Player;
import cardfactory.com.extremeschnapsen.models.RoundPoints;
import cardfactory.com.extremeschnapsen.networking.INetworkDisplay;
import cardfactory.com.extremeschnapsen.networking.NetworkManager;

public class Round {

    //region Declarations

    private Game game; //Game Deklaration -> Instanz unten

    private List<Card> allCards; //contains a list of all cards
    private List<Deck> currentDeck; //contains the current deck
    private DeckDataSource deckDataSource; //contains a reference to the deck-datasource
    private CardDataSource cardDataSource; //contains a reference to the card-datasource
    private RoundPointsDataSource roundPointsDataSource; //contains a reference to the roundpoints-datasource

    private PlayerDataSource playerDataSource; //contains a reference to the player-datasource

    private NetworkManager networkManager; //the network manager object (singleton)

    private boolean myTurn; //always set after a turn
    private boolean isGroupOwner; //shows if the current devices is groupowner (= server) or not (= client)
    private boolean lastStuch;
    private boolean hasturned;
    private int turnpoints;
    private boolean roundWon;

    //private int moves; ist jetzt in RoundPointsDataSource
    //contains the number of the current moves

    private String trump; //contains the trump card suit (e.g. Kreuz)

    private Player player; //contains the current player

    private INetworkDisplay networkDisplay; //contains the networkDisplay-object for displaying status messages

    private RoundPoints points; //contains the round points

    private boolean sightJokerUsed; //true, if the sight joker was used in the current move
    private boolean sightJokerReceived; //true, if the opposite player used the sight joker, so the current player can use the parry sight joker

    //falls Button Ã¶fters gedrÃ¼ckt wird
    private boolean [] twentyfortyalreadyplayed = new boolean[4];

    //dass nur KÃ¶nig oder Dame gespielt werden kÃ¶nnen, nach 20/40

    private String justplayed2040;

    //endregion

    //region Constructors

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
        lastStuch = false;
        hasturned = false;
        turnpoints = 0;
        roundWon = false;

        points = roundPointsDataSource.getCurrentRoundPointsObject();


        game = new Game (context, true);


        deckDataSource.deleteDeckTable(); //delete the deck from the database before creating a new one

        networkDisplay = (INetworkDisplay)context;

        networkManager = NetworkManager.getInstance(context); //get the singleton object from the network-manager

        playerDataSource = new PlayerDataSource(context);
        playerDataSource.open();

        List<Player> players = playerDataSource.getAllPlayers();
        player = players.get(0);
      
        for (boolean played20 : twentyfortyalreadyplayed){
            played20 = false;
        }

        justplayed2040 = "";

        sightJokerReceived = false;
        sightJokerUsed = false;
    }

    /**
     * used only for the unit tests
     */
    public Round() {

    }

    /**
     * initialize a new round and shuffle the deck (server only!)
     */
    public void initializeRound() {
        allCards = cardDataSource.getAllCards();
        currentDeck = deckDataSource.shuffelDeck(allCards);
        isGroupOwner = true;
    }

    //endregion

    //region Getters and Setters

    /**
     * set the roundpointsdatasource
     * @param roundPointsDataSource the roundpointsdatasource object
     */
    public void setRoundPointsDataSource(RoundPointsDataSource roundPointsDataSource) {
        this.roundPointsDataSource = roundPointsDataSource;
    }

    /**
     * set the deckdatasource
     */
    public void setDeckDataSource(DeckDataSource deckDataSource) {
        this.deckDataSource = deckDataSource;
    }

    /**
     * set the playerdatasource
     */
    public void setPlayerDataSource(PlayerDataSource playerDataSource) {
        this.playerDataSource = playerDataSource;
    }

    /**
     * set the groupowner
     * @param groupOwner true or false, if group owner or not
     */
    public void setGroupOwner(boolean groupOwner) {
        isGroupOwner = groupOwner;
    }

    /**
     * returns the status of the variable receiveSightJoker
     * @return true, if the sight joker was received, else false
     */
    public boolean getSightJokerReceived() {
        return sightJokerReceived;
    }

    /**
     * get the name of the current user
     * @return the name of the current user
     */
    public String getUsername() {
        return player.getUsername();
    }

    /**
     * set if it is my turn or not
     * @param myTurn is it my turn or not
     */
    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    /**
     * get the information if it is currently my turn
     * @return true, if my turn
     */
    public boolean getMyTurn() {
        return myTurn;
    }

    /**
     * set the value for 2040 just played
     * @param justplayed2040 contains the suit
     */
    public void setJustplayed2040(String justplayed2040) {
        this.justplayed2040 = justplayed2040;
    }

    /**
     * set the network display object
     * @param networkDisplay contains the network display
     */
    public void setNetworkDisplay(INetworkDisplay networkDisplay) {
        this.networkDisplay = networkDisplay;
    }

    /**
     * set the network manager object (only for unit testing to provide a mock object)
     * @param networkManager the mock network manager
     */
    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    /**
     * set the information if the sight joker was used
     * @param sightJokerUsed true, if used
     */
    public void setSightJokerUsed(boolean sightJokerUsed) {
        this.sightJokerUsed = sightJokerUsed;
    }

    /**
     * get the information if the current player won the round
     * @return true, if the round was won, else false
     */
    public boolean roundWon() {
        return roundWon;
    }

    /**
     * set the card data source (only for unit testing to provide a mock object)
     * @param cardDataSource contains the card data source mock object
     */
    public void setCardDataSource(CardDataSource cardDataSource) {
        this.cardDataSource = cardDataSource;
    }

    //endregion

    //region Database

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

    //endregion

    //region Deck & Database

    /**
     * get the cards which the current player has on his hand
     * @return a list of deck-items on hand
     */
    public List<Deck> getCardsOnHand() {
        List<Deck> deckonhands = new ArrayList<>();


        if (isGroupOwner) {
            for (Deck deck : this.deckDataSource.getAllDeck()) {
                if (deck.getDeckStatus() == 1) {
                    deckonhands.add(deck);
                }
            }
        } else {
            for (Deck deck : this.deckDataSource.getAllDeck()) {
                if (deck.getDeckStatus() == 2) {
                    deckonhands.add(deck);
                }
            }
        }
        return deckonhands;

    }

    /**
     * get the cards on hand from the opposite player
     * @return a list of deck-items
     */
    public List<Deck> getCardsOnHandOpponent() {
        List<Deck> deckonhands = new ArrayList<>();


        if (isGroupOwner) {
            for (Deck deck : this.deckDataSource.getAllDeck()) {
                if (deck.getDeckStatus() == 2) {
                    deckonhands.add(deck);
                }
            }
        } else {
            for (Deck deck : this.deckDataSource.getAllDeck()) {
                if (deck.getDeckStatus() == 1) {
                    deckonhands.add(deck);
                }
            }
        }
        return deckonhands;

    }

    /**
     * get the cards on hand with a given player-id
     * @param player the id of the player (1 or 2)
     * @return a list of deck-items
     */
    public List<Deck> getCardsOnHand(int player) {
        List<Deck> onHand = new ArrayList<>();

        for (Deck deck : this.deckDataSource.getAllDeck()) {
            if (deck.getDeckStatus() == player) {
                onHand.add(deck);
            }
        }

        return onHand;
    }

    /**
     * returns a list of all deck cards
     * @return list of deck items
     */
    public List<Deck> getAllDecks() {
        deckDataSource.open();
        return deckDataSource.getAllDeck();
    }

    /**
     * get the open card (offene karte)
     * @return a deck item with the open card
     */
    public Deck getOpenCard() {
        Deck opencard = null;
        for (Deck deck : this.deckDataSource.getAllDeck()) {
            if (deck.getDeckStatus() == 3) {
                opencard = deck;
            }
        }
        return opencard;
    }

    /**
     * get the card which player 1 played (lies on table)
     * @return a deck item
     */
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

    /**
     * get the card which player 2 played (lies on table)
     * @return a deck item
     */
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
     * initialize a new round with an already shuffled the deck (client only!)
     * @param shuffledDeckIDs contains the card-ids of the shuffled deck
     */
    public void getShuffledDeck(int[] shuffledDeckIDs) {
        allCards = cardDataSource.getAllCards();
        currentDeck = deckDataSource.receiveShuffeldDeck(shuffledDeckIDs, allCards);
        isGroupOwner = false;
    }

    /**
     * get the next free card from the deck
     * @param playerID used to identify which player gets the free card
     */
    public void getNextFreeCard(int playerID) {
        boolean found = false;

        for (Deck deck : this.deckDataSource.getAllDeck()) {
            if (deck.getDeckStatus() == 4) {
                deck.setDeckStatus(playerID);
                deckDataSource.updateDeckStatus(deck.getCardID(), playerID);
                found = true;
                break;
            }
        }

        if (!found) {
            for (Deck deck : this.deckDataSource.getAllDeck()) {
                if (deck.getDeckStatus() == 3) {
                    deck.setDeckStatus(playerID);
                    deckDataSource.updateDeckStatus(deck.getCardID(), playerID);
                }
            }
        }
    }

    /**
     * get the already played cards for the own player
     * @return a list of played cards (deck-items)
     */
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

    //endregion

    //region Round & Database

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

    public String getRoundPointsPlayer1() {
        RoundPoints roundPoints = roundPointsDataSource.getCurrentRoundPointsObject();
        return String.valueOf(roundPoints.getPointsplayer1());
    }

    public String getRoundPointsPlayer2() {
        RoundPoints roundPoints = roundPointsDataSource.getCurrentRoundPointsObject();
        return String.valueOf(roundPoints.getPointsplayer2());
    }

    /**
     * increase the number of moves
     */
    public void increaseMoves() {
        this.roundPointsDataSource.increaseMoves();
    }

    //endregion

    //region Game & Database

    public String getGamePointsPlayer1(){
        return String.valueOf(game.getGamePointsPlayer1());
    }

    public String getGamePointsPlayer2(){
        return String.valueOf(game.getGamePointsPlayer2());
    }

    //endregion

    //region Network

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
     * tell the volley-client to wait for a card from the server
     */
    public void waitForCard() {
        networkManager.waitForCard(false);
    }

    //endregion

    //region Actions

    /**
     * checks, if 20 or 40 can be played with a given suit
     * @param suit contains the suit
     * @return true if 20/40 is possible, else false
     */
    public String check2040(String suit) {

        //temporÃ¤re Variable, falls spieler Ã¶fters auf button 2040 drÃ¼ckt
        //muss nicht synchron gehalten werden, weil ja nur ein Spieler den jeweiligen 20er haben kann
        boolean alreadyplayed = false;

        switch (suit){
            case "herz":
                alreadyplayed = twentyfortyalreadyplayed[0];
                break;
            case "karo":
                alreadyplayed = twentyfortyalreadyplayed[1];
                break;
            case "pik":
                alreadyplayed = twentyfortyalreadyplayed[2];
                break;
            case "kreuz":
                alreadyplayed = twentyfortyalreadyplayed[3];
                break;
        }

        if (myTurn && numberPlayedCards() == 0 && !alreadyplayed && justplayed2040.equals("")) {
            int i = 0;
            String check2040 = "";
            RoundPoints rp = new RoundPoints(1,1,0,0);

            for (Deck deck : this.getCardsOnHand()){
                if (suit.equals(deck.getCardSuit()) && deck.getCardValue() <=4 && deck.getCardValue() >2){
                    if(deck.getDeckTrump() == 1)
                        i = 2 + i;
                    else
                        i++;
                }
            }
            //20er 40er
            if (i == 2 || i ==4) {

                switch (suit){
                    case "herz":
                        twentyfortyalreadyplayed[0] = true;
                        justplayed2040 = "herz";
                        break;
                    case "karo":
                        twentyfortyalreadyplayed[1] = true;
                        justplayed2040 = "karo";
                        break;
                    case "pik":
                        twentyfortyalreadyplayed[2] = true;
                        justplayed2040 = "pik";
                        break;
                    case "kreuz":
                        twentyfortyalreadyplayed[3] = true;
                        justplayed2040 = "kreuz";
                        break;
                }


                check2040 = suit;
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
                networkManager.send2040(suit);
            } else if (i == 4) {
                networkDisplay.displayUserInformation(MessageHelper.FORTYPLAYED);
                networkManager.send2040(suit);
            }

            //als RÃ¼ckgabeparameter fÃ¼r Message an anderen Spieler
            return check2040;
        } else {
            return "";
        }

    }

    /**
     * processes the information that the opposite player played 20/40
     * @param suit contains the suit
     */
    public void receiveCheck2040(String suit){
        int i = 0;
        RoundPoints rp = new RoundPoints(1,1,0,0);

        deckDataSource.open();
        this.trump = deckDataSource.getTrump();

        if (trump.equals(suit))
            i=4;
        else if (!suit.equals(""))
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

    public void turn (){
        roundPointsDataSource.open();
        points = roundPointsDataSource.getCurrentRoundPointsObject();


        if (myTurn && points.getMoves() < 4){
            for (Deck deck : this.getAllDecks()) {
                if (deck.getDeckStatus() == 4 || deck.getDeckStatus() ==3) {
                    deck.setDeckStatus(9); //wie bekomme ich das in die GUI?
                    deckDataSource.updateDeckStatus(deck.getCardID(), 9);
                }
            }
            hasturned = true;

            if (isGroupOwner){
               if (points.getPointsplayer2() == 0){
                    turnpoints = 3;

               }
               else if (points.getPointsplayer2() <33){
                   turnpoints = 2;
               }
               else {
                   turnpoints = 1;

               }
            }
            else {
                if (points.getPointsplayer1() == 0){
                    turnpoints = 3;

                }
                else if (points.getPointsplayer1() <33){
                    turnpoints = 2;
                }
                else {
                    turnpoints = 1;

                }
            }



            networkManager.sendTurn();
            networkDisplay.displayUserInformation(MessageHelper.TURNEDCALLEDSUCESS);
            points.setMoves(20);
            roundPointsDataSource.updateMoves(20);
        }
        else {
            networkDisplay.displayUserInformation(MessageHelper.TURNEDCALLEDFAIL);

        }
    }

    public void turnReceived (){
        roundPointsDataSource.open();
        points = roundPointsDataSource.getCurrentRoundPointsObject();


        for (Deck deck : this.getAllDecks()) {
            if (deck.getDeckStatus() == 4 || deck.getDeckStatus() ==3) {
                deck.setDeckStatus(9); //wie bekomme ich das in die GUI?
                deckDataSource.updateDeckStatus(deck.getCardID(), 9);
            }
        }

        if (!isGroupOwner){
            if (points.getPointsplayer2() == 0){
                turnpoints = 3;

            }
            else if (points.getPointsplayer2() <33){
                turnpoints = 2;
            }
            else {
                turnpoints = 1;

            }
        }
        else {
            if (points.getPointsplayer1() == 0){
                turnpoints = 3;

            }
            else if (points.getPointsplayer1() <33){
                turnpoints = 2;
            }
            else {
                turnpoints = 1;

            }
        }

        points.setMoves(20);
        roundPointsDataSource.updateMoves(20);
        networkDisplay.displayUserInformation(MessageHelper.TURNEDRECEIVED);
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
     * play a card (send it to the opposite player and save it to the database)
     * @param cardID contains the id of the played card
     * @return true if successful, false if it was not my turn or the end of the round has already been reached
     */
    public boolean playCard(int cardID) {

        //ich bin dran und und schlussphase
        roundPointsDataSource.open();
        points = roundPointsDataSource.getCurrentRoundPointsObject();
        Deck wanttoplaycard = new Deck();

        if (!justplayed2040.equals("")){
            for (Deck deck : this.getAllDecks()){
                if (deck.getCardID() == cardID){
                    wanttoplaycard = deck;
                    break;
                }
            }

            if (checkFor2040(wanttoplaycard, justplayed2040)) {
                //zurÃ¼cksetzen
                justplayed2040 = "";
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

    public void receivePlayCard(int card) {
        List<Deck> allDecks = getAllDecks();

        for (Deck deck : allDecks) {
            if (deck.getDeckStatus() == 1 && deck.getCardID() == card)
                updateCard(card, 5);
            else if (deck.getDeckStatus() == 2 && deck.getCardID() == card)
                updateCard(card, 6);
        }

        setMyTurn(true);

        networkDisplay.displayUserInformation(MessageHelper.YOURTURN);

        compareCards();
    }

    /**
     * called, when the player successfully used the sight joker with the light sensor
     */
    public void sightJoker() {
        roundPointsDataSource.open();
        RoundPoints roundPoints = roundPointsDataSource.getCurrentRoundPointsObject();

        if (isGroupOwner) {
            roundPoints.setSightJokerPlayer1(1);
        } else {
            roundPoints.setSightJokerPlayer2(1);
        }

        sightJokerUsed = true;
        roundPointsDataSource.updateJoker(roundPoints);

        networkManager.sendSightJoker();
    }

    /**
     * called, when the player receives the information that the opposite player used the sight joker
     */
    public void receiveSightJoker() {
        roundPointsDataSource.open();
        RoundPoints roundPoints = roundPointsDataSource.getCurrentRoundPointsObject();
        sightJokerReceived = true;

        if (isGroupOwner) {
            roundPoints.setSightJokerPlayer2(1);
        } else {
            roundPoints.setSightJokerPlayer1(1);
        }
        roundPointsDataSource.updateJoker(roundPoints);
    }

    /**
     * called, when the player used the parry sight joker
     * @param success true, if the player was right (the other one used the sight joker), else false
     */
    public void parrySightJoker(boolean success) {
        roundPointsDataSource.open();
        RoundPoints roundPoints = roundPointsDataSource.getCurrentRoundPointsObject();
        String message = "";

        if (isGroupOwner && success) {
            roundPoints.setParrySightJokerPlayer1(1);
            roundPoints.setPointsplayer1(10);
            message = MessageHelper.PARRYSIGHTJOKER_SUCCESS_WON;
        } else if (!isGroupOwner && success) {
            roundPoints.setParrySightJokerPlayer2(1);
            roundPoints.setPointsplayer2(10);
            message = MessageHelper.PARRYSIGHTJOKER_SUCCESS_WON;
        } else if (isGroupOwner && !success) {
            roundPoints.setPointsplayer2(10);
            message = MessageHelper.PARRYSIGHTJOKER_FAIL_lOST;
        } else if (!isGroupOwner && !success) {
            roundPoints.setPointsplayer1(10);
            message = MessageHelper.PARRYSIGHTJOKER_FAIL_lOST;
        }

        roundPointsDataSource.saveRoundPoints(roundPoints);
        roundPointsDataSource.updateJoker(roundPoints);

        networkManager.sendParrySightJoker();
        sightJokerReceived = false;

        networkDisplay.displayUserInformation(message);
    }

    /**
     * called when the player gets the information that the other player used the parry sight joker
     */
    public void receiveParrySightJoker() {
        roundPointsDataSource.open();
        RoundPoints roundPoints = roundPointsDataSource.getCurrentRoundPointsObject();
        String message = "";

        if ((isGroupOwner && getPlayedCardPlayer1() != null && sightJokerUsed) || (!isGroupOwner && getPlayedCardPlayer2() != null && sightJokerUsed)) {
            if (isGroupOwner) {
                roundPoints.setParrySightJokerPlayer2(1);
                roundPoints.setPointsplayer2(10);
            } else {
                roundPoints.setParrySightJokerPlayer1(1);
                roundPoints.setPointsplayer1(10);
            }

            message = MessageHelper.PARRYSIGHTJOKER_SUCCESS_lOST;

        } else {
            if (isGroupOwner) {
                roundPoints.setPointsplayer1(10);
            } else {
                roundPoints.setPointsplayer2(10);
            }

            message = MessageHelper.PARRYSIGHTJOKER_FAIL_WON;
        }

        roundPointsDataSource.saveRoundPoints(roundPoints);
        roundPointsDataSource.updateJoker(roundPoints);

        networkDisplay.displayUserInformation(message);
    }

    public boolean cardExchange(int cardID_A){


        boolean checkTausch = false;
        Deck wanttoexchange = new Deck();
        Deck getforexchange = new Deck();
        points = roundPointsDataSource.getCurrentRoundPointsObject();
        int alreadyPlayedJoker;

        if (myTurn) {

            if (isGroupOwner){
                alreadyPlayedJoker = points.getCardExchangeJokerPlayer1();
            }
            else {
                alreadyPlayedJoker = points.getCardExchangeJokerPlayer2();
            }

            if (alreadyPlayedJoker == 0) {

                checkTausch = true;
                int size_card_on_hand_opponent = this.getCardsOnHandOpponent().size();
                int random_list_index = new Random().nextInt(size_card_on_hand_opponent);

                //sucht passendes Deck Objekt fÃ¼r zu tauschende KartenID
                for (Deck deck : this.getAllDecks()) {
                    if (deck.getCardID() == cardID_A) {
                        wanttoexchange = deck;
                        break;
                    }
                }

                getforexchange = this.getCardsOnHandOpponent().get(random_list_index);

                //Status fÃ¼r wantoexchange wird upgedatet
                for (Deck deck : this.getAllDecks()) {
                    if (deck.getCardID() == cardID_A) {
                        if (isGroupOwner) {
                            deck.setDeckStatus(2); //wie bekomme ich das in die GUI?
                            deckDataSource.updateDeckStatus(deck.getCardID(), 2);
                        } else {
                            deck.setDeckStatus(1);
                            deckDataSource.updateDeckStatus(deck.getCardID(), 1);
                        }
                    }
                }

                //Status fÃ¼r getforexchange wird upgedatet
                for (Deck deck : this.getAllDecks()) {
                    if (deck.getCardID() == getforexchange.getCardID()) {
                        if (isGroupOwner) {
                            deck.setDeckStatus(1); //wie bekomme ich das in die GUI?
                            deckDataSource.updateDeckStatus(deck.getCardID(), 1);
                        } else {
                            deck.setDeckStatus(2);
                            deckDataSource.updateDeckStatus(deck.getCardID(), 2);
                        }
                    }
                }

                //update Status Joker bereis gespielt
                if (isGroupOwner) {
                    points.setCardExchangeJokerPlayer1(1);
                    this.roundPointsDataSource.updateJoker(points);
                } else {
                    points.setCardExchangeJokerPlayer2(1);
                    this.roundPointsDataSource.updateJoker(points);
                }
                //Sende Karten ID zum anderen Spieler
                networkDisplay.displayUserInformation(MessageHelper.CARD_EXCHANGE);
                networkManager.sendCardExchange((int)wanttoexchange.getCardID(), (int) getforexchange.getCardID());

            }
        }

        return checkTausch;
    }

    public void receiveCardExchange(int cardID_A, int cardID_B){

        points = roundPointsDataSource.getCurrentRoundPointsObject();

        //Status fÃ¼r wantoexchange wird upgedatet
        for (Deck deck : this.getAllDecks()) {
            if (deck.getCardID() == cardID_A) {
                if (deck.getDeckStatus() == 1) {
                    deck.setDeckStatus(2); //wie bekomme ich das in die GUI?
                    deckDataSource.updateDeckStatus(deck.getCardID(), 2);
                } else {
                    deck.setDeckStatus(1);
                    deckDataSource.updateDeckStatus(deck.getCardID(), 1);
                }
            }
        }

        //Status fÃ¼r gettoexchange wird upgedatet
        for (Deck deck : this.getAllDecks()) {
            if (deck.getCardID() == cardID_B) {
                if (deck.getDeckStatus() == 1) {
                    deck.setDeckStatus(2); //wie bekomme ich das in die GUI?
                    deckDataSource.updateDeckStatus(deck.getCardID(), 2);
                } else {
                    deck.setDeckStatus(1);
                    deckDataSource.updateDeckStatus(deck.getCardID(), 1);
                }
            }
        }

        //update Status Joker bereis gespielt
        if (isGroupOwner) {
            points.setCardExchangeJokerPlayer2(1);
            this.roundPointsDataSource.updateJoker(points);
        } else {
            points.setCardExchangeJokerPlayer1(1);
            this.roundPointsDataSource.updateJoker(points);
        }

        networkDisplay.displayUserInformation(MessageHelper.CARD_EXCHANGE_RECEIVED);
    }

    //endregion

    //region Stich

    /**
     * checks, if 20 or 40 can be played with the given card
     * @param wantToPlay the card that would be played
     * @param suit the suit that would be used
     * @return true, if possible, else false
     */
    public boolean checkFor2040(Deck wantToPlay, String suit){

        if (wantToPlay.getCardSuit().equals(suit)){
            if (wantToPlay.getCardValue() == 4 || wantToPlay.getCardValue() == 3)
                return true;
        }
        return false;
    }

    /**
     * checks, if farb or stuchzwang is necessary with the given cards
     * @param playedCard the card that is already on the table
     * @param wantToPlay the card that a player would play
     * @return true, if it is possible to play this card, else false
     */
    public boolean checkForFarbStuchzwang (Deck playedCard, Deck wantToPlay){

        boolean farbe = false;
        boolean highercard = false;
        boolean trumpOnHand = false;
        List<Deck> tempcardsToPlay = new ArrayList<>();
        List<Deck> cardsToPlay = new ArrayList<>();

        //check ob Karte von gleicher Farbe, wenn ja wird es der List cardsToPlay hinzugefÃƒÂ¼gt
        for (Deck deck : this.getCardsOnHand()) {
            if (deck.getCardSuit().equals(playedCard.getCardSuit())) {
                farbe = true;
                tempcardsToPlay.add(deck);
            }
        }


        //check wenn gleiche Farbe vorhanden auf hÃƒÂ¶here Karte
        if (farbe){
            for (Deck deck : tempcardsToPlay){
                if (deck.getCardValue() > playedCard.getCardValue()){
                    highercard = true;
                    break;
                }
            }
        }
        //wenn gleiche Farbe und HÃƒÂ¶hre Karte vorhanden lÃƒÂ¶sche niedrigere Karten
        if (highercard){
            for (Deck deck : tempcardsToPlay){
                if (deck.getCardValue() > playedCard.getCardValue()){
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
                if (deck.getCardID() == wantToPlay.getCardID()) {
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
     * update the status of a given card
     * @param cardID contains the id of the desired card
     * @param status contains the new status
     */
    public void updateCard(int cardID, int status) {
        deckDataSource.updateDeckStatus(cardID, status);

        for (Deck deck : this.deckDataSource.getAllDeck()) {
            if (deck.getCardID() == cardID) {
                deck.setDeckStatus(status);
            }
        }
    }

    /**
     * compares the two played cards and decides which player gets the appropriate round points
     */
    public void compareCards() {
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

            sightJokerReceived = false; //if both cards are set, the parry sight joker cannot be used any more
            sightJokerUsed = false; //if both cards are set, the sight joker usage vanishes

            if (player1Won && isGroupOwner) {
                myTurn = true;
                lastStuch = true;
                getNextFreeCard(1);
                getNextFreeCard(2);
                networkDisplay.displayUserInformation(MessageHelper.WON);
            } else if (player2Won && !isGroupOwner) {
                myTurn = true;
                lastStuch = true;
                getNextFreeCard(2);
                getNextFreeCard(1);
                networkDisplay.displayUserInformation(MessageHelper.WON);
            } else if (player1Won && !isGroupOwner) {
                myTurn = false;
                lastStuch = false;
                networkManager.waitForCard(false);
                getNextFreeCard(1);
                getNextFreeCard(2);
                networkDisplay.displayUserInformation(MessageHelper.LOST);
            } else if (player2Won && isGroupOwner) {
                myTurn = false;
                lastStuch = false;
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
    }

    public boolean checkFor66() {
        openDatabases();
        RoundPoints rp2 = roundPointsDataSource.getCurrentRoundPointsObject();

        if (rp2.getMoves() <=10 || (rp2.getMoves() >=20 && rp2.getMoves()<25)) {
            if (rp2.getPointsplayer1() >= 66) {

                if (rp2.getPointsplayer2() >= 33) {
                    // 1 Punkt
                    game.updateGamePoints(1, 0);
                } else if (rp2.getPointsplayer2() > 0 && rp2.getPointsplayer2() < 33) {
                    // 2 Punkte
                    game.updateGamePoints(2, 0);
                } else {
                    // 3 Punkte
                    game.updateGamePoints(3, 0);
                }

                if (isGroupOwner){
                    roundWon = true;
                }
                else{
                    roundWon = false;
                }


                return true;

            } else if (rp2.getPointsplayer2() >= 66) {
                if (rp2.getPointsplayer1() >= 33) {
                    // 1 Punkt
                    game.updateGamePoints(0, 1);
                } else if (rp2.getPointsplayer1() > 0 && rp2.getPointsplayer1() < 33) {
                    // 2 Punkte
                    game.updateGamePoints(0, 2);
                } else {
                    // 3 Punkte
                    game.updateGamePoints(0, 3);
                }

                if (!isGroupOwner){
                    roundWon = true;
                }
                else{
                    roundWon = false;
                }
                return true;
            }
        }

        if (rp2.getMoves() == 10){
            if (lastStuch == true && isGroupOwner){
                game.updateGamePoints(1, 0);
                roundWon = true;
            }
            else if (lastStuch == true && !isGroupOwner){
                game.updateGamePoints(0, 1);
                roundWon = true;
            }
            else if (lastStuch == false && isGroupOwner){
                game.updateGamePoints(0, 1);
                roundWon = false;
            }
            else if (lastStuch == false && !isGroupOwner){
                game.updateGamePoints(1, 1);
                roundWon = false;

            }
        }

        if (rp2.getMoves() == 25){
            if (hasturned == true && isGroupOwner){
                if(rp2.getPointsplayer1() >=66){
                    game.updateGamePoints(turnpoints, 0);
                    roundWon = true;
                }
                else if (turnpoints == 3){
                    game.updateGamePoints(0, 3);
                    roundWon = false;
                }
                else {
                    game.updateGamePoints(0, 2);
                    roundWon = false;
                }
            }
            else if (hasturned == true && !isGroupOwner){
                if(rp2.getPointsplayer2() >=66){
                    game.updateGamePoints(0, turnpoints);
                    roundWon = true;
                }
                else if (turnpoints == 3){
                    game.updateGamePoints(3, 0);
                    roundWon = false;
                }
                else {
                    game.updateGamePoints(2, 0);
                    roundWon = false;
                }
            }
            else if (hasturned == false && isGroupOwner){
                if(rp2.getPointsplayer2() >=66){
                    game.updateGamePoints(0, turnpoints);
                    roundWon = false;
                }
                else if (turnpoints == 3){
                    game.updateGamePoints(3, 0);
                    roundWon = true;
                }
                else {
                    game.updateGamePoints(2, 0);
                    roundWon = true;
                }
            }
            else if (hasturned == false && !isGroupOwner){
                if(rp2.getPointsplayer1() >=66){
                    roundWon = false;
                    game.updateGamePoints(turnpoints, 0);
                }
                else if (turnpoints == 3){
                    game.updateGamePoints(0, 3);
                    roundWon = true;
                }
                else {
                    game.updateGamePoints(0, 2);
                    roundWon = true;
                }

            }
            return true;
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

    public int numberPlayedCards(){
        int number = 0;
        if (getPlayedCards().size() == 1)
            number = 1;
        else if (getPlayedCards().size()==2){
            number = 2;
        }

        return number;

    }

    public boolean getMyTurnInCurrentMove() {
        if (isGroupOwner && getPlayedCardPlayer2() != null) {
            return true;
        } else if (!isGroupOwner && getPlayedCardPlayer1() != null) {
            return true;
        }

        return false;
    }
  
    //endregion
}

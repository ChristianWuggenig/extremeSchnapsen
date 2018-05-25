package cardfactory.com.extremeschnapsen;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Round {

    private List<Card> allCards;
    private List<Deck> currentDeck;
    private DeckDataSource deckDataSource;
    private CardDataSource cardDataSource;
    private RoundPointsDataSource roundPointsDataSource;
    //Game Deklaration -> Instanz unten
    Game game_round;

    private RoundPoints points;

    private NetworkManager networkManager;

    private boolean myTurn;
    private boolean isGroupOwner;
    //ist jetzt in RoundPointsDataSource
    //private int moves;

    private String trump;

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

        deckDataSource.deleteDeckTable();

        networkManager = NetworkManager.getInstance(context, (INetworkDisplay) context);

        game_round = new Game (context, true);

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

    public void exchangeTrump(){
        RoundPoints rp = new RoundPoints(1,0,0,0);
        rp = roundPointsDataSource.getCurrentRoundPointsObject();
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

                    this.deckDataSource.updateDeckStatus(deck.getCardID(), 3);
                    this.currentDeck = this.deckDataSource.getAllDeck();
                    break;
                }
            }
        }
    }

    public void receiveExchangeTrump(){
        RoundPoints rp = new RoundPoints(1,0,0,0);
        rp = roundPointsDataSource.getCurrentRoundPointsObject();
        //if (myTurn && rp.getTrumpExchanged() == 0) {
            for (Deck deck : this.getAllCards()) {
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

        //nicht this.currentDeck sondern aktuelles Deck in der Datenbank
        for (Deck deck : this.getAllCards()) {
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
        this.roundPointsDataSource.increaseMoves();
    }

    public void startServer() {
        networkManager.startHttpServer(currentDeck);
    }

    public void startClient() {
        networkManager.startHttpClient();
    }

    public boolean playCard(int cardID) {

        //ich bin dran und und schlussphase
        points = roundPointsDataSource.getCurrentRoundPointsObject();

        if (points.getMoves()<5) {

            if (myTurn) {

                for (Deck deck : this.getAllCards()) {
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
                //this.increaseMoves();

                return true;
            }
        }

        if (points.getMoves()>=5) {
            if (myTurn) {

                List<Deck> playedCards = getPlayedCards();
                if(playedCards.size() == 1) {

                    for (Deck deck : this.getAllCards()) {
                        if (deck.getCardID() == cardID) {

                        }
                    }
                }
                networkManager.sendCard(cardID);
                myTurn = false;
                //this.increaseMoves();

                return true;
            }

        }
        return false;
    }

    public boolean checkForFarbStuchzwang (Deck playedcard, Deck wanttoplay){
        boolean farbe = false;
        boolean trumpOnHand = false;
        List<Deck> cardsToPlay = new ArrayList<>();

        //check ob Karte von gleicher Farbe, wenn ja wird es des List cardsToPlay hinzugefügt
        for (Deck deck : this.getCardsOnHand()) {
            if (deck.getCardSuit().equals(playedcard.getCardSuit())) {
                farbe = true;
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


    public void updateCard(int cardID, int status) {
        deckDataSource.updateDeckStatus(cardID, status);

        for (Deck deck : currentDeck) {
            if (deck.getCardID() == cardID) {
                deck.setDeckStatus(status);
            }
        }
    }

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
            //wenn noch keinen Stuch und 20/40 angesagt; und jetzt ein Stuch -> so werden
            //die "versteckten Punkte" jetzt gutgeschrieben
            HiddenPointsToRealPoints();

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
                game_round.gpds.getAllGamePoints();
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
                game_round.gpds.getAllGamePoints();
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
        else
            i=2;

        //20er 40er
        if (i == 2 || i ==4) {
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
    }

    //Der Button Herz, Pik, Karo oder Kreuz wird gedrückt um 20/40 anzusagen
    public String check2040(String farbe){
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
        //als Rückgabeparameter für Message an anderen Spieler
        return check2040;
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

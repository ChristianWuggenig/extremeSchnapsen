package cardfactory.com.extremeschnapsen.gameengine;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import cardfactory.com.extremeschnapsen.database.CardDataSource;
import cardfactory.com.extremeschnapsen.database.DeckDataSource;
import cardfactory.com.extremeschnapsen.database.GamePointsDataSource;
import cardfactory.com.extremeschnapsen.database.PlayerDataSource;
import cardfactory.com.extremeschnapsen.database.RoundPointsDataSource;
import cardfactory.com.extremeschnapsen.gui.GameActivity;
import cardfactory.com.extremeschnapsen.gui.MessageHelper;
import cardfactory.com.extremeschnapsen.models.Card;
import cardfactory.com.extremeschnapsen.models.Deck;
import cardfactory.com.extremeschnapsen.models.GamePoints;
import cardfactory.com.extremeschnapsen.models.Player;
import cardfactory.com.extremeschnapsen.models.RoundPoints;
import cardfactory.com.extremeschnapsen.networking.INetworkDisplay;
import cardfactory.com.extremeschnapsen.networking.NetworkManager;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class RoundUnitTest {

    /*
    bitte vorher durchlesen:

    die methoden stehen in der testklasse in der gleichen reihenfolge wie in der richtigen round-klasse.
    wenn ihr beim testen einer methode irgendwelche anderen methoden bereits mittestet, dann schreibt das bitte in die liste unten dazu, damit wir nichts doppelt testen
    bitte unbedingt vorm commiten nochmal alle tests laufen lassen, sonst kann es sein dass sie im travis-ci fehlschlagen

    bereits getestet:
        getCardsOnHand
        getCardsOnHand(int player)
        getCardsOnHandOpponent
        getOpenCard
        getPlayedCardPlayer1
        getPlayedCardPlayer2
        getPlayedCards
        getShuffledDeck
        getNextFreeCard
        getAlreadyPlayedCards

        check2040
        receiveCheck2040
        sightJoker
        receiveSightJoker
        parrySightJoker
        receiveParrySightJoker

        getMyTurnInCurrentMove
     */

    Round round;
    Context context;
    CardDataSource cardDataSource;
    DeckDataSource deckDataSource;
    RoundPointsDataSource roundPointsDataSource;
    RoundPoints roundPoints;
    List<Deck> currentDeck;
    List<Card> allCards;
    Card card1;
    Deck deck1;
    Deck deck2;
    Game game;
    Player player;
    PlayerDataSource playerDataSource;
    GamePointsDataSource gpds;
    GamePoints gp;


    INetworkDisplay networkDisplay;
    NetworkManager networkManager;

    @Before
    public void init() {
        context = mock(GameActivity.class);
        round = new Round();

        game = mock(Game.class);
        round.setGame(game);


        deck1 = mock(Deck.class);
        deck2 = mock(Deck.class);

        currentDeck = new ArrayList<>();
        currentDeck.add(deck1);
        currentDeck.add(deck2);

        card1 = mock(Card.class);
        allCards = new ArrayList<>();
        allCards.add(card1);

        cardDataSource = mock(CardDataSource.class);
        when(cardDataSource.getAllCards()).thenReturn(allCards);
        round.setCardDataSource(cardDataSource);


        deckDataSource = mock(DeckDataSource.class);
        when(deckDataSource.getAllDeck()).thenReturn(currentDeck);

        roundPointsDataSource = mock(RoundPointsDataSource.class);
        roundPoints = mock(RoundPoints.class);
        when(roundPoints.getPointsplayer1()).thenReturn(0);
        when(roundPoints.getPointsplayer2()).thenReturn(0);
        when(roundPointsDataSource.getCurrentRoundPointsObject()).thenReturn(roundPoints);
        when(roundPoints.getMoves()).thenReturn(1);


        player = mock(Player.class);
        playerDataSource = mock(PlayerDataSource.class);
        round.setPlayerDataSource(playerDataSource);

        /*
        gpds = mock(GamePointsDataSource.class);
        gp = mock(GamePoints.class);
        when(gpds.getCurrentGamePointsObject()).thenReturn(gp);
        game.setGpds(gpds);
*/

        round.setDeckDataSource(deckDataSource);
        round.setRoundPointsDataSource(roundPointsDataSource);



        networkDisplay = mock(INetworkDisplay.class);
        round.setNetworkDisplay(networkDisplay);

        networkManager = mock(NetworkManager.class);
        round.setNetworkManager(networkManager);
    }

    //region Deck & Database

    @Test
    public void testGetCardsOnHandWithPlayer1() {
        when(deck1.getDeckStatus()).thenReturn(1);

        List<Deck> onHand = round.getCardsOnHand(1);

        assertEquals(deck1, onHand.get(0));
    }

    @Test
    public void testGetCardsOnHandWithPlayer2() {
        when(deck1.getDeckStatus()).thenReturn(2);

        List<Deck> onHand = round.getCardsOnHand(2);

        assertEquals(deck1, onHand.get(0));
    }

    @Test
    public void testGetCardsOnHandOpponentWithGroupOwner() {
        round.setGroupOwner(true);
        when(deck1.getDeckStatus()).thenReturn(2);

        List<Deck> onHand = round.getCardsOnHandOpponent();

        assertEquals(deck1, onHand.get(0));
    }

    @Test
    public void testGetCardsOnHandOpponentWithoutGroupOwner() {
        round.setGroupOwner(false);
        when(deck1.getDeckStatus()).thenReturn(1);

        List<Deck> onHand = round.getCardsOnHandOpponent();

        assertEquals(deck1, onHand.get(0));
    }

    @Test
    public void testGetOpenCard() {
        when(deck1.getDeckStatus()).thenReturn(3);

        Deck open = round.getOpenCard();

        assertEquals(deck1, open);
    }

    @Test
    public void testGetPlayedCardPlayer1() {
        when(deck1.getDeckStatus()).thenReturn(5);

        Deck played = round.getPlayedCardPlayer1();

        assertEquals(deck1, played);
    }

    @Test
    public void testGetPlayedCardPlayer2() {
        when(deck1.getDeckStatus()).thenReturn(6);

        Deck played = round.getPlayedCardPlayer2();

        assertEquals(deck1, played);
    }

    @Test
    public void testGetPlayedCardsWithGroupOwner() {
        round.setGroupOwner(true);

        when(deck1.getDeckStatus()).thenReturn(5);
        when(deck2.getDeckStatus()).thenReturn(6);

        List<Deck> playedCards = round.getPlayedCards();

        assertEquals(deck1, playedCards.get(0));
        assertEquals(deck2, playedCards.get(1));
    }

    @Test
    public void testGetPlayedCardsWithoutGroupOwner() {
        round.setGroupOwner(false);

        when(deck1.getDeckStatus()).thenReturn(5);
        when(deck2.getDeckStatus()).thenReturn(6);

        List<Deck> playedCards = round.getPlayedCards();

        assertEquals(deck1, playedCards.get(0));
        assertEquals(deck2, playedCards.get(1));
    }

    @Test
    public void testGetShuffledDeck() {
        int[] shuffledDeckIDs = new int[1];

        round.getShuffledDeck(shuffledDeckIDs);

        verify(cardDataSource).getAllCards();
        verify(deckDataSource).receiveShuffeldDeck(shuffledDeckIDs, allCards);
    }

    @Test
    public void testGetNextFreeCardWithPlayer1() {
        when(deck1.getDeckStatus()).thenReturn(4);
        when(deck1.getCardID()).thenReturn(1l);

        round.getNextFreeCard(1);

        verify(deckDataSource).updateDeckStatus(1, 1);
    }

    @Test
    public void testGetNextFreeCardWithPlayer2WithOpenCard() {
        when(deck1.getDeckStatus()).thenReturn(3);
        when(deck1.getCardID()).thenReturn(1l);

        round.getNextFreeCard(2);

        verify(deckDataSource).updateDeckStatus(1, 2);
    }

    @Test
    public void testGetAlreadyPlayedCardsWithPlayer1() {
        round.setGroupOwner(true);
        when(deck1.getDeckStatus()).thenReturn(7);

        List<Deck> alreadyPlayed = round.getAlreadyPlayedCards();

        assertEquals(deck1, alreadyPlayed.get(0));
    }

    @Test
    public void testGetAlreadyPlayedCardsWithPlayer2() {
        round.setGroupOwner(false);
        when(deck1.getDeckStatus()).thenReturn(8);

        List<Deck> alreadyPlayed = round.getAlreadyPlayedCards();

        assertEquals(deck1, alreadyPlayed.get(0));
    }

    //endregion

    //region Actions

    private void prepareCheck2040() {
        round.setMyTurn(true);
        round.setJustplayed2040("");
        when(deck1.getCardValue()).thenReturn(3);
        when(deck2.getCardValue()).thenReturn(4);
    }

    @Test
    public void testCheck2040WithKaroWithGroupOwnerWith40WithoutStuch() {
        prepareCheck2040();
        round.setGroupOwner(true);
        when(deck1.getDeckTrump()).thenReturn(1);
        when(deck2.getDeckTrump()).thenReturn(1);
        when(deck1.getDeckStatus()).thenReturn(1);
        when(deck2.getDeckStatus()).thenReturn(1);
        when(deck1.getCardSuit()).thenReturn("karo");
        when(deck2.getCardSuit()).thenReturn("karo");

        assertEquals("karo", round.check2040("karo"));
    }

    @Test
    public void testCheck2040WithKaroWithGroupOwnerWith20WithoutStuch() {
        prepareCheck2040();
        round.setGroupOwner(true);
        when(deck1.getDeckTrump()).thenReturn(0);
        when(deck2.getDeckTrump()).thenReturn(0);
        when(deck1.getDeckStatus()).thenReturn(1);
        when(deck2.getDeckStatus()).thenReturn(1);
        when(deck1.getCardSuit()).thenReturn("karo");
        when(deck2.getCardSuit()).thenReturn("karo");

        assertEquals("karo", round.check2040("karo"));
    }

    @Test
    public void testCheck2040WithKaroWithoutGroupOwnerWith40WithoutStuch() {
        prepareCheck2040();
        round.setGroupOwner(false);
        when(deck1.getDeckStatus()).thenReturn(2);
        when(deck2.getDeckStatus()).thenReturn(2);
        when(deck1.getDeckTrump()).thenReturn(1);
        when(deck2.getDeckTrump()).thenReturn(1);
        when(deck1.getCardSuit()).thenReturn("karo");
        when(deck2.getCardSuit()).thenReturn("karo");

        assertEquals("karo", round.check2040("karo"));
    }

    @Test
    public void testCheck2040WithKaroWithoutGroupOwnerWith20WithoutStuch() {
        prepareCheck2040();
        round.setGroupOwner(false);
        when(deck1.getDeckStatus()).thenReturn(2);
        when(deck2.getDeckStatus()).thenReturn(2);
        when(deck1.getDeckTrump()).thenReturn(0);
        when(deck2.getDeckTrump()).thenReturn(0);
        when(deck1.getCardSuit()).thenReturn("karo");
        when(deck2.getCardSuit()).thenReturn("karo");

        assertEquals("karo", round.check2040("karo"));
    }

    @Test
    public void testCheck2040WithHerzWithoutGroupOwnerWith40WithoutStuch() {
        prepareCheck2040();
        round.setGroupOwner(false);
        when(deck1.getDeckStatus()).thenReturn(2);
        when(deck2.getDeckStatus()).thenReturn(2);
        when(deck1.getDeckTrump()).thenReturn(1);
        when(deck2.getDeckTrump()).thenReturn(1);
        when(deck1.getCardSuit()).thenReturn("herz");
        when(deck2.getCardSuit()).thenReturn("herz");

        assertEquals("herz", round.check2040("herz"));
    }

    @Test
    public void testCheck2040WithHerzWithoutGroupOwnerWith20WithoutStuch() {
        prepareCheck2040();
        round.setGroupOwner(false);
        when(deck1.getDeckStatus()).thenReturn(2);
        when(deck2.getDeckStatus()).thenReturn(2);
        when(deck1.getDeckTrump()).thenReturn(0);
        when(deck2.getDeckTrump()).thenReturn(0);
        when(deck1.getCardSuit()).thenReturn("herz");
        when(deck2.getCardSuit()).thenReturn("herz");

        assertEquals("herz", round.check2040("herz"));
    }

    @Test
    public void testCheck2040WithPikWithGroupOwnerWith40WithoutStuch() {
        prepareCheck2040();
        round.setGroupOwner(true);
        when(deck1.getDeckTrump()).thenReturn(1);
        when(deck2.getDeckTrump()).thenReturn(1);
        when(deck1.getDeckStatus()).thenReturn(1);
        when(deck2.getDeckStatus()).thenReturn(1);
        when(deck1.getCardSuit()).thenReturn("pik");
        when(deck2.getCardSuit()).thenReturn("pik");

        assertEquals("pik", round.check2040("pik"));
    }

    @Test
    public void testCheck2040WithPikWithGroupOwnerWith20WithoutStuch() {
        prepareCheck2040();
        round.setGroupOwner(true);
        when(deck1.getDeckTrump()).thenReturn(0);
        when(deck2.getDeckTrump()).thenReturn(0);
        when(deck1.getDeckStatus()).thenReturn(1);
        when(deck2.getDeckStatus()).thenReturn(1);
        when(deck1.getCardSuit()).thenReturn("pik");
        when(deck2.getCardSuit()).thenReturn("pik");

        assertEquals("pik", round.check2040("pik"));
    }

    @Test
    public void testCheck2040WithKreuzWithGroupOwnerWith40WithStuch() {
        prepareCheck2040();
        round.setGroupOwner(true);
        when(deck1.getDeckTrump()).thenReturn(1);
        when(deck2.getDeckTrump()).thenReturn(1);
        when(deck1.getDeckStatus()).thenReturn(1);
        when(deck2.getDeckStatus()).thenReturn(1);
        when(deck1.getCardSuit()).thenReturn("kreuz");
        when(deck2.getCardSuit()).thenReturn("kreuz");
        when(roundPoints.getPointsplayer1()).thenReturn(10);

        assertEquals("kreuz", round.check2040("kreuz"));
    }

    @Test
    public void testCheck2040WithKreuzWithoutGroupOwnerWith20WithStuch() {
        prepareCheck2040();
        round.setGroupOwner(false);
        when(deck1.getDeckTrump()).thenReturn(0);
        when(deck2.getDeckTrump()).thenReturn(0);
        when(deck1.getDeckStatus()).thenReturn(2);
        when(deck2.getDeckStatus()).thenReturn(2);
        when(deck1.getCardSuit()).thenReturn("kreuz");
        when(deck2.getCardSuit()).thenReturn("kreuz");
        when(roundPoints.getPointsplayer2()).thenReturn(10);

        assertEquals("kreuz", round.check2040("kreuz"));
    }

    @Test
    public void testCheck2040Empty() {
        assertEquals("", round.check2040(""));
    }

    @Test
    public void testReceiveCheck2040WithKreuzWithGroupOwnerWith20() {
        prepareCheck2040();
        round.setGroupOwner(true);
        round.setNetworkDisplay(networkDisplay);
        when(deckDataSource.getTrump()).thenReturn("pik");

        round.receiveCheck2040("kreuz");

        verify(deckDataSource).getTrump();
        verify(networkDisplay).displayUserInformation(MessageHelper.TWENTYRECEIVED);

        assertTrue(true); //if the statements above don't fail, the test is successful
    }

    @Test
    public void testReceiveCheck2040WithPikWithoutGroupOwnerWith40() {
        prepareCheck2040();
        round.setGroupOwner(false);
        round.setNetworkDisplay(networkDisplay);
        when(deckDataSource.getTrump()).thenReturn("pik");

        round.receiveCheck2040("pik");

        verify(deckDataSource).getTrump();
        verify(networkDisplay).displayUserInformation(MessageHelper.FORTYRECEIVED);
    }








    @Test
    public void testSightJokerWithGroupOwner() {
        round.setGroupOwner(true);

        round.sightJoker();

        verify(roundPoints).setSightJokerPlayer1(1);
        verify(roundPointsDataSource).updateJoker(roundPoints);
        verify(networkManager).sendSightJoker();
    }

    @Test
    public void testSightJokerWithoutGroupOwner() {
        round.setGroupOwner(false);

        round.sightJoker();

        verify(roundPoints).setSightJokerPlayer2(1);
        verify(roundPointsDataSource).updateJoker(roundPoints);
        verify(networkManager).sendSightJoker();
    }

    @Test
    public void testReceiveSightJokerWithGroupOwner() {
        round.setGroupOwner(true);

        round.receiveSightJoker();

        verify(roundPoints).setSightJokerPlayer2(1);
        verify(roundPointsDataSource).updateJoker(roundPoints);
    }

    @Test
    public void testReceiveSightJokerWithoutGroupOwner() {
        round.setGroupOwner(false);

        round.receiveSightJoker();

        verify(roundPoints).setSightJokerPlayer1(1);
        verify(roundPointsDataSource).updateJoker(roundPoints);
    }

    @Test
    public void testParrySightJokerWithGroupOwnerSuccess() {
        round.setGroupOwner(true);

        round.parrySightJoker(true);

        verify(roundPoints).setParrySightJokerPlayer1(1);
        verify(roundPoints).setPointsplayer1(10);
        verify(roundPointsDataSource).saveRoundPoints(roundPoints);
        verify(roundPointsDataSource).updateJoker(roundPoints);
        verify(networkManager).sendParrySightJoker();
        verify(networkDisplay).displayUserInformation(MessageHelper.PARRYSIGHTJOKER_SUCCESS_WON);
    }

    @Test
    public void testParrySightJokerWithGroupOwnerLost() {
        round.setGroupOwner(true);

        round.parrySightJoker(false);

        verify(roundPoints).setPointsplayer2(10);
        verify(roundPointsDataSource).saveRoundPoints(roundPoints);
        verify(roundPointsDataSource).updateJoker(roundPoints);
        verify(networkManager).sendParrySightJoker();
        verify(networkDisplay).displayUserInformation(MessageHelper.PARRYSIGHTJOKER_FAIL_lOST);
    }

    @Test
    public void testParrySightJokerWithoutGroupOwnerSuccess() {
        round.setGroupOwner(false);

        round.parrySightJoker(true);

        verify(roundPoints).setParrySightJokerPlayer2(1);
        verify(roundPoints).setPointsplayer2(10);
        verify(roundPointsDataSource).saveRoundPoints(roundPoints);
        verify(roundPointsDataSource).updateJoker(roundPoints);
        verify(networkManager).sendParrySightJoker();
        verify(networkDisplay).displayUserInformation(MessageHelper.PARRYSIGHTJOKER_SUCCESS_WON);
    }

    @Test
    public void testParrySightJokerWithoutGroupOwnerLost() {
        round.setGroupOwner(false);

        round.parrySightJoker(false);

        verify(roundPoints).setPointsplayer1(10);
        verify(roundPointsDataSource).saveRoundPoints(roundPoints);
        verify(roundPointsDataSource).updateJoker(roundPoints);
        verify(networkManager).sendParrySightJoker();
        verify(networkDisplay).displayUserInformation(MessageHelper.PARRYSIGHTJOKER_FAIL_lOST);
    }

    @Test
    public void testReceiveParrySightJokerWithGroupOwnerWithSightJokerUsed() {
        round.setGroupOwner(true);
        round.setSightJokerUsed(true);

        when(deck1.getDeckStatus()).thenReturn(5);

        round.receiveParrySightJoker();

        verify(roundPoints).setParrySightJokerPlayer2(1);
        verify(roundPoints).setPointsplayer2(10);
        verify(roundPointsDataSource).saveRoundPoints(roundPoints);
        verify(roundPointsDataSource).updateJoker(roundPoints);
        verify(networkDisplay).displayUserInformation(MessageHelper.PARRYSIGHTJOKER_SUCCESS_lOST);
    }

    @Test
    public void testReceiveParrySightJokerWithGroupOwnerWithoutSightJokerUsed() {
        round.setGroupOwner(true);
        round.setSightJokerUsed(false);

        when(deck1.getDeckStatus()).thenReturn(5);

        round.receiveParrySightJoker();

        verify(roundPoints).setPointsplayer1(10);
        verify(roundPointsDataSource).saveRoundPoints(roundPoints);
        verify(roundPointsDataSource).updateJoker(roundPoints);
        verify(networkDisplay).displayUserInformation(MessageHelper.PARRYSIGHTJOKER_FAIL_WON);
    }

    @Test
    public void testReceiveParrySightJokerWithoutGroupOwnerWithSightJokerUsed() {
        round.setGroupOwner(false);
        round.setSightJokerUsed(true);

        when(deck1.getDeckStatus()).thenReturn(6);

        round.receiveParrySightJoker();

        verify(roundPoints).setParrySightJokerPlayer1(1);
        verify(roundPoints).setPointsplayer1(10);
        verify(roundPointsDataSource).saveRoundPoints(roundPoints);
        verify(roundPointsDataSource).updateJoker(roundPoints);
        verify(networkDisplay).displayUserInformation(MessageHelper.PARRYSIGHTJOKER_SUCCESS_lOST);
    }

    @Test
    public void testReceiveParrySightJokerWithoutGroupOwnerWithoutSightJokerUsed() {
        round.setGroupOwner(false);
        round.setSightJokerUsed(false);

        when(deck1.getDeckStatus()).thenReturn(6);

        round.receiveParrySightJoker();

        verify(roundPoints).setPointsplayer2(10);
        verify(roundPointsDataSource).saveRoundPoints(roundPoints);
        verify(roundPointsDataSource).updateJoker(roundPoints);
        verify(networkDisplay).displayUserInformation(MessageHelper.PARRYSIGHTJOKER_FAIL_WON);
    }

    //endregion

    //region Stich

    @Test
    public void testCheckFor2040WithEqualSuitAndEqualValue() {
        when(deck1.getCardSuit()).thenReturn("karo");
        when(deck1.getCardValue()).thenReturn(4);

        assertEquals(true, round.checkFor2040(deck1, "karo"));
    }

    @Test
    public void testCheckFor2040WithNotEqualSuitAndEqualValue() {
        when(deck1.getCardSuit()).thenReturn("karo");
        when(deck1.getCardValue()).thenReturn(4);

        assertEquals(false, round.checkFor2040(deck1, "pik"));
    }

    @Test
    public void testCheckForFarbStuchzwangWithHigherCard() {
        when(deck1.getCardSuit()).thenReturn("pik");
        when(deck1.getCardValue()).thenReturn(4);
        when(deck1.getDeckStatus()).thenReturn(2);
        when(deck1.getCardID()).thenReturn(0l);

        Deck playedCard = mock(Deck.class);
        when(playedCard.getCardSuit()).thenReturn("pik");
        when(playedCard.getCardValue()).thenReturn(3);

        Deck wantToPlay = mock(Deck.class);
        when(wantToPlay.getCardID()).thenReturn(0l);

        assertEquals(true, round.checkForFarbStuchzwang(playedCard, wantToPlay));
    }

    @Test
    public void testCheckForFarbStuchzwangWithoutHigherCard() {
        when(deck1.getCardSuit()).thenReturn("pik");
        when(deck1.getCardValue()).thenReturn(3);
        when(deck1.getDeckStatus()).thenReturn(2);
        when(deck1.getCardID()).thenReturn(0l);

        Deck playedCard = mock(Deck.class);
        when(playedCard.getCardSuit()).thenReturn("pik");
        when(playedCard.getCardValue()).thenReturn(3);

        Deck wantToPlay = mock(Deck.class);
        when(wantToPlay.getCardID()).thenReturn(1l);

        assertEquals(false, round.checkForFarbStuchzwang(playedCard, wantToPlay));
    }

    @Test
    public void testCheckForFarbStuchzwangWithHigherCardWithTrumpOnHand() {
        when(deck1.getCardSuit()).thenReturn("karo");
        when(deck1.getCardValue()).thenReturn(3);
        when(deck1.getDeckStatus()).thenReturn(2);
        when(deck1.getDeckTrump()).thenReturn(1);

        Deck playedCard = mock(Deck.class);
        when(playedCard.getCardSuit()).thenReturn("pik");
        when(playedCard.getCardValue()).thenReturn(4);

        Deck wantToPlay = mock(Deck.class);
        when(wantToPlay.getCardID()).thenReturn(1l);

        assertEquals(false, round.checkForFarbStuchzwang(playedCard, wantToPlay));
    }

    @Test
    public void testCheckForFarbStuchzwangWithoutHigherCardWithTrumpOnHandWithGroupOwner() {
        when(deck1.getCardSuit()).thenReturn("pik");
        when(deck1.getCardValue()).thenReturn(3);
        when(deck1.getDeckStatus()).thenReturn(2);

        Deck playedCard = mock(Deck.class);
        when(playedCard.getCardSuit()).thenReturn("pik");
        when(playedCard.getCardValue()).thenReturn(3);

        Deck wantToPlay = mock(Deck.class);
        when(wantToPlay.getCardID()).thenReturn(1l);

        assertEquals(false, round.checkForFarbStuchzwang(playedCard, wantToPlay));
    }

    @Test
    public void testCheckForFarbStuchzwangWithoutHigherCardWithoutTrumpOnHand() {
        when(deck1.getCardSuit()).thenReturn("pik");
        when(deck1.getCardValue()).thenReturn(3);
        when(deck1.getDeckStatus()).thenReturn(2);
        when(deck1.getDeckTrump()).thenReturn(0);

        Deck playedCard = mock(Deck.class);
        when(playedCard.getCardSuit()).thenReturn("karo");
        when(playedCard.getCardValue()).thenReturn(3);

        Deck wantToPlay = mock(Deck.class);
        when(wantToPlay.getCardID()).thenReturn(1l);

        assertEquals(true, round.checkForFarbStuchzwang(playedCard, wantToPlay));
    }






    @Test
    public void testGetMyTurnInCurrentMovePlayer1Player2NotPlayed() {
        round.setGroupOwner(true);
        assertFalse(round.getMyTurnInCurrentMove());
    }

    @Test
    public void testGetMyTurnInCurrentMovePlayer1Player2Played() {
        when(deck1.getDeckStatus()).thenReturn(6);
        round.setGroupOwner(true);
        assertTrue(round.getMyTurnInCurrentMove());
    }

    @Test
    public void testGetMyTurnInCurrentMovePlayer2Player1NotPlayed() {
        round.setGroupOwner(false);
        assertFalse(round.getMyTurnInCurrentMove());
    }

    @Test
    public void testGetMyTurnInCurrentMovePlayer2Player1Played() {
        when(deck1.getDeckStatus()).thenReturn(5);
        round.setGroupOwner(false);
        assertTrue(round.getMyTurnInCurrentMove());
    }

    //endregion
    @Test
    public void testCheckFor66_P1_66_P2_0() {

        when(roundPoints.getMoves()).thenReturn(1);
        when(roundPoints.getPointsplayer1()).thenReturn(66);
        when(roundPoints.getPointsplayer2()).thenReturn(0);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(3,0);
    }

    @Test
    public void testCheckFor66_P1_66_P2_32() {

        round.setGroupOwner(true);
        when(roundPoints.getMoves()).thenReturn(1);
        when(roundPoints.getPointsplayer1()).thenReturn(66);
        when(roundPoints.getPointsplayer2()).thenReturn(32);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(2,0);
    }

    @Test
    public void testCheckFor66_P1_66_P2_33() {

        when(roundPoints.getMoves()).thenReturn(1);
        when(roundPoints.getPointsplayer1()).thenReturn(66);
        when(roundPoints.getPointsplayer2()).thenReturn(33);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(1,0);
    }

    @Test
    public void testCheckFor66_P2_66_P1_0() {

        when(roundPoints.getMoves()).thenReturn(1);
        when(roundPoints.getPointsplayer2()).thenReturn(66);
        when(roundPoints.getPointsplayer1()).thenReturn(0);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(0,3);
    }

    @Test
    public void testCheckFor66_P2_66_P1_32() {

        round.setGroupOwner(true);
        when(roundPoints.getMoves()).thenReturn(1);
        when(roundPoints.getPointsplayer2()).thenReturn(66);
        when(roundPoints.getPointsplayer1()).thenReturn(32);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(0,2);
    }

    @Test
    public void testCheckFor66_P2_66_P1_33() {

        when(roundPoints.getMoves()).thenReturn(1);
        when(roundPoints.getPointsplayer2()).thenReturn(66);
        when(roundPoints.getPointsplayer1()).thenReturn(33);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(0,1);
    }
    @Test
    public void testCheckFor66_LastStuch_true_GroupOwner_true() {

        round.setGroupOwner(true);
        round.setLastStuch(true);
        when(roundPoints.getMoves()).thenReturn(10);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(1,0);
    }

    @Test
    public void testCheckFor66_LastStuch_true_GroupOwner_false() {

        round.setGroupOwner(false);
        round.setLastStuch(true);
        when(roundPoints.getMoves()).thenReturn(10);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(0,1);
    }

    @Test
    public void testCheckFor66_LastStuch_false_GroupOwner_true() {

        round.setGroupOwner(true);
        round.setLastStuch(false);
        when(roundPoints.getMoves()).thenReturn(10);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(0,1);
    }

    @Test
    public void testCheckFor66_LastStuch_false_GroupOwner_flase() {

        round.setGroupOwner(false);
        round.setLastStuch(false);
        when(roundPoints.getMoves()).thenReturn(10);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(1,0);
    }

    @Test
    public void testCheckFor66_Zugedreht_GrpOwner_True_Won() {

        round.setGroupOwner(true);
        round.setHasturned(true);
        round.setTurnpoints(3);
        when(roundPoints.getMoves()).thenReturn(25);
        when(roundPoints.getPointsplayer1()).thenReturn(66);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(3,0);
    }

    @Test
    public void testCheckFor66_Zugedreht_GrpOwner_True_Lost_Turnpoints_3() {

        round.setGroupOwner(true);
        round.setHasturned(true);
        round.setTurnpoints(3);
        when(roundPoints.getMoves()).thenReturn(25);
        when(roundPoints.getPointsplayer1()).thenReturn(65);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(0,3);
    }

    @Test
    public void testCheckFor66_Zugedreht_GrpOwner_True_Lost_Turnpoints_2() {

        round.setGroupOwner(true);
        round.setHasturned(true);
        round.setTurnpoints(2);
        when(roundPoints.getMoves()).thenReturn(25);
        when(roundPoints.getPointsplayer1()).thenReturn(65);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(0,2);
    }

    @Test
    public void testCheckFor66_Zugedreht_GrpOwner_False_Won() {

        round.setGroupOwner(false);
        round.setHasturned(true);
        round.setTurnpoints(3);
        when(roundPoints.getMoves()).thenReturn(25);
        when(roundPoints.getPointsplayer2()).thenReturn(66);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(0,3);
    }

    @Test
    public void testCheckFor66_Zugedreht_GrpOwner_False_Lost_Turnpoints_3() {

        round.setGroupOwner(false);
        round.setHasturned(true);
        round.setTurnpoints(3);
        when(roundPoints.getMoves()).thenReturn(25);
        when(roundPoints.getPointsplayer2()).thenReturn(65);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(3,0);
    }

    @Test
    public void testCheckFor66_Zugedreht_GrpOwner_False_Lost_Turnpoints_2() {

        round.setGroupOwner(false);
        round.setHasturned(true);
        round.setTurnpoints(2);
        when(roundPoints.getMoves()).thenReturn(25);
        when(roundPoints.getPointsplayer2()).thenReturn(65);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(2,0);
    }

    @Test
    public void testCheckFor66_Zugedreht_notTurned_GrpOwner_True_Won() {

        round.setGroupOwner(true);
        round.setHasturned(false);
        round.setTurnpoints(3);
        when(roundPoints.getMoves()).thenReturn(25);
        when(roundPoints.getPointsplayer2()).thenReturn(66);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(0,3);
    }

    @Test
    public void testCheckFor66_Zugedreht_notTurned_GrpOwner_True_Lost_Turnpoints_3() {

        round.setGroupOwner(true);
        round.setHasturned(false);
        round.setTurnpoints(3);
        when(roundPoints.getMoves()).thenReturn(25);
        when(roundPoints.getPointsplayer2()).thenReturn(65);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(3,0);
    }

    @Test
    public void testCheckFor66_Zugedreht_notTurned_GrpOwner_True_Lost_Turnpoints_2() {

        round.setGroupOwner(true);
        round.setHasturned(false);
        round.setTurnpoints(2);
        when(roundPoints.getMoves()).thenReturn(25);
        when(roundPoints.getPointsplayer2()).thenReturn(65);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(2,0);
    }

    @Test
    public void testCheckFor66_Zugedreht_notTurned_GrpOwner_False_Won() {

        round.setGroupOwner(false);
        round.setHasturned(false);
        round.setTurnpoints(3);
        when(roundPoints.getMoves()).thenReturn(25);
        when(roundPoints.getPointsplayer1()).thenReturn(66);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(3,0);
    }

    @Test
    public void testCheckFor66_Zugedreht_notTurned_GrpOwner_False_Lost_Turnpoints_3() {

        round.setGroupOwner(false);
        round.setHasturned(false);
        round.setTurnpoints(3);
        when(roundPoints.getMoves()).thenReturn(25);
        when(roundPoints.getPointsplayer1()).thenReturn(65);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(0,3);
    }

    @Test
    public void testCheckFor66_Zugedreht_notTurned_GrpOwner_False_Lost_Turnpoints_2() {

        round.setGroupOwner(false);
        round.setHasturned(false);
        round.setTurnpoints(2);
        when(roundPoints.getMoves()).thenReturn(25);
        when(roundPoints.getPointsplayer1()).thenReturn(65);

        assertEquals(true, round.checkFor66());
        verify(game).updateGamePoints(0,2);
    }

    @Test
    public void testCheckFor66_P1_65_P2_50() {

        when(roundPoints.getMoves()).thenReturn(1);
        when(roundPoints.getPointsplayer1()).thenReturn(65);
        when(roundPoints.getPointsplayer2()).thenReturn(50);

        assertEquals(false, round.checkFor66());
    }

}

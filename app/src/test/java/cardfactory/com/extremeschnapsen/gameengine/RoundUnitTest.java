package cardfactory.com.extremeschnapsen.gameengine;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import cardfactory.com.extremeschnapsen.database.DeckDataSource;
import cardfactory.com.extremeschnapsen.database.RoundPointsDataSource;
import cardfactory.com.extremeschnapsen.gui.GameActivity;
import cardfactory.com.extremeschnapsen.models.Deck;
import cardfactory.com.extremeschnapsen.models.RoundPoints;
import cardfactory.com.extremeschnapsen.networking.INetworkDisplay;
import cardfactory.com.extremeschnapsen.networking.NetworkManager;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class RoundUnitTest {
    Round round;
    Context context;
    DeckDataSource deckDataSource;
    RoundPointsDataSource roundPointsDataSource;
    RoundPoints roundPoints;
    List<Deck> currentDeck;
    Deck deck1;
    Deck deck2;

    INetworkDisplay networkDisplay;
    NetworkManager networkManager;

    @Before
    public void init() {
        context = mock(GameActivity.class);
        round = new Round();

        deck1 = mock(Deck.class);
        deck2 = mock(Deck.class);

        currentDeck = new ArrayList<>();
        currentDeck.add(deck1);
        currentDeck.add(deck2);

        deckDataSource = mock(DeckDataSource.class);
        when(deckDataSource.getAllDeck()).thenReturn(currentDeck);

        roundPointsDataSource = mock(RoundPointsDataSource.class);
        roundPoints = mock(RoundPoints.class);
        when(roundPoints.getPointsplayer1()).thenReturn(0);
        when(roundPoints.getPointsplayer2()).thenReturn(0);
        when(roundPointsDataSource.getCurrentRoundPointsObject()).thenReturn(roundPoints);

        round.setDeckDataSource(deckDataSource);
        round.setRoundPointsDataSource(roundPointsDataSource);

        networkDisplay = mock(INetworkDisplay.class);
        round.setNetworkDisplay(networkDisplay);

        networkManager = mock(NetworkManager.class);
        round.setNetworkManager(networkManager);
    }

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

    //endregion

    //region Stich

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

    @Test
    public void testRoundWonPlayer1WithGroupOwner() {
        when(roundPoints.getPointsplayer1()).thenReturn(66);
        round.setGroupOwner(true);
        assertTrue(round.roundWon());
    }

    @Test
    public void testRoundWonPlayer1WithNotGroupOwner() {
        when(roundPoints.getPointsplayer1()).thenReturn(66);
        round.setGroupOwner(false);
        assertFalse(round.roundWon());
    }

    @Test
    public void testRoundWonPlayer2WithGroupOwner() {
        when(roundPoints.getPointsplayer2()).thenReturn(66);
        round.setGroupOwner(true);
        assertFalse(round.roundWon());
    }

    @Test
    public void testRoundWonPlayer2WithNotGroupOwner() {
        when(roundPoints.getPointsplayer2()).thenReturn(66);
        round.setGroupOwner(false);
        assertTrue(round.roundWon());
    }

    //endregion
}

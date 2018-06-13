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
    }

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

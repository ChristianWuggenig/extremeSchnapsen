package cardfactory.com.extremeschnapsen.gameengine;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;

import cardfactory.com.extremeschnapsen.database.RoundPointsDataSource;
import cardfactory.com.extremeschnapsen.gui.GameActivity;
import cardfactory.com.extremeschnapsen.models.RoundPoints;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class RoundUnitTest {
    Round round;
    Context context;
    RoundPointsDataSource roundPointsDataSource;
    RoundPoints roundPoints;

    @Before
    public void init() {
        context = mock(GameActivity.class);
        round = new Round();
        roundPointsDataSource = mock(RoundPointsDataSource.class);
        roundPoints = mock(RoundPoints.class);
        when(roundPoints.getPointsplayer1()).thenReturn(0);
        when(roundPoints.getPointsplayer2()).thenReturn(0);
        when(roundPointsDataSource.getCurrentRoundPointsObject()).thenReturn(roundPoints);

        round.setRoundPointsDataSource(roundPointsDataSource);
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
}

package cardfactory.com.extremeschnapsen;

import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import cardfactory.com.extremeschnapsen.database.RoundPointsDataSource;
import cardfactory.com.extremeschnapsen.models.RoundPoints;

import static org.junit.Assert.assertEquals;

/**
 * Created by Patrick on 19.06.2018.
 */

public class RoundPointsDataSourceUnitTest {
    RoundPointsDataSource rpds;

    @Before
    public void init() {
        rpds = new RoundPointsDataSource(InstrumentationRegistry.getTargetContext());
        rpds.open();
    }

    @Test
    public void createRoundPoints() throws Exception {
        RoundPoints rp = rpds.createRoundPoints(1,1,34,22);
        RoundPoints rp2 = rpds.getCurrentRoundPointsObject();
        assertEquals(rp.getRoundpointsID(), rp2.getRoundpointsID());
        assertEquals(rp.getCurrentroundpoints(), rp2.getCurrentroundpoints() );
        assertEquals(rp.getPointsplayer1(), rp2.getPointsplayer1());
        assertEquals(rp.getPointsplayer2(), rp2.getPointsplayer2());


    }

    @Test
    public void getCurrentRoundPointsObject() throws Exception {
        RoundPoints rp = rpds.createRoundPoints(1,2,34,22);
        RoundPoints rp2 = rpds.getCurrentRoundPointsObject();
        assertEquals(rp.getRoundpointsID(), rp2.getRoundpointsID());
        assertEquals(rp.getCurrentroundpoints(), rp2.getCurrentroundpoints() );
        assertEquals(rp.getPointsplayer1(), rp2.getPointsplayer1());
        assertEquals(rp.getPointsplayer2(), rp2.getPointsplayer2());
    }

    @Test
    public void updateRoundPoints() throws Exception {
        RoundPoints rp= rpds.createRoundPoints(1,2,11,7);
        rpds.updateRoundPoints(rp);
        RoundPoints rp2= rpds.getCurrentRoundPointsObject();
        assertEquals(rp.getRoundpointsID(),rp2.getRoundpointsID());
        assertEquals(rp.getCurrentroundpoints(),rp2.getCurrentroundpoints());
        assertEquals(rp.getPointsplayer1(),rp2.getPointsplayer1());
        assertEquals(rp.getPointsplayer2(), rp2.getPointsplayer2());
    }

    @Test
    public void saveRoundPoints() throws Exception {
        rpds.open();
        RoundPoints rp = rpds.createRoundPoints(1,10,4,6);
        rpds.saveRoundPoints(rp);
        rp.getHiddenpointsplayer1();
        rp.getHiddenpointsplayer2();
        RoundPoints rp2 = rpds.getCurrentRoundPointsObject();
        assertEquals(rp.getHiddenpointsplayer1(), rp2.getHiddenpointsplayer1());
        assertEquals(rp.getHiddenpointsplayer2(), rp2.getHiddenpointsplayer2());
        assertEquals(rp.getRoundpointsID(),rp2.getRoundpointsID());
        assertEquals(rp.getCurrentroundpoints(),rp2.getCurrentroundpoints());
        assertEquals(rp.getPointsplayer1(),rp2.getPointsplayer1());
        assertEquals(rp.getPointsplayer2(), rp2.getPointsplayer2());


    }

    @Test
    public void updtateTrumpExchanged() throws Exception {
        RoundPoints rp = rpds.createRoundPoints(1,10,4,6);
        rp.setTrumpExchanged(1);
        rpds.saveRoundPoints(rp);
        RoundPoints rp2 = rpds.getCurrentRoundPointsObject();
        assertEquals(rp.getTrumpExchanged(),rp2.getTrumpExchanged());


    }

    @Test
    public void updateJoker() throws Exception {
        RoundPoints rp = rpds.createRoundPoints(1,10,4,6);
        rp.setSightJokerPlayer1(1);
        rp.setSightJokerPlayer2(1);
        rp.setCardExchangeJokerPlayer1(1);
        rp.setCardExchangeJokerPlayer2(1);
        rp.setParrySightJokerPlayer1(1);
        rp.setParrySightJokerPlayer2(1);
        rpds.saveRoundPoints(rp);
        RoundPoints rp2 = rpds.getCurrentRoundPointsObject();
        assertEquals(rp.getSightJokerPlayer1(),rp2.getSightJokerPlayer1());
        assertEquals(rp.getSightJokerPlayer2(),rp2.getSightJokerPlayer2());
        assertEquals(rp.getCardExchangeJokerPlayer1(),rp2.getCardExchangeJokerPlayer1());
        assertEquals(rp.getCardExchangeJokerPlayer2(),rp2.getCardExchangeJokerPlayer2());
        assertEquals(rp.getParrySightJokerPlayer1(),rp2.getParrySightJokerPlayer1());
        assertEquals(rp.getParrySightJokerPlayer2(),rp2.getParrySightJokerPlayer2());
    }

    @Test
    public void increaseMoves() throws Exception {
        RoundPoints rp = rpds.createRoundPoints(1,10,4,6);
        rp.setMoves(1);
        rpds.saveRoundPoints(rp);
        RoundPoints rp2 = rpds.getCurrentRoundPointsObject();
        assertEquals(rp.getMoves(),rp2.getMoves());
    }

    @Test
    public void deleteRoundPointsTable() throws Exception {
        rpds.createRoundPoints(1,2,22, 66 );
        rpds.deleteRoundPointsTable();
        RoundPoints rp2= rpds.getCurrentRoundPointsObject();
        assertEquals(null,rp2);
    }

}

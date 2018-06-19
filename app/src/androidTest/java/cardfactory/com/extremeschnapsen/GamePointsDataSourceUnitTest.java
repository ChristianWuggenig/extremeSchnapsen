package cardfactory.com.extremeschnapsen;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cardfactory.com.extremeschnapsen.database.GamePointsDataSource;
import cardfactory.com.extremeschnapsen.gameengine.Game;
import cardfactory.com.extremeschnapsen.models.GamePoints;

import static org.junit.Assert.*;

/**
 * Created by Patrick on 06.06.2018.
 */
public class GamePointsDataSourceUnitTest {
    GamePointsDataSource gpd;

    @Before
    public void setUp() throws Exception {
        gpd = new GamePointsDataSource(InstrumentationRegistry.getTargetContext());
        gpd.open();

    }

    @After
    public void tearDown() throws Exception {
        gpd = null;
    }

    @Test
    public void open() throws Exception {
        gpd.open();
        gpd.getAllGamePoints();
        assertTrue(true);
    }

    @Test(expected = Exception.class)
    public void openFail() {
        gpd.getAllGamePoints();

    }

    @Test
    public void getCurrentGamePointsObject() {
        GamePoints gp = gpd.createGamePoints(1, 0, 0);
        GamePoints gp2 = gpd.getCurrentGamePointsObject();
        assertEquals(gp.getGameID(), gp2.getGameID());
        assertEquals(gp.getGamePointsPlayer1(), gp2.getGamePointsPlayer1());
        assertEquals(gp.getGamePoinstsPlayer2(), gp2.getGamePoinstsPlayer2());

    }

    @Test
    public void updateGamePoints(){
        gpd.createGamePoints(1,1,1);
      GamePoints gp= gpd.updateGamePoints(1,34,12);
      GamePoints gp2= gpd.getCurrentGamePointsObject();
      assertEquals(gp.getGameID(),gp2.getGameID());
      assertEquals(gp.getGamePointsPlayer1(),gp2.getGamePointsPlayer1());
      assertEquals(gp.getGamePoinstsPlayer2(),gp2.getGamePoinstsPlayer2());

    }
    @Test
    public void deleteGamePoinsTable(){
        gpd.createGamePoints(1,1,1);
        gpd.deleteGamePoinsTable();
        GamePoints gp2= gpd.getCurrentGamePointsObject();
        assertEquals(null,gp2);



    }
}
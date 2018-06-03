package cardfactory.com.extremeschnapsen.gameengine;

import android.content.Context;
import java.io.Serializable;
import cardfactory.com.extremeschnapsen.database.GamePointsDataSource;
import cardfactory.com.extremeschnapsen.database.PlayerDataSource;
import cardfactory.com.extremeschnapsen.models.GamePoints;
import cardfactory.com.extremeschnapsen.models.Player;

/**
 * Created by NapeStar on 08.05.18.
 */

public class Game implements Serializable {

    private Player player;
    private PlayerDataSource playerDataSource;
    private GamePointsDataSource gpds;
    private GamePoints gp;

    public Game (Context context) {
        this.playerDataSource = new PlayerDataSource(context);
        this.gpds = new GamePointsDataSource(context);
        gpds.open();
        playerDataSource.open();
        gpds.deleteGamePoinsTable();
        gpds.createGamePoints(1,0,0);
        gp = gpds.getCurrentGamePointsObject();
        player = playerDataSource.getCurrentPlayerObject();
    }

    public Game (Context context, boolean isAlreadyCreated){
        this.gpds = new GamePointsDataSource(context);
        this.gpds.open();
        gp = gpds.getCurrentGamePointsObject();
        this.playerDataSource = new PlayerDataSource(context);
        this.playerDataSource.open();
        player = playerDataSource.getCurrentPlayerObject();
    }

    public void updateGamePoints(int won_pointsplayer1, int won_pointsplayer2){
        gp = gpds.getCurrentGamePointsObject();
        gp.setGamePointsPlayer1(gp.getGamePointsPlayer1()+ won_pointsplayer1);
        gp.setGamePoinstsPlayer2(gp.getGamePoinstsPlayer2()+ won_pointsplayer2);
        gpds.updateGamePoints(1,gp.getGamePointsPlayer1(),gp.getGamePoinstsPlayer2());
    }

    public boolean gameWon(boolean isGroupOwner){
        boolean won = false;
        gp = gpds.getCurrentGamePointsObject();

        if (gp.getGamePointsPlayer1() >=7){
            if (isGroupOwner){
                playerDataSource.updtatePlayerStatistics(1,1);
            }
            else {
                playerDataSource.updtatePlayerStatistics(1,0);
            }
        }
        else if (gp.getGamePoinstsPlayer2() >=7){
            if (isGroupOwner){
                playerDataSource.updtatePlayerStatistics(1,0);
            }
            else {
                playerDataSource.updtatePlayerStatistics(1,1);

            }

        }
        return won;

    }
  
    public int getGamePointsPlayer1() {
        gp = gpds.getCurrentGamePointsObject();
        return gp.getGamePointsPlayer1();
    }

    public int getGamePointsPlayer2() {
        gp = gpds.getCurrentGamePointsObject();
        return gp.getGamePoinstsPlayer2();
    }
  
    public void openDatabases() {
        gpds.open();
        playerDataSource.open();
    }

    public void closeDatabases() {
        gpds.close();
        playerDataSource.close();
    }
}

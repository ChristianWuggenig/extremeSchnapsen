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
        openDatabases();
        gp = gpds.getCurrentGamePointsObject();
        gp.setGamePointsPlayer1(gp.getGamePointsPlayer1()+ won_pointsplayer1);
        gp.setGamePoinstsPlayer2(gp.getGamePointsPlayer2()+ won_pointsplayer2);
        gpds.updateGamePoints(1,gp.getGamePointsPlayer1(),gp.getGamePointsPlayer2());
    }

    public boolean gameWon(boolean isGroupOwner){
        openDatabases();
        gp = gpds.getCurrentGamePointsObject();

        if (gp.getGamePointsPlayer1() >= 7 && isGroupOwner) {
            return true;
        } else if (gp.getGamePointsPlayer1() >= 7 && !isGroupOwner) {
            return false;
        } else if (gp.getGamePointsPlayer2() >= 7 && isGroupOwner) {
            return false;
        } else if (gp.getGamePointsPlayer2() >= 7 && !isGroupOwner) {
            return true;
        }
        return false;
    }

    public boolean gameOver(boolean isGroupOwner) {
        openDatabases();
        gp = gpds.getCurrentGamePointsObject();

        if (gp.getGamePointsPlayer1() >=7){

            gpds.updateGamePoints(1, 0, 0);

            if (isGroupOwner){
                playerDataSource.updatePlayerStatistics(1,1);
                return true;
            }
            else {
                playerDataSource.updatePlayerStatistics(1,0);
                return true;
            }

        }
        else if (gp.getGamePointsPlayer2() >=7){

            gpds.updateGamePoints(1, 0, 0);

            if (isGroupOwner){
                playerDataSource.updatePlayerStatistics(1,0);
                return true;
            }
            else {
                playerDataSource.updatePlayerStatistics(1,1);
                return true;

            }

        }

        return false;
    }
  
    public int getGamePointsPlayer1() {
        openDatabases();
        gp = gpds.getCurrentGamePointsObject();
        return gp.getGamePointsPlayer1();
    }

    public int getGamePointsPlayer2() {
        openDatabases();
        gp = gpds.getCurrentGamePointsObject();
        return gp.getGamePointsPlayer2();
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

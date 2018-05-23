package cardfactory.com.extremeschnapsen.models;

import android.content.Context;

import cardfactory.com.extremeschnapsen.database.GamePointsDataSource;

/**
 * Created by NapeStar on 08.05.18.
 */

public class Game {

    public GamePointsDataSource gpds;
    public GamePoints gp;

    public Game (Context context) {
        this.gpds = new GamePointsDataSource(context);
        this.gp = gp;
        gpds.open();
    }




}

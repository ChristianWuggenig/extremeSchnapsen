package cardfactory.com.extremeschnapsen;

import android.content.Context;

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

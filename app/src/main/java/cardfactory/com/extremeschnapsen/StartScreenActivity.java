package cardfactory.com.extremeschnapsen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;


/**
 * Created by NapeStar on 05.04.18.
 * shows the start screen and creates db connection
 */

public class StartScreenActivity extends AppCompatActivity {

    //for Output in Logcat and filtering
    public static final String LOG_TAG = StartScreenActivity.class.getSimpleName();
    private PlayerDataSource playerDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        Log.d(LOG_TAG, "Das Datenquellen-Objekt wird angelegt.");
        playerDataSource = new PlayerDataSource(this);
    }

    private boolean isEmptyPlayerList () {
        List<Player> playerList = playerDataSource.getAllPlayers();
        if (playerList.isEmpty()){
            return true;
        }
        else
            return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "Die Datenquelle wird geöffnet.");
        playerDataSource.open();

        Log.d(LOG_TAG, "Folgende Einträge sind in der Datenbank vorhanden:");
        playerDataSource.getAllPlayers();

        Log.d(LOG_TAG, "Ist die PlayerList leer: " + isEmptyPlayerList());

        if (isEmptyPlayerList()) {
            Log.d(LOG_TAG, "Die RegisterActivity wird aufgerufen");
            Intent i = new Intent(this, RegisterActivity.class);
            this.startActivity(i);
        }
        else {
            Log.d(LOG_TAG, "Die MainMenuActivity Activity wird aufgerufen");
            Intent i = new Intent(this, MainMenuActivity.class);
            this.startActivity(i);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(LOG_TAG, "Die Datenquelle wird geschlossen.");
        playerDataSource.close();
    }
}

package cardfactory.com.extremeschnapsen.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import cardfactory.com.extremeschnapsen.R;
import cardfactory.com.extremeschnapsen.database.PlayerDataSource;
import cardfactory.com.extremeschnapsen.models.Player;

public class ProfileActivity extends AppCompatActivity {

    private PlayerDataSource playerDataSource;
    private Player currentplayer;
    Float winStatFloat;
    int winStat;
    TextView playername;
    TextView playedGames;
    TextView wonGames;
    TextView winQuote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profil);

        playerDataSource = new PlayerDataSource(this);
        playerDataSource.open();
        currentplayer = playerDataSource.getCurrentPlayerObject();

        playername =  (TextView) findViewById(R.id.tvPlayerName);
        playedGames = (TextView) findViewById(R.id.tvPlayedGames);
        wonGames = (TextView) findViewById(R.id.tvWonGames);
        winQuote = (TextView) findViewById(R.id.tvWinQuote);

        playername.setText(currentplayer.getUsername());
        playedGames.setText(Integer.toString(currentplayer.getPlayed_games()));
        wonGames.setText(Integer.toString(currentplayer.getWon_games()));

        if (currentplayer.getPlayed_games() > 0) {

            winStatFloat = 100f * (float) currentplayer.getWon_games() / (float) currentplayer.getPlayed_games();
            winStat = Math.round(winStatFloat);

            winQuote.setText(Integer.toString(winStat));
        }
        else
            winQuote.setText("nie gespielt");

    }

    @Override
    protected void onResume() {
        super.onResume();
        playerDataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        playerDataSource.close();
    }

   public void onClickBtnBackToMenu(View view) {
    Intent i = new Intent(this, MainMenuActivity.class);
    this.startActivity(i);
    }
}



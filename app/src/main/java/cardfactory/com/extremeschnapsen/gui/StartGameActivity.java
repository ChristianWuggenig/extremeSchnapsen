package cardfactory.com.extremeschnapsen.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import cardfactory.com.extremeschnapsen.R;
import cardfactory.com.extremeschnapsen.gameengine.Game;

public class StartGameActivity extends AppCompatActivity {
    public static Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        //Instanz eines neuen Spiel
        game = new Game(this);
        //startGameActivityIntent.putExtra("game_s", game);
        //game.gpds.getAllGamePoints();

    }

    @Override
    protected void onResume() {
        super.onResume();
        game.gpds.open();
        Intent local = this.getIntent();

        if (!game.gameWon(local.getBooleanExtra("IS_GROUP_OWNER", true))){
            Intent startGameActivityIntent = new Intent(this, GameActivity.class);
            startGameActivityIntent.putExtra("IS_GROUP_OWNER", local.getBooleanExtra("IS_GROUP_OWNER", true));
            this.startActivityForResult(startGameActivityIntent, 0);
        }
        else if(game.gameWon(local.getBooleanExtra("IS_GROUP_OWNER", true))){
            finish();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        game.gpds.close();
    }

}

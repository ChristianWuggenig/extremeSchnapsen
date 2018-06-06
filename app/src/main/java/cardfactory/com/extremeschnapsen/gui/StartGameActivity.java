package cardfactory.com.extremeschnapsen.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.Window;

import cardfactory.com.extremeschnapsen.R;
import cardfactory.com.extremeschnapsen.database.PlayerDataSource;
import cardfactory.com.extremeschnapsen.gameengine.Game;
import cardfactory.com.extremeschnapsen.networking.IStartGame;
import cardfactory.com.extremeschnapsen.networking.NetworkHelper;
import cardfactory.com.extremeschnapsen.networking.NetworkManager;

public class StartGameActivity extends AppCompatActivity implements IStartGame {
    private static Game game;

    private static View.OnClickListener onClickListenerStart;
    private static View.OnClickListener onClickListenerFinish;
    private static View.OnClickListener onClickListenerStartNextRound;

    private static AppCompatButton btnStartGame;
    private static AppCompatButton btnFinishGame;

    private static boolean alreadyStarted;
    private static boolean gameModeExtreme;
    private static boolean isGroupOwner;

    private static Intent local;

    private static NetworkManager networkManager;

    private static PlayerDataSource playerDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_start_game);

        alreadyStarted = false;
        gameModeExtreme = false;

        networkManager = NetworkManager.getInstance(this);

        local = this.getIntent();
        isGroupOwner = local.getBooleanExtra(IntentHelper.IS_GROUP_OWNER, false);

        playerDataSource = new PlayerDataSource(this);
        playerDataSource.open();

        if (isGroupOwner) {
            networkManager.startHttpServer(playerDataSource.getCurrentPlayerObject().getGame_mode());
        } else {
            networkManager.startHttpClient(playerDataSource.getCurrentPlayerObject().getGame_mode());
        }

        //Instanz eines neuen Spiel
        game = new Game(this);
        //startGameActivityIntent.putExtra("game_s", game);
        //game.gpds.getAllGamePoints();

        btnStartGame = this.findViewById(R.id.btnStartRound);
        btnFinishGame = this.findViewById(R.id.btnFinishGame);

        onClickListenerStart = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnStartGameClick(view);
            }
        };

        onClickListenerStartNextRound = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnStartNextRoundClick(view);
            }
        };

        onClickListenerFinish = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnFinishGameClick(view);
            }
        };

        btnFinishGame.setOnClickListener(onClickListenerFinish);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (alreadyStarted) {
            btnStartGame.setText(R.string.btnStartNextRound);
            btnStartGame.setOnClickListener(onClickListenerStartNextRound);
        }

        alreadyStarted = true;

        game.openDatabases();
        
        boolean isGroupOwner = local.getBooleanExtra(IntentHelper.IS_GROUP_OWNER, false);

        if (game.gameOver(isGroupOwner)) {
            if (game.gameWon(isGroupOwner)) {
                btnStartGame.setText(R.string.btnGameWon);
            } else {
                btnStartGame.setText(R.string.btnGameLost);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        game.closeDatabases();
        playerDataSource.close();
    }

    public void btnStartGameClick(View view) {
        Intent startGameIntent;
        if (gameModeExtreme) {
            startGameIntent = new Intent(this, ExtremeGameActivity.class);
        } else {
            startGameIntent = new Intent(this, GameActivity.class);
        }

        startGameIntent.putExtra(IntentHelper.IS_GROUP_OWNER, local.getBooleanExtra(IntentHelper.IS_GROUP_OWNER, true));
        this.startActivity(startGameIntent);
    }

    public void btnFinishGameClick(View view) {
        if (isGroupOwner) {
            networkManager.stopHttpServer();
        }

        Intent mainMenuIntent = new Intent(this, MainMenuActivity.class);
        this.startActivity(mainMenuIntent);
    }

    public void btnStartNextRoundClick(View view) {
        Intent startGameActivityIntent = new Intent(this, GameActivity.class);
        startGameActivityIntent.putExtra(IntentHelper.IS_GROUP_OWNER, local.getBooleanExtra(IntentHelper.IS_GROUP_OWNER, true));
        this.startActivity(startGameActivityIntent);
    }

    @Override
    public void setGameMode(final String mode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mode.equals(NetworkHelper.MODE_EXTREME)) {
                    gameModeExtreme = true;
                    btnStartGame.setText(R.string.btnStartGameExtreme);
                } else {
                    gameModeExtreme = false;
                    btnStartGame.setText(R.string.btnStartGameNormal);
                }

                btnStartGame.setTextColor(getResources().getColor(R.color.white));
                btnStartGame.setOnClickListener(onClickListenerStart);
            }
        });
    }
}

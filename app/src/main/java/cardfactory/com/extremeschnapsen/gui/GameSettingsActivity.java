package cardfactory.com.extremeschnapsen.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import cardfactory.com.extremeschnapsen.R;
import cardfactory.com.extremeschnapsen.database.PlayerDataSource;

public class GameSettingsActivity extends AppCompatActivity {

    PlayerDataSource playerDataSource;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_game_settings);

        radioGroup = this.findViewById(R.id.rgGameSettings);

        playerDataSource = new PlayerDataSource(this);
        playerDataSource.open();

        switch (playerDataSource.getCurrentPlayerObject().getGame_mode()) {
            case "normal":
                radioGroup.check(R.id.rbtnNormal);
                break;
            case "extreme":
                radioGroup.check(R.id.rbtnExtreme);
        }
    }

    public void onClickBtnBackToMenu(View view) {

        int selectedId = radioGroup.getCheckedRadioButtonId();

        switch (selectedId) {
            case R.id.rbtnNormal:
                playerDataSource.updateGameMode("normal");
                break;
            case R.id.rbtnExtreme:
                playerDataSource.updateGameMode("extreme");
                break;
        }

        playerDataSource.close();

        finish();
    }
}

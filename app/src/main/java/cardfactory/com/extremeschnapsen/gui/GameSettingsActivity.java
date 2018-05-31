package cardfactory.com.extremeschnapsen.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import cardfactory.com.extremeschnapsen.R;

public class GameSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_game_settings);
    }

    public void onClickBtnBackToMenu(View view) {
        RadioGroup radioGroup = this.findViewById(R.id.rgGameSettings);

        int selectedId = radioGroup.getCheckedRadioButtonId();

        switch (selectedId) {
            case R.id.rbtnNormal:
                break;
            case R.id.rbtnExtreme:
                break;
            case R.id.rbtnDoesNotMatter:
                break;
        }
    }
}

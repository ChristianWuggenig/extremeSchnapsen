package cardfactory.com.extremeschnapsen.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import cardfactory.com.extremeschnapsen.R;

public class ExtremeGameActivity extends GameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_extreme_game);
    }
}

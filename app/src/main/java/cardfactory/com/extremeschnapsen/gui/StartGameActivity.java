package cardfactory.com.extremeschnapsen.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import cardfactory.com.extremeschnapsen.R;

public class StartGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_start_game);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent local = this.getIntent();
        Intent startGameActivityIntent = new Intent(this, GameActivity.class);
        startGameActivityIntent.putExtra("IS_GROUP_OWNER", local.getBooleanExtra("IS_GROUP_OWNER", true));
        this.startActivityForResult(startGameActivityIntent, 0);
    }
}

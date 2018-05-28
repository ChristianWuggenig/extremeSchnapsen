package cardfactory.com.extremeschnapsen.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import cardfactory.com.extremeschnapsen.R;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main_menu);
    }

    public void onClickBtnStartGame(View view) {
        Intent i = new Intent(this, SearchActivity.class);
        this.startActivity(i);
    }

    public void onClickBtnProfil(View view) {
        Intent i = new Intent (this, ProfileActivity.class);
        this.startActivity(i);

    }




    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        return true;
    }
    //Button profil =  (Button) findViewById(R.id.btnProfile);
    //Button settings =  (Button) findViewById(R.id.btnSettings);
    //Button start =  (Button) findViewById(R.id.btnStartGame);

}

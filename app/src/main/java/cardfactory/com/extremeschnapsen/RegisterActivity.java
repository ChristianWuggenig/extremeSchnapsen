package cardfactory.com.extremeschnapsen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

//import android.widget.ArrayAdapter;
//import android.widget.ListView;

/**
 * Created by NapeStar on 05.04.18.
 * manage Register
 * methods: constructor, onCreate(Bundle savedInstanceState),
 * onResume(), onPause(), onClickBtnRegister(), activateRegisterButton()
 */
public class RegisterActivity extends AppCompatActivity {

    public static final String LOG_TAG = RegisterActivity.class.getSimpleName();
    private PlayerDataSource playerDataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_register);

        Log.d(LOG_TAG, "Das Datenquellen-Objekt wird angelegt.");
        playerDataSource = new PlayerDataSource(this);

        activateRegisterButton();
    }

   /*
   private void showAllListEntries () {
        List<Player> playerList = playerDataSource.getAllPlayers();

       ArrayAdapter<Player> playerArrayAdapter = new ArrayAdapter<> (
                this,
                android.R.layout.simple_list_item_multiple_choice,
                playerList);

        ListView playersListView = (ListView) findViewById(R.id.listview_players);
        playersListView.setAdapter(playerArrayAdapter);
        }
    */

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "Die Datenquelle wird geöffnet.");
        playerDataSource.open();

        Log.d(LOG_TAG, "Folgende Einträge sind in der Datenbank vorhanden:");
        playerDataSource.getAllPlayers();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(LOG_TAG, "Die Datenquelle wird geschlossen.");
        playerDataSource.close();
    }

    //redirect to MainMenuActivity Activity
    private void onClickBtnRegister(){
        Log.d(LOG_TAG, "Die MainMenuActivity Activity wird aufgerufen");
        Intent i = new Intent(this, MainMenuActivity.class);
        this.startActivity(i);
    }

    private void activateRegisterButton() {
        Button buttonRegisterUser = (Button) findViewById(R.id.registerBtn);
        final EditText editTextUsername = (EditText) findViewById(R.id.usernameEditText);

        buttonRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String usernameString = editTextUsername.getText().toString();

                if(TextUtils.isEmpty(usernameString)) {
                    editTextUsername.setError(getString(R.string.editText_errorMessage));
                    return;
                }

                editTextUsername.setText("");

                playerDataSource.createPlayer(usernameString);

                InputMethodManager inputMethodManager;
                inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(getCurrentFocus() != null) {
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                playerDataSource.getAllPlayers();
                onClickBtnRegister();

            }
        });

    }
}

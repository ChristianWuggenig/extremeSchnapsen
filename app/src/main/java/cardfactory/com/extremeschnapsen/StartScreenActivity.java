package cardfactory.com.extremeschnapsen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import java.util.List;


/**
 * Created by NapeStar on 05.04.18.
 * shows the start screen and creates db connection
 */

public class StartScreenActivity extends AppCompatActivity {

    //for Output in Logcat and filtering
    public static final String LOG_TAG = StartScreenActivity.class.getSimpleName();
    private PlayerDataSource playerDataSource;
    private CardDataSource cardDataSource;
    private DeckDataSource deckDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_start_screen);

        Log.d(LOG_TAG, "Das Datenquellen-Objekt wird angelegt.");
        playerDataSource = new PlayerDataSource(this);
        cardDataSource = new CardDataSource(this);
        deckDataSource = new DeckDataSource(this);
    }

    private boolean isEmptyPlayerList () {
        List<Player> playerList = playerDataSource.getAllPlayers();
        if (playerList.isEmpty()){
            return true;
        }
        else
            return false;
    }

    private boolean isEmptyCardList(){
        List<Card> cardList = cardDataSource.getAllCards();
        if (cardList.isEmpty()){
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
        cardDataSource.open();
        deckDataSource.open();

        //Spielkarten werden in DBTabelle geschrieben, wenn nicht vorhanden
        if (cardDataSource.getAllCards().isEmpty()){
            Log.d(LOG_TAG, "Spielkarten werden in DB Tabelle geschrieben.");

            cardDataSource.createCard("Heart", "Ass", 11);
            cardDataSource.createCard("Heart", "Ten", 10);
            cardDataSource.createCard("Heart", "King", 4);
            cardDataSource.createCard("Heart", "Queen", 3);
            cardDataSource.createCard("Heart", "Jack", 2);

            cardDataSource.createCard("Diamond", "Ass", 11);
            cardDataSource.createCard("Diamond", "Ten", 10);
            cardDataSource.createCard("Diamond", "King", 4);
            cardDataSource.createCard("Diamond", "Queen", 3);
            cardDataSource.createCard("Diamond", "Jack", 2);

            cardDataSource.createCard("Spade", "Ass", 11);
            cardDataSource.createCard("Spade", "Ten", 10);
            cardDataSource.createCard("Spade", "King", 4);
            cardDataSource.createCard("Spade", "Queen", 3);
            cardDataSource.createCard("Spade", "Jack", 2);

            cardDataSource.createCard("Club", "Ass", 11);
            cardDataSource.createCard("Club", "Ten", 10);
            cardDataSource.createCard("Club", "King", 4);
            cardDataSource.createCard("Club", "Queen", 3);
            cardDataSource.createCard("Club", "Jack", 2);


        }

        //Aus CardList wird eine zufällige DeckList erstellt und in DB geschrieben
        deckDataSource.shuffelDeck(cardDataSource.getAllCards());

        Log.d(LOG_TAG, "Folgende Einträge sind in der Datenbank vorhanden:");
        playerDataSource.getAllPlayers();
        cardDataSource.getAllCards();
        deckDataSource.getAllDeck();

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
        cardDataSource.close();
        deckDataSource.close();
    }
}

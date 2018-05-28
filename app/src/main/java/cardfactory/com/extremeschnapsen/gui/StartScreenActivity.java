package cardfactory.com.extremeschnapsen.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import java.util.List;

import cardfactory.com.extremeschnapsen.database.RoundPointsDataSource;
import cardfactory.com.extremeschnapsen.models.Card;
import cardfactory.com.extremeschnapsen.database.CardDataSource;
import cardfactory.com.extremeschnapsen.database.DeckDataSource;
import cardfactory.com.extremeschnapsen.database.GamePointsDataSource;
import cardfactory.com.extremeschnapsen.models.Player;
import cardfactory.com.extremeschnapsen.database.PlayerDataSource;
import cardfactory.com.extremeschnapsen.R;
import cardfactory.com.extremeschnapsen.models.RoundPoints;


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
    private GamePointsDataSource gamePointsDataSource;
    private RoundPointsDataSource roundPointsDataSource;

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
        roundPointsDataSource = new RoundPointsDataSource(this);
        gamePointsDataSource = new GamePointsDataSource(this);
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
        roundPointsDataSource.open();
        gamePointsDataSource.open();

        //Spielkarten werden in DBTabelle geschrieben, wenn nicht vorhanden
        if (cardDataSource.getAllCards().isEmpty()){
            Log.d(LOG_TAG, "Spielkarten werden in DB Tabelle geschrieben.");

            cardDataSource.createCard("herz", "ass", 11);
            cardDataSource.createCard("herz", "10", 10);
            cardDataSource.createCard("herz", "koenig", 4);
            cardDataSource.createCard("herz", "dame", 3);
            cardDataSource.createCard("herz", "bub", 2);

            cardDataSource.createCard("karo", "ass", 11);
            cardDataSource.createCard("karo", "10", 10);
            cardDataSource.createCard("karo", "koenig", 4);
            cardDataSource.createCard("karo", "dame", 3);
            cardDataSource.createCard("karo", "bub", 2);

            cardDataSource.createCard("pik", "ass", 11);
            cardDataSource.createCard("pik", "10", 10);
            cardDataSource.createCard("pik", "koenig", 4);
            cardDataSource.createCard("pik", "dame", 3);
            cardDataSource.createCard("pik", "bub", 2);

            cardDataSource.createCard("kreuz", "ass", 11);
            cardDataSource.createCard("kreuz", "10", 10);
            cardDataSource.createCard("kreuz", "koenig", 4);
            cardDataSource.createCard("kreuz", "dame", 3);
            cardDataSource.createCard("kreuz", "bub", 2);


        }

        //Aus CardList wird eine zufällige DeckList erstellt und in DB geschrieben
        //deckDataSource.shuffelDeck(cardDataSource.getAllCards());

        Log.d(LOG_TAG, "Folgende Einträge sind in der RoundPoint Datenbank vorhanden:");

        RoundPoints rp = new RoundPoints(0,1,10,20,0,0,0,0,1,1,1,1,1,1,1);
        roundPointsDataSource.deleteRoundPointsTable();

        roundPointsDataSource.createRoundPoints(1,1,0,0);
        roundPointsDataSource.getAllRoundPoints();
        roundPointsDataSource.updateJoker(rp);
        rp.setTrumpExchanged(1);
        roundPointsDataSource.updtateTrumpExchanged(rp);
        roundPointsDataSource.deleteRoundPointsTable();

        Log.d(LOG_TAG, "Folgende Einträge sind in der GamePoint Datenbank vorhanden:");

        gamePointsDataSource.createGamePoints(1,0,0);
        gamePointsDataSource.getAllGamePoints();
        gamePointsDataSource.updateGamePoints(1,2,3);
        gamePointsDataSource.getAllGamePoints();
        gamePointsDataSource.deleteGamePoinsTable();
        gamePointsDataSource.getAllGamePoints();



        //playerDataSource.getAllPlayers();
        //cardDataSource.getAllCards();



        //deckDataSource.updateDeckStatus(1,9);
        //deckDataSource.getAllDeck();
        //deckDataSource.deleteDeckTable();
        //deckDataSource.getAllDeck();

        //int[] test = {20,19,18,17,16,15,14,13,12,11,10,9,8,7,6,5,4,3,2,1};

        //deckDataSource.receiveShuffeldDeck(test, cardDataSource.getAllCards());
        //deckDataSource.getAllDeck();

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
        roundPointsDataSource.close();
        gamePointsDataSource.close();
    }
}

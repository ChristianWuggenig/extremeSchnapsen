package cardfactory.com.extremeschnapsen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StartGameActivity extends AppCompatActivity {

    private ConstraintLayout deckholder;
    List<Deck> currentDeck;
    boolean deckSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_start_game);

        deckholder = (ConstraintLayout) findViewById(R.id.layoutPlaceholder);
        List<ImageView> cardList = new ArrayList<ImageView>();
        cardList.add((ImageView) findViewById(R.id.iv_card_1));
        cardList.add((ImageView) findViewById(R.id.iv_card_2));
        cardList.add((ImageView) findViewById(R.id.iv_card_3));
        cardList.add((ImageView) findViewById(R.id.iv_card_4));
        cardList.add((ImageView) findViewById(R.id.iv_card_5));
        cardList.add((ImageView) findViewById(R.id.iv_card_trump));
        cardList.add((ImageView) findViewById(R.id.iv_card_played));

        String [] cardCat = {"herz", "karo", "kreuz", "pik"};
        String [] cardValue = {"ass", "10", "koenig", "dame", "bub"};

        for(ImageView card : cardList){
            Random r = new Random();
            int cat_index = r.nextInt(4);
            int value_index = r.nextInt(5);

            int res_id = getResources().getIdentifier(cardCat[cat_index] + cardValue[value_index], "drawable", this.getPackageName() );
            card.setImageResource(res_id);
        }

        Intent intent = this.getIntent();
        boolean isGroupOwner = intent.getBooleanExtra("IS_GROUP_OWNER", true);

        final TextView txvPlayer = this.findViewById(R.id.txvPlayer);
        Round round = new Round(this);

        if(isGroupOwner) {
            currentDeck = round.initializeRound();

            ServerAsyncTask serverAsyncTask = new ServerAsyncTask() {
                @Override
                public void updateDecks(List<Deck> currentDeckFromClient) {
                    if(!deckSet) {
                        currentDeck = currentDeckFromClient;
                        deckSet = true;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txvPlayer.setText(currentDeck.get(0).toString());
                            }
                        });

                    }

                }
            };

            serverAsyncTask.execute(currentDeck);
        }
        else {
            currentDeck = round.initializeRound();
            ClientAsyncTask clientAsyncTask = new ClientAsyncTask();
            clientAsyncTask.execute(currentDeck);
            txvPlayer.setText(currentDeck.get(0).toString());
        }
    }
}

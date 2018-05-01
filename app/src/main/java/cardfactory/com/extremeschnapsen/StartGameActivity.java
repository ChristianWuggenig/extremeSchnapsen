package cardfactory.com.extremeschnapsen;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StartGameActivity extends AppCompatActivity implements INetworkDisplay {

    private ConstraintLayout deckholder;
    List<Deck> currentDeck;
    List<Deck> cardsOnHand;
    Deck opencard;

    TextView txvPlayer;

    Round round;

    NetworkManager networkManager;

    private View.OnClickListener onClickListener;
    private ImageView ivCardOne;
    private ImageView ivCardTwo;
    private ImageView ivCardThree;
    private ImageView ivCardFour;
    private ImageView ivCardFive;

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

        Intent intent = this.getIntent();
        boolean isGroupOwner = intent.getBooleanExtra("IS_GROUP_OWNER", true);

        round = new Round(this);

        //muss noch umgesetzt werden
        //if (isGroupOwner){
            currentDeck = round.initializeRound();
        //}
        //else


        String [] cardCat = {"herz", "karo", "kreuz", "pik"};
        String [] cardValue = {"ass", "10", "koenig", "dame", "bub"};

        int index = 0;
        cardsOnHand = round.getCardsOnHand(isGroupOwner);
        opencard = round.getOpenCard();
        String karte = null;

        for(ImageView card : cardList){

            if (index <5) {
                karte = cardsOnHand.get(index).getCardSuit() + cardsOnHand.get(index).getCardRank();
            }
            if (index == 5){
                karte = opencard.getCardSuit() + opencard.getCardRank();
            }
            index ++;
            if (index == 6){
                karte = "herzass";
            }

            int res_id = getResources().getIdentifier(karte, "drawable", this.getPackageName() );
            card.setImageResource(res_id);
        }



        if(isGroupOwner) {
            round.startServer();
            round.setMyTurn(true);
        }
        else {
            round.startClient();
            round.setMyTurn(false);
            round.playCard(0);
        }

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivCardClicked(view);
            }
        };

        ivCardOne = this.findViewById(R.id.iv_card_1);
        ivCardTwo = this.findViewById(R.id.iv_card_2);
        ivCardThree = this.findViewById(R.id.iv_card_3);
        ivCardFour = this.findViewById(R.id.iv_card_4);
        ivCardFive = this.findViewById(R.id.iv_card_5);

        txvPlayer = this.findViewById(R.id.txvPlayer);
    }

    @Override
    public void displayStatus(final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txvPlayer.setText(message);
            }
        });

    }

    public void ivCardClicked(View view) {
        ImageView imageView = (ImageView)view;

        switch (imageView.getId()) {
            case R.id.iv_card_1:
                txvPlayer.setText("card 1 played");
                round.playCard(1);
                break;
            case R.id.iv_card_2:
                txvPlayer.setText("card 2");
                round.playCard(2);
                break;
        }


    }
}

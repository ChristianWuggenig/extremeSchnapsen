package cardfactory.com.extremeschnapsen;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
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
    List<Deck> cardsOnHand;
    Deck opencard;

    List<ImageView> cardList;

    TextView txvPlayer;

    Round round;

    private View.OnClickListener onClickListener;

    private AlertDialog.Builder builder;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_start_game);

        builder = new AlertDialog.Builder(StartGameActivity.this);

        builder.setMessage(R.string.msgWaitingForOpposite)
        .setTitle(R.string.msgWaiting);

        dialog = builder.create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();

        deckholder = (ConstraintLayout) findViewById(R.id.layoutPlaceholder);
        cardList = new ArrayList<ImageView>();
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

        if(isGroupOwner) {
            round.startServer();
            round.setMyTurn(false);
            round.initializeRound();
            displayDeck();
        }
        else {
            round.startClient();
            round.setMyTurn(true);
        }

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivCardClicked(view);
            }
        };

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

        dialog.dismiss();

    }

    public void displayDeck() {
        //wird dadurch auch die gespielte karte in iv_card_played angezeigt (also jene mit status 5 oder 6)?
        int index = 0;
        cardsOnHand = round.getCardsOnHand();
        opencard = round.getOpenCard();
        String karte = "";

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
    }

    @Override
    public void displayShuffledDeck(int[] shuffledDeckIDs) {
        round.getShuffledDeck(shuffledDeckIDs);
        displayDeck();
    }

    public void ivCardClicked(View view) {
        ImageView imageView = (ImageView)view;

        switch (imageView.getId()) {
            case R.id.iv_card_1:
                playCard((int)cardsOnHand.get(0).getCardID()); //bekomme ich dadurch die richtige karte?
                break;
            case R.id.iv_card_2:
                playCard((int)cardsOnHand.get(1).getCardID());
                break;
            case R.id.iv_card_3:
                playCard((int)cardsOnHand.get(2).getCardID());
                break;
            case R.id.iv_card_4:
                playCard((int)cardsOnHand.get(3).getCardID());
                break;
            case R.id.iv_card_5:
                playCard((int)cardsOnHand.get(4).getCardID());
                break;
        }
    }

    public void playCard(int cardID) {
        if(round.playCard(cardID)) {
            txvPlayer.setText("card " + String.valueOf(cardID) + " played");
            displayDeck();
        }

        else
            txvPlayer.setText("not your turn or end of round reached");
    }

    @Override
    public void setMyTurn(boolean value) {
        round.setMyTurn(true);
        round.increaseMoves();
    }
}

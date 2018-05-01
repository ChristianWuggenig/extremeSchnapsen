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
    List<Deck> currentDeck;
    List<Deck> cardsOnHand;
    Deck opencard;

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
        currentDeck = round.initializeRound();

        if(isGroupOwner) {
            round.startServer();
            round.setMyTurn(false);
        }
        else {
            round.startClient();
            round.setMyTurn(true);
        }

        int index = 0;
        cardsOnHand = round.getCardsOnHand(isGroupOwner);
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

    public void ivCardClicked(View view) {
        ImageView imageView = (ImageView)view;

        switch (imageView.getId()) {
            case R.id.iv_card_1:
                playCard(1);
                break;
            case R.id.iv_card_2:
                playCard(2);
                break;
            case R.id.iv_card_3:
                playCard(3);
                break;
            case R.id.iv_card_4:
                playCard(4);
                break;
            case R.id.iv_card_5:
                playCard(5);
                break;
        }
    }

    public void playCard(int cardID) {
        if(round.playCard(cardID))
            txvPlayer.setText("card " + String.valueOf(cardID) + " played");
        else
            txvPlayer.setText("not your turn or end of round reached");
    }

    @Override
    public void setMyTurn(boolean value) {
        round.setMyTurn(true);
        round.increaseMoves();
    }
}

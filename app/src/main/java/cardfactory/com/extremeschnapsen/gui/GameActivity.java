package cardfactory.com.extremeschnapsen.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cardfactory.com.extremeschnapsen.models.CardImageView;
import cardfactory.com.extremeschnapsen.models.Deck;
import cardfactory.com.extremeschnapsen.networking.INetworkDisplay;
import cardfactory.com.extremeschnapsen.R;
import cardfactory.com.extremeschnapsen.gameengine.Round;

public class GameActivity extends AppCompatActivity implements INetworkDisplay {

    private static ConstraintLayout deckholder;
    private static List<Deck> cardsOnHand;
    private static Deck open;
    private static List<Deck> playedCards;
    private static Deck playedCardPlayer1;
    private static Deck playedCardPlayer2;

    private static List<ImageView> cardList;
    private static List<CardImageView> cardsToCheckFor20;

    private static TextView txvPlayer;
    private static TextView txvPlayer1;
    private static TextView txvPlayer2;

    private static Round round;

    private static boolean isGroupOwner;

    private static View.OnClickListener onClickListener;

    private static AlertDialog.Builder builder;

    private static AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_game);

        //startService(new Intent(this, MySensorService.class));

        builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.msgWaitingForOpposite)
                .setTitle(R.string.msgWaiting);

        dialog = builder.create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();

        deckholder = (ConstraintLayout) findViewById(R.id.layoutPlaceholder);
        cardList = new ArrayList<>();
        cardList.add((ImageView) findViewById(R.id.iv_card_1));
        cardList.add((ImageView) findViewById(R.id.iv_card_2));
        cardList.add((ImageView) findViewById(R.id.iv_card_3));
        cardList.add((ImageView) findViewById(R.id.iv_card_4));
        cardList.add((ImageView) findViewById(R.id.iv_card_5));
        cardList.add((ImageView) findViewById(R.id.iv_card_trump));
        cardList.add((ImageView) findViewById(R.id.iv_card_played));
        cardList.add((ImageView) findViewById(R.id.iv_card_played2));

        /*cardsToCheckFor20 = new ArrayList<>();
        cardsToCheckFor20.add((CardImageView) findViewById(R.id.iv_card_1));
        cardsToCheckFor20.add((CardImageView) findViewById(R.id.iv_card_2));
        cardsToCheckFor20.add((CardImageView) findViewById(R.id.iv_card_3));
        cardsToCheckFor20.add((CardImageView) findViewById(R.id.iv_card_4));
        cardsToCheckFor20.add((CardImageView) findViewById(R.id.iv_card_5));*/

        txvPlayer = this.findViewById(R.id.txvPlayer);
        txvPlayer1 = this.findViewById(R.id.txv_user1);
        txvPlayer2 = this.findViewById(R.id.txv_user2);

        Intent intent = this.getIntent();
        isGroupOwner = intent.getBooleanExtra("IS_GROUP_OWNER", true);

        round = new Round(this);

        if(isGroupOwner) {
            round.initializeRound();
            round.startServer();
            round.setMyTurn(false);
            txvPlayer1.setText(round.getUsername());
            txvPlayer2.setText(R.string.msgWaiting);
            txvPlayer.setText(R.string.msgWaitingForConnection);
            displayDeck();
        }
        else {
            round.startClient();
            round.setMyTurn(true);
            txvPlayer2.setText(round.getUsername());
            txvPlayer1.setText(R.string.msgWaiting);
        }

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivCardClicked(view);
            }
        };

        for (int count = 0; count < 5; count++) {
            cardList.get(count).setOnClickListener(onClickListener);
        }
    }

    @Override
    public void dismissDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void displayStatus(final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (message) {
                    case "won":
                        txvPlayer.setText(R.string.msgPlayerWon);
                        break;
                    case "lost":
                        txvPlayer.setText(R.string.msgPlayerLost);
                        break;
                    case "waiting":
                        txvPlayer.setText(R.string.msgWaitingForOpppositeMove);
                        break;
                    case "yourTurn":
                        txvPlayer.setText(R.string.msgYourTurn);
                        break;
                }
            }
        });
    }

    public void displayDeck() {
        //TODO: bitte hier den code so anpassen, das die Karten nicht nach links nachrutschen wenn eine action getriggered wurde, erschwert unnötig die abfrage und das trigger der 20er, 40er Ansage

        int index = 0;
        cardsOnHand = round.getCardsOnHand();
        open = round.getOpenCard();
        playedCards = round.getPlayedCards();
        playedCardPlayer1 = round.getPlayedCardPlayer1();
        playedCardPlayer2 = round.getPlayedCardPlayer2();
        String karte = "";

        //round.checkFor20(cardsOnHand, cardsToCheckFor20);

        for(ImageView card : cardList) {
            karte = "";
            if (index < cardsOnHand.size()) {
                karte = cardsOnHand.get(index).getCardSuit() + cardsOnHand.get(index).getCardRank();
            }
            if (index == 5 && open != null){
                karte = open.getCardSuit() + open.getCardRank();
            }
            if (index == 6 && playedCardPlayer1 != null) {
                karte = playedCardPlayer1.getCardSuit() + playedCardPlayer1.getCardRank();
            }
            if (index == 7 && playedCardPlayer2 != null) {
                karte = playedCardPlayer2.getCardSuit() + playedCardPlayer2.getCardRank();
            }
            if (karte == "")
                karte = "logo";

            index ++;

            int res_id = getResources().getIdentifier(karte, "drawable", this.getPackageName());
            card.setImageResource(res_id);
        }
        index = 0;

        /*for(CardImageView civ : cardsToCheckFor20){
            if (index < cardsOnHand.size()) {
                civ.setCardId (cardsOnHand.get(index).getCardID() );
            }
            index++;
        }*/
    }

    @Override
    public void displayShuffledDeck(final int[] shuffledDeckIDs, final String playerName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                round.getShuffledDeck(shuffledDeckIDs);
                txvPlayer1.setText(playerName);
                displayDeck();
            }
        });

    }

    public void ivCardClicked(View view) {

        switch (view.getId()) {
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
        /*for(CardImageView civ : cardsToCheckFor20){
            if((int) civ.getCardId() == cardID){
                if( civ.isEnable_20_strike() ){
                    //TODO: was soll passieren wenn die gespielte karte freigeschalten wurde für die 20er ansage ?
                }
            }
        }*/


        if(round.playCard(cardID)) {
            if(round.compareCards()){
                finish();
            }
        }
    }

    @Override
    public void setMyTurn(final int cardPlayed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<Deck> allDecks = round.getAllDecks();

                for (Deck deck : allDecks) {
                    if (deck.getDeckStatus() == 1 && deck.getCardID() == cardPlayed)
                        round.updateCard(cardPlayed, 5);
                    else if (deck.getDeckStatus() == 2 && deck.getCardID() == cardPlayed)
                        round.updateCard(cardPlayed, 6);
                }

                round.setMyTurn(true);

                if(round.compareCards()){
                 //GameActivity.this.finish();
                 //GameActivity.super.onDestroy();
                    finish();
                }
            }
        });

    }

    @Override
    public void updateDeck() {
        displayDeck();
    }

    @Override
    public void displayPlayer(final String playerName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txvPlayer2.setText(playerName);
            }
        });
    }

    @Override
    public void waitForCard() {
        round.waitForCard();
    }
}

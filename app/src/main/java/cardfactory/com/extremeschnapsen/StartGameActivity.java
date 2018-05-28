package cardfactory.com.extremeschnapsen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
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

public class StartGameActivity extends AppCompatActivity implements INetworkDisplay {

    private ConstraintLayout deckholder;
    List<Deck> cardsOnHand;
    Deck open;
    List<Deck> playedCards;
    Deck playedCardPlayer1;
    Deck playedCardPlayer2;
    //für Game Object -> serializable
    //Intent i = getIntent();
    //Game game_test = (Game)i.getSerializableExtra("game_s");

    List<ImageView> cardList;
    List<CardImageView> cardsToCheckFor20;

    TextView txvPlayer;

    Round round;

    boolean isGroupOwner;

    private View.OnClickListener onClickListener;

    private AlertDialog.Builder builder;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_start_game);

        startService(new Intent(this, MySensorService.class));

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

        Intent intent = this.getIntent();
        isGroupOwner = intent.getBooleanExtra("IS_GROUP_OWNER", true);

        round = new Round(this);

        if(isGroupOwner) {
            round.initializeRound();
            round.startServer();
            round.setMyTurn(false);
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

        for (int count = 0; count < 6; count++) {
            cardList.get(count).setOnClickListener(onClickListener);
        }


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

    @Override
    public void dismissDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
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
    public void displayShuffledDeck(int[] shuffledDeckIDs) {
        round.getShuffledDeck(shuffledDeckIDs);
        displayDeck();
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

            case R.id.iv_card_trump:
                exchangeTrump();
                break;
        }
    }

    public void exchangeTrump(){
        this.round.exchangeTrump();
        displayDeck();
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
            txvPlayer.setText("card " + String.valueOf(cardID) + " played");
            if(round.compareCards()){
               recreate();
            }
            displayDeck();
        }
        else
            txvPlayer.setText("not your turn or end of round reached");
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
                 //StartGameActivity.this.finish();
                 //StartGameActivity.super.onDestroy();
                    recreate();
                }
                displayDeck();

            }
        });

    }

    public void finishGUI() {
        this.finish();
    }
}

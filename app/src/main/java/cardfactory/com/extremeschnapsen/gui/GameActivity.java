package cardfactory.com.extremeschnapsen.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cardfactory.com.extremeschnapsen.models.Deck;
import cardfactory.com.extremeschnapsen.networking.INetworkDisplay;
import cardfactory.com.extremeschnapsen.R;
import cardfactory.com.extremeschnapsen.gameengine.Round;
import cardfactory.com.extremeschnapsen.networking.NetworkHelper;
import cardfactory.com.extremeschnapsen.services.MySensorService;

public class GameActivity extends AppCompatActivity implements INetworkDisplay {

    private static List<Deck> cardsOnHand;
    private Deck open;
    private Deck playedCardPlayer1;
    private Deck playedCardPlayer2;

    private static List<ImageView> cardList;
    protected static List<ImageView> showCardList;

    protected static TextView txvUserInformation;
    private static TextView txvPlayer1;
    private static TextView txvPlayer2;
    private static TextView txvPoints;
    private static TextView txvGamePoints1;
    private static TextView txvGamePoints2;

    protected static Round round;

    protected boolean isGroupOwner;
    private boolean isShaked;
    private boolean isTurned;

    private static View.OnClickListener ivOnClickListener;

    private static AlertDialog.Builder connectionBuilder;
    private static AlertDialog.Builder showCardsBuilder;

    private static AlertDialog connectionDialog;
    private static AlertDialog showCardsDialog;

    private BroadcastReceiver mSensorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //get both boolean values and handle both actions -> trigger more actions
            boolean isShake = intent.getBooleanExtra(IntentHelper.MOVEMENT_ACTIONS[0],false);
            boolean isTurn = intent.getBooleanExtra(IntentHelper.MOVEMENT_ACTIONS[1], false);

            if(isShake && !isShaked){
                isShaked = true;
                Log.d("MainActivity", "shake happened!");
            }
            if(isTurn && !isTurned){
                round.turn();
                isTurned = true;
                displayDeck();
                Log.d("MainActivity", "turn happened!");
            }
        }
    };

    //region Activity methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_game);

        startService(new Intent(this, MySensorService.class));

        connectionBuilder = new AlertDialog.Builder(this);

        connectionBuilder.setMessage(R.string.msgWaitingForOpposite)
                .setTitle(R.string.msgWaiting);

        connectionDialog = connectionBuilder.create();

        connectionDialog.setCancelable(false);
        connectionDialog.setCanceledOnTouchOutside(false);

        connectionDialog.show();

        showCardsBuilder = new AlertDialog.Builder(this);

        cardList = new ArrayList<>();
        cardList.add((ImageView) findViewById(R.id.iv_card_1));
        cardList.add((ImageView) findViewById(R.id.iv_card_2));
        cardList.add((ImageView) findViewById(R.id.iv_card_3));
        cardList.add((ImageView) findViewById(R.id.iv_card_4));
        cardList.add((ImageView) findViewById(R.id.iv_card_5));
        cardList.add((ImageView) findViewById(R.id.iv_card_trump));
        cardList.add((ImageView) findViewById(R.id.iv_card_played));
        cardList.add((ImageView) findViewById(R.id.iv_card_played2));

        txvUserInformation = this.findViewById(R.id.txvUserInformation);
        txvPlayer1 = this.findViewById(R.id.txv_user1);
        txvPlayer2 = this.findViewById(R.id.txv_user2);
        txvPoints = this.findViewById(R.id.txv_mypoints);
        txvGamePoints1 = this.findViewById(R.id.txv_points1);
        txvGamePoints2 = this.findViewById(R.id.txv_points2);

        Intent intent = this.getIntent();
        isGroupOwner = intent.getBooleanExtra(IntentHelper.IS_GROUP_OWNER, true);

        round = new Round(this);

        //register BroadcastReceiver and set IntentFilter for MovementSensor
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mSensorReceiver, new IntentFilter(IntentHelper.MOVEMENT_SENSOR_KEY));

        if(isGroupOwner) {
            round.initializeRound();
            round.startServer();
            round.setMyTurn(false);
            txvPlayer1.setText(round.getUsername());
            txvPlayer2.setText(R.string.msgWaiting);
            txvUserInformation.setText(R.string.msgWaitingForConnection);
            displayDeck();
        }
        else {
            round.startClient();
            round.setMyTurn(true);
            txvPlayer2.setText(round.getUsername());
            txvPlayer1.setText(R.string.msgWaiting);
        }

        ivOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivCardClicked(view);
            }
        };

        for (int count = 0; count < 6; count++) {
            cardList.get(count).setOnClickListener(ivOnClickListener);
        }

        showCardList = new ArrayList<>();

        isTurned = false;
        isShaked = false;
    }

    @Override
    protected void onDestroy() {
        //deregister BroadcastReceiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSensorReceiver);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        round.closeDatabases();
    }

    @Override
    protected void onResume() {
        super.onResume();
        round.openDatabases();
    }

    //endregion

    //region Listeners

    //msg20Received, msg40Received noch verwenden bitte
    public void onClickBtnHerz(View view) {
        round.check2040("herz");
        if (round.checkFor66()) {
            finishActivity(round.roundWon());
        }
    }

    public void onClickBtnKaro(View view) {
        round.check2040("karo");
        if (round.checkFor66()) {
            finishActivity(round.roundWon());
        }
    }

    public void onClickBtnPik(View view) {
        round.check2040("pik");
        if (round.checkFor66()) {
            finishActivity(round.roundWon());
        }
    }

    public void onClickBtnKreuz(View view) {
        round.check2040("kreuz");
        if (round.checkFor66()) {
            finishActivity(round.roundWon());
        }
    }

    public void onClickBtnExchange(View view) {

        ivOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivCardClicked_CardExchange(view);
            }
        };

        for (int count = 0; count < 5; count++) {
            cardList.get(count).setOnClickListener(ivOnClickListener);
        }

    }

    public void onClickBtnCards(View view) {
        showCardDialog(false);
    }

    public void setPlayCardListener() {
        ivOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivCardClicked(view);
            }
        };

        for (int count = 0; count < 6; count++) {
            cardList.get(count).setOnClickListener(ivOnClickListener);
        }
    }

    public void onClickBtnParrySightJoker(View view) {

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
                round.exchangeTrump();
                displayDeck();
                break;
        }
    }

    public void ivCardClicked_CardExchange(View view) {

        switch (view.getId()) {
            case R.id.iv_card_1:
                exchangeCard((int)cardsOnHand.get(0).getCardID());
                break;
            case R.id.iv_card_2:
                exchangeCard((int)cardsOnHand.get(1).getCardID());
                break;
            case R.id.iv_card_3:
                exchangeCard((int)cardsOnHand.get(2).getCardID());
                break;
            case R.id.iv_card_4:
                exchangeCard((int)cardsOnHand.get(3).getCardID());
                break;
            case R.id.iv_card_5:
                exchangeCard((int)cardsOnHand.get(4).getCardID());
                break;

        }
        setPlayCardListener();
        displayDeck();
    }

    //endregion

    //region Interface methods

    @Override
    public void dismissDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectionDialog.dismiss();
            }
        });
    }

    @Override
    public void displayUserInformation(final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (message) {
                    case MessageHelper.WON:
                        txvUserInformation.setText(R.string.msgPlayerWon);
                        break;
                    case MessageHelper.LOST:
                        txvUserInformation.setText(R.string.msgPlayerLost);
                        break;
                    case MessageHelper.WAITING:
                        txvUserInformation.setText(R.string.msgWaitingForOpppositeMove);
                        break;
                    case MessageHelper.YOURTURN:
                        txvUserInformation.setText(R.string.msgYourTurn);
                        break;
                    case MessageHelper.TWENTYRECEIVED:
                        txvUserInformation.setText(R.string.msg20Received);
                        break;
                    case MessageHelper.FORTYRECEIVED:
                        txvUserInformation.setText(R.string.msg40Received);
                        break;
                    case MessageHelper.TWENTYPLAYED:
                        txvUserInformation.setText(R.string.msg20Played);
                        break;
                    case MessageHelper.FORTYPLAYED:
                        txvUserInformation.setText(R.string.msg40Played);
                        break;
                    case MessageHelper.CARD_EXCHANGE:
                        txvUserInformation.setText(R.string.msgCardExchanged);
                        break;
                    case MessageHelper.CARD_EXCHANGE_RECEIVED:
                        txvUserInformation.setText(R.string.msgCardExchangeReceived);
                        break;
                    case MessageHelper.PARRYSIGHTJOKER_SUCCESS_WON:
                        txvUserInformation.setText(R.string.msgParrySightJokerSuccessWon);
                        showRoundPoints();
                        break;
                    case MessageHelper.PARRYSIGHTJOKER_SUCCESS_lOST:
                        txvUserInformation.setText(R.string.msgParrySightJokerSuccessLost);
                        break;
                    case MessageHelper.PARRYSIGHTJOKER_FAIL_WON:
                        txvUserInformation.setText(R.string.msgParrySightJokerFailWon);
                        showRoundPoints();
                        break;
                    case MessageHelper.PARRYSIGHTJOKER_FAIL_lOST:
                        txvUserInformation.setText(R.string.msgParrySightJokerFailLost);
                        break;
                    case MessageHelper.TURNEDCALLEDSUCESS:
                        txvUserInformation.setText(R.string.msgTurnedCalledSuccess);
                        break;
                    case MessageHelper.TURNEDCALLEDFAIL:
                        txvUserInformation.setText(R.string.msgTurnedCalledFail);
                        break;
                    case MessageHelper.TURNEDRECEIVED:
                        txvUserInformation.setText(R.string.msgTurnedReceived);
                        break;
                }
            }
        });
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

    @Override
    public void receiveAction(final String action, final String value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (action) {
                    case NetworkHelper.ID:
                        round.receivePlayCard(Integer.parseInt(value));
                        if(round.checkFor66()){
                            finishActivity(round.roundWon());
                        }
                        break;
                    case NetworkHelper.TRUMP:
                        round.receiveExchangeTrump();
                        displayDeck();
                        break;
                    case NetworkHelper.TURN:
                        round.turnReceived();
                        displayDeck();
                        break;
                    case NetworkHelper.TWENTYFORTY:
                        round.receiveCheck2040(value);
                        if (round.checkFor66()) {
                            finishActivity(round.roundWon());
                        }
                        break;
                    case NetworkHelper.SIGHTJOKER:
                        round.receiveSightJoker();
                        break;
                    case NetworkHelper.PARRYSIGHTJOKER:
                        round.receiveParrySightJoker();
                        if (round.checkFor66()) {
                            finishActivity(round.roundWon());
                        }
                        break;
                    case NetworkHelper.CARD_EXCHANGE:
                        String[] splitted = value.split(";");
                        if (splitted[0] != "") {
                            round.receiveCardExchange(Integer.parseInt(splitted[0]), Integer.parseInt(splitted[1]));
                            displayDeck();
                        }
                        break;
                }
            }
        });
    }

    //endregion

    //region Actions

    public void exchangeCard (int cardID){
        round.cardExchange(cardID);

    }

    public void playCard(int cardID) {
        if(round.playCard(cardID)) {
            round.compareCards();
            if(round.checkFor66()){
                finishActivity(round.roundWon());
            }
        }
    }

    //endregion

    //region Display UI

    public void displayDeck() {

        int index = 0;
        cardsOnHand = round.getCardsOnHand();
        open = round.getOpenCard();
        List<Deck> playedCards = round.getPlayedCards();
        playedCardPlayer1 = round.getPlayedCardPlayer1();
        playedCardPlayer2 = round.getPlayedCardPlayer2();
        String karte = "";

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

        showGamePoints();
        showRoundPoints();
    }

    public void showRoundPoints() {
        if (isGroupOwner) {
            txvPoints.setText(round.getRoundPointsPlayer1() + " Punkte");
        } else {
            txvPoints.setText(round.getRoundPointsPlayer2() + " Punkte");
        }
    }

    protected void showGamePoints() {
        if (isGroupOwner) {
            txvGamePoints1.setText(round.getGamePointsPlayer1());
            txvGamePoints2.setText(round.getGamePointsPlayer2());
        } else {
            txvGamePoints1.setText(round.getGamePointsPlayer2());
            txvGamePoints2.setText(round.getGamePointsPlayer1());
        }
    }

    protected void showCardDialog(boolean sightJoker) {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.cards, null);

        if (showCardList.size() == 0) {
            initializeShowCardList(dialogView);
        } else {
            showCardList = new ArrayList<>();
            initializeShowCardList(dialogView);
        }


        showCardsBuilder.setView(dialogView);

        showCardsBuilder.setPositiveButton(R.string.btnClose, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissDialog();
            }
        });

        List<Deck> cards;

        if (sightJoker && isGroupOwner) {
            cards = round.getCardsOnHand(2);
            txvUserInformation.setText(R.string.msgSightJoker);
        } else if (sightJoker && !isGroupOwner) {
            cards = round.getCardsOnHand(1);
            txvUserInformation.setText(R.string.msgSightJoker);
        } else {
            cards = round.getAlreadyPlayedCards();
        }

        for (int index = 0; index < cards.size(); index++) {
            int res_id = getResources().getIdentifier(cards.get(index).getCardSuit() + cards.get(index).getCardRank(), "drawable", this.getPackageName());
            showCardList.get(index).setImageResource(res_id);
        }

        showCardsDialog = showCardsBuilder.create();
        showCardsDialog.show();
        showCardsDialog.getWindow().setLayout(getWindow().getDecorView().getWidth() - 100, getWindow().getDecorView().getHeight());
    }

    protected void initializeShowCardList(View dialogView) {
        showCardList.add((ImageView) dialogView.findViewById(R.id.ivShowCard1));
        showCardList.add((ImageView) dialogView.findViewById(R.id.ivShowCard2));
        showCardList.add((ImageView) dialogView.findViewById(R.id.ivShowCard3));
        showCardList.add((ImageView) dialogView.findViewById(R.id.ivShowCard4));
        showCardList.add((ImageView) dialogView.findViewById(R.id.ivShowCard5));
        showCardList.add((ImageView) dialogView.findViewById(R.id.ivShowCard6));
        showCardList.add((ImageView) dialogView.findViewById(R.id.ivShowCard7));
        showCardList.add((ImageView) dialogView.findViewById(R.id.ivShowCard8));
        showCardList.add((ImageView) dialogView.findViewById(R.id.ivShowCard9));
        showCardList.add((ImageView) dialogView.findViewById(R.id.ivShowCard10));
        showCardList.add((ImageView) dialogView.findViewById(R.id.ivShowCard11));
        showCardList.add((ImageView) dialogView.findViewById(R.id.ivShowCard12));
        showCardList.add((ImageView) dialogView.findViewById(R.id.ivShowCard13));
        showCardList.add((ImageView) dialogView.findViewById(R.id.ivShowCard14));
        showCardList.add((ImageView) dialogView.findViewById(R.id.ivShowCard15));
    }

    //endregion

    //region Finish

    protected void finishActivity(boolean won) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(IntentHelper.GAMEWON, won);
        setResult(Activity.RESULT_OK, returnIntent);

        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
    }

    //endregion
}

package cardfactory.com.extremeschnapsen.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cardfactory.com.extremeschnapsen.R;
import cardfactory.com.extremeschnapsen.services.LightSensorService;

public class ExtremeGameActivity extends GameActivity {

    private boolean lightSensorUsed;
    private Button btnCardExchange;
    private Button btnParrySightJoker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_extreme_game);

        btnCardExchange = this.findViewById(R.id.btn_kartentausch);
        btnParrySightJoker = this.findViewById(R.id.btn_endtarnjoker);

        btnCardExchange.setVisibility(View.VISIBLE);
        btnParrySightJoker.setVisibility(View.VISIBLE);

        lightSensorUsed = false;

        Intent i = new Intent(this, LightSensorService.class);
        startService(i);

        BroadcastReceiver lightSensorReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean sensorCovered = intent.getBooleanExtra(IntentHelper.LIGHTSENSOR_COVERED, false);
                if (!lightSensorUsed && sensorCovered) {
                    lightSensorCovered();
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(lightSensorReceiver, new IntentFilter(IntentHelper.LIGHTSENSOR_KEY));

    }

    private void lightSensorCovered() {
        if (round.getPlayedCardPlayer1() == null && round.getPlayedCardPlayer2() == null && round.getMyTurn()) {
            showCardDialog(true);
            round.sightJoker();
            lightSensorUsed = true;
        } else
            txvUserInformation.setText(R.string.msgSightJokerNotPossible);

    }

    @Override
    public void onClickBtnParrySightJoker(View view) {
        if (round.getMyTurn() && round.getMyTurnInCurrentMove()) {
            if (round.getSightJokerReceived()) {
                round.parrySightJoker(true);
                if (round.checkFor66()) {
                    finishActivity(round.roundWon());
                }
            } else {
                round.parrySightJoker(false);
            }
        } else {
            txvUserInformation.setText(R.string.msgParrySightJokerNotPossible);
        }
    }

    @Override
    protected void finishActivity(boolean won) {
        lightSensorUsed = false;
        super.finishActivity(won);
    }
}

package cardfactory.com.extremeschnapsen.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.view.View;

import cardfactory.com.extremeschnapsen.R;
import cardfactory.com.extremeschnapsen.services.LightSensorService;

public class ExtremeGameActivity extends GameActivity {

    private boolean lightSensorUsed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_extreme_game);

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
            round.sightJokerUsed();
            lightSensorUsed = true;
        } else
            txvUserInformation.setText(R.string.msgSightJokerNotPossible);

    }

    @Override
    public void onClickBtnParrySightJoker(View view) {
        if (round.getMyTurn() && round.getMyTurnInCurrentMove()) {
            if (round.getSightJokerReceived()) {
                round.parrySightJokerUsed(true);
                txvUserInformation.setText(R.string.msgParrySightJokerSuccess);
            } else {
                round.parrySightJokerUsed(false);
                txvUserInformation.setText(R.string.msgParrySightJokerFail);
            }
        } else {
            txvUserInformation.setText(R.string.msgParrySightJokerNotPossible);
        }
    }

    @Override
    protected void finishActivity() {
        lightSensorUsed = false;
        super.finishActivity();
    }
}

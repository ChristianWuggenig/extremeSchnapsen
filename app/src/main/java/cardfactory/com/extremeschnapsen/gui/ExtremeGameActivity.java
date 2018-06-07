package cardfactory.com.extremeschnapsen.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import java.util.List;

import cardfactory.com.extremeschnapsen.R;
import cardfactory.com.extremeschnapsen.models.Deck;
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
                    lightSensorUsed = true;
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(lightSensorReceiver, new IntentFilter(IntentHelper.LIGHTSENSOR_KEY));

    }

    private void lightSensorCovered() {
        showCardDialog(true);
        round.sightJokerUsed();

        /*List<Deck> oppositeCards;

        if (isGroupOwner) {
            oppositeCards = round.getCardsOnHand(1);
        } else {
            oppositeCards = round.getCardsOnHand(2);
        }

        round.sightJokerUsed();

        for (int index = 0; index < oppositeCards.size(); index++) {
            int res_id = getResources().getIdentifier(oppositeCards.get(index).getCardSuit() + oppositeCards.get(index).getCardRank(), "drawable", this.getPackageName());
            showCardList.get(index).setImageResource(res_id);
        }

        showCardDialog();*/
    }

    public void onClickBtnParrySightJoker(View view) {
        round.sightJokerParryUsed();
        txvUserInformation.setText(R.string.msgParrySightJoker);
    }
}

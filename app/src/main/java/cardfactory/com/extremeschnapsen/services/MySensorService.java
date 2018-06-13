package cardfactory.com.extremeschnapsen.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import static android.content.ContentValues.TAG;
import cardfactory.com.extremeschnapsen.R;
import cardfactory.com.extremeschnapsen.gui.IntentHelper;

import static cardfactory.com.extremeschnapsen.R.raw.shufflecards;

public class MySensorService extends Service implements SensorEventListener {

        float xAccel, yAccel, zAccel;
        float xPreviousAccel, yPreviousAccel, zPreviousAccel;

        boolean firstUpdate = true;
        boolean shakeInitiated = false;
        boolean turnInitiated = false;
        float shakeThreshold= 5f;
        float turnThreshold= 20f;

        String TAG = "SensorService";

        Sensor accelerometer;
        SensorManager sm;
        MediaPlayer sound;

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            sm = (SensorManager)getSystemService(SENSOR_SERVICE);
            accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sm.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);

            sound = MediaPlayer.create(getApplicationContext(), R.raw.shufflecards);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}

        @Override
        public void onSensorChanged(SensorEvent event) {
            updateAccelParameters(event.values[0], event.values[1],event.values[2]);

            if(isAccelerationChanged()[0]) {
                if ((!shakeInitiated) && isAccelerationChanged()[0]) {
                    shakeInitiated = true;
                } else if ((shakeInitiated) && isAccelerationChanged()[0]) {
                    excuteShakeAction();
                    sendMessageToGUI("shake", true);
                } else if ((shakeInitiated) && !isAccelerationChanged()[0]) {
                    shakeInitiated = false;
                }
            }else if(isAccelerationChanged()[1]) {

                if ((!turnInitiated) && isAccelerationChanged()[1]) {
                    turnInitiated = true;
                } else if ((turnInitiated) && isAccelerationChanged()[1]) {
                    executeTurnAction();
                    sendMessageToGUI("turn", true);
                } else if ((turnInitiated) && !isAccelerationChanged()[1]) {
                    turnInitiated = false;
                }
            }
        }

        private boolean[] isAccelerationChanged() {
            //Detect if acceleration values changed --> 2 axis changed --> detect shake motion
            float deltaX = Math.abs(xPreviousAccel - xAccel);
            float deltaY = Math.abs(yPreviousAccel - yAccel);
            float deltaZ = Math.abs(zPreviousAccel - zAccel);


            boolean isTurn = false;
            boolean isShake = false;
            boolean []result = new boolean[2];

            if(deltaZ >= turnThreshold){
                isTurn = true;
            }else{
                isTurn = false;
            }

            if(deltaX > shakeThreshold && deltaY > shakeThreshold){
                isShake = true;
            }

            result[0] = isShake;
            result[1] = isTurn;

            return result;
        }

        private void updateAccelParameters(float xNewAccel, float yNewAccel, float zNewAccel) {
            if(firstUpdate){
                xPreviousAccel = xNewAccel;
                yPreviousAccel = yNewAccel;
                zPreviousAccel = zNewAccel;
                firstUpdate = false;
            }else {
                xPreviousAccel = xAccel;
                yPreviousAccel = yAccel;
                zPreviousAccel = zAccel;
            }
            xAccel = xNewAccel;
            yAccel = yNewAccel;
            zAccel = zNewAccel;

        }

        @SuppressLint("MissingPermission")
        private void excuteShakeAction() {

            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);
            sound.start();

            Log.d(TAG, "excuteShakeAction: Handy wurde geschüttelt!");
            sendMessageToGUI(IntentHelper.MOVEMENT_ACTIONS[0], true);
        }

        private void executeTurnAction(){
            sendMessageToGUI(IntentHelper.MOVEMENT_ACTIONS[1], true);
            Log.d(TAG, "executeTurnAction: Handy wurde umgedreht!");
        }

        private void sendMessageToGUI(String action, boolean isAction) {
            Intent intent = new Intent(IntentHelper.MOVEMENT_SENSOR_KEY);
            intent.putExtra(action, isAction);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

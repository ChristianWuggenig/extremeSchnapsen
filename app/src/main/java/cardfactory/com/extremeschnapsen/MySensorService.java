package cardfactory.com.extremeschnapsen;

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

import static cardfactory.com.extremeschnapsen.R.raw.shufflecards;

public class MySensorService extends Service implements SensorEventListener {

    float xAccel, yAccel, zAccel;
    float xPreviousAccel, yPreviousAccel, zPreviousAccel;

    boolean firstUpdate = true;
    boolean shakeInitiated = false;
    float shakeThreshold= 5f;

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
        sm.registerListener(this,accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        sound = MediaPlayer.create(getApplicationContext(), R.raw.shufflecards);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        updateAccelParameters(event.values[0], event.values[1],event.values[2]);

        if((!shakeInitiated) && isAccelerationChanged()){
            shakeInitiated = true;
        }else if ((shakeInitiated) && isAccelerationChanged()) {
           excuteShakeAction();
        }else if ((shakeInitiated) && !isAccelerationChanged()){
            shakeInitiated = false;
        }
    }

    @SuppressLint("MissingPermission")
    private void excuteShakeAction() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
        sound.start();

    }

    private boolean isAccelerationChanged() {
        //Detect if acceleration values changed --> 2 axis changed --> detect shake motion
        float deltaX = Math.abs(xPreviousAccel - xAccel);
        float deltaY = Math.abs(yPreviousAccel - yAccel);
        float deltaZ = Math.abs(zPreviousAccel - zAccel);

        return (deltaX > shakeThreshold && deltaY > shakeThreshold)
                || (deltaY > shakeThreshold && deltaZ > shakeThreshold)
                || (deltaX > shakeThreshold && deltaZ > shakeThreshold);
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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

package cardfactory.com.extremeschnapsen.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import cardfactory.com.extremeschnapsen.gui.IntentHelper;

/**
 * this services listens for changes on the light sensor to start the sight joker
 */
public class LightSensorService extends Service implements SensorEventListener {

    private SensorManager sensorManager; //contains the sensor manager, provided by the android framework

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * on start, the service registers the sensor manager to listen for the light sensor
     * @param intent the intent of the calling activity (extreme game activity)
     * @param flags several flags
     * @param startId the start id
     * @return a code given by the super class
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); //contains the sensor object (light sensor in this case)

        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * fired when the sensor value has changed
     * @param sensorEvent contains a whole set of events of the sensor
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float value = sensorEvent.values[0];

        //10f defines the density for the sensor
        if (value < 10f) {
            sendMessageToGUI(true);
        }
    }

    /**
     * needs to be overridden, but does not do anything
     * @param sensor
     * @param i
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * unregister the listener on destroying of the service
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    /**
     * send a message to the gui when the light sensor is covered via the local broadcast manager
     * @param sensorCovered true, if the sensor is covered
     */
    public void sendMessageToGUI(boolean sensorCovered) {
        Intent intent = new Intent(IntentHelper.LIGHTSENSOR_KEY);
        intent.putExtra(IntentHelper.LIGHTSENSOR_COVERED, sensorCovered);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}

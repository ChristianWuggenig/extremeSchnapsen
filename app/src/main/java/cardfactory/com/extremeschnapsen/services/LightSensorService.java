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

import cardfactory.com.extremeschnapsen.gui.GameActivity;
import cardfactory.com.extremeschnapsen.gui.IntentHelper;

public class LightSensorService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float value = sensorEvent.values[0];

        if (value < 10f) {
            sendMessageToGUI(true);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        float value = i;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    private void sendMessageToGUI(boolean sensorCovered) {
        Intent intent = new Intent(IntentHelper.LIGHTSENSOR_KEY);
        intent.putExtra(IntentHelper.LIGHTSENSOR_COVERED, sensorCovered);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}

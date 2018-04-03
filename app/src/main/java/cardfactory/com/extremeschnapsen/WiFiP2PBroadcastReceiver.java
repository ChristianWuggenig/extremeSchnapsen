package cardfactory.com.extremeschnapsen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.*;
import android.util.Log;

/**
 * Created by Christian on 03.04.2018.
 */

public class WiFiP2PBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private Search searchActivity;
    WifiP2pManager.PeerListListener myPeerListListener;

    public WiFiP2PBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                    Search activity, WifiP2pManager.PeerListListener myPeerListListener) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.searchActivity = activity;
        this.myPeerListListener = myPeerListListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.d("tag", "wifi p2p works!");
            } else {
                // Wi-Fi P2P is not enabled
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (mManager != null) {
                mManager.requestPeers(mChannel, myPeerListListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }

}

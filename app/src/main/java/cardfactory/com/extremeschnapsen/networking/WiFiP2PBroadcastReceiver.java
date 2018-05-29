package cardfactory.com.extremeschnapsen.networking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.*;
import android.util.Log;
import android.widget.Toast;

import cardfactory.com.extremeschnapsen.R;
import cardfactory.com.extremeschnapsen.gui.SearchActivity;
import cardfactory.com.extremeschnapsen.gui.StartGameActivity;
import cardfactory.com.extremeschnapsen.gameengine.Game;

/**
 * Created by Christian on 03.04.2018.
 */

public class WiFiP2PBroadcastReceiver extends BroadcastReceiver {

    //declare the necessary P2P-Objects
    private WifiP2pManager p2pManager;
    private WifiP2pManager.Channel p2pChannel;
    WifiP2pManager.PeerListListener peerListListener;

    //declare activity-context to show toast-messages on the search-activity
    private SearchActivity searchActivity;

    public WiFiP2PBroadcastReceiver(WifiP2pManager manager,
                                    WifiP2pManager.Channel channel,
                                    SearchActivity activity,
                                    WifiP2pManager.PeerListListener myPeerListListener) {
        super();
        this.p2pManager = manager;
        this.p2pChannel = channel;
        this.searchActivity = activity;
        this.peerListListener = myPeerListListener;
    }

    //is executed every time a system action with a defined filter in "intentFilters" is thrown
    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();

        //decide which P2P-Action was thrown
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, 1);

            //check if P2P is possible with the given hardware-settings (for example if P2P is not available for some reason, a toast message is shown)
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Toast.makeText(searchActivity, R.string.msgWifiP2pSearching, Toast.LENGTH_SHORT).show();
                Log.d("tag", "wifi p2p works!");
            } else {
                Toast.makeText(searchActivity, R.string.msgWifiP2pNotWorking, Toast.LENGTH_LONG).show();
            }
        }
        //this action is thrown when peers are available on the network
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (p2pManager != null) {

                //tell the manager to request the available peers
                p2pManager.requestPeers(p2pChannel, peerListListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            p2pManager.requestConnectionInfo(p2pChannel, new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                    if(wifiP2pInfo.groupFormed){
                        Intent startGameActivityIntent = new Intent(context, StartGameActivity.class);
                        startGameActivityIntent.putExtra("IS_GROUP_OWNER", wifiP2pInfo.isGroupOwner);
                        searchActivity.startActivity(startGameActivityIntent);
                    }
                }
            });
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }

}

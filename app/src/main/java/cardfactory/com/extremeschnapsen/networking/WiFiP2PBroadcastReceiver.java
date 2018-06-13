package cardfactory.com.extremeschnapsen.networking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.*;
import android.util.Log;
import android.widget.Toast;

import cardfactory.com.extremeschnapsen.R;
import cardfactory.com.extremeschnapsen.gui.IntentHelper;
import cardfactory.com.extremeschnapsen.gui.SearchActivity;
import cardfactory.com.extremeschnapsen.gui.StartGameActivity;
import cardfactory.com.extremeschnapsen.gameengine.Game;

/**
 * Created by Christian on 03.04.2018.
 */

public class WiFiP2PBroadcastReceiver extends BroadcastReceiver {

    //declare the necessary P2P-Objects
    private WifiP2pManager p2pManager; //the manager object used to manage the p2p connection
    private WifiP2pManager.Channel p2pChannel; //contains the p2p channel
    WifiP2pManager.PeerListListener peerListListener; //the peerlistlistener object, which contains a list of all available peers

    private SearchActivity searchActivity; //declare activity-context to show toast-messages on the search-activity

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

    /**
     * is executed every time a system action with a defined filter in "intentFilters" is thrown
     * @param context the activity context
     * @param intent the intent with the information on the p2p-action
     */
    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();

        //decide which P2P-Action was thrown
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, 1);

            //check if P2P is possible with the given hardware-settings (for example if P2P is not available for some reason, a toast message is shown)
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Toast.makeText(searchActivity, R.string.msgWifiP2pSearching, Toast.LENGTH_SHORT).show();
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

        }
        // Respond to new connection or disconnections
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            p2pManager.requestConnectionInfo(p2pChannel, new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                    if(wifiP2pInfo.groupFormed){
                        Intent startGameActivityIntent = new Intent(context, StartGameActivity.class);
                        startGameActivityIntent.putExtra(IntentHelper.IS_GROUP_OWNER, wifiP2pInfo.isGroupOwner);
                        searchActivity.startActivity(startGameActivityIntent);
                    }
                }
            });
        }
    }

}

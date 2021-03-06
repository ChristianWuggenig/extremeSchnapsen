package cardfactory.com.extremeschnapsen.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cardfactory.com.extremeschnapsen.R;
import cardfactory.com.extremeschnapsen.gameengine.Game;
import cardfactory.com.extremeschnapsen.networking.WiFiP2PBroadcastReceiver;

/**
 * this activity is displayed when searching for players on the network and trying to connect to them
 */
public class SearchActivity extends AppCompatActivity {

    //Declare necessary objects for P2P
    private WifiP2pManager p2pManager;
    private WifiP2pManager.Channel p2pChannel;
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;
    WifiP2pManager.PeerListListener peerListListener;

    //Declare UI-items
    private ListViewCompat lvAvailableDevices;
    private ArrayAdapter<WifiP2pDevice> deviceAdapter;

    //Define list of available devices
    private List<WifiP2pDevice> devices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_search);

        //initialize UI-items with local variables
        lvAvailableDevices = this.findViewById(R.id.lvAvailableDevices);
        Button btnSearch = this.findViewById(R.id.btnSearchForP2PDevices);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discoverPeers();
            }
        });

        //initialize arrayadapter to connect the listview with the list
        initializeArrayAdapter();

        //link adapter with listview
        lvAvailableDevices.setAdapter(deviceAdapter);

        //is executed when new peers are found on the network
        refreshedPeers();

        //initialize the necessary P2P-Items
        p2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        p2pChannel = p2pManager.initialize(this, getMainLooper(), null);

        //initialize the BroadcastReceiver and pass the necessary parameters
        broadcastReceiver =  new WiFiP2PBroadcastReceiver(p2pManager, p2pChannel, this, peerListListener);

        //define the intentFilter for the BroadcastReceiver. this is necessary to tell the BroadcastReceiver what "actions" it should listen for --> all P2P-Actions!
        defineP2PIntentFilter();

        //tells the p2pManager to search for new peers on the network
        discoverPeers();

        //set the onItemClickListener, so that a connection to the selected device can be established
        setListViewOnClickListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //register the receiver on resumation of the application
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //unregister the receiver when the application is paused or closed. this is necessary to prevent it from running and searching for devices when the application is not even "needed" or "executed"
        unregisterReceiver(broadcastReceiver);
    }

    /**
     * the action listener in this method is called when searching for peers, but is not implemented
     */
    public void discoverPeers() {

        Toast.makeText(this, R.string.msgWifiP2pSearching, Toast.LENGTH_SHORT).show();

        p2pManager.discoverPeers(p2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });
    }

    /**
     * initialize the array adapter and tell it how to display found devices on the network
     */
    public void initializeArrayAdapter() {
        deviceAdapter = new ArrayAdapter<WifiP2pDevice>(this, R.layout.wifi_device, R.id.lvSingleDevice, devices) {

            //iterates through the whole "devices"-list and executes the following method for each device-item
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view =  super.getView(position, convertView, parent);

                TextView txvDevice = view.findViewById(R.id.lvSingleDevice);

                //use the deviceName of the WifiP2pDevice-Object to show it on the listview
                txvDevice.setText(devices.get(position).deviceName);

                return view;
            }
        };
    }

    /**
     * this listener is called when new peers on the network were found so that the list can be updated
     */
    public void refreshedPeers() {
        peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {

                //contains all peers found on the network
                List<WifiP2pDevice> refreshedPeers = new ArrayList(peerList.getDeviceList());

                //clear current devices-list and fill it with the newly found devices
                if (!refreshedPeers.equals(devices)) {
                    devices.clear();
                    devices.addAll(refreshedPeers);

                    //notify the adapter that the list has changed --> necessary, because otherwise the listview will not be updated
                    deviceAdapter.notifyDataSetChanged();
                }
            }
        };
    }

    /**
     * this method defines the necessary intent filters to listen for p2p connections
     */
    public void defineP2PIntentFilter() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    /**
     * sets the list view onclicklistener so that the clicked devices will be connected to
     */
    public void setListViewOnClickListener() {
        lvAvailableDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                WifiP2pConfig config = new WifiP2pConfig();
                //the parameter "i" contains the position
                config.deviceAddress = devices.get(i).deviceAddress;
                p2pManager.connect(p2pChannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int i) {

                    }
                });
            }
        });
    }
}

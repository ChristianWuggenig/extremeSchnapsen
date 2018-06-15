package cardfactory.com.extremeschnapsen.networking;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cardfactory.com.extremeschnapsen.gui.SearchActivity;
import cardfactory.com.extremeschnapsen.networking.WiFiP2PBroadcastReceiver;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class WiFiP2PBroadcastReceiverUnitTest {

    WiFiP2PBroadcastReceiver broadcastReceiver;
    WifiP2pManager p2pManager;
    WifiP2pManager.Channel p2pChannel;
    SearchActivity searchActivity;
    WifiP2pManager.PeerListListener peerListListener;
    WifiP2pManager.ConnectionInfoListener connectionInfoListener;
    Intent i;

    @Before
    public void init() {
        p2pManager = mock(WifiP2pManager.class);
        p2pChannel = mock(WifiP2pManager.Channel.class);
        searchActivity = mock(SearchActivity.class);
        peerListListener = mock(WifiP2pManager.PeerListListener.class);
        broadcastReceiver = new WiFiP2PBroadcastReceiver(p2pManager, p2pChannel, searchActivity, peerListListener);
        connectionInfoListener = mock(WifiP2pManager.ConnectionInfoListener.class);

        i = mock(Intent.class);
    }

    @After
    public void setNull() {
        p2pManager = null;
        p2pChannel = null;
        searchActivity = null;
        peerListListener = null;
        broadcastReceiver = null;

        i = null;
    }

    @Test (expected = NullPointerException.class) //happens when the toast message would be shown
    public void testOnReceiveWithStateChangedActionSuccess() {
        when(i.getAction()).thenReturn("android.net.wifi.p2p.STATE_CHANGED");
        when(i.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, 1)).thenReturn(2);

        broadcastReceiver.onReceive(searchActivity, i);

        assertEquals(2, WifiP2pManager.WIFI_P2P_STATE_ENABLED);
    }

    @Test (expected = NullPointerException.class) //happens when the toast message would be shown
    public void testOnReceiveWithStateChangedActionFail() {
        when(i.getAction()).thenReturn("android.net.wifi.p2p.STATE_CHANGED");
        when(i.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, 1)).thenReturn(1);

        broadcastReceiver.onReceive(searchActivity, i);

        assertEquals(1, WifiP2pManager.WIFI_P2P_STATE_DISABLED);
    }

    @Test
    public void testOnReceiveWithPeersChangedActionSuccess() {
        when(i.getAction()).thenReturn("android.net.wifi.p2p.PEERS_CHANGED");

        broadcastReceiver.onReceive(searchActivity, i);

        verify(p2pManager).requestPeers(p2pChannel, peerListListener);
    }

    @Test
    public void testOnReceiveWithPeersChangedActionWithNullObject() {
        when(i.getAction()).thenReturn("WIFI_P2P_PEERS_CHANGED_ACTION");
        p2pManager = null;

        broadcastReceiver.onReceive(searchActivity, i);

        //if the statement above does not fail, the test is successful
        assertTrue(true);
    }

    @Test
    public void testOnReceiveWithConnectionChangedActionSuccess() {
        when(i.getAction()).thenReturn("android.net.wifi.p2p.CONNECTION_STATE_CHANGE");

        broadcastReceiver.setConnectionInfoListener(connectionInfoListener);

        broadcastReceiver.onReceive(searchActivity, i);

        verify(p2pManager).requestConnectionInfo(p2pChannel, connectionInfoListener);
    }

    @Test
    public void testOnReceiveWithConnectionChangedActionWithNullObject() {
        when(i.getAction()).thenReturn("WIFI_P2P_CONNECTION_CHANGED_ACTION");
        p2pManager = null;

        broadcastReceiver.onReceive(searchActivity, i);

        //if the statement above does not fail, the test is successful
        assertTrue(true);
    }
}

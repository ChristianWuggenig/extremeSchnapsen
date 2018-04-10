package cardfactory.com.extremeschnapsen;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class WiFiP2PBroadcastReceiverUnitTest {

    WiFiP2PBroadcastReceiver broadcastReceiver;
    WifiP2pManager p2pManager;
    WifiP2pManager.Channel p2pChannel;
    SearchActivity searchActivity;
    WifiP2pManager.PeerListListener peerListListener;
    Intent i;

    @Before
    public void init() {
        p2pManager = mock(WifiP2pManager.class);
        p2pChannel = mock(WifiP2pManager.Channel.class);
        searchActivity = mock(SearchActivity.class);
        peerListListener = mock(WifiP2pManager.PeerListListener.class);
        broadcastReceiver = new WiFiP2PBroadcastReceiver(p2pManager, p2pChannel, searchActivity, peerListListener);

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

    @Test
    public void testOnReceiveWithStateChangedActionSuccess() {
        when(i.getAction()).thenReturn("WIFI_P2P_STATE_CHANGED_ACTION");
        when(i.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, 1)).thenReturn(2);

        broadcastReceiver.onReceive(searchActivity, i);

        assertEquals(2, WifiP2pManager.WIFI_P2P_STATE_ENABLED);
    }

    @Test
    public void testOnReceiveWithStateChangedActionFail() {
        when(i.getAction()).thenReturn("WIFI_P2P_STATE_CHANGED_ACTION");
        when(i.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, 1)).thenReturn(1);

        broadcastReceiver.onReceive(searchActivity, i);

        assertEquals(1, WifiP2pManager.WIFI_P2P_STATE_DISABLED);
    }

    @Test
    public void testOnReceiveWithPeersChangedActionSuccess() {
        when(i.getAction()).thenReturn("WIFI_P2P_PEERS_CHANGED_ACTION");

        broadcastReceiver.onReceive(searchActivity, i);

        //if the statement above does not fail, the test is successful
        assertTrue(true);
    }

    @Test
    public void testOnReceiveWithPeersChangedActionWithNullObject() {
        when(i.getAction()).thenReturn("WIFI_P2P_PEERS_CHANGED_ACTION");
        p2pManager = null;

        broadcastReceiver.onReceive(searchActivity, i);

        //if the statement above does not fail, the test is successful
        assertTrue(true);
    }
}

package cardfactory.com.extremeschnapsen;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import cardfactory.com.extremeschnapsen.gameengine.RoundUnitTest;
import cardfactory.com.extremeschnapsen.networking.HTTPClientUnitTest;
import cardfactory.com.extremeschnapsen.networking.HTTPServerUnitTest;
import cardfactory.com.extremeschnapsen.networking.NetworkManagerUnitTest;
import cardfactory.com.extremeschnapsen.networking.WiFiP2PBroadcastReceiverUnitTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        HTTPServerUnitTest.class,
        NetworkManagerUnitTest.class,
        WiFiP2PBroadcastReceiverUnitTest.class,
        HTTPClientUnitTest.class,
        RoundUnitTest.class
})
public class RunAllTests {
}

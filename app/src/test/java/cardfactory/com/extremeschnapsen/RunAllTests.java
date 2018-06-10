package cardfactory.com.extremeschnapsen;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        HTTPServerUnitTest.class,
        NetworkManagerUnitTest.class,
        WiFiP2PBroadcastReceiverUnitTest.class,
        HTTPClientUnitTest.class
})
public class RunAllTests {
}

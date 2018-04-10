package cardfactory.com.extremeschnapsen;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.espresso.Espresso;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SearchActivityTest {

    @Rule
    public ActivityTestRule<SearchActivity> searchActivityRule = new ActivityTestRule<>(SearchActivity.class);

    @Test
    public void testBtnSearchClick() {
        //click the "search"-button
        Espresso.onView(ViewMatchers.withId(R.id.btnSearchForP2PDevices)).perform(ViewActions.click());

        //verify that the Toast-Message is shown
        Espresso.onView(withText(R.string.msgWifiP2pSearching)).inRoot(withDecorView(not(is(searchActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
}

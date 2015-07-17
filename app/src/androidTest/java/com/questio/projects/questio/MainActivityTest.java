package com.questio.projects.questio;

import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.espresso.util.TreeIterables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;

import com.questio.projects.questio.activities.MainActivity;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.questio.projects.questio.TestHelper.WaitForId.waitId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    public static final String LOG_TAG = MainActivityTest.class.getSimpleName();
    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);


    @Before
    public void setUp() {
        Log.d(LOG_TAG, "Test was set up!");
    }

    @After
    public void tearDown() {
        Log.d(LOG_TAG, "Test was torn down!");
    }

    @Test
    public void shouldBeAbleToLaunchMainScreen() {
        onView(withId(R.id.ranking_section))
                .check(matches(isDisplayed()));
    }

    // Test Navigate Tab
    @Test
    public void shouldGoToRankingSectionWhenClickRankingIcon() {

        onView(withContentDescription("rankingTab"))
                .perform(click());
        onView(isRoot())
                .perform(waitId(R.id.ranking_section, 5000));
        onView(withId(R.id.ranking_section))
                .check(matches(isDisplayed()));

    }

    @Test
    public void shouldGoToSearchSectionWhenClickSeachIcon() {
        onView(withContentDescription("searchTab"))
                .perform(click());
        onView(isRoot())
                .perform(waitId(R.id.search_section, 5000));
        onView(withId(R.id.search_section))
                .check(matches(isDisplayed()));

    }

    @Test
    public void shouldGoToQuestSectionWhenClickQuestIcon() {
        onView(withContentDescription("questTab"))
                .perform(click());
        onView(isRoot())
                .perform(waitId(R.id.quest_section, 5000));
        onView(withId(R.id.quest_section))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldGoToHOFSectionWhenClickHOFIcon() {
        onView(withContentDescription("hofTab"))
                .perform(click());
        onView(isRoot())
                .perform(waitId(R.id.hof_section, 5000));

        onView(withId(R.id.hof_section))
                .check(matches(isDisplayed()));

    }

    @Test
    public void shouldGoToProfileSectionWhenClickProfileIcon() {

        onView(withContentDescription("profileTab"))
                .perform(click());
        onView(isRoot())
                .perform(waitId(R.id.profile_section, 5000));
        onView(withId(R.id.profile_section))
                .check(matches(isDisplayed()));

    }
    // End of navigate tab

    @Test
    public void shouldShowCameraLayoutWhenClickQRCodeScanIconQuest() {
        if (!QuestioApplication.isLogin()) {
            onView(withId(R.id.sign_in_button))
                    .perform(click());
        }
        onView(withContentDescription("questTab"))
                .perform(click());
        onView(withId(R.id.action_qrcode_scan))
                .perform(click());
        onView(withId(R.id.cameraPreview))
                .check(matches(isDisplayed()));
        pressBack();
        onView(withId(R.id.action_qrcode_scan))
                .check(matches(isDisplayed()));

    }

}
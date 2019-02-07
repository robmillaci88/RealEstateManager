package com.example.robmillaci.realestatemanager;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.example.robmillaci.realestatemanager.activities.about_activity.AboutActivity;
import com.example.robmillaci.realestatemanager.activities.add_listing_activity.AddListingView;
import com.example.robmillaci.realestatemanager.activities.book_viewing_activity.BookViewingActivity;
import com.example.robmillaci.realestatemanager.activities.contact_activity.ContactActivity;
import com.example.robmillaci.realestatemanager.activities.customer_account.AccountActivity;
import com.example.robmillaci.realestatemanager.activities.feedback_activities.FeedBackAwaitingAction;
import com.example.robmillaci.realestatemanager.activities.feedback_activities.FeedbackPendingReceived;
import com.example.robmillaci.realestatemanager.activities.listing_map_activity.ListingsMapView;
import com.example.robmillaci.realestatemanager.activities.main_activity.MainActivityView;
import com.example.robmillaci.realestatemanager.activities.offers_activities.AcceptedOffers;
import com.example.robmillaci.realestatemanager.activities.offers_activities.MakeAnOffer;
import com.example.robmillaci.realestatemanager.activities.offers_activities.OffersAwaitingAction;
import com.example.robmillaci.realestatemanager.activities.search_activity.SearchActivityView;
import com.example.robmillaci.realestatemanager.activities.search_activity.SearchResultsView;
import com.example.robmillaci.realestatemanager.activities.search_activity.StreetViewActivity;
import com.example.robmillaci.realestatemanager.activities.sign_in_activities.StartActivity;
import com.example.robmillaci.realestatemanager.activities.valuations_activities.BookEvaluationActivity;
import com.example.robmillaci.realestatemanager.activities.valuations_activities.ConfirmationActivity;
import com.example.robmillaci.realestatemanager.activities.valuations_activities.UpcomingValuations;
import com.example.robmillaci.realestatemanager.activities.viewings_activities.AwaitingAction;
import com.example.robmillaci.realestatemanager.activities.viewings_activities.ConfirmedViewings;
import com.example.robmillaci.realestatemanager.activities.viewings_activities.ViewingsHistory;
import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.example.robmillaci.realestatemanager.fragments.ListingItemFragment;
import com.example.robmillaci.realestatemanager.fragments.MapViewFragment;
import com.example.robmillaci.realestatemanager.utils.Utils;

import junit.framework.AssertionFailedError;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.example.robmillaci.realestatemanager.activities.search_activity.SearchResultsView.FRAGMENT_TAG;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UITests {

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.robmillaci.realestatemanager", appContext.getPackageName());
    }

    @Before
    public void waitForSplashScreen() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void releaseIntents() {
        try {
            Intents.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Rule
    public ActivityTestRule<StartActivity> mStartActivityActivityTestRule =
            new ActivityTestRule<>(StartActivity.class);

    @Rule
    public final ActivityTestRule<MainActivityView> mMainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivityView.class);


    private void openNavDrawer() {
        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.appbar),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());
    }


    private void waitForProgressDialog() {
        try {
            Thread.sleep(3500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }


    private static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Activity getActivityInstance() {
        final Activity[] currentActivity = new Activity[1];
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection<Activity> resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                //noinspection LoopStatementThatDoesntLoop
                for (Activity act : resumedActivities) {
                    currentActivity[0] = act;
                    break;
                }
            }
        });

        return currentActivity[0];
    }

    @Test
    public void checkNavDrawerHasData() {
        onView(withId(R.id.drawer_layout)).perform(click()); //open the nav drawer

        //check user name is populated
        final String userName = FirebaseHelper.getLoggedInUser();

        onView(withId(R.id.user_name)).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                if (noViewFoundException != null) {
                    throw noViewFoundException;
                }
                assertEquals(((TextView) view).getText(), "Hi " + userName);
            }
        });
    }


    @Test
    public void networkConnectivity() {
        boolean networkConnection = false;
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mMainActivityActivityTestRule.getActivity().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        //noinspection SimplifiableIfStatement
        if (mConnectivityManager != null) {
            networkConnection = mConnectivityManager.getActiveNetworkInfo() != null
                    && mConnectivityManager.getActiveNetworkInfo().isAvailable()
                    && mConnectivityManager.getActiveNetworkInfo().isConnected();
        }

        if (networkConnection) {
            assertTrue(Utils.CheckConnectivity(mMainActivityActivityTestRule.getActivity().getApplicationContext()));
        } else {
            assertFalse(Utils.CheckConnectivity(mMainActivityActivityTestRule.getActivity().getApplicationContext()));
        }
    }


    //Check nav view clicks
    @Test
    public void navClicksSearch() {
        navClicksCheck(R.id.search_nav, SearchActivityView.class.getName());
    }

    @Test
    public void navClicksContact() {
        navClicksCheck(R.id.contact_nav, ContactActivity.class.getName());
    }

    @Test
    public void navClicksAbout() {
        navClicksCheck(R.id.about_nav, AboutActivity.class.getName());
    }

    @Test
    public void navClicksBookEvaluation() {
        navClicksCheck(R.id.bookevaluation_text, BookEvaluationActivity.class.getName());
    }

    @Test
    public void navClicksBookUpcomingEvals() {
        navClicksCheck(R.id.upcoming_valuations_text, UpcomingValuations.class.getName());
    }

    @Test
    public void navClicksAwaitingAction() {
        navClicksCheck(R.id.awaiting_action, AwaitingAction.class.getName());
    }


    @Test
    public void navClicksConfirmedViewings() {
        navClicksCheck(R.id.confirmed_viewings, ConfirmedViewings.class.getName());
    }


    @Test
    public void navClicksViewingsHistory() {
        navClicksCheck(R.id.viewings_history, ViewingsHistory.class.getName());
    }

    @Test
    public void navClicksAwaitingFeedback() {
        navClicksCheck(R.id.awaiting_feedback, FeedBackAwaitingAction.class.getName());
    }

    @Test
    public void navClicksPendingRecievedFeedback() {
        navClicksCheck(R.id.pending_recieved_feedback, FeedbackPendingReceived.class.getName());
    }

    @Test
    public void navClicksOffersAwaitingAction() {
        navClicksCheck(R.id.offers_awaiting_action, OffersAwaitingAction.class.getName());
    }

    @Test
    public void navClicksAcceptedOffers() {
        navClicksCheck(R.id.accepted_offers, AcceptedOffers.class.getName());
    }

    @Test
    public void navClicksSettings() {
        navClicksCheck(R.id.settings_btn, AccountActivity.class.getName());
    }


    private void navClicksCheck(int viewId, String className) {
        openNavDrawer();

        Intents.init();
        onView(withId(viewId)).perform(click());

        waitForProgressDialog();

        intended(hasComponent(className));
        Intents.release();
    }


    //Check Floating action buttons
    @Test
    public void checkSearchFab() {
        Intents.init();
        onView(withId(R.id.search_fab)).perform(click());

        intended(hasComponent(SearchActivityView.class.getName()));
        Intents.release();
    }

    @Test
    public void checkAddListingFab() {
        Intents.init();
        onView(withId(R.id.add_listing_fab)).perform(click());

        intended(hasComponent(AddListingView.class.getName()));
        Intents.release();
    }

    @Test
    public void checkMapViewFab() {
        //give permissions if neccessary
        PermissionAllower.allowPermissionsIfNeeded(INTERNET);

        Intents.init();
        onView(withId(R.id.geolocate_fab)).perform(click());

        Activity activity = getActivityInstance();

        //if we have internet check that the intent is launched, otherwise check a toast is displayed
        if (Utils.CheckConnectivity(activity.getApplication().getApplicationContext())) {
            intended(hasComponent(ListingsMapView.class.getName()));
            Intents.release();
        } else {
            Activity a = getActivityInstance();
            ToastChecker.checkToast(a.getString(R.string.internet_required), a);
        }
    }

    @Test
    public void checkSyncFab() {
        onView(withId(R.id.sync_db_fab)).perform(click());

        //check the progress dialog is shown
        onView(withText(R.string.database_sync_message)).check(matches(isDisplayed()));
    }


    //Test search results
    @Test
    public void SearchResultsView() {
        Intents.init();
        onView(withId(R.id.search_fab)).perform(click());
        onView(withId(R.id.search_btn)).perform(click());
        intended(hasComponent(SearchResultsView.class.getName()));


        //now check if we have recycler view items, that clicking them creates the Listing fragment
        sleep(1000);

        onView(withId(R.id.search_results_recyclerview)).check(new RecyclerViewItemCountAssertion(1));

        onView(withId(R.id.search_results_recyclerview)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        sleep(2000);

        //now check the fragment is added and visible
        Fragment currentFragment = ((SearchResultsView) getActivityInstance()).getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);

        if (currentFragment instanceof ListingItemFragment) {
            assertTrue(currentFragment.isAdded() && currentFragment.isVisible());
        } else {
            throw new AssertionFailedError("Fragment is not here...");
        }


        //now we can test the different functions within the fragment
        String activityTitle = getActivityInstance().getTitle().toString();

        ItemFragmentDescription(activityTitle);
        ItemFragmentMap();
        ItemFragmentStreetView();
        ItemFragmentMakeAnOffer();
        ItemFragmentBookAViewing();


    }


    //Listing item fragment tests...
    private void ItemFragmentDescription(String activityTitle) {
        onView(withId(R.id.description_tv)).perform(click());
        onView(withText(String.format("%s %s", "About", activityTitle))).check(matches(isDisplayed()));
        onView(withText(R.string.points_of_interest)).check(matches(isDisplayed()));
        onView(withText("Close")).perform(click()); //close the alert dialog
    }


    private void ItemFragmentMap() {
        PermissionAllower.allowPermissionsIfNeeded(ACCESS_FINE_LOCATION);

        if (!Utils.isTablet(mMainActivityActivityTestRule.getActivity().getApplicationContext())) {
            onView(withId(R.id.mapbtn)).perform(click());
        }

        Fragment currentFragment = ((SearchResultsView) getActivityInstance()).getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);

        if (currentFragment instanceof MapViewFragment) {
            assertTrue(currentFragment.isAdded() && currentFragment.isVisible());

            if (Utils.CheckConnectivity(getActivityInstance().getApplication().getApplicationContext())) {
                assertEquals(1, ((MapViewFragment) currentFragment).getMapMarker().size()); //we should have and only have one marker on the map
            } else {
                Activity a = getActivityInstance();
                ToastChecker.checkToast(a.getString(R.string.no_location), a);
            }
        } else {
            throw new AssertionFailedError("Fragment is not here...");
        }

        Espresso.pressBack();
    }


    private void ItemFragmentStreetView() {
        //allow the permission if neccessary
        PermissionAllower.allowPermissionsIfNeeded(ACCESS_FINE_LOCATION);

        onView(withId(R.id.streetView)).perform(click());

        //if we have internet, check the intent is launched. If there is no internet, check toast is displayed
        Activity a = getActivityInstance();
        if (Utils.CheckConnectivity(a.getApplication().getApplicationContext())) {
            intended(hasComponent(StreetViewActivity.class.getName()));
        } else {
            ToastChecker.checkToast(a.getString(R.string.internet_required), a);
        }

        Espresso.pressBack();
    }


    private void ItemFragmentBookAViewing() {
        Intents.init();

        onView(withId(R.id.book_a_viewing_btn))
                .perform(scrollTo());

        onView(withId(R.id.book_a_viewing_btn)).perform(click());
        intended(hasComponent(BookViewingActivity.class.getName()));

        //now check we can submit a viewing time
        onView(withId(R.id.times_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        sleep(2000);

        //Scroll to the bottom of the page if required to display the continue button
        onView(withId(R.id.continue_btn))
                .perform(scrollTo());

        onView(withId(R.id.continue_btn)).perform(click());

        sleep(2000);
        intended(hasComponent(ConfirmationActivity.class.getName()));

        //test that the correct view is set for the confirmation activity class
        View rootView = getActivityInstance().getWindow().getDecorView().findViewById(android.R.id.content);
        View expectedView = rootView.findViewById(R.id.book_viewing_confirmation_id);

        assertNotNull(expectedView);

        Espresso.pressBack();
    }


    private void ItemFragmentMakeAnOffer() {
        onView(withId(R.id.make_an_offer_button)).perform(click());
        intended(hasComponent(MakeAnOffer.class.getName()));

        //try to make an offer without entering a offer value - toast should display
        onView(withId(R.id.submit_offer_button)).perform(click());


        ToastChecker.checkToast(getActivityInstance().getString(R.string.enter_offer_value), getActivityInstance());

        //now enter an offer value
        onView(withId(R.id.offer_edit_text)).perform(typeTextIntoFocusedView("300000"));
        //click the button again
        onView(withId(R.id.submit_offer_button)).perform(click());
        intended(hasComponent(ConfirmationActivity.class.getName()));

        //test that the correct view is set for the confirmation activity class
        View rootView = getActivityInstance().getWindow().getDecorView().findViewById(android.R.id.content);
        View expectedView = rootView.findViewById(R.id.make_an_offer_confirmation);

        assertNotNull(expectedView);

        Espresso.pressBack();
        Espresso.pressBack();

        Intents.release();
    }


}

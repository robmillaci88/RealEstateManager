package com.example.robmillaci.realestatemanager.activities.main_activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.BaseActivity;
import com.example.robmillaci.realestatemanager.activities.about_activity.AboutActivity;
import com.example.robmillaci.realestatemanager.activities.add_listing_activity.AddListingView;
import com.example.robmillaci.realestatemanager.activities.contact_activity.ContactActivity;
import com.example.robmillaci.realestatemanager.activities.customer_account.AccountActivity;
import com.example.robmillaci.realestatemanager.activities.customer_account.ProfileActivity;
import com.example.robmillaci.realestatemanager.activities.feedback_activities.FeedBackAwaitingAction;
import com.example.robmillaci.realestatemanager.activities.feedback_activities.FeedbackPendingReceived;
import com.example.robmillaci.realestatemanager.activities.listing_map_activity.ListingsMapView;
import com.example.robmillaci.realestatemanager.activities.offers_activities.AcceptedOffers;
import com.example.robmillaci.realestatemanager.activities.offers_activities.OffersAwaitingAction;
import com.example.robmillaci.realestatemanager.activities.search_activity.SearchActivityView;
import com.example.robmillaci.realestatemanager.activities.valuations_activities.BookEvaluationActivity;
import com.example.robmillaci.realestatemanager.activities.valuations_activities.UpcomingValuations;
import com.example.robmillaci.realestatemanager.activities.viewings_activities.AwaitingAction;
import com.example.robmillaci.realestatemanager.activities.viewings_activities.ConfirmedViewings;
import com.example.robmillaci.realestatemanager.activities.viewings_activities.ViewingsHistory;
import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.example.robmillaci.realestatemanager.utils.SharedPreferenceHelper;
import com.example.robmillaci.realestatemanager.utils.ToastModifications;
import com.example.robmillaci.realestatemanager.utils.Utils;
import com.example.robmillaci.realestatemanager.utils.network_utils.NetworkListener;
import com.example.robmillaci.realestatemanager.utils.network_utils.SynchListenerCallback;
import com.jakewharton.rxbinding3.view.RxView;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static com.example.robmillaci.realestatemanager.databases.firebase.FirebaseContract.USER_DATABASE_ISADMIN_FIELD;

/**
 * The main activity of this app. Creates the nav drawer and manages administrator vs user views
 * This activity also creates and registers a network reciever to monitor network connectivity. See {@link NetworkListener}
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class MainActivityView extends BaseActivity implements SynchListenerCallback, FirebaseHelper.AdminCheckCallback, MainActivityPresenter.View {
    private static long back_pressed; //the time between back button presses

    @SuppressWarnings("unused")
    private TextView textViewMain;
    @SuppressWarnings("unused")
    private TextView textViewQuantity;


    private ArrayList<View> adminViews; //Arraylist to store all views to be show if the user is an admin, or hidden if they are a regular user
    private DrawerLayout mDrawerLayout; //the DrawerLayout
    private CompositeDisposable mCompositeDisposable; //holds all disposables

    private ProgressDialog pd;

    private IntentFilter intentFilter; //the intent filter used for network changes
    private NetworkListener mNetworkListener; //the network change listener

    private MainActivityPresenter mMainActivityPresenter; //this views presenter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeNavDrawer();
        initializeNavViewClicks();

        adminViews = new ArrayList<>(); //this stores all views that are visible to administrators

        TextView userName = findViewById(R.id.user_name);
        if (FirebaseHelper.getLoggedInUser() != null) {
            userName.setText(String.format(getString(R.string.hi), FirebaseHelper.getLoggedInUser()));
        } else {
            userName.setText(R.string.profile_text);
        }

        mMainActivityPresenter = new MainActivityPresenter(this);

        displayAdminViews();

        setNetworkListener();

        configureFabs();

        //this.textViewMain = findViewById(R.id.activity_second_activity_text_view_main);
        //this caused an initial error because it is refering to a textview defined in second activity and we are trying to find the ID
        //after inflating the View for the main activity.
        //This should be this.textViewMain = findViewByID(R.id.activity_main_activity_text_view_main)

        // this.textViewMain = findViewById(R.id.activity_main_activity_text_view_main);
        //this.textViewQuantity = findViewById(R.id.activity_main_activity_text_view_quantity);


        if (Utils.CheckConnectivity(this)) { //Check whether the user logged in has admin access or not
            FirebaseHelper.getInstance().setAdminCallback(this).checkAdminAccess();
        }

        checkProfileUpdated(); //check whether this is the users first login, if so display the "update profile" snackbar
    }


    /**
     * Checks whether the user has updated their profile or not and displays a snackbar if not
     */
    private void checkProfileUpdated() {
        if (new SharedPreferenceHelper(getApplicationContext()).isProfileUpdated()) {
            createProfileSnackbar();
        }
    }


    /**
     * Snackbar that informs the user to update their profile details. This is shown indefinitely until the user opens the profile activity
     */
    private void createProfileSnackbar() {
        Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.update_profile_message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.update_action, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new SharedPreferenceHelper(getApplicationContext()).setProfileUpdated(); //the user has opened their profile, we can set the profile update to true
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.pastel_green))
                .show();
    }


    /**
     * Create the Nav drawer
     */
    private void initializeNavDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            findViewById(R.id.appbar).bringToFront();
        }

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);

        toggle.syncState();

    }


    /**
     * Initialize all the nav bar on click methods
     */
    @SuppressLint("CheckResult")
    private void initializeNavViewClicks() {
        mCompositeDisposable = new CompositeDisposable();

        NavigationView navigationView = findViewById(R.id.nav_view);

        ImageView search_nav = navigationView.findViewById(R.id.search_nav);
        Disposable searchDisposable = RxView.clicks(search_nav).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                startActivity(new Intent(getApplicationContext(), SearchActivityView.class));
            }
        });
        mCompositeDisposable.add(searchDisposable);


        ImageView contact_nav = navigationView.findViewById(R.id.contact_nav);
        Disposable contactDisposable = RxView.clicks(contact_nav)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        startActivity(new Intent(getApplicationContext(), ContactActivity.class));
                    }
                });
        mCompositeDisposable.add(contactDisposable);


        ImageView about_nav = navigationView.findViewById(R.id.about_nav);
        Disposable about_navDisposable = RxView.clicks(about_nav)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                    }
                });
        mCompositeDisposable.add(about_navDisposable);


        TextView bookevaluation_text = navigationView.findViewById(R.id.bookevaluation_text);
        Disposable bookevaluation_textDisposable = RxView.clicks(bookevaluation_text)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        startActivity(new Intent(getApplicationContext(), BookEvaluationActivity.class));
                    }
                });
        mCompositeDisposable.add(bookevaluation_textDisposable);


        TextView upcoming_evaluation_text = navigationView.findViewById(R.id.upcoming_valuations_text);
        Disposable upcoming_evaluation_textDisposable = RxView.clicks(upcoming_evaluation_text)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        startActivity(new Intent(getApplicationContext(), UpcomingValuations.class));
                    }
                });
        mCompositeDisposable.add(upcoming_evaluation_textDisposable);


        TextView awaiting_action = navigationView.findViewById(R.id.awaiting_action);
        Disposable awaiting_actionDisposable = RxView.clicks(awaiting_action)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        startActivity(new Intent(getApplicationContext(), AwaitingAction.class));
                    }
                });
        mCompositeDisposable.add(awaiting_actionDisposable);


        TextView confirmed_viewings = navigationView.findViewById(R.id.confirmed_viewings);
        Disposable confirmed_viewingsDisposable = RxView.clicks(confirmed_viewings)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        startActivity(new Intent(getApplicationContext(), ConfirmedViewings.class));
                    }
                });
        mCompositeDisposable.add(confirmed_viewingsDisposable);


        TextView viewings_history = navigationView.findViewById(R.id.viewings_history);
        Disposable viewings_historyDisposable = RxView.clicks(viewings_history)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        startActivity(new Intent(getApplicationContext(), ViewingsHistory.class));
                    }
                });
        mCompositeDisposable.add(viewings_historyDisposable);


        TextView feedback_awaiting_action = navigationView.findViewById(R.id.awaiting_feedback);
        Disposable awaiting_feedbackDisposable = RxView.clicks(feedback_awaiting_action)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        startActivity(new Intent(getApplicationContext(), FeedBackAwaitingAction.class));
                    }
                });
        mCompositeDisposable.add(awaiting_feedbackDisposable);


        TextView feedback_pending_received = navigationView.findViewById(R.id.pending_recieved_feedback);
        Disposable pending_recieved_feedbackDisposable = RxView.clicks(feedback_pending_received)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        startActivity(new Intent(getApplicationContext(), FeedbackPendingReceived.class));
                    }
                });
        mCompositeDisposable.add(pending_recieved_feedbackDisposable);


        TextView offers_awaiting_action = navigationView.findViewById(R.id.offers_awaiting_action);
        Disposable offers_awaiting_actionDisposable = RxView.clicks(offers_awaiting_action)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        startActivity(new Intent(getApplicationContext(), OffersAwaitingAction.class));
                    }
                });
        mCompositeDisposable.add(offers_awaiting_actionDisposable);


        TextView accepted_offers = navigationView.findViewById(R.id.accepted_offers);
        Disposable accepted_offersDisposable = RxView.clicks(accepted_offers)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        startActivity(new Intent(getApplicationContext(), AcceptedOffers.class));
                    }
                });
        mCompositeDisposable.add(accepted_offersDisposable);


        TextView user_name = navigationView.findViewById(R.id.user_name);
        Disposable user_nameDisposable = RxView.clicks(user_name)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                    }
                });
        mCompositeDisposable.add(user_nameDisposable);


        ImageView settingsIcon = navigationView.findViewById(R.id.settings_btn);
        Disposable settings_btnDisposable = RxView.clicks(settingsIcon)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) {
                        startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                    }
                });
        mCompositeDisposable.add(settings_btnDisposable);
    }


    /**
     * Configure the floating action buttons and adds the relevant FAB's to the admin views arraylist
     */
    @SuppressLint("CheckResult")
    private void configureFabs() {
        FloatingActionButton addListingsFab = findViewById(R.id.add_listing_fab); //the add listing FAB
        FloatingActionButton search_fab = findViewById(R.id.search_fab);//the search listing FAB
        FloatingActionButton geolocate_fab = findViewById(R.id.geolocate_fab);//the mapview of listings FAB
        FloatingActionButton sync_db_fab = findViewById(R.id.sync_db_fab);//the sync database FAB

        RxView.clicks(addListingsFab).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                Intent i = new Intent(getApplicationContext(), AddListingView.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); //fade the activity out when transitioning
            }
        });

        adminViews.add(addListingsFab);

        RxView.clicks(geolocate_fab).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                Intent i = new Intent(getApplicationContext(), ListingsMapView.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        adminViews.add(geolocate_fab);

        RxView.clicks(sync_db_fab).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                showProgressDialog();
                mMainActivityPresenter.syncData();
            }
        });

        adminViews.add(sync_db_fab);


        RxView.clicks(search_fab).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) {
                startActivity(new Intent(getApplicationContext(), SearchActivityView.class));
            }
        });

    }


    /**
     * If the user is an admin, this method will show all floating action buttons
     * If the user is not an admin, only the search floating action button will be visible
     *
     * @param response is the user an admin or not
     */
    private void configureAdminViews(boolean response) {
        for (View v : adminViews) {
            if (response) {
                v.setVisibility(View.VISIBLE);
            } else {
                v.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


//    private void configureTextViewMain() {
//        this.textViewMain.setTextSize(15);
//        this.textViewMain.setText(R.string.main_textview_text);
//    }
//
//    private void configureTextViewQuantity() {
//        int quantity = Utils.convertDollarToEuro(100);
//        this.textViewQuantity.setTextSize(20);

    //this.textViewQuantity.setText(quantity); this caused an error because we are trying to use the the .setText() method which takes a string parameter
    //We are trying to assign an integer value and hence this causes a crash with "String resource not found"
    //Modified code below

//        this.textViewQuantity.setText(String.valueOf(quantity));
    //  }



    /**
     * Handles wether the user exits the application when the back button is pressed
     */
    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            finishAffinity();
            finish();
        } else {
            ToastModifications.createToast(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT);
            back_pressed = System.currentTimeMillis();
        }
    }



    //Set the network listener for connectivity changes
    private void setNetworkListener() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        mNetworkListener = new NetworkListener(this);
    }



    @Override
    protected void onResume() {
        super.onResume();
        try {
            registerReceiver(mNetworkListener, intentFilter); //register the reciever on resume
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mNetworkListener); //unregister the reciever on pause
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }


    @Override
    protected void onDestroy() {
        mCompositeDisposable.clear();
        super.onDestroy();
    }


    /**
     * Show the progress dialog when synching with the databases
     */
    @Override
    public void showProgressDialog() {
        keepScreenOn(true);
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.database_sync_message));
        pd.show();
    }


    /**
     * Updates the progress dialog while the databases are synching
     * @param count the number of listings remaining
     * @param message the message to display
     */
    @Override
    public void updateProgressDialog(final int count, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (pd != null && pd.isShowing()) {
                    pd.setMessage(message + " " + String.valueOf(count) + " " + getString(R.string.listings_remaining));
                }
            }
        });

    }


    /**
     * Once the sync is completed with an error or not, this method dismisses the progress dialog and displays a toast to the user
     * @param error did we get an error synching or not ?
     */
    @Override
    public void dismissProgressDialog(final boolean error) {
        keepScreenOn(false);
        if (pd != null && pd.isShowing()) {
            pd.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (error) {
                        ToastModifications.createToast(MainActivityView.this, getString(R.string.sync_timeout), Toast.LENGTH_LONG);
                    } else {
                        ToastModifications.createToast(MainActivityView.this, getString(R.string.sync_completed), Toast.LENGTH_LONG);
                    }
                }
            });
        }
    }


    /**
     * This method keeps the screen on or turns it off. When synching the users mobile screen is kept on. This flag is removed when synching completes.
     * @param screenOn screen on or off?
     */
    private void keepScreenOn(boolean screenOn) {
        if (screenOn) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            });
        }
    }


    /**
     * Callback from {@link FirebaseHelper#checkAdminAccess}
     * if the user is an admin , the admin views will be displayed, if not they will be hidden
     * @param isAdmin is the user an admin or not?
     */
    @SuppressLint("ApplySharedPref")
    @Override
    public void isAdmin(boolean isAdmin) {
        SharedPreferences.Editor spEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        if (isAdmin) {
            spEditor.putBoolean(USER_DATABASE_ISADMIN_FIELD, true);
            spEditor.commit();
        } else {
            spEditor.putBoolean(USER_DATABASE_ISADMIN_FIELD, false);
            spEditor.commit();
        }
        displayAdminViews();
    }


    /**
     * Display or hide the admin views based on wether the user is an admin in the database or not
     */
    private void displayAdminViews() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean(USER_DATABASE_ISADMIN_FIELD, false)) {
            configureAdminViews(true);
        } else {
            configureAdminViews(false);
        }
    }


    /**
     * Callback from the presenter when synching is completed. Called {@link #dismissProgressDialog(boolean)}
     * @param error if an error is returned whilst synching
     */
    @Override
    public void syncDataComplete(final boolean error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissProgressDialog(error);
            }
        });

    }


    /**
     * Updated the progress dialog when performing a manual synch
     * @param count the number of listings remaining to be synched
     * @param message the message to display in the progress dialog
     */
    @Override
    public void updateManualSyncProgress(int count, String message) {
        this.updateProgressDialog(count, message);
    }
}


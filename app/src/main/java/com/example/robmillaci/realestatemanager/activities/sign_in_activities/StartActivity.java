package com.example.robmillaci.realestatemanager.activities.sign_in_activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.splash_screen.SplashScreenActivity;
import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.example.robmillaci.realestatemanager.utils.ToastModifications;

import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * This class created the first activity presented to a new user<br>
 * It deals with authenticating the user via Facebook, Google and Twitter and handles the login event
 */
public class StartActivity extends AppCompatActivity implements FaceBookLoginManager.loginManagerCallback, FirebaseGoogleAuthManager.fireBaseGoogleAuthCallback {
    private static final String TAG = "StartActivity";
    private static final String GOOGLE_ID_TOKEN = "23584158539-oljjebrgtrl42h6kdq5iej8t9d00je7f.apps.googleusercontent.com";
    public static final String LOGOUT_INTENT_KEY = "logout";
    public static final int LOGOUT_INTENT_VALUE = 1;

    private GoogleSignInClient mGoogleSignInClient; //the GoogleSignInClient
    private FaceBookLoginManager faceBookLoginManager;
    private ProgressDialog pd;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_no_action_bar_no_status_bar);
        setContentView(R.layout.activity_start);

        initializeFacebookLogin();
        initializeGoogleSignIn();


        Bundle intentBundle = getIntent().getExtras();
        if (intentBundle != null){
            if (intentBundle.getInt(LOGOUT_INTENT_KEY) == LOGOUT_INTENT_VALUE){
                //sign the user out
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                mGoogleSignInClient.signOut();
            }
        }
    }




    private void initializeFacebookLogin() {
        faceBookLoginManager = new FaceBookLoginManager(this);
        faceBookLoginManager.createLoginManager();

        final LoginButton facebookLoginBtn = findViewById(R.id.facebook_login_btn); //default facebook login button
        facebookLoginBtn.setReadPermissions("email", "public_profile"); //the read permissions

        final Button facebookLogin = findViewById(R.id.facebook_login);
        //custom facebook login button. Visually different from the default button but will call the default buttons onClick method
        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(StartActivity.this);
                pd.setMessage(getString(R.string.signing_in_message));
                facebookLoginBtn.callOnClick();
            }
        });
    }


    private void initializeGoogleSignIn() {
        //build the google sign in options, passing in the ID token
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GOOGLE_ID_TOKEN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso); //get the GoogleSignIn client passing in the sign in options

        Button googleLogin = findViewById(R.id.google_login); //the google login button
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1);
            }
        });
    }

    //Handles the results of the Google sign in intent and then handle the results using handleSignInResults(task)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
                break;
            default:
                faceBookLoginManager.getCallbackManager().onActivityResult(requestCode, resultCode, data);
        }
    }


    //Get the results of the sign in intent and if successful, authenticate the users with fireback
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            if (account != null) {
                new FirebaseGoogleAuthManager(this).firebaseAuthWithGoogle(account);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            Toast.makeText(this, R.string.login_failed, Toast.LENGTH_LONG).show();
            Log.d(TAG, "google login failed: " + e + "status code" + e.getStatusCode() + " " + e.getCause());
        }
    }



    @SuppressWarnings("ConstantConditions")
    /*
     * Once the user has been authenticated and loged in, launch the SplashScreenActivity
     */
    private void updateUI() {
        if (FirebaseHelper.getmAuth().getCurrentUser() != null) {
            Intent launchMain = new Intent(this, SplashScreenActivity.class);
            FirebaseHelper.updateUserDetails();
            if(pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            startActivity(launchMain);
        }
    }



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){

            Log.i("Username","user logged in "  + user.getEmail());
        }
        else{
            Log.i("Username", "there is no user");
        }


        updateUI();
    }


    //Facebook login manager callbacks
    @Override
    public void facebookSignInSuccessNewUser(FirebaseUser user) {
        ToastModifications.createToast(StartActivity.this,getString(R.string.welcome) + user.getDisplayName(), Toast.LENGTH_LONG);
        FirebaseHelper.addUserToDB(user);
        updateUI();
    }

    @Override
    public void facebookSignInSuccessReturningUser(FirebaseUser user) {
        ToastModifications.createToast(StartActivity.this,getString(R.string.welcome_back) + user.getDisplayName(),Toast.LENGTH_LONG);
        updateUI();
    }

    @Override
    public void facebookAuthFailure() {
        Toast.makeText(StartActivity.this, R.string.auth_failed,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void facebookSignInCancelled() {
        Toast.makeText(StartActivity.this, R.string.sign_in_cancelled,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void facebookSignInError() {
        Toast.makeText(StartActivity.this, R.string.sign_in_error,Toast.LENGTH_SHORT).show();

    }



    //google login manager callbacks
    @Override
    public void newFirebaseGoogleUsercallback(FirebaseUser  user) {
        ToastModifications.createToast(StartActivity.this,getString(R.string.welcome) + user.getDisplayName(), Toast.LENGTH_LONG);
        FirebaseHelper.addUserToDB(user);
        updateUI();
    }

    @Override
    public void existingFirebaseGoogleUsercallback(FirebaseUser  user) {
        ToastModifications.createToast(StartActivity.this,getString(R.string.welcome_back) + user.getDisplayName(),Toast.LENGTH_LONG);
        updateUI();
    }

    @Override
    public void error() {
        Toast.makeText(StartActivity.this, R.string.sign_in_error,Toast.LENGTH_LONG).show();
    }
    
}





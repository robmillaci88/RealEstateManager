package com.example.robmillaci.realestatemanager.activities.sign_in_activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.activities.BaseActivity;
import com.example.robmillaci.realestatemanager.activities.splash_screen.SplashScreenActivity;
import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.example.robmillaci.realestatemanager.utils.ToastModifications;
import com.example.robmillaci.realestatemanager.utils.Utils;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * This class created the first activity presented to a new user<br>
 * It deals with authenticating the user via Facebook, Google and Twitter and handles the login event
 */
public class StartActivity extends BaseActivity implements FaceBookLoginManager.FacebookloginManagerCallback,
        FirebaseGoogleAuthManager.fireBaseGoogleAuthCallback, UsernameAndPasswordLoginManager.FirebaseLoginManagerCallback {
    private static final String GOOGLE_ID_TOKEN = "23584158539-oljjebrgtrl42h6kdq5iej8t9d00je7f.apps.googleusercontent.com";
    public static final String LOGOUT_INTENT_KEY = "logout";
    public static final int LOGOUT_INTENT_VALUE = 1;

    private GoogleSignInClient mGoogleSignInClient; //the GoogleSignInClient
    private FaceBookLoginManager mFaceBookLoginManager; //the facebook login manager
    private ProgressDialog mProgressDialog;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_no_action_bar_no_status_bar);
        setContentView(R.layout.activity_start);

        initializeFacebookLogin();
        initializeGoogleSignIn();
        initializeEmailPasswordSignIn();


        //Has this activity been called because a user wants to log out?
        //if so call the logout methods
        Bundle intentBundle = getIntent().getExtras();
        if (intentBundle != null) {
            if (intentBundle.getInt(LOGOUT_INTENT_KEY) == LOGOUT_INTENT_VALUE) {
                //sign the user out
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                mGoogleSignInClient.signOut();
            }
        }
    }

    /**
     * This method initializes the password sign in dialog for a new or existing user.
     * From this dialog a user has an option of signing up to the application with a new account or signing in with an already created account
     */
    private void initializeEmailPasswordSignIn() {
        Button emailPasswordSignin = findViewById(R.id.email_password_signup);

        emailPasswordSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("InflateParams") View diagView = getLayoutInflater().inflate(R.layout.new_or_exiting_diag, null);
                final AlertDialog.Builder newOrExistingUserDiag = new AlertDialog.Builder(StartActivity.this);
                newOrExistingUserDiag.setView(diagView)
                        .setTitle(R.string.new_or_existing_user);

                final TextView newUserTextView = diagView.findViewById(R.id.new_user_tv);
                final TextView existingUserTextView = diagView.findViewById(R.id.existing_user_tv);

                newUserTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createSignUpDialog(); //if the user is a new user, create the sign up dialog
                    }
                });

                existingUserTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createSignInDialog(); //if the user is an existing user, create the sign in dialog
                    }
                });

                newOrExistingUserDiag.setNegativeButton(R.string.cancel_btn_txt, null).show();
            }
        });
    }


    /**
     * Called when a user attempts to sign in as an existing user with their email and password
     */
    private void createSignInDialog() {
        @SuppressLint("InflateParams") View diagView = getLayoutInflater().inflate(R.layout.signup_dialog_view, null);
        final AlertDialog.Builder newUserDialog = new AlertDialog.Builder(StartActivity.this);
        newUserDialog.setView(diagView)
                .setTitle(R.string.sign_in_text);

        final EditText userEmail = diagView.findViewById(R.id.user_email); //the users email input
        final EditText userPassword = diagView.findViewById(R.id.userPasswordSignUp); //the users password input
        final TextView forgottenpassword = diagView.findViewById(R.id.forgottenPasswordLink); //the reset password link

        newUserDialog.setNegativeButton(getString(R.string.cancel_btn_txt), null)
                .setPositiveButton(R.string.sign_in_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new UsernameAndPasswordLoginManager(StartActivity.this).signInUserWithEmailAndPassword(userEmail.getText().toString()
                                , userPassword.getText().toString()); //calls the firebase signInUserWithEmailAndPassword method to sign the user in

                    }
                });

        forgottenpassword.setOnClickListener(new View.OnClickListener() { //the onlick event for the forgotten password link
            @Override
            public void onClick(View v) {
                @SuppressLint("InflateParams") View diagView = getLayoutInflater().inflate(R.layout.forgotten_password_dialog, null);
                final AlertDialog.Builder resetPasswordDialog = new AlertDialog.Builder(StartActivity.this);
                resetPasswordDialog.setView(diagView)
                        .setTitle(R.string.forgotten_password);

                final EditText userEmail = diagView.findViewById(R.id.user_email);

                resetPasswordDialog.setNegativeButton(getString(R.string.cancel_btn_txt), null)
                        .setPositiveButton(R.string.reset_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String emailaddress = userEmail.getText().toString(); //the users entered email address
                                if (android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail.getText()).matches()) { //if the email address entered looks like a valid email
                                    //email is a valid email
                                    new UsernameAndPasswordLoginManager(StartActivity.this).sendPasswordResetEmail(emailaddress); //send the user a password reset email
                                } else {
                                    Toast.makeText(StartActivity.this, R.string.enter_valid_email, Toast.LENGTH_LONG).show(); //otherwise display a message informing the email appears to be invalid
                                }
                            }
                        });
                resetPasswordDialog.show();
            }
        });

        newUserDialog.show();
    }


    /**
     * Called when the user is new and is registering to use this app with their email and password
     */
    private void createSignUpDialog() {
        @SuppressLint("InflateParams") View diagView = getLayoutInflater().inflate(R.layout.signup_dialog_view, null);
        final AlertDialog.Builder newUserDialog = new AlertDialog.Builder(StartActivity.this);
        newUserDialog.setView(diagView)
                .setTitle(R.string.sign_up_string);

        final EditText userEmail = diagView.findViewById(R.id.user_email); //the users email input
        final EditText userPassword = diagView.findViewById(R.id.userPasswordSignUp); //the users password input
        final TextView forgottenpassword = diagView.findViewById(R.id.forgottenPasswordLink); //the forgotten password link
        forgottenpassword.setVisibility(View.GONE); //hide the password link here as its not used when signing up


        newUserDialog.setNegativeButton(R.string.cancel_btn_txt, null)
                .setPositiveButton(R.string.sign_up_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail.getText()).matches(); //check the email looks like a real email
                        boolean isPasswordValid = Utils.isPasswordValid(userPassword.getText()); //check that  the password meets the password criteria (see the method comments)

                        if (isEmailValid && isPasswordValid) { //if the email and the password are both valie then call Firebase createUserWithEmailAndPassword
                            new UsernameAndPasswordLoginManager(StartActivity.this).createUserWithEmailAndPassword(userEmail.getText().toString()
                                    , userPassword.getText().toString());

                        } else if (isEmailValid) { //if the email is valid put the password isn't
                            Toast.makeText(StartActivity.this, R.string.invalid_password_chosen, Toast.LENGTH_LONG).show();

                        } else { //the email is invalid
                            Toast.makeText(StartActivity.this, R.string.invalid_email_entered, Toast.LENGTH_LONG).show();
                        }
                    }
                });

        newUserDialog.show();
    }


    /**
     * Creates a new instance of FacebookLoginManager to create the login manager and handle the callback
     */
    private void initializeFacebookLogin() {
        mFaceBookLoginManager = new FaceBookLoginManager(this);
        mFaceBookLoginManager.createLoginManager();

        final LoginButton facebookLoginBtn = findViewById(R.id.facebook_login_btn); //default facebook login button
        facebookLoginBtn.setReadPermissions("email", "public_profile"); //the read permissions

        final Button facebookLogin = findViewById(R.id.facebook_login);
        //custom facebook login button. Visually different from the default button but will call the default buttons onClick method
        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = new ProgressDialog(StartActivity.this);
                mProgressDialog.setMessage(getString(R.string.signing_in_message));
                facebookLoginBtn.callOnClick();
            }
        });
    }


    /**
     * Creates the google sign in options and handles the results of logging in via google
     */
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
                mFaceBookLoginManager.getCallbackManager().onActivityResult(requestCode, resultCode, data);
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
        }
    }


    @SuppressWarnings("ConstantConditions")
        /*
         * Once the user has been authenticated and loged in, launch the SplashScreenActivity
         */
    void updateUI() {
        if (FirebaseHelper.getmAuth().getCurrentUser() != null) {
            Intent launchMain = new Intent(this, SplashScreenActivity.class);
            FirebaseHelper.updateUserDetails();
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            startActivity(launchMain);
        }
    }


    /**
     * Produces a sign in error message from either Facebook sign in or Google sign in
     *
     * @param e the exception thrown when sign in fails
     */
    private void signInErrorMessage(Exception e) {
        StringBuilder message = new StringBuilder();
        message.append(getString(R.string.sign_in_error));

        if (e != null) {
            message.append(" ").append(e.getMessage());
        }

        Toast.makeText(StartActivity.this, message, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }


    private void addUserDisplayName(final FirebaseUser user) {
        AlertDialog.Builder addUserName = new AlertDialog.Builder(this);
        addUserName.setTitle(R.string.choose_display_name);

        @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.choose_display_name_diag, null);
        addUserName.setView(v);

        final EditText displayName = v.findViewById(R.id.display_name_et);
        Button okBtn = v.findViewById(R.id.ok_button);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String displayNameText = displayName.getText().toString();
                if (displayNameText.length() > 0) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(displayNameText).build();

                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            addNewUser(user);
                        }
                    });
                } else {
                    Toast.makeText(StartActivity.this, "You must choose a display name", Toast.LENGTH_LONG).show();
                }
            }
        });
        addUserName.show();

    }


    private void addNewUser(FirebaseUser user) {
        ToastModifications.createToast(StartActivity.this, getString(R.string.welcome)
                + (user.getDisplayName() != null ? user.getDisplayName() : getString(R.string.welcome_to_realestate)), Toast.LENGTH_LONG);
        FirebaseHelper.addUserToDB(user);
        updateUI();
    }

    private void existingUser(FirebaseUser user) {
        ToastModifications.createToast(StartActivity.this, getString(R.string.welcome_back)
                + (user.getDisplayName() != null ? user.getDisplayName() : getString(R.string.welcome_to_realestate)), Toast.LENGTH_LONG);
        updateUI();
    }


    //Facebook login manager callbacks
    @Override
    public void facebookSignInSuccessNewUser(FirebaseUser user) {
        addNewUser(user);

    }


    @Override
    public void facebookSignInSuccessReturningUser(FirebaseUser user) {
        existingUser(user);
    }

    @Override
    public void facebookAuthFailure() {
        Toast.makeText(StartActivity.this, R.string.auth_failed,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void facebookSignInCancelled() {
        Toast.makeText(StartActivity.this, R.string.sign_in_cancelled, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void facebookSignInError(Exception exception) {
        signInErrorMessage(exception);
    }


    //google login manager callbacks
    @Override
    public void newFirebaseGoogleUsercallback(FirebaseUser user) {
        addNewUser(user);
    }

    @Override
    public void existingFirebaseGoogleUsercallback(FirebaseUser user) {
        existingUser(user);
    }

    @Override
    public void googleSignInError(Exception e) {
        signInErrorMessage(e);
    }


    @Override
    public void firebaseLoginCompleteNewUser(FirebaseUser user) {
        addUserDisplayName(user);
    }


    @Override
    public void firebaseLoginCompleteExistingUser(FirebaseUser user) {
        existingUser(user);
    }

    @Override
    public void firebaseLoginError(Exception e) {
        signInErrorMessage(e);
    }

    @Override
    public void passwordResetSuccessful(String email) {
        Toast.makeText(this, getString(R.string.reset_email_sent) + email, Toast.LENGTH_LONG).show();
    }

    @Override
    public void passwordResetFail(Exception e) {
        Toast.makeText(this, getString(R.string.password_reset_failed) + e.getMessage(), Toast.LENGTH_LONG).show();
    }
}





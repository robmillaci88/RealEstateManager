package com.example.robmillaci.realestatemanager.activities.sign_in_activities;

import android.support.annotation.NonNull;

import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.Executor;


public class FirebaseGoogleAuthManager {
    private fireBaseGoogleAuthCallback mFireBaseGoogleAuthCallback;

    FirebaseGoogleAuthManager(fireBaseGoogleAuthCallback callback) {
        this.mFireBaseGoogleAuthCallback = callback;
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FirebaseHelper.getmAuth().signInWithCredential(credential)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = FirebaseHelper.getmAuth().getCurrentUser();

                            if (user != null) {
                                FirebaseUserMetadata metadata = user.getMetadata();
                                //noinspection ConstantConditions
                                if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
                                    // The user is new
                                    mFireBaseGoogleAuthCallback.newFirebaseGoogleUsercallback(user);

                                } else {
                                    mFireBaseGoogleAuthCallback.existingFirebaseGoogleUsercallback(user);
                                }

                            } else {
                                mFireBaseGoogleAuthCallback.error();
                                // If sign in fails, display a message to the user.

                            }
                        }
                    }
                });
    }

    interface fireBaseGoogleAuthCallback {
        void newFirebaseGoogleUsercallback(FirebaseUser user);

        void existingFirebaseGoogleUsercallback(FirebaseUser user);

        void error();
    }
}

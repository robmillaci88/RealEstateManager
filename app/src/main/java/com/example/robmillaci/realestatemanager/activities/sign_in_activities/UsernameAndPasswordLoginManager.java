package com.example.robmillaci.realestatemanager.activities.sign_in_activities;

import android.support.annotation.NonNull;

import com.example.robmillaci.realestatemanager.databases.firebase.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;

class UsernameAndPasswordLoginManager {

    private final FirebaseLoginManagerCallback mFirebaseLoginManagerCallback;

    UsernameAndPasswordLoginManager(FirebaseLoginManagerCallback callback) {
        this.mFirebaseLoginManagerCallback = callback;
    }

    void createUserWithEmailAndPassword(String email, String password) {
        FirebaseHelper.getmAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                                    mFirebaseLoginManagerCallback.firebaseLoginCompleteNewUser(user);

                                } else {
                                    //this is an existing user
                                    mFirebaseLoginManagerCallback.firebaseLoginCompleteExistingUser(user);
                                }

                            } else {
                                mFirebaseLoginManagerCallback.firebaseLoginError(task.getException());
                                // If sign in fails, display a message to the user.

                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mFirebaseLoginManagerCallback.firebaseLoginError(e);
            }
        });
    }


    void signInUserWithEmailAndPassword(String email, String password) {
        FirebaseHelper.getmAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                                    mFirebaseLoginManagerCallback.firebaseLoginCompleteNewUser(user);

                                } else {
                                    //this is an existing user
                                    mFirebaseLoginManagerCallback.firebaseLoginCompleteExistingUser(user);
                                }

                            } else {
                                mFirebaseLoginManagerCallback.firebaseLoginError(task.getException());
                                // If sign in fails, display a message to the user.

                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mFirebaseLoginManagerCallback.firebaseLoginError(e);
            }
        });
    }


    void sendPasswordResetEmail(final String email) {
        FirebaseHelper.getmAuth().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFirebaseLoginManagerCallback.passwordResetSuccessful(email);
                        } else {
                            mFirebaseLoginManagerCallback.passwordResetFail(task.getException());
                        }
                    }
                });
    }

    interface FirebaseLoginManagerCallback {
        void firebaseLoginCompleteNewUser(FirebaseUser user);

        void firebaseLoginCompleteExistingUser(FirebaseUser user);

        void firebaseLoginError(Exception e);

        void passwordResetSuccessful(String email);

        void passwordResetFail(Exception e);

    }
}

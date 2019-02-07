package com.example.robmillaci.realestatemanager;

import android.app.Activity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;

 class ToastChecker {
     static void checkToast(String toastMessage, Activity a){
        onView(withText(toastMessage)).
                inRoot(withDecorView(not(is(a.getWindow().getDecorView())))).
                check(matches(isDisplayed()));
    }
}

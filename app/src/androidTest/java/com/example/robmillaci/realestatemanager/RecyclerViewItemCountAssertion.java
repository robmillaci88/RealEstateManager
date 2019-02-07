package com.example.robmillaci.realestatemanager;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import static junit.framework.Assert.assertTrue;

/**
 * This class checks that the expected items in the recycler view adaptor matches
 * the actual item count in the adaptor
 */
class RecyclerViewItemCountAssertion implements ViewAssertion {

    private final int expectedCount;

    @SuppressWarnings("SameParameterValue")
    RecyclerViewItemCountAssertion(int expectedCountAtLeast) {
        this.expectedCount = expectedCountAtLeast;
    }

    @Override
    public void check(View view, NoMatchingViewException noViewFoundException) {
        if (noViewFoundException != null) {
            throw noViewFoundException;
        }

        RecyclerView recyclerView = (RecyclerView) view;
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter != null) {
            assertTrue(adapter.getItemCount() >= expectedCount);
        }
    }
}

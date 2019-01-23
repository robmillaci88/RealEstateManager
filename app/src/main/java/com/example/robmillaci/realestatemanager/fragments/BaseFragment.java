package com.example.robmillaci.realestatemanager.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.robmillaci.realestatemanager.R;
import com.example.robmillaci.realestatemanager.utils.Utils;

public class BaseFragment extends Fragment {
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if(!Utils.isTablet(getActivity().getApplicationContext())) {
            MenuItem sortMenuItem = menu.findItem(R.id.sort_by);
            if (sortMenuItem != null) {
                sortMenuItem.setVisible(false);
            }
        }
    }
}

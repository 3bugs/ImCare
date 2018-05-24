package com.example.imcare.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class BaseFragment extends Fragment {

    private String mTitle = "";

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(mTitle);
            }
        }
    }

    protected void setTitle(String title) {
        mTitle = title;
    }
}

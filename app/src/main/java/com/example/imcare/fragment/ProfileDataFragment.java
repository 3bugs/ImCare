package com.example.imcare.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.imcare.R;

public class ProfileDataFragment extends Fragment {

    private ProfileDataFragmentListener mListener;

    public ProfileDataFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_data, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileDataFragmentListener) {
            mListener = (ProfileDataFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ProfileDataFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ProfileDataFragmentListener {
        void onFragmentInteraction(Uri uri);
    }
}

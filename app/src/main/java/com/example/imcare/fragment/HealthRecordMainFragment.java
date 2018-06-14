package com.example.imcare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.imcare.R;

public class HealthRecordMainFragment extends BaseFragment
        implements View.OnClickListener {

    private HealthRecordMainFragmentListener mListener;

    public HealthRecordMainFragment() {
        // Required empty public constructor
        setTitle("ผลการตรวจสุขภาพ");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_health_record_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.card_view_1).setOnClickListener(this);
        view.findViewById(R.id.card_view_2).setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HealthRecordMainFragmentListener) {
            mListener = (HealthRecordMainFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HealthRecordMainFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.card_view_1:
                if (mListener != null) {
                    mListener.onHealthRecordMainButtonClick(1);
                }
                break;
            case R.id.card_view_2:
                if (mListener != null) {
                    mListener.onHealthRecordMainButtonClick(2);
                }
                break;
        }
    }

    public interface HealthRecordMainFragmentListener {
        void onHealthRecordMainButtonClick(int which);
    }
}

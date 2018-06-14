package com.example.imcare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.imcare.R;

public class CheckupGuideMainFragment extends BaseFragment
        implements View.OnClickListener {

    private CheckupGuideMainFragmentListener mListener;

    public CheckupGuideMainFragment() {
        // Required empty public constructor
        setTitle("โปรแกรมการตรวจสุขภาพ");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkup_guide_main, container, false);
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
        if (context instanceof CheckupGuideMainFragmentListener) {
            mListener = (CheckupGuideMainFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CheckupGuideMainFragmentListener");
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
                    mListener.onCheckupGuideMainButtonClick(1);
                }
                break;
            case R.id.card_view_2:
                if (mListener != null) {
                    mListener.onCheckupGuideMainButtonClick(2);
                }
                break;
        }
    }

    public interface CheckupGuideMainFragmentListener {
        void onCheckupGuideMainButtonClick(int which);
    }
}

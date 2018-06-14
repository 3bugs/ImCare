package com.example.imcare.fragment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.imcare.R;

import static com.example.imcare.etc.Const.SEX_FEMALE;
import static com.example.imcare.etc.Const.SEX_MALE;
import static com.example.imcare.etc.Const.SEX_UNDEFINED;

public class CheckupGuideFormFragment extends BaseFragment implements View.OnClickListener {

    private static final String KEY_AGE = "age";
    private static final String KEY_SEX = "sex";

    private CheckupGuideFormFragmentListener mListener;

    private int mSex = SEX_UNDEFINED;
    private EditText mAgeEditText;
    private FrameLayout mMaleImageLayout, mFemaleImageLayout;

    public CheckupGuideFormFragment() {
        // Required empty public constructor
        setTitle("โปรแกรมการตรวจสุขภาพ\nที่เหมาะสมกับแต่ละบุคคล");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkup_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAgeEditText = view.findViewById(R.id.age_edit_text);
        mMaleImageLayout = view.findViewById(R.id.male_image_layout);
        mFemaleImageLayout = view.findViewById(R.id.female_image_layout);
        Button submitButton = view.findViewById(R.id.next_button);

        mMaleImageLayout.setOnClickListener(this);
        mFemaleImageLayout.setOnClickListener(this);
        submitButton.setOnClickListener(this);

        if (savedInstanceState != null) {
            String age = savedInstanceState.getString(KEY_AGE);
            mAgeEditText.setText(age);
            mSex = savedInstanceState.getInt(KEY_SEX);
            updateSexImage();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSexImage();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_AGE, mAgeEditText.getText().toString());
        outState.putInt(KEY_SEX, mSex);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CheckupGuideFormFragmentListener) {
            mListener = (CheckupGuideFormFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CheckupGuideFormFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        hideKeyboard();
        switch (view.getId()) {
            case R.id.male_image_layout:
                mSex = SEX_MALE;
                updateSexImage();
                break;
            case R.id.female_image_layout:
                mSex = SEX_FEMALE;
                updateSexImage();
                break;
            case R.id.next_button:
                submitButtonClicked();
                break;
        }
    }

    private void hideKeyboard() {
        if (getActivity() != null) {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void updateSexImage() {
        Context context = getContext();
        if (context == null) {
            return;
        }

        mMaleImageLayout.setForeground(null);
        mFemaleImageLayout.setForeground(null);
        switch (mSex) {
            case SEX_MALE:
                mMaleImageLayout.setForeground(
                        new ColorDrawable(
                                ContextCompat.getColor(getContext(), R.color.foreground_select)
                        )
                );
                break;
            case SEX_FEMALE:
                mFemaleImageLayout.setForeground(
                        new ColorDrawable(
                                ContextCompat.getColor(getContext(), R.color.foreground_select)
                        )
                );
                break;
            case SEX_UNDEFINED:
                break;
        }
    }

    private void submitButtonClicked() {
        String errorMessage;
        if (mAgeEditText.getText().toString().trim().isEmpty()) {
            mAgeEditText.requestFocus();
            errorMessage = "กรุณากรอกอายุ";
            mAgeEditText.setError(errorMessage);
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
        } else if (mSex == SEX_UNDEFINED) {
            errorMessage = "กรุณาระบุเพศ";
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
        } else {
            try {
                int age = Integer.parseInt(mAgeEditText.getText().toString());
                mListener.onSubmitCheckupGuideForm(age, mSex);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                errorMessage = "กรอกอายุไม่ถูกต้อง";
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    public interface CheckupGuideFormFragmentListener {
        void onSubmitCheckupGuideForm(int age, int sex);
    }
}

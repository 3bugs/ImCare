package com.example.imcare.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.imcare.R;
import com.example.imcare.db.CareDb;
import com.example.imcare.etc.Utils;
import com.example.imcare.model.HealthRecord;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HealthRecordForm1Fragment extends BaseFragment implements View.OnClickListener {

    private HealthRecordForm1FragmentListener mListener;

    private EditText mDateEditText, mPlaceEditText, mDoctorEditText;

    public HealthRecordForm1Fragment() {
        // Required empty public constructor
        setTitle("บันทึกผลการตรวจสุขภาพ");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_health_record_form_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDateEditText = view.findViewById(R.id.date_edit_text);
        mDateEditText.setOnClickListener(this);
        ImageView dateIconImageView = view.findViewById(R.id.date_icon_image_view);
        dateIconImageView.setOnClickListener(this);

        mPlaceEditText = view.findViewById(R.id.place_edit_text);
        mDoctorEditText = view.findViewById(R.id.doctor_edit_text);

        view.findViewById(R.id.next_button).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        HealthRecord healthRecord = mListener.getHealthRecord();

        if (healthRecord != null) {
            mListener.getHealthRecordDate().setTime(
                    healthRecord.date
            );
            mPlaceEditText.setText(healthRecord.place);
            mDoctorEditText.setText(healthRecord.doctor);
        }
        updateDateEditText();
    }

    private void updateDateEditText() {
        if (mListener == null) return;

        Date date = mListener.getHealthRecordDate().getTime();

        SimpleDateFormat monthFormatter = new SimpleDateFormat("MM", Locale.US);
        String month = monthFormatter.format(date);

        SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy", Locale.US);
        String yearInBe = String.valueOf(Integer.valueOf(yearFormatter.format(date)) + 543);

        SimpleDateFormat dayFormatter = new SimpleDateFormat("dd", Locale.US);
        String day = dayFormatter.format(date);

        String dateText = String.format(
                Locale.getDefault(),
                "%s   |   %s   |   %s",
                day, month, yearInBe
        );
        mDateEditText.setText(dateText);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HealthRecordForm1FragmentListener) {
            mListener = (HealthRecordForm1FragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HealthRecordForm1FragmentListener");
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
            case R.id.date_edit_text:
            case R.id.date_icon_image_view:
                final DatePickerDialog.OnDateSetListener dateSetListener =
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                mListener.getHealthRecordDate().set(Calendar.YEAR, year);
                                mListener.getHealthRecordDate().set(Calendar.MONTH, monthOfYear);
                                mListener.getHealthRecordDate().set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                updateDateEditText();
                                mListener.setHealthRecord(null);
                            }
                        };
                if (getActivity() != null) {
                    new DatePickerDialog(
                            getActivity(),
                            dateSetListener,
                            mListener.getHealthRecordDate().get(Calendar.YEAR),
                            mListener.getHealthRecordDate().get(Calendar.MONTH),
                            mListener.getHealthRecordDate().get(Calendar.DAY_OF_MONTH)
                    ).show();
                }
                break;
            case R.id.next_button:
                if (validateForm()) {
                    final CareDb careDb = new CareDb(getActivity());
                    HealthRecord healthRecord = mListener.getHealthRecord();

                    if (healthRecord != null) {
                        careDb.updateHealthRecordByDate(
                                healthRecord.date,
                                mPlaceEditText.getText().toString().trim(),
                                mDoctorEditText.getText().toString().trim()
                        );
                        mListener.onHealthRecordForm1_Next();
                    } else {
                        healthRecord = careDb.getHealthRecordByDate(mListener.getHealthRecordDate().getTime());
                        if (healthRecord != null) {
                            final HealthRecord tempHealthRecord = healthRecord;

                            new AlertDialog.Builder(getActivity())
                                    .setMessage("มีบันทึกผลการตรวจสุขภาพของวันที่ระบุแล้ว ต้องการแก้ไขข้อมูลเดิมหรือไม่?")
                                    .setPositiveButton("แก้ไข", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            careDb.updateHealthRecordByDate(
                                                    tempHealthRecord.date,
                                                    mPlaceEditText.getText().toString().trim(),
                                                    mDoctorEditText.getText().toString().trim()
                                            );
                                            mListener.setHealthRecord(tempHealthRecord);
                                            mListener.onHealthRecordForm1_Next();
                                        }
                                    })
                                    .setNegativeButton("ยกเลิก", null)
                                    .show();
                        } else {
                            healthRecord = new CareDb(getActivity()).addHealthRecord(
                                    mListener.getHealthRecordDate().getTime(),
                                    mPlaceEditText.getText().toString().trim(),
                                    mDoctorEditText.getText().toString().trim()
                            );
                            if (healthRecord != null) {
                                mListener.setHealthRecord(healthRecord);
                                mListener.onHealthRecordForm1_Next();
                            } else {
                                Utils.showOkDialog(getActivity(), "เกิดข้อผิดพลาดในการบันทึกข้อมูล");
                            }
                        }
                    }
                }
                break;
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        if (mDateEditText.getText().toString().isEmpty()) {
            mDateEditText.setError("ระบุวันที่ตรวจสุขภาพ");
            valid = false;
        }
        if (mPlaceEditText.getText().toString().trim().isEmpty()) {
            mPlaceEditText.setError("กรอกสถานที่ตรวจสุขภาพ");
            valid = false;
        }
        if (mDoctorEditText.getText().toString().trim().isEmpty()) {
            mDoctorEditText.setError("กรอกชื่อแพทย์ผู้ตรวจสุขภาพ");
            valid = false;
        }
        return valid;
    }

    public interface HealthRecordForm1FragmentListener {
        Calendar getHealthRecordDate();

        void setHealthRecord(HealthRecord healthRecord);

        HealthRecord getHealthRecord();

        void onHealthRecordForm1_Next();
    }
}

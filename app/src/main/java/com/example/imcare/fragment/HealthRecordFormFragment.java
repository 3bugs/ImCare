package com.example.imcare.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.imcare.R;
import com.example.imcare.adapter.HealthRecordListAdapter;
import com.example.imcare.db.CareDb;
import com.example.imcare.etc.SharedPref;
import com.example.imcare.model.HealthRecordItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HealthRecordFormFragment extends Fragment implements View.OnClickListener {

    private HealthRecordFormListener mListener;

    private EditText mDateEditText;
    private HealthRecordListAdapter mAdapter;
    private Calendar mCalendar = Calendar.getInstance();
    private List<HealthRecordItem> mHealthRecordItemList = new ArrayList<>();

    public HealthRecordFormFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_health_record_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDateEditText = view.findViewById(R.id.date_edit_text);
        mDateEditText.setOnClickListener(this);
        ImageView dateIconImageView = view.findViewById(R.id.date_icon_image_view);
        dateIconImageView.setOnClickListener(this);

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new HealthRecordListAdapter(
                getContext(),
                new HealthRecordListAdapter.HealthRecordListAdapterListener() {
                    @Override
                    public void onSaveHealthRecordItem(HealthRecordItem healthRecordItem) {
                        if (getContext() == null) return;

                        if (new CareDb(getContext()).saveHealthRecord(
                                healthRecordItem, mCalendar.getTime()
                        )) {
                            Toast.makeText(getContext(), "บันทึกข้อมูลเรียบร้อย", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "เกิดข้อผิดพลาดในการบันทึกข้อมูล!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        recyclerView.setAdapter(mAdapter);

        Button viewGraphButton = view.findViewById(R.id.view_graph_button);
        viewGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onViewGraphButtonClicked(mCalendar.getTime());
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        mCalendar.setTime(SharedPref.getInstance().getDate(getContext()));
        updateDateEditText();
        updateList();
    }

    private void updateList() {
        if (getContext() != null) {
            mAdapter.setHealthRecordItemList(
                    new CareDb(getContext()).getHealthRecordItemListByDate(mCalendar.getTime())
            );
            mAdapter.notifyDataSetChanged();
        }
    }

    private void updateDateEditText() {
        Date date =  mCalendar.getTime();

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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.date_edit_text:
            case R.id.date_icon_image_view:
                final DatePickerDialog.OnDateSetListener dateSetListener =
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                mCalendar.set(Calendar.YEAR, year);
                                mCalendar.set(Calendar.MONTH, monthOfYear);
                                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                updateDateEditText();
                                updateList();
                                SharedPref.getInstance().setDate(mCalendar.getTime(), getContext());
                            }
                        };
                if (getActivity() != null) {
                    new DatePickerDialog(
                            getActivity(),
                            dateSetListener,
                            mCalendar.get(Calendar.YEAR),
                            mCalendar.get(Calendar.MONTH),
                            mCalendar.get(Calendar.DAY_OF_MONTH)
                    ).show();
                }
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HealthRecordFormListener) {
            mListener = (HealthRecordFormListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HealthRecordFormListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface HealthRecordFormListener {
        void onViewGraphButtonClicked(Date date);
    }
}

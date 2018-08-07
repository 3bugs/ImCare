package com.example.imcare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.imcare.R;
import com.example.imcare.adapter.HealthRecordListAdapter;
import com.example.imcare.db.CareDb;
import com.example.imcare.model.HealthRecord;
import com.example.imcare.model.HealthRecordItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.imcare.etc.Const.HEALTH_RECORD_CATEGORY_SYSTEM;

public class HealthRecordForm2Fragment extends BaseFragment implements View.OnClickListener {

    private static final String ARG_HEALTH_RECORD_CATEGORY = "health_record_category";

    private int mHealthRecordCategory;

    private HealthRecordForm2FragmentListener mListener;

    private HealthRecordListAdapter mAdapter;
    private List<HealthRecordItem> mHealthRecordItemList = new ArrayList<>();

    public HealthRecordForm2Fragment() {
        // Required empty public constructor
    }

    public static HealthRecordForm2Fragment newInstance(int healthRecordCategory) {
        HealthRecordForm2Fragment fragment = new HealthRecordForm2Fragment();
        Bundle args = new Bundle();
        args.putInt(ARG_HEALTH_RECORD_CATEGORY, healthRecordCategory);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mHealthRecordCategory = getArguments().getInt(ARG_HEALTH_RECORD_CATEGORY);
        }

        Date date = mListener.getHealthRecord().date;

        SimpleDateFormat monthFormatter = new SimpleDateFormat("MM", Locale.US);
        String month = monthFormatter.format(date);

        SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy", Locale.US);
        String yearInBe = String.valueOf(Integer.valueOf(yearFormatter.format(date)) + 543);

        SimpleDateFormat dayFormatter = new SimpleDateFormat("dd", Locale.US);
        String day = dayFormatter.format(date);

        String msg = String.format(
                Locale.getDefault(),
                "บันทึกผลการตรวจสุขภาพ [%s.%s.%s]",
                day, month, yearInBe
        );
        setTitle(msg);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_health_record_form_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.previous_button).setOnClickListener(this);
        Button nextButton = view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(this);

        if (mHealthRecordCategory == HEALTH_RECORD_CATEGORY_SYSTEM) {
            nextButton.setText("เสร็จสิ้น");
        }

        final RecyclerView recyclerView = view.findViewById(R.id.record_item_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new HealthRecordListAdapter(
                getContext(),
                new HealthRecordListAdapter.HealthRecordListAdapterListener() {
                    @Override
                    public void onSaveHealthRecordItem(HealthRecordItem healthRecordItem) {
                        if (getContext() == null) return;

                        if (new CareDb(getContext()).saveHealthRecordItem(
                                mListener.getHealthRecord().id,
                                healthRecordItem
                        )) {
                            Toast.makeText(getContext(), "บันทึกข้อมูลเรียบร้อย", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "เกิดข้อผิดพลาดในการบันทึกข้อมูล!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    private void updateList() {
        if (getContext() != null) {
            mAdapter.setHealthRecordItemList(
                    new CareDb(getContext()).getHealthRecordItemListByDateAndCategory(
                            mListener.getHealthRecord().date,
                            mHealthRecordCategory
                    )
            );
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HealthRecordForm2FragmentListener) {
            mListener = (HealthRecordForm2FragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HealthRecordForm2FragmentListener");
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
            case R.id.previous_button:
                mListener.onHealthRecordForm2_Previous(mHealthRecordCategory);
                break;
            case R.id.next_button:
                mListener.onHealthRecordForm2_Next(mHealthRecordCategory);
                break;
        }
    }

    public interface HealthRecordForm2FragmentListener {
        HealthRecord getHealthRecord();

        void onHealthRecordForm2_Previous(int currentHealthRecordCategory);

        void onHealthRecordForm2_Next(int currentHealthRecordCategory);
    }
}

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
import android.widget.TextView;

import com.example.imcare.R;
import com.example.imcare.adapter.ProfileHealthRecordListAdapter;
import com.example.imcare.db.CareDb;
import com.example.imcare.etc.MyDateFormatter;
import com.example.imcare.model.HealthRecord;
import com.example.imcare.model.HealthRecordItem;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ProfileHealthRecordFragment extends BaseFragment {

    private static final String TAG = ProfileHealthRecordFragment.class.getName();
    private static final String ARG_HEALTH_RECORD = "health_record";

    private HealthRecord mHealthRecord;
    private List<HealthRecordItem> mHealthRecordItemList = new ArrayList<>();

    private ProfileHealthRecordFragmentListener mListener;

    public ProfileHealthRecordFragment() {
        // Required empty public constructor
        setTitle("ข้อมูลผลการตรวจสุขภาพ");
    }

    public static ProfileHealthRecordFragment newInstance(HealthRecord healthRecord) {
        ProfileHealthRecordFragment fragment = new ProfileHealthRecordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HEALTH_RECORD, new Gson().toJson(healthRecord));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mHealthRecord = new Gson().fromJson(getArguments().getString(ARG_HEALTH_RECORD), HealthRecord.class);
            if (getContext() != null) {
                mHealthRecordItemList = new CareDb(getContext())
                        .getHealthRecordItemListByDateAndCategory(mHealthRecord.date, -1);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView dateTextView = view.findViewById(R.id.date_text_view);
        TextView ageTextView = view.findViewById(R.id.age_text_view);
        TextView placeTextView = view.findViewById(R.id.place_text_view);
        TextView doctorTextView = view.findViewById(R.id.doctor_text_view);

        ageTextView.setText(String.valueOf(new CareDb(getActivity()).getProfile().getAge(mHealthRecord.date)));
        dateTextView.setText(MyDateFormatter.formatUi(mHealthRecord.date));
        placeTextView.setText(mHealthRecord.place);
        doctorTextView.setText(mHealthRecord.doctor);

        final RecyclerView recyclerView = view.findViewById(R.id.record_item_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ProfileHealthRecordListAdapter adapter = new ProfileHealthRecordListAdapter(
                getContext(),
                mHealthRecordItemList
        );
        recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_health_record, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileHealthRecordFragmentListener) {
            mListener = (ProfileHealthRecordFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ProfileHealthRecordFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ProfileHealthRecordFragmentListener {
    }
}

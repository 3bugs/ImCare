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

import com.example.imcare.R;
import com.example.imcare.adapter.HealthRecordGraphListAdapter;
import com.example.imcare.db.CareDb;
import com.example.imcare.model.HealthRecord;
import com.example.imcare.model.HealthRecordItem;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HealthRecordGraphFragment extends BaseFragment {

    private static final String TAG = HealthRecordGraphFragment.class.getName();
    private static final String ARG_HEALTH_RECORD = "health_record";

    private HealthRecord mHealthRecord;
    private List<HealthRecordItem> mHealthRecordItemList = new ArrayList<>();

    private HealthRecordGraphFragmentListener mListener;

    public HealthRecordGraphFragment() {
        // Required empty public constructor
        setTitle("เปรียบเทียบผลการตรวจสุขภาพ");
    }

    public static HealthRecordGraphFragment newInstance(HealthRecord healthRecord) {
        HealthRecordGraphFragment fragment = new HealthRecordGraphFragment();
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
        //Toast.makeText(getContext(), "Count: " + mHealthRecordItemList.size(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_health_record_graph, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        HealthRecordGraphListAdapter adapter = new HealthRecordGraphListAdapter(
                getContext(),
                mHealthRecordItemList
        );
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HealthRecordGraphFragmentListener) {
            mListener = (HealthRecordGraphFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HealthRecordGraphFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface HealthRecordGraphFragmentListener {
    }
}

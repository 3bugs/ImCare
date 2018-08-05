package com.example.imcare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.imcare.R;
import com.example.imcare.db.CareDb;
import com.example.imcare.model.HealthRecord;

public class HealthRecordListFragment extends BaseFragment {

    private static final String ARG_WHICH = "which";

    private HealthRecordListFragmentListener mListener;

    private int mWhich;

    public HealthRecordListFragment() {
        // Required empty public constructor
    }

    public static HealthRecordListFragment newInstance(int which) {
        HealthRecordListFragment fragment = new HealthRecordListFragment();
        Bundle arg = new Bundle();
        arg.putInt(ARG_WHICH, which);
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arg = getArguments();
        if (arg != null) {
            mWhich = arg.getInt(ARG_WHICH);
            switch (mWhich) {
                case 1:
                    setTitle("เปรียบเทียบผลการตรวจสุขภาพ");
                    break;
                case 2:
                    setTitle("ข้อมูลของฉัน");
                    break;
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_health_record_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null && getView() != null) {
            final ArrayAdapter<HealthRecord> adapter = new ArrayAdapter<>(
                    getContext(),
                    R.layout.item_health_record,
                    new CareDb(getContext()).getHealthRecord()
            );
            ListView listView = getView().findViewById(R.id.list_view);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    HealthRecord healthRecord = adapter.getItem(position);
                    if (mListener != null) {
                        mListener.onHealthRecordItemClick(healthRecord, mWhich);
                    }
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HealthRecordListFragmentListener) {
            mListener = (HealthRecordListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HealthRecordListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface HealthRecordListFragmentListener {
        void onHealthRecordItemClick(HealthRecord healthRecord, int which);
    }
}

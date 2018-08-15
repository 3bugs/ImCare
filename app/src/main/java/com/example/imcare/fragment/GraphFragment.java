package com.example.imcare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.imcare.R;
import com.example.imcare.model.HealthRecordLookup;
import com.github.mikephil.charting.charts.LineChart;
import com.google.gson.Gson;

public class GraphFragment extends Fragment {

    private static final String ARG_HEALTH_RECORD_LOOKUP_JSON = "health_record_lookup_json";

    private HealthRecordLookup mHealthRecordLookup;

    private GraphFragmentListener mListener;

    public GraphFragment() {
        // Required empty public constructor
    }

    public static GraphFragment newInstance(HealthRecordLookup healthRecordLookup) {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HEALTH_RECORD_LOOKUP_JSON, new Gson().toJson(healthRecordLookup));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String healthRecordLookupJson = getArguments().getString(ARG_HEALTH_RECORD_LOOKUP_JSON);
            mHealthRecordLookup = new Gson().fromJson(healthRecordLookupJson, HealthRecordLookup.class);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_graph, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LineChart graphView = view.findViewById(R.id.graph_view);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GraphFragmentListener) {
            mListener = (GraphFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement GraphFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface GraphFragmentListener {
    }
}

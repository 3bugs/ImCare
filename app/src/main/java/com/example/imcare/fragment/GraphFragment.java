package com.example.imcare.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.imcare.R;
import com.example.imcare.db.CareDb;
import com.example.imcare.model.HealthRecordLookup;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphFragment extends Fragment {

    private static final String ARG_HEALTH_RECORD_LOOKUP_JSON = "health_record_lookup_json";

    private HealthRecordLookup mHealthRecordLookup;
    private List<Map<String, Object>> mMapList;

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
            mMapList = new CareDb(getContext()).getHealthRecordItemByLookup(mHealthRecordLookup.id);
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

        float minValue = mHealthRecordLookup.minValue;
        float maxValue = mHealthRecordLookup.maxValue;

        //float minGraph =  value < minValue ? value : minValue;
        //float maxGraph =  value > maxValue ? value : maxValue;

        //https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/LineChartActivity1.java

        graphView.setDrawBorders(true);

        XAxis xAxis = graphView.getXAxis();
        xAxis.setEnabled(true);
        //xAxis.setAxisMinimum(0);
        //xAxis.setAxisMaximum(mMapList.size() + 1);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                axis.setGranularityEnabled(true);
                axis.resetAxisMaximum();
                axis.isAxisMaxCustom();
                return String.valueOf(mMapList.get(Math.round(value) - 1).get("date"));
            }
        });

        YAxis leftAxis = graphView.getAxisLeft();
        //leftAxis.setAxisMaximum(900f);
        //leftAxis.setAxisMinimum(-250f);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawGridLines(false);

        graphView.getAxisRight().setEnabled(false);

        // กำหนดข้อมูล
        ArrayList<Entry> valueEntries = new ArrayList<>();
        ArrayList<Entry> minValueEntries = new ArrayList<>();
        ArrayList<Entry> maxValueEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < mMapList.size(); i++) {
            float value = (float) mMapList.get(i).get("value");
            valueEntries.add(new Entry(i + 1, value));
            minValueEntries.add(new Entry(i + 1, minValue));
            maxValueEntries.add(new Entry(i + 1, maxValue));
            labels.add(String.valueOf(mMapList.get(i).get("date")));
        }

        LineDataSet valueDataSet = new LineDataSet(valueEntries, "ค่าของคุณ");
        valueDataSet.setColor(Color.parseColor("#289afb"));
        valueDataSet.setDrawCircles(false);
        valueDataSet.setLineWidth(2f);

        LineDataSet minValueDataSet = new LineDataSet(minValueEntries, "ค่าต่ำสุด");
        minValueDataSet.setColor(Color.parseColor("#ffa435"));
        minValueDataSet.setDrawCircles(false);
        minValueDataSet.setLineWidth(1f);

        LineDataSet maxValueDataSet = new LineDataSet(maxValueEntries, "ค่าสูงสุด");
        maxValueDataSet.setColor(Color.parseColor("#ff7e7e"));
        maxValueDataSet.setDrawCircles(false);
        maxValueDataSet.setLineWidth(1f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(valueDataSet); // add the datasets
        dataSets.add(minValueDataSet);
        dataSets.add(maxValueDataSet);

        //LineData data = new LineData(valueDataSet);
        LineData data = new LineData(dataSets);

        graphView.setExtraOffsets(4, 4, 4, 4);
        graphView.setMarker(null);
        graphView.setData(data);
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

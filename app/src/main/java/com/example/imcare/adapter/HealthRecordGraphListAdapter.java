package com.example.imcare.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.imcare.R;
import com.example.imcare.model.HealthRecordItem;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

//https://www.numetriclabz.com/android-line-chart-using-mpandroidchart-tutorial/

public class HealthRecordGraphListAdapter extends
        RecyclerView.Adapter<HealthRecordGraphListAdapter.GraphViewHolder> {

    private final Context mContext;
    private List<HealthRecordItem> mHealthRecordItemList;

    public HealthRecordGraphListAdapter(Context context, List<HealthRecordItem> healthRecordItemList) {
        mContext = context;
        mHealthRecordItemList = healthRecordItemList;
    }

    @NonNull
    @Override
    public GraphViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_graph, parent, false
        );
        return new GraphViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GraphViewHolder holder, int position) {
        final HealthRecordItem healthRecordItem = mHealthRecordItemList.get(position);
        holder.mHeaderTextView.setText(healthRecordItem.title);

        float value = healthRecordItem.getValue();
        float minValue = healthRecordItem.minValue;
        float maxValue = healthRecordItem.maxValue;

        //float minGraph =  value < minValue ? value : minValue;
        //float maxGraph =  value > maxValue ? value : maxValue;

        //https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/LineChartActivity1.java

        holder.mGraphView.setDrawBorders(true);

        XAxis xAxis = holder.mGraphView.getXAxis();
        xAxis.setEnabled(false);

        YAxis leftAxis = holder.mGraphView.getAxisLeft();
        //leftAxis.setAxisMaximum(900f);
        //leftAxis.setAxisMinimum(-250f);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawGridLines(false);

        holder.mGraphView.getAxisRight().setEnabled(false);

        // กำหนดข้อมูล
        ArrayList<Entry> valueEntries = new ArrayList<>();
        valueEntries.add(new Entry(0, value));
        valueEntries.add(new Entry(1, value));

        ArrayList<Entry> minValueEntries = new ArrayList<>();
        minValueEntries.add(new Entry(0, minValue));
        minValueEntries.add(new Entry(1, minValue));

        ArrayList<Entry> maxValueEntries = new ArrayList<>();
        maxValueEntries.add(new Entry(0, maxValue));
        maxValueEntries.add(new Entry(1, maxValue));

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

        holder.mGraphView.setExtraOffsets(4, 4, 4, 4);
        holder.mGraphView.setMarker(null);
        holder.mGraphView.setData(data);
    }

    @Override
    public int getItemCount() {
        return mHealthRecordItemList.size();
    }

    class GraphViewHolder extends RecyclerView.ViewHolder {

        private final TextView mHeaderTextView;
        private final LineChart mGraphView;

        GraphViewHolder(View itemView) {
            super(itemView);
            mHeaderTextView = itemView.findViewById(R.id.header_text_view);
            mGraphView = itemView.findViewById(R.id.graph_view);
        }
    }
}

package com.example.imcare.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.imcare.R;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CheckupGuideResultListAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private int mItemLayoutResId;
    private List<String> mCheckupList;

    public CheckupGuideResultListAdapter(@NonNull Context context,
                                         int resource,
                                         @NonNull List<String> checkupList) {
        super(context, resource, checkupList);
        mContext = context;
        mItemLayoutResId = resource;
        mCheckupList = checkupList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            view = inflater.inflate(mItemLayoutResId, parent, false);
        }

        TextView numberTextView = view.findViewById(R.id.number_text_view);
        TextView titleTextView = view.findViewById(R.id.title_text_view);

        String numberText = String.valueOf(position + 1) + ".";
        numberTextView.setText(numberText);
        titleTextView.setText(mCheckupList.get(position));

        return view;
    }
}

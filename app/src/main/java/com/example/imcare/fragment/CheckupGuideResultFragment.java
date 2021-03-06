package com.example.imcare.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.imcare.R;
import com.example.imcare.adapter.CheckupGuideResultListAdapter;
import com.example.imcare.db.CareDb;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.imcare.etc.Const.SEX_MALE;

public class CheckupGuideResultFragment extends BaseFragment {

    private static final String ARG_AGE = "age";
    private static final String ARG_SEX = "sex";

    private int mAge;
    private int mSex;
    private List<String> mCheckupList = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;

    private TextView mListCountTextView;

    public CheckupGuideResultFragment() {
        // Required empty public constructor
        setTitle("โปรแกรมการตรวจสุขภาพ\nที่เหมาะสมกับคุณ");
    }

    public static CheckupGuideResultFragment newInstance(int age, int sex) {
        CheckupGuideResultFragment fragment = new CheckupGuideResultFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_AGE, age);
        args.putInt(ARG_SEX, sex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAge = getArguments().getInt(ARG_AGE);
            mSex = getArguments().getInt(ARG_SEX);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkup_guide_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListCountTextView = view.findViewById(R.id.list_count_text_view);

        TextView ageSexTextView = view.findViewById(R.id.age_sex_text_view);
        ageSexTextView.setText(String.format(
                Locale.getDefault(),
                "เพศ: %s, อายุ: %d ปี",
                mSex == SEX_MALE ? "ชาย" : "หญิง",
                mAge
        ));

        if (getContext() != null) {
            mAdapter = new CheckupGuideResultListAdapter(
                    getContext(),
                    R.layout.item_checkup,
                    mCheckupList
            );
            ListView listView = view.findViewById(R.id.list_view);
            listView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataIntoList();
    }

    private void loadDataIntoList() {
        if (getContext() == null) {
            return;
        }
        List<String> checkupList = new CareDb(getContext()).getCheckupListByAgeAndSex(mAge, mSex);
        mListCountTextView.setText(String.valueOf(checkupList.size()));
        mCheckupList.clear();
        mCheckupList.addAll(checkupList);
        mAdapter.notifyDataSetChanged();
    }
}

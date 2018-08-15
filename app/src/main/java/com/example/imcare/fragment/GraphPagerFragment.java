package com.example.imcare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.imcare.R;
import com.example.imcare.db.CareDb;
import com.example.imcare.model.HealthRecord;
import com.example.imcare.model.HealthRecordLookup;

import java.util.List;

public class GraphPagerFragment extends BaseFragment {

    private GraphPagerFragmentListener mListener;

    public GraphPagerFragment() {
        // Required empty public constructor
        setTitle("เปรียบเทียบผลการตรวจสุขภาพ");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_graph_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null) {
            TabLayout tabLayout = view.findViewById(R.id.tab_layout);
            ViewPager viewPager = view.findViewById(R.id.view_pager);
            GraphPagerAdapter adapter = new GraphPagerAdapter(
                    getContext(),
                    getChildFragmentManager(),
                    new CareDb(getContext()).getHealthRecordLookup()
            );
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GraphPagerFragmentListener) {
            mListener = (GraphPagerFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement GraphPagerFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface GraphPagerFragmentListener {
    }

    private static class GraphPagerAdapter extends FragmentStatePagerAdapter {

        private Context mContext;
        private List<HealthRecordLookup> mHealthRecordLookupList;

        public GraphPagerAdapter(Context context, FragmentManager fm, List<HealthRecordLookup> healthRecordLookupList) {
            super(fm);
            mContext = context;
            mHealthRecordLookupList = healthRecordLookupList;
        }

        @Override
        public Fragment getItem(int position) {
            return GraphFragment.newInstance(mHealthRecordLookupList.get(position));
        }

        @Override
        public int getCount() {
            return mHealthRecordLookupList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mHealthRecordLookupList.get(position).toString();
        }
    }
}

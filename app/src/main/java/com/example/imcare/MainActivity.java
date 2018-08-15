package com.example.imcare;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.imcare.db.CareDb;
import com.example.imcare.etc.MyDateFormatter;
import com.example.imcare.etc.Utils;
import com.example.imcare.fragment.BaseFragment;
import com.example.imcare.fragment.CheckupGuideFormFragment;
import com.example.imcare.fragment.CheckupGuideMainFragment;
import com.example.imcare.fragment.CheckupGuideResultFragment;
import com.example.imcare.fragment.GraphFragment;
import com.example.imcare.fragment.GraphPagerFragment;
import com.example.imcare.fragment.HealthRecordForm1Fragment;
import com.example.imcare.fragment.HealthRecordForm2Fragment;
import com.example.imcare.fragment.HealthRecordFormFragment;
import com.example.imcare.fragment.HealthRecordGraphFragment;
import com.example.imcare.fragment.HealthRecordListFragment;
import com.example.imcare.fragment.HealthRecordMainFragment;
import com.example.imcare.fragment.ProfileDataFragment;
import com.example.imcare.fragment.ProfileHealthRecordFragment;
import com.example.imcare.model.HealthRecord;
import com.example.imcare.model.Profile;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.imcare.etc.Const.CHECKUP_GUIDE;
import static com.example.imcare.etc.Const.HEALTH_RECORD;
import static com.example.imcare.etc.Const.HEALTH_RECORD_CATEGORY_BODY;
import static com.example.imcare.etc.Const.HEALTH_RECORD_CATEGORY_FAT_GLUCOSE;
import static com.example.imcare.etc.Const.HEALTH_RECORD_CATEGORY_HEART_BLOOD;
import static com.example.imcare.etc.Const.HEALTH_RECORD_CATEGORY_SYSTEM;
import static com.example.imcare.etc.Const.PROFILE_DATA;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        CheckupGuideMainFragment.CheckupGuideMainFragmentListener,
        CheckupGuideFormFragment.CheckupGuideFormFragmentListener,
        HealthRecordMainFragment.HealthRecordMainFragmentListener,
        HealthRecordForm1Fragment.HealthRecordForm1FragmentListener,
        HealthRecordForm2Fragment.HealthRecordForm2FragmentListener,
        HealthRecordFormFragment.HealthRecordFormListener,
        ProfileDataFragment.ProfileDataFragmentListener,
        ProfileHealthRecordFragment.ProfileHealthRecordFragmentListener,
        HealthRecordGraphFragment.HealthRecordGraphFragmentListener,
        HealthRecordListFragment.HealthRecordListFragmentListener,
        GraphPagerFragment.GraphPagerFragmentListener,
        GraphFragment.GraphFragmentListener {

    private static final String TAG = MainActivity.class.getName();

    ImageView mCheckupGuideImageView, mHealthRecordImageView, mProfileDataImageView;

    private Calendar mHealthRecordDate = Calendar.getInstance();
    private HealthRecord mHealthRecord = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();

        mCheckupGuideImageView = findViewById(R.id.checkup_guide_image_view);
        mHealthRecordImageView = findViewById(R.id.health_record_image_view);
        mProfileDataImageView = findViewById(R.id.profile_data_image_view);

        mCheckupGuideImageView.setOnClickListener(this);
        mHealthRecordImageView.setOnClickListener(this);
        mProfileDataImageView.setOnClickListener(this);

        loadTopFragment(HEALTH_RECORD);

        CareDb careDb = new CareDb(this);
        String dateString = new MyDateFormatter().formatDb(careDb.getProfile().birthDate);
        int sex = careDb.getProfile().sex;
        String msg = String.format(
                Locale.getDefault(),
                "Birth date: %s\nSex: %d",
                dateString,
                sex
        );
        Utils.showLongToast(this, msg);

        List<Map<String, Object>> list = new CareDb(this).getHealthRecordItemByLookup(1);
        for (Map<String, Object> map : list) {
            String date = String.valueOf(map.get("date"));
            float value = (float) map.get("value");
            Log.i(TAG, String.format(Locale.getDefault(), "Date: %s, Value: %.1f", date, value));
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.health_record_image_view:
                loadTopFragment(HEALTH_RECORD);
                break;
            case R.id.checkup_guide_image_view:
                loadTopFragment(CHECKUP_GUIDE);
                break;
            case R.id.profile_data_image_view:
                loadTopFragment(PROFILE_DATA);
                break;
        }
    }

    private void loadTopFragment(int type) {
        Fragment fragment = null;

        switch (type) {
            case HEALTH_RECORD:
                fragment = new HealthRecordMainFragment();
                break;
            case CHECKUP_GUIDE:
                fragment = new CheckupGuideMainFragment();
                break;
            case PROFILE_DATA:
                //fragment = new ProfileDataFragment();
                fragment = HealthRecordListFragment.newInstance(2);
                break;
        }

        clearBackStack();
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.fragment_container,
                    fragment
            ).commit();
        }

        updateBottomNavImage(type);
    }

    private void clearBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    private void loadFragment(BaseFragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(
                        R.id.fragment_container,
                        fragment
                ).addToBackStack(null)
                .commit();
    }

    private void updateBottomNavImage(int type) {
        mCheckupGuideImageView.setImageResource(R.drawable.ic_checkup_guide_off);
        mHealthRecordImageView.setImageResource(R.drawable.ic_health_record_off);
        mProfileDataImageView.setImageResource(R.drawable.ic_profile_data_off);

        switch (type) {
            case CHECKUP_GUIDE:
                mCheckupGuideImageView.setImageResource(R.drawable.ic_checkup_guide_on);
                break;
            case HEALTH_RECORD:
                mHealthRecordImageView.setImageResource(R.drawable.ic_health_record_on);
                break;
            case PROFILE_DATA:
                mProfileDataImageView.setImageResource(R.drawable.ic_profile_data_on);
                break;
        }
    }

    @Override
    public void onSubmitCheckupGuideForm(int age, int sex) {
        getSupportFragmentManager().popBackStackImmediate();
        CheckupGuideResultFragment fragment = CheckupGuideResultFragment.newInstance(age, sex);
        loadFragment(fragment);
    }

    @Override
    public void onViewGraphButtonClicked(Date date) {
        /*Fragment fragment = HealthRecordGraphFragment.newInstance(date);
        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragment_container,
                fragment
        ).addToBackStack(null).commit();*/
    }

    @Override
    public void onHealthRecordMainButtonClick(int which) {
        BaseFragment fragment = null;
        switch (which) {
            case 1:
                fragment = new HealthRecordForm1Fragment();
                break;
            case 2:
                //fragment = new HealthRecordGraphFragment();
                //fragment = HealthRecordListFragment.newInstance(1);
                fragment = new GraphPagerFragment();
                break;
        }
        loadFragment(fragment);
    }

    @Override
    public void onCheckupGuideMainButtonClick(int which) {
        BaseFragment fragment = null;
        switch (which) {
            case 1:
                Profile profile = new CareDb(this).getProfile();
                int age = profile.getAge();
                int sex = profile.sex;
                fragment = CheckupGuideResultFragment.newInstance(age, sex);
                break;
            case 2:
                fragment = new CheckupGuideFormFragment();
                break;
        }
        loadFragment(fragment);
    }

    @Override
    public Calendar getHealthRecordDate() {
        return mHealthRecordDate;
    }

    @Override
    public void setHealthRecord(HealthRecord healthRecord) {
        mHealthRecord = healthRecord;
    }

    @Override
    public HealthRecord getHealthRecord() {
        return mHealthRecord;
    }

    @Override
    public void onHealthRecordForm1_Next() {
        loadFragment(HealthRecordForm2Fragment.newInstance(HEALTH_RECORD_CATEGORY_BODY));
    }

    @Override
    public void onHealthRecordForm2_Previous(int currentHealthRecordCategory) {
        getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onHealthRecordForm2_Next(int currentHealthRecordCategory) {
        int nextHealthRecordCategory = 0;
        switch (currentHealthRecordCategory) {
            case HEALTH_RECORD_CATEGORY_BODY:
                nextHealthRecordCategory = HEALTH_RECORD_CATEGORY_HEART_BLOOD;
                break;
            case HEALTH_RECORD_CATEGORY_HEART_BLOOD:
                nextHealthRecordCategory = HEALTH_RECORD_CATEGORY_FAT_GLUCOSE;
                break;
            case HEALTH_RECORD_CATEGORY_FAT_GLUCOSE:
                nextHealthRecordCategory = HEALTH_RECORD_CATEGORY_SYSTEM;
                break;
            case HEALTH_RECORD_CATEGORY_SYSTEM:

                break;
        }
        if (currentHealthRecordCategory != HEALTH_RECORD_CATEGORY_SYSTEM) {
            loadFragment(HealthRecordForm2Fragment.newInstance(nextHealthRecordCategory));
        } else {
            loadTopFragment(HEALTH_RECORD);
            mHealthRecord = null;
            Utils.showLongToast(this, "บันทึกข้อมูลเรียบร้อย");
        }
    }

    @Override
    public void onHealthRecordItemClick(HealthRecord healthRecord, int which) {
        BaseFragment fragment;
        switch (which) {
            case 1:
                fragment = HealthRecordGraphFragment.newInstance(healthRecord);
                loadFragment(fragment);
                break;
            case 2:
                fragment = ProfileHealthRecordFragment.newInstance(healthRecord);
                loadFragment(fragment);
                break;
        }
    }
}

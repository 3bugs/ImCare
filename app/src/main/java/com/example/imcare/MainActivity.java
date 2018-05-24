package com.example.imcare;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.example.imcare.fragment.CheckupGuideFormFragment;
import com.example.imcare.fragment.CheckupGuideResultFragment;
import com.example.imcare.fragment.HealthRecordFormFragment;
import com.example.imcare.fragment.HealthRecordGraphFragment;
import com.example.imcare.fragment.ProfileDataFragment;

import java.util.Date;

import static com.example.imcare.etc.Const.CHECKUP_GUIDE;
import static com.example.imcare.etc.Const.HEALTH_RECORD;
import static com.example.imcare.etc.Const.PROFILE_DATA;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        CheckupGuideFormFragment.CheckupGuideFormFragmentListener,
        HealthRecordFormFragment.HealthRecordFormListener,
        ProfileDataFragment.ProfileDataFragmentListener,
        HealthRecordGraphFragment.HealthRecordGraphFragmentListener {

    ImageView mCheckupGuideImageView, mHealthRecordImageView, mProfileDataImageView;

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

        loadFragment(HEALTH_RECORD);
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
            case R.id.checkup_guide_image_view:
                loadFragment(CHECKUP_GUIDE);
                break;
            case R.id.health_record_image_view:
                loadFragment(HEALTH_RECORD);
                break;
            case R.id.profile_data_image_view:
                loadFragment(PROFILE_DATA);
                break;
        }
    }

    private void loadFragment(int type) {
        Fragment fragment = null;

        switch (type) {
            case CHECKUP_GUIDE:
                fragment = new CheckupGuideFormFragment();
                break;
            case HEALTH_RECORD:
                fragment = new HealthRecordFormFragment();
                break;
            case PROFILE_DATA:
                fragment = new ProfileDataFragment();
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
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
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
        Fragment fragment = CheckupGuideResultFragment.newInstance(age, sex);
        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragment_container,
                fragment
        ).addToBackStack(null).commit();
    }

    @Override
    public void onViewGraphButtonClicked(Date date) {
        Fragment fragment = HealthRecordGraphFragment.newInstance(date);
        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragment_container,
                fragment
        ).addToBackStack(null).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

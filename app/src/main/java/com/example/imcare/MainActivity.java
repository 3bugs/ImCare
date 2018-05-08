package com.example.imcare;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.imcare.fragment.CheckupGuideFormFragment;
import com.example.imcare.fragment.CheckupGuideResultFragment;
import com.example.imcare.fragment.HealthRecordFormFragment;
import com.example.imcare.fragment.ProfileDataFragment;

import static com.example.imcare.etc.Const.CHECKUP_GUIDE;
import static com.example.imcare.etc.Const.HEALTH_RECORD;
import static com.example.imcare.etc.Const.PROFILE_DATA;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        CheckupGuideFormFragment.CheckupGuideFormFragmentListener,
        HealthRecordFormFragment.HealthRecordFormListener,
        ProfileDataFragment.ProfileDataFragmentListener {

    ImageView mCheckupGuideImageView, mHealthRecordImageView, mProfileDataImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCheckupGuideImageView = findViewById(R.id.checkup_guide_image_view);
        mHealthRecordImageView = findViewById(R.id.health_record_image_view);
        mProfileDataImageView = findViewById(R.id.profile_data_image_view);

        mCheckupGuideImageView.setOnClickListener(this);
        mHealthRecordImageView.setOnClickListener(this);
        mProfileDataImageView.setOnClickListener(this);

        loadFragment(CHECKUP_GUIDE);
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
        mCheckupGuideImageView.setImageResource(R.drawable.ic_checkup_guide_normal);
        mHealthRecordImageView.setImageResource(R.drawable.ic_health_record_normal);
        mProfileDataImageView.setImageResource(R.drawable.ic_profile_data_normal);

        switch (type) {
            case CHECKUP_GUIDE:
                mCheckupGuideImageView.setImageResource(R.drawable.ic_checkup_guide_pressed);
                break;
            case HEALTH_RECORD:
                mHealthRecordImageView.setImageResource(R.drawable.ic_health_record_pressed);
                break;
            case PROFILE_DATA:
                mProfileDataImageView.setImageResource(R.drawable.ic_profile_data_pressed);
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSubmitCheckupGuideForm(int age, int sex) {
        Fragment fragment = CheckupGuideResultFragment.newInstance(age, sex);
        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragment_container,
                fragment
        ).addToBackStack(null).commit();
    }
}

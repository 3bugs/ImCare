package com.example.imcare;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imcare.etc.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.imcare.etc.Const.SEX_FEMALE;
import static com.example.imcare.etc.Const.SEX_MALE;
import static com.example.imcare.etc.Const.SEX_UNDEFINED;

public class FirstRunFormActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mBirthDateEditText;
    private ImageView mMaleImageView, mFemaleImageView;
    private TextView mSexTextView;

    private int mSex = SEX_UNDEFINED;
    private Calendar mCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run_form);

        mSexTextView = findViewById(R.id.sex_text_view);
        mBirthDateEditText = findViewById(R.id.birth_date_edit_text);
        mBirthDateEditText.setOnClickListener(this);
        mMaleImageView = findViewById(R.id.male_image_view);
        mFemaleImageView = findViewById(R.id.female_image_view);
        mMaleImageView.setOnClickListener(this);
        mFemaleImageView.setOnClickListener(this);
        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(this);
    }

    private void showDatePicker() {
        final DatePickerDialog.OnDateSetListener dateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, monthOfYear);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateEditText();
                        mBirthDateEditText.setError(null);
                    }
                };
        new DatePickerDialog(
                FirstRunFormActivity.this,
                dateSetListener,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void updateDateEditText() {
        Date date = mCalendar.getTime();

        SimpleDateFormat monthFormatter = new SimpleDateFormat("MM", Locale.US);
        String month = monthFormatter.format(date);

        SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy", Locale.US);
        String yearInBe = String.valueOf(Integer.valueOf(yearFormatter.format(date)) + 543);

        SimpleDateFormat dayFormatter = new SimpleDateFormat("dd", Locale.US);
        String day = dayFormatter.format(date);

        String dateText = String.format(
                Locale.getDefault(),
                "%s / %s / %s",
                day, month, yearInBe
        );
        mBirthDateEditText.setText(dateText);
    }

    private void updateSexImage() {
        float padding;
        switch (mSex) {
            case SEX_MALE:
                mSexTextView.setText("ชาย");

                mMaleImageView.setImageResource(R.drawable.ic_male_on);
                padding = Utils.convertDpToPixel(0, this);
                mMaleImageView.setPadding(Math.round(padding), Math.round(padding),
                        Math.round(padding), Math.round(padding));

                mFemaleImageView.setImageResource(R.drawable.ic_female_off);
                padding = Utils.convertDpToPixel(0, this);
                mFemaleImageView.setPadding(Math.round(padding), Math.round(padding),
                        Math.round(padding), Math.round(padding));
                break;

            case SEX_FEMALE:
                mSexTextView.setText("หญิง");

                mMaleImageView.setImageResource(R.drawable.ic_male_off);
                padding = Utils.convertDpToPixel(0, this);
                mMaleImageView.setPadding(Math.round(padding), Math.round(padding),
                        Math.round(padding), Math.round(padding));

                mFemaleImageView.setImageResource(R.drawable.ic_female_on);
                padding = Utils.convertDpToPixel(0, this);
                mFemaleImageView.setPadding(Math.round(padding), Math.round(padding),
                        Math.round(padding), Math.round(padding));
                break;

            case SEX_UNDEFINED:
                break;
        }
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void submitButtonClicked() {
        if (validateForm()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "กรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateForm() {
        String errorMessage;
        boolean valid = true;

        if (mBirthDateEditText.getText().toString().trim().isEmpty()) {
            mBirthDateEditText.requestFocus();
            errorMessage = "กรุณาระบุวัน/เดือน/ปีเกิด";
            mBirthDateEditText.setError(errorMessage);
            valid = false;
        }
        if (mSex == SEX_UNDEFINED) {
            errorMessage = "กรุณาระบุเพศ";
            mSexTextView.setError(errorMessage);
            valid = false;
        }

        return valid;
    }

    @Override
    public void onClick(View view) {
        hideKeyboard();
        switch (view.getId()) {
            case R.id.birth_date_edit_text:
                showDatePicker();
                break;
            case R.id.male_image_view:
                mSex = SEX_MALE;
                updateSexImage();
                mSexTextView.setError(null);
                break;
            case R.id.female_image_view:
                mSex = SEX_FEMALE;
                updateSexImage();
                mSexTextView.setError(null);
                break;
            case R.id.submit_button:
                submitButtonClicked();
                break;
        }
    }
}

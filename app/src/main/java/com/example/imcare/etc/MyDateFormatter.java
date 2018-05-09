package com.example.imcare.etc;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.imcare.db.CareDb.STORED_DATE_FORMAT;

public class MyDateFormatter {

    private static final String TAG = MyDateFormatter.class.getName();

    private SimpleDateFormat mDateFormatter;

    public MyDateFormatter() {
        mDateFormatter = new SimpleDateFormat(STORED_DATE_FORMAT, Locale.US);
    }

    public String format(Date date) {
        return mDateFormatter.format(date);
    }

    public Date parse(String dateString) {
        Date date = null;
        try {
            date = mDateFormatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "Error parsing date");
        }
        return date;
    }
}
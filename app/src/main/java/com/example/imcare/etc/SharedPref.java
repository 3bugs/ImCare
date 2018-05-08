package com.example.imcare.etc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.imcare.db.CareDb.STORED_DATE_FORMAT;

public class SharedPref {

    private static final String KEY_PREF = "pref";
    private static final String KEY_LAST_SELECTED_DATE = "last_selected_date";

    private static SharedPref sInstance = null;

    private SharedPref() {
    }

    public static SharedPref getInstance() {
        if (sInstance == null) {
            sInstance = new SharedPref();
        }
        return sInstance;
    }

    @SuppressLint("ApplySharedPref")
    public boolean setDate(Date date, Context context) {
        SimpleDateFormat formatter = new SimpleDateFormat(STORED_DATE_FORMAT, Locale.US);
        String dateString = formatter.format(date);

        SharedPreferences.Editor editor = context.getSharedPreferences(KEY_PREF, 0).edit();
        editor.putString(KEY_LAST_SELECTED_DATE, dateString);
        return editor.commit();
    }

    public Date getDate(Context context) {
        Date now = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat(STORED_DATE_FORMAT, Locale.US);
        String nowString = formatter.format(now);

        SharedPreferences pref = context.getSharedPreferences(KEY_PREF, 0);
        String dateString = pref.getString(KEY_LAST_SELECTED_DATE, nowString);

        Date date = null;
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}

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

    // จัดรูปแบบวันที่ สำหรับเก็บลง database
    public String formatDb(Date date) {
        return mDateFormatter.format(date);
    }

    // parse วันที่ ที่อ่านมาจาก database
    public Date parseDb(String dateString) {
        Date date = null;
        try {
            date = mDateFormatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "Error parsing date");
        }
        return date;
    }

    // จัดรูปแบบวันที่ สำหรับแสดงผลบนหน้าจอ
    public static String formatUi(Date date) {
        SimpleDateFormat monthFormatter = new SimpleDateFormat("MM", Locale.US);
        String month = monthFormatter.format(date);

        SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy", Locale.US);
        String yearInBe = String.valueOf(Integer.valueOf(yearFormatter.format(date)));

        SimpleDateFormat dayFormatter = new SimpleDateFormat("dd", Locale.US);
        String day = dayFormatter.format(date);

        return String.format(
                Locale.getDefault(),
                "%s.%s.%s",
                day, month, yearInBe
        );
    }
}
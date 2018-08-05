package com.example.imcare.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HealthRecord {

    public final long id;
    public final Date date;
    public final String place;
    public final String doctor;

    public HealthRecord(long id, Date date, String place, String doctor) {
        this.id = id;
        this.date = date;
        this.place = place;
        this.doctor = doctor;
    }

    @Override
    public String toString() {
        SimpleDateFormat monthFormatter = new SimpleDateFormat("MM", Locale.US);
        String month = monthFormatter.format(date);

        SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy", Locale.US);
        String yearInBe = String.valueOf(Integer.valueOf(yearFormatter.format(date)) + 543);

        SimpleDateFormat dayFormatter = new SimpleDateFormat("dd", Locale.US);
        String day = dayFormatter.format(date);

        String dateText = String.format(
                Locale.getDefault(),
                "%s.%s.%s",
                day, month, yearInBe
        );

        return "ข้อมูลผลการตรวจสุขภาพ : " + dateText;
    }
}

package com.example.imcare.model;

import com.example.imcare.etc.MyDateFormatter;

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
        return "ข้อมูลผลการตรวจสุขภาพ : " + MyDateFormatter.formatUi(this.date);
    }
}

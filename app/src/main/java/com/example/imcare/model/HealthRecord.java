package com.example.imcare.model;

import java.util.Date;

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
}

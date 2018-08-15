package com.example.imcare.model;

public class HealthRecordLookup {

    public final long id;
    public final String title;
    public final String name;
    public final float minValue;
    public final float maxValue;
    public final int category;

    public HealthRecordLookup(long id, String title, String name, float minValue, float maxValue, int category) {
        this.id = id;
        this.title = title;
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.category = category;
    }

    @Override
    public String toString() {
        return title;
    }
}

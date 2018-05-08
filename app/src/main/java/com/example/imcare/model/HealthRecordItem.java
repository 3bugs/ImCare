package com.example.imcare.model;

public class HealthRecordItem {

    public final int id;
    public final String title;
    public final String name;
    public final String unit;
    private float value;
    public final float minValue;
    public final float maxValue;
    public final int category;
    //public final int recordId;

    public HealthRecordItem(int id, String title, String name, String unit, float value,
                            float minValue, float maxValue, int category/*, int recordId*/) {
        this.id = id;
        this.title = title;
        this.name = name;
        this.unit = unit;
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.category = category;
        //this.recordId = recordId;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return title;
    }
}

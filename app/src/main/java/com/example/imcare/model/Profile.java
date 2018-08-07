package com.example.imcare.model;

import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class Profile {

    public final Date birthDate;
    public final int sex;

    public Profile(Date birthDate, int sex) {
        this.birthDate = birthDate;
        this.sex = sex;
    }

    public int getAge() {
        return getDiffYears(birthDate, new Date());
    }

    public int getAge(Date date) {
        return getDiffYears(birthDate, date);
    }

    private int getDiffYears(Date first, Date last) {
        Calendar a = Calendar.getInstance();
        a.setTime(first);
        Calendar b = Calendar.getInstance();
        b.setTime(last);
        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH) ||
                (a.get(MONTH) == b.get(MONTH)
                        && a.get(Calendar.DAY_OF_MONTH) > b.get(Calendar.DAY_OF_MONTH))) {
            diff--;
        }
        return diff;
    }
}

package com.example.imcare.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.imcare.etc.MyDateFormatter;
import com.example.imcare.model.HealthRecord;
import com.example.imcare.model.HealthRecordItem;
import com.example.imcare.model.Profile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.imcare.etc.Const.HEALTH_RECORD_CATEGORY_BODY;
import static com.example.imcare.etc.Const.HEALTH_RECORD_CATEGORY_FAT_GLUCOSE;
import static com.example.imcare.etc.Const.HEALTH_RECORD_CATEGORY_HEART_BLOOD;
import static com.example.imcare.etc.Const.HEALTH_RECORD_CATEGORY_SYSTEM;
import static com.example.imcare.etc.Const.HEALTH_RECORD_VALUE_EMPTY;
import static com.example.imcare.etc.Const.SEX_FEMALE;
import static com.example.imcare.etc.Const.SEX_MALE;

public class CareDb {

    private static final String TAG = CareDb.class.getName();
    public static final String STORED_DATE_FORMAT = "yyyy-MM-dd";

    private static final String DATABASE_NAME = "care.db";
    private static final int DATABASE_VERSION = 15;

    // เทเบิล checkup_guide
    // +-----+-------+-----+---------+---------+
    // | _id | title | sex | min_age | max_age |
    // +-----+-------+-----+---------+---------+
    // |     |       |     |         |         |

    private static final String TABLE_CHECKUP_GUIDE = "checkup_guide";
    private static final String COL_ID = "_id";
    private static final String COL_TITLE = "title";
    private static final String COL_SEX = "sex";
    private static final String COL_MIN_AGE = "min_age";
    private static final String COL_MAX_AGE = "max_age";

    private static final String SQL_CREATE_TABLE_CHECK_GUIDE =
            "CREATE TABlE " + TABLE_CHECKUP_GUIDE + "("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_TITLE + " TEXT, "
                    + COL_SEX + " INTEGER, " // SEX_MALE, SEX_FEMALE
                    + COL_MIN_AGE + " INTEGER, "
                    + COL_MAX_AGE + " INTEGER"
                    + ")";

    // เทเบิล health_record_lookup
    // +-----+-------+------+------+-----------+-----------+----------+
    // | _id | title | name | unit | min_value | max_value | category |
    // +-----+-------+------+------+-----------+-----------+----------+
    // |     |       |      |      |           |           |          |

    private static final String TABLE_HEALTH_RECORD_LOOKUP = "health_record_lookup";
    private static final String COL_NAME = "name";
    private static final String COL_UNIT = "unit";
    private static final String COL_MIN_VALUE = "min_value";
    private static final String COL_MAX_VALUE = "max_value";
    private static final String COL_CATEGORY = "category";

    private static final String SQL_CREATE_TABLE_HEALTH_RECORD_LOOKUP =
            "CREATE TABlE " + TABLE_HEALTH_RECORD_LOOKUP + "("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_TITLE + " TEXT, "
                    + COL_NAME + " TEXT, "
                    + COL_UNIT + " TEXT, "
                    + COL_MIN_VALUE + " REAL, "
                    + COL_MAX_VALUE + " REAL, "
                    + COL_CATEGORY + " INTEGER"
                    + ")";

    // เทเบิล health_record
    // +-----+------+-------+--------+
    // | _id | date | place | doctor |
    // +-----+------+-------+--------+
    // |     |      |       |        |

    private static final String TABLE_HEALTH_RECORD = "health_record";
    private static final String COL_DATE = "date";
    private static final String COL_PLACE = "place";
    private static final String COL_DOCTOR = "doctor";

    private static final String SQL_CREATE_TABLE_HEALTH_RECORD =
            "CREATE TABlE " + TABLE_HEALTH_RECORD + "("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_DATE + " TEXT, "
                    + COL_PLACE + " TEXT, "
                    + COL_DOCTOR + " TEXT "
                    + ")";

    // เทเบิล health_record_details
    // +-----------+-----------+-------+
    // | record_id | lookup_id | value |
    // +-----------+-----------+-------+
    // |           |           |       |

    private static final String TABLE_HEALTH_RECORD_DETAILS = "health_record_details";
    private static final String COL_RECORD_ID = "record_id";
    private static final String COL_LOOKUP_ID = "lookup_id";
    private static final String COL_VALUE = "value";

    private static final String SQL_CREATE_TABLE_HEALTH_RECORD_DETAILS =
            "CREATE TABlE " + TABLE_HEALTH_RECORD_DETAILS + "("
                    + COL_RECORD_ID + " INTEGER, "
                    + COL_LOOKUP_ID + " INTEGER, "
                    + COL_VALUE + " REAL, "
                    + " PRIMARY KEY (" + COL_RECORD_ID + ", " + COL_LOOKUP_ID + ")"
                    + ")";

    // เทเบิล profile
    // +------------+-----+
    // | birth_date | sex |
    // +------------+-----+
    // |            |     |

    private static final String TABLE_PROFILE = "profile";
    private static final String COL_BIRTH_DATE = "birth_date";

    private static final String SQL_CREATE_TABLE_PROFILE =
            "CREATE TABlE " + TABLE_PROFILE + "("
                    + COL_BIRTH_DATE + " TEXT, "
                    + COL_SEX + " INTEGER " // SEX_MALE, SEX_FEMALE
                    + ")";

    private static DatabaseHelper sDbHelper;
    private SQLiteDatabase mDatabase;
    private Context mContext;

    public CareDb(Context context) {
        mContext = context.getApplicationContext();

        if (sDbHelper == null) {
            sDbHelper = new DatabaseHelper(context);
        }
        mDatabase = sDbHelper.getWritableDatabase();
    }

    public void setProfile(Date birthDate, int sex) {
        mDatabase.delete(TABLE_PROFILE, null, null);

        ContentValues cv = new ContentValues();
        cv.put(COL_BIRTH_DATE, new MyDateFormatter().formatDb(birthDate));
        cv.put(COL_SEX, sex);
        mDatabase.insert(TABLE_PROFILE, null, cv);
    }

    public Profile getProfile() {
        Cursor cursor = mDatabase.query(
                TABLE_PROFILE,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Profile profile;
        if (cursor.getCount() == 0) {
            profile = null;
        } else {
            cursor.moveToFirst();
            profile = new Profile(
                    new MyDateFormatter().parseDb(cursor.getString(cursor.getColumnIndex(COL_BIRTH_DATE))),
                    cursor.getInt(cursor.getColumnIndex(COL_SEX))
            );
        }
        cursor.close();
        return profile;
    }

    public List<String> getCheckupListByAgeAndSex(int age, int sex) {
        /*String sql = "SELECT " + COL_TITLE + " FROM " + TABLE_CHECKUP_GUIDE
                + " WHERE " + COL_SEX + " = " + sex + " AND "
                + COL_MIN_AGE + " <= " + age + " AND " + COL_MAX_AGE + " >= " + age;*/

        String selection = String.format(
                Locale.getDefault(),
                "%s <= ? AND %s >= ? AND %s = ?",
                COL_MIN_AGE,
                COL_MAX_AGE,
                COL_SEX
        );
        Cursor cursor = mDatabase.query(
                TABLE_CHECKUP_GUIDE,
                null,
                selection,
                new String[]{String.valueOf(age), String.valueOf(age), String.valueOf(sex)},
                null,
                null,
                null
        );
        List<String> checkupList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(COL_TITLE));
            checkupList.add(title);
        }
        cursor.close();
        return checkupList;
    }

    public List<HealthRecord> getHealthRecord() {
        List<HealthRecord> healthRecordList = new ArrayList<>();

        Cursor cursor = mDatabase.query(
                TABLE_HEALTH_RECORD,
                null,
                null,
                null,
                null,
                null,
                COL_DATE
        );
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
            String dateString = cursor.getString(cursor.getColumnIndex(COL_DATE));
            String place = cursor.getString(cursor.getColumnIndex(COL_PLACE));
            String doctor = cursor.getString(cursor.getColumnIndex(COL_DOCTOR));

            healthRecordList.add(
                    new HealthRecord(id, new MyDateFormatter().parseDb(dateString), place, doctor)
            );
        }
        cursor.close();
        return healthRecordList;
    }

    public void getHealthRecordItemByLookup(int lookupId) {
        String sql = "SELECT hr.date AS date, hrd.value AS value "
                + " FROM health_record hr INNER JOIN health_record_details hrd ON hr._id = hrd.record_id "
                + " WHERE hrd.lookup_id = ? ";
        Cursor cursor = mDatabase.rawQuery(sql, new String[]{String.valueOf(lookupId)});

        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex("date"));
            float value = cursor.getFloat(cursor.getColumnIndex("value"));
            Log.i(TAG, String.format(Locale.getDefault(), "Date: %s, Value: %.1f", date, value));
        }

        cursor.close();
    }

    public List<HealthRecordItem> getHealthRecordItemListByDateAndCategory(Date date, int category) {
        List<HealthRecordItem> healthRecordItemList = new ArrayList<>();

        final String LOOKUP_ID = "lookup_id";
        String dateString = new MyDateFormatter().formatDb(date);

        String sql = "SELECT " + " l." + COL_ID + " AS " + LOOKUP_ID
                + ", l." + COL_TITLE + ", l." + COL_NAME + ", l." + COL_UNIT
                + ", l." + COL_MIN_VALUE + ", l." + COL_MAX_VALUE + ", l." + COL_CATEGORY
                + ", r." + COL_VALUE
                + " FROM health_record_lookup l LEFT JOIN "
                + " (SELECT hrd.lookup_id, hrd.value FROM health_record hr INNER JOIN health_record_details hrd ON hr._id = hrd.record_id AND hr.date='" + dateString + "') AS r "
                + " ON l." + COL_ID + "=" + " r." + COL_LOOKUP_ID
                + (category == -1 ? "" : " WHERE " + COL_CATEGORY + "=?");

        Cursor cursor;
        if (category == -1) {
            cursor = mDatabase.rawQuery(sql, null);
        } else {
            cursor = mDatabase.rawQuery(sql, new String[]{String.valueOf(category)});
        }

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(LOOKUP_ID));
            String title = cursor.getString(cursor.getColumnIndex(COL_TITLE));
            String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
            String unit = cursor.getString(cursor.getColumnIndex(COL_UNIT));
            float minValue = cursor.getFloat(cursor.getColumnIndex(COL_MIN_VALUE));
            float maxValue = cursor.getFloat(cursor.getColumnIndex(COL_MAX_VALUE));
            int cat = cursor.getInt(cursor.getColumnIndex(COL_CATEGORY));

            float value = HEALTH_RECORD_VALUE_EMPTY;
            int valueColumnIndex = cursor.getColumnIndex(COL_VALUE);
            if (!cursor.isNull(valueColumnIndex)) {
                value = cursor.getFloat(valueColumnIndex);
            }

            healthRecordItemList.add(
                    new HealthRecordItem(
                            id, title, name, unit, value,
                            minValue, maxValue, cat/*, recordId*/
                    )
            );
        }
        cursor.close();
        return healthRecordItemList;
    }

    /*public List<HealthRecordItem> getHealthRecordItemListByDate(Date date) {
        List<HealthRecordItem> healthRecordItemList = new ArrayList<>();

        final String LOOKUP_ID = "lookup_id";
        final String RECORD_ID = "record_id";
        String sql = "SELECT " + " l." + COL_ID + " AS " + LOOKUP_ID
                + ", l." + COL_TITLE + ", l." + COL_NAME + ", l." + COL_UNIT
                + ", l." + COL_MIN_VALUE + ", l." + COL_MAX_VALUE + ", l." + COL_CATEGORY
                *//*+ ", r." + COL_ID + " AS " + RECORD_ID*//*
                + ", r." + COL_DATE + ", r." + COL_VALUE
                + " FROM "
                + TABLE_HEALTH_RECORD_LOOKUP + " l LEFT JOIN " + TABLE_HEALTH_RECORD + " r "
                + " ON l." + COL_ID + "=" + " r." + COL_LOOKUP_ID
                + " AND " + COL_DATE + "=?";

        SimpleDateFormat formatter = new SimpleDateFormat(STORED_DATE_FORMAT, Locale.US);
        String dateString = formatter.formatDb(date);
        Cursor cursor = mDatabase.rawQuery(sql, new String[]{dateString});

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(LOOKUP_ID));
            String title = cursor.getString(cursor.getColumnIndex(COL_TITLE));
            String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
            String unit = cursor.getString(cursor.getColumnIndex(COL_UNIT));
            float minValue = cursor.getFloat(cursor.getColumnIndex(COL_MIN_VALUE));
            float maxValue = cursor.getFloat(cursor.getColumnIndex(COL_MAX_VALUE));
            int category = cursor.getInt(cursor.getColumnIndex(COL_CATEGORY));

            float value = HEALTH_RECORD_VALUE_EMPTY;
            int valueColumnIndex = cursor.getColumnIndex(COL_VALUE);
            if (!cursor.isNull(valueColumnIndex)) {
                value = cursor.getFloat(valueColumnIndex);
            }

            *//*int recordId = 0;
            int recordIdColumnIndex = cursor.getColumnIndex(RECORD_ID);
            if (!cursor.isNull(recordIdColumnIndex)) {
                recordId = cursor.getInt(recordIdColumnIndex);
            }*//*

            healthRecordItemList.add(
                    new HealthRecordItem(
                            id, title, name, unit, value,
                            minValue, maxValue, category*//*, recordId*//*
                    )
            );
        }
        cursor.close();
        return healthRecordItemList;
    }*/

    public boolean saveHealthRecord(HealthRecordItem healthRecordItem, Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(STORED_DATE_FORMAT, Locale.US);
        String dateString = formatter.format(date);

        ContentValues cv = new ContentValues();
        cv.put(COL_LOOKUP_ID, healthRecordItem.id);
        cv.put(COL_DATE, dateString);
        cv.put(COL_VALUE, healthRecordItem.getValue());
        long insertResult = mDatabase.insert(TABLE_HEALTH_RECORD, null, cv);
        if (insertResult == -1) {
            cv = new ContentValues();
            cv.put(COL_VALUE, healthRecordItem.getValue());
            return mDatabase.update(
                    TABLE_HEALTH_RECORD,
                    cv,
                    COL_LOOKUP_ID + "=? AND " + COL_DATE + "=?",
                    new String[]{String.valueOf(healthRecordItem.id), dateString}
            ) > 0;
        } else {
            return true;
        }
    }

    public boolean saveHealthRecordItem(long recordId, HealthRecordItem healthRecordItem) {
        ContentValues cv = new ContentValues();
        cv.put(COL_RECORD_ID, recordId);
        cv.put(COL_LOOKUP_ID, healthRecordItem.id);
        cv.put(COL_VALUE, healthRecordItem.getValue());
        long insertResult = mDatabase.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);
        if (insertResult == -1) {
            Log.d(TAG, "มีข้อมูลแล้ว, ทำการอัพเดท");

            cv = new ContentValues();
            cv.put(COL_VALUE, healthRecordItem.getValue());
            return mDatabase.update(
                    TABLE_HEALTH_RECORD_DETAILS,
                    cv,
                    COL_RECORD_ID + "=? AND " + COL_LOOKUP_ID + "=?",
                    new String[]{String.valueOf(recordId), String.valueOf(healthRecordItem.id)}
            ) > 0;
        } else {
            return true;
        }
    }

    public HealthRecord addHealthRecord(Date date, String place, String doctor) {
        String dateString = new MyDateFormatter().formatDb(date);

        ContentValues cv = new ContentValues();
        cv.put(COL_DATE, dateString);
        cv.put(COL_PLACE, place);
        cv.put(COL_DOCTOR, doctor);
        mDatabase.insert(TABLE_HEALTH_RECORD, null, cv);

        return getHealthRecordByDate(date);
    }

    public void updateHealthRecordByDate(Date date, String place, String doctor) {
        ContentValues cv = new ContentValues();
        cv.put(COL_PLACE, place);
        cv.put(COL_DOCTOR, doctor);

        mDatabase.update(
                TABLE_HEALTH_RECORD,
                cv,
                COL_DATE + "=?",
                new String[]{new MyDateFormatter().formatDb(date)}
        );
    }

    public HealthRecord getHealthRecordByDate(Date date) {
        String dateString = new MyDateFormatter().formatDb(date);

        Cursor cursor = mDatabase.query(
                TABLE_HEALTH_RECORD,
                null,
                COL_DATE + "=?",
                new String[]{dateString},
                null,
                null,
                null
        );
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        } else {
            cursor.moveToFirst();
            HealthRecord healthRecord = new HealthRecord(
                    cursor.getLong(cursor.getColumnIndex(COL_ID)),
                    new MyDateFormatter().parseDb(cursor.getString(cursor.getColumnIndex(COL_DATE))),
                    cursor.getString(cursor.getColumnIndex(COL_PLACE)),
                    cursor.getString(cursor.getColumnIndex(COL_DOCTOR))
            );
            cursor.close();
            return healthRecord;
        }
    }

    public List<HealthRecordItem> testQuery() {
        String dateString = "2018-05-08";

        String sql = "SELECT " + " l." + COL_ID + " AS lookup_id "
                + ", l." + COL_TITLE + ", l." + COL_NAME + ", l." + COL_UNIT
                + ", l." + COL_MIN_VALUE + ", l." + COL_MAX_VALUE + ", l." + COL_CATEGORY
                + ", r." + COL_VALUE
                + " FROM health_record_lookup l LEFT JOIN "
                + " (SELECT hrd.lookup_id, hrd.value FROM health_record hr INNER JOIN health_record_details hrd ON hr._id = hrd.record_id AND hr.date='" + dateString + "') AS r "
                + " ON l." + COL_ID + "=" + " r." + COL_LOOKUP_ID
                + " WHERE " + COL_CATEGORY + "=?";

        Cursor cursor = mDatabase.rawQuery(sql, new String[]{String.valueOf(1)});

        String dump = DatabaseUtils.dumpCursorToString(cursor);
        Log.d(TAG, dump);

        return null;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_TABLE_CHECK_GUIDE);
            db.execSQL(SQL_CREATE_TABLE_HEALTH_RECORD_LOOKUP);
            db.execSQL(SQL_CREATE_TABLE_HEALTH_RECORD);
            db.execSQL(SQL_CREATE_TABLE_HEALTH_RECORD_DETAILS);
            db.execSQL(SQL_CREATE_TABLE_PROFILE);
            insertInitialData(db);
        }

        private void insertInitialData(SQLiteDatabase db) {
            ContentValues cv = new ContentValues();
            cv.put(COL_TITLE, "การซักประวัติ เพื่อค้นหาความผิดปรกติและประเมินความเสี่ยงด้านสุขภาพ (Taking History)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 18);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การซักประวัติ เพื่อค้นหาความผิดปรกติและประเมินความเสี่ยงด้านสุขภาพ (Taking History)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 18);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจสัญญาณชีพและการตรวจร่างกาย (PE)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 2);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจสัญญาณชีพและการตรวจร่างกาย (PE)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 2);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การประเมินพัฒนาการและสุขภาพจิต (Maturity)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 2);
            cv.put(COL_MAX_AGE, 18);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การประเมินพัฒนาการและสุขภาพจิต (Maturity)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 2);
            cv.put(COL_MAX_AGE, 18);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจความสมบูรณ์ของเม็ดเลือด (CBC)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 2);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจความสมบูรณ์ของเม็ดเลือด (CBC)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 2);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจระดับน้ำตาลในเลือด (FPG)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 2);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจระดับน้ำตาลในเลือด (FPG)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 2);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจไขมันในเลือด (Cholesterol)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 2);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจไขมันในเลือด (Cholesterol)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 2);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจปัสสาวะ (UA)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 2);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจปัสสาวะ (UA)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 2);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจคลื่นไฟฟ้าหัวใจ (EKG)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 2);
            cv.put(COL_MAX_AGE, 18);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจคลื่นไฟฟ้าหัวใจ (EKG)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 2);
            cv.put(COL_MAX_AGE, 18);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจคลื่นไฟฟ้าหัวใจ (EKG)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 30);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจคลื่นไฟฟ้าหัวใจ (EKG)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 30);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การให้วัคซีนป้องกันโรค (Vaccinate)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 2);
            cv.put(COL_MAX_AGE, 18);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การให้วัคซีนป้องกันโรค (Vaccinate)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 2);
            cv.put(COL_MAX_AGE, 18);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การเอกซเรย์ปอด (CXR)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 2);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การเอกซเรย์ปอด (CXR)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 2);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจการทำงานของไต (GFR)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 18);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจการทำงานของไต (GFR)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 18);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจหาโรคเกาต์ (กรดยูริก Uric Acid)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 18);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจหาโรคเกาต์ (กรดยูริก Uric Acid)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 18);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจการทำงานของตับ (LFTs)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 18);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจการทำงานของตับ (LFTs)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 18);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การอัลตราซาวนด์ช่องท้องทั้งหมด (Ultrasound Whole Abdomen)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 18);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การอัลตราซาวนด์ช่องท้องทั้งหมด (Ultrasound Whole Abdomen)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 18);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจคัดกรองทางอาชีวอนามัย (Occupation Vision Test)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 30);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจคัดกรองทางอาชีวอนามัย (Occupation Vision Test)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 30);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจอุจจาระร่วมกับการตรวจหาเลือดในอุจจาระ (Stool Test)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 30);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจอุจจาระร่วมกับการตรวจหาเลือดในอุจจาระ (Stool Test)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 30);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจการทำงานของต่อมไทรอยด์โดยการวัดค่า TSH และ Free T4");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 40);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจการทำงานของต่อมไทรอยด์โดยการวัดค่า TSH และ Free T4");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 40);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจตา (วัดความสามารถในการมองเห็นและความดันลูกตา)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 40);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจตา (วัดความสามารถในการมองเห็นและความดันลูกตา)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 40);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจ CEA เพื่อประเมินความเสี่ยงของมะเร็งกระเพาะอาหาร");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 50);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจ CEA เพื่อประเมินความเสี่ยงของมะเร็งกระเพาะอาหาร");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 50);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจ AFP เพื่อประเมินความเสี่ยงของมะเร็งตับ");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 50);
            cv.put(COL_MAX_AGE, 70);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจ AFP เพื่อประเมินความเสี่ยงของมะเร็งตับ");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 50);
            cv.put(COL_MAX_AGE, 70);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจระดับวิตามินบี 12 (B12)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 61);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจระดับวิตามินบี 12 (B12)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 61);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจระดับวิตามินดี (Vitamin D)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 61);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจระดับวิตามินดี (Vitamin D)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 61);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจระดับแคลเซียม (Calcium)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 61);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจระดับแคลเซียม (Calcium)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 61);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การวัดระดับไมโครอัลบูมินในปัสสาวะ (Microalbuminuria)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 71);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การวัดระดับไมโครอัลบูมินในปัสสาวะ (Microalbuminuria)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 71);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจความหนาแน่นกระดูกช่วงกระดูกสันหลังส่วนเอวและสะโพก (Bone Density L-Spine & Hip)");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 71);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจความหนาแน่นกระดูกช่วงกระดูกสันหลังส่วนเอวและสะโพก (Bone Density L-Spine & Hip)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 71);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจ PSA เพื่อประเมินความเสี่ยงของมะเร็งต่อมลูกหมาก");
            cv.put(COL_SEX, SEX_MALE);
            cv.put(COL_MIN_AGE, 50);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจแปปสเมียร์และการตรวจภายใน (Pap Smear)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 18);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจเต้านมโดยแพทย์/บุคลากรสาธารณสุขที่ได้รับการฝึกอบรม (CBE)");
            cv.put(COL_SEX, SEX_FEMALE);
            cv.put(COL_MIN_AGE, 18);
            cv.put(COL_MAX_AGE, 999);
            db.insert(TABLE_CHECKUP_GUIDE, null, cv);

            /////////////////////////////////////////////////////////////////////////

            cv = new ContentValues();
            cv.put(COL_TITLE, "น้ำหนัก (W)");
            cv.put(COL_NAME, "weight");
            cv.put(COL_UNIT, "กิโลกรัม");
            cv.put(COL_MIN_VALUE, 0);
            cv.put(COL_MAX_VALUE, 0);
            cv.put(COL_CATEGORY, HEALTH_RECORD_CATEGORY_BODY);
            db.insert(TABLE_HEALTH_RECORD_LOOKUP, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "ส่วนสูง (H)");
            cv.put(COL_NAME, "height");
            cv.put(COL_UNIT, "เซนติเมตร");
            cv.put(COL_MIN_VALUE, 0);
            cv.put(COL_MAX_VALUE, 0);
            cv.put(COL_CATEGORY, HEALTH_RECORD_CATEGORY_BODY);
            db.insert(TABLE_HEALTH_RECORD_LOOKUP, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "อุณหภูมิ");
            cv.put(COL_NAME, "temperature");
            cv.put(COL_UNIT, "องศาเซลเซียส");
            cv.put(COL_MIN_VALUE, 37);
            cv.put(COL_MAX_VALUE, 37);
            cv.put(COL_CATEGORY, HEALTH_RECORD_CATEGORY_BODY);
            db.insert(TABLE_HEALTH_RECORD_LOOKUP, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "ความดันโลหิต (BP) ค่าบน");
            cv.put(COL_UNIT, "mm Hg");
            cv.put(COL_MIN_VALUE, 120);
            cv.put(COL_MAX_VALUE, 129);
            cv.put(COL_CATEGORY, HEALTH_RECORD_CATEGORY_HEART_BLOOD);
            db.insert(TABLE_HEALTH_RECORD_LOOKUP, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "ความดันโลหิต (BP) ค่าล่าง");
            cv.put(COL_UNIT, "mm Hg");
            cv.put(COL_MIN_VALUE, 80);
            cv.put(COL_MAX_VALUE, 84);
            cv.put(COL_CATEGORY, HEALTH_RECORD_CATEGORY_HEART_BLOOD);
            db.insert(TABLE_HEALTH_RECORD_LOOKUP, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "อัตราชีพจร (PR)");
            cv.put(COL_NAME, "heart_rate");
            cv.put(COL_UNIT, "ครั้ง/นาที");
            cv.put(COL_MIN_VALUE, 60);
            cv.put(COL_MAX_VALUE, 100);
            cv.put(COL_CATEGORY, HEALTH_RECORD_CATEGORY_HEART_BLOOD);
            db.insert(TABLE_HEALTH_RECORD_LOOKUP, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "Hemoglobin (Hb/HGB)");
            cv.put(COL_MIN_VALUE, 13.5);
            cv.put(COL_MAX_VALUE, 17.5);
            cv.put(COL_CATEGORY, HEALTH_RECORD_CATEGORY_HEART_BLOOD);
            db.insert(TABLE_HEALTH_RECORD_LOOKUP, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "Hematocrit (Hct)");
            cv.put(COL_MIN_VALUE, 40);
            cv.put(COL_MAX_VALUE, 50);
            cv.put(COL_CATEGORY, HEALTH_RECORD_CATEGORY_HEART_BLOOD);
            db.insert(TABLE_HEALTH_RECORD_LOOKUP, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "ระดับน้ำตาลในเลือด (FPG)");
            cv.put(COL_UNIT, "mg/dL");
            cv.put(COL_MIN_VALUE, 82);
            cv.put(COL_MAX_VALUE, 110);
            cv.put(COL_CATEGORY, HEALTH_RECORD_CATEGORY_FAT_GLUCOSE);
            db.insert(TABLE_HEALTH_RECORD_LOOKUP, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "ไขมันในเลือด (Cholesterol)");
            cv.put(COL_UNIT, "mg/dL");
            cv.put(COL_MIN_VALUE, 150);
            cv.put(COL_MAX_VALUE, 200);
            cv.put(COL_CATEGORY, HEALTH_RECORD_CATEGORY_FAT_GLUCOSE);
            db.insert(TABLE_HEALTH_RECORD_LOOKUP, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "อัตราส่วนโคเลสเตอรอลกับไขมันความหนาแน่นสูง (HDL)");
            cv.put(COL_UNIT, "mg/dL");
            cv.put(COL_MIN_VALUE, 40);
            cv.put(COL_MAX_VALUE, 999);
            cv.put(COL_CATEGORY, HEALTH_RECORD_CATEGORY_FAT_GLUCOSE);
            db.insert(TABLE_HEALTH_RECORD_LOOKUP, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "ระดับไขมันความหนาแน่นต่ำ (LDL)");
            cv.put(COL_UNIT, "mg/dL");
            cv.put(COL_MIN_VALUE, 0);
            cv.put(COL_MAX_VALUE, 150);
            cv.put(COL_CATEGORY, HEALTH_RECORD_CATEGORY_FAT_GLUCOSE);
            db.insert(TABLE_HEALTH_RECORD_LOOKUP, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "การตรวจหาโรคเกาต์ (กรดยูริก Uric Acid )");
            cv.put(COL_MIN_VALUE, 3);
            cv.put(COL_MAX_VALUE, 8);
            cv.put(COL_CATEGORY, HEALTH_RECORD_CATEGORY_SYSTEM);
            db.insert(TABLE_HEALTH_RECORD_LOOKUP, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "ระดับเอนไซม์จากตับ SGOT (AST)");
            cv.put(COL_MIN_VALUE, 0);
            cv.put(COL_MAX_VALUE, 40);
            cv.put(COL_CATEGORY, HEALTH_RECORD_CATEGORY_SYSTEM);
            db.insert(TABLE_HEALTH_RECORD_LOOKUP, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "ระดับการทำงานของต่อมไทรอยด์");
            cv.put(COL_MIN_VALUE, 0.5);
            cv.put(COL_MAX_VALUE, 5);
            cv.put(COL_CATEGORY, HEALTH_RECORD_CATEGORY_SYSTEM);
            db.insert(TABLE_HEALTH_RECORD_LOOKUP, null, cv);

            cv = new ContentValues();
            cv.put(COL_TITLE, "ไตรกลีเซอไรด์ (Triglycerides)");
            cv.put(COL_UNIT, "mg/dL");
            cv.put(COL_MIN_VALUE, 10);
            cv.put(COL_MAX_VALUE, 190);
            cv.put(COL_CATEGORY, HEALTH_RECORD_CATEGORY_SYSTEM);
            db.insert(TABLE_HEALTH_RECORD_LOOKUP, null, cv);

            /////////////////////////////////////////////////////////////////////////

            insertTestHealthRecordData(db);
        }

        private void insertTestHealthRecordData(SQLiteDatabase db) {
            ContentValues cv = new ContentValues();
            cv.put(COL_DATE, "2017-06-01");
            cv.put(COL_PLACE, "รพ.รามาธิบดี");
            cv.put(COL_DOCTOR, "นพ.รามา ธิบดี");
            db.insert(TABLE_HEALTH_RECORD, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 1);
            cv.put(COL_LOOKUP_ID, 1);
            cv.put(COL_VALUE, 48);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 1);
            cv.put(COL_LOOKUP_ID, 2);
            cv.put(COL_VALUE, 158);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 1);
            cv.put(COL_LOOKUP_ID, 3);
            cv.put(COL_VALUE, 36);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 1);
            cv.put(COL_LOOKUP_ID, 4);
            cv.put(COL_VALUE, 143);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 1);
            cv.put(COL_LOOKUP_ID, 5);
            cv.put(COL_VALUE, 95);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 1);
            cv.put(COL_LOOKUP_ID, 6);
            cv.put(COL_VALUE, 78);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 1);
            cv.put(COL_LOOKUP_ID, 7);
            cv.put(COL_VALUE, 13.2);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 1);
            cv.put(COL_LOOKUP_ID, 8);
            cv.put(COL_VALUE, 46);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 1);
            cv.put(COL_LOOKUP_ID, 9);
            cv.put(COL_VALUE, 123);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 1);
            cv.put(COL_LOOKUP_ID, 10);
            cv.put(COL_VALUE, 220);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 1);
            cv.put(COL_LOOKUP_ID, 11);
            cv.put(COL_VALUE, 948);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 1);
            cv.put(COL_LOOKUP_ID, 12);
            cv.put(COL_VALUE, 131);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 1);
            cv.put(COL_LOOKUP_ID, 13);
            cv.put(COL_VALUE, 5);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 1);
            cv.put(COL_LOOKUP_ID, 14);
            cv.put(COL_VALUE, 33);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 1);
            cv.put(COL_LOOKUP_ID, 15);
            cv.put(COL_VALUE, 4);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 1);
            cv.put(COL_LOOKUP_ID, 16);
            cv.put(COL_VALUE, 198);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_DATE, "2018-06-01");
            cv.put(COL_PLACE, "รพ.รามาธิบดี");
            cv.put(COL_DOCTOR, "นพ.รามา ธิบดี");
            db.insert(TABLE_HEALTH_RECORD, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 2);
            cv.put(COL_LOOKUP_ID, 1);
            cv.put(COL_VALUE, 50);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 2);
            cv.put(COL_LOOKUP_ID, 2);
            cv.put(COL_VALUE, 160);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 2);
            cv.put(COL_LOOKUP_ID, 3);
            cv.put(COL_VALUE, 38);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 2);
            cv.put(COL_LOOKUP_ID, 4);
            cv.put(COL_VALUE, 145);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 2);
            cv.put(COL_LOOKUP_ID, 5);
            cv.put(COL_VALUE, 97);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 2);
            cv.put(COL_LOOKUP_ID, 6);
            cv.put(COL_VALUE, 80);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 2);
            cv.put(COL_LOOKUP_ID, 7);
            cv.put(COL_VALUE, 15.2);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 2);
            cv.put(COL_LOOKUP_ID, 8);
            cv.put(COL_VALUE, 48);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 2);
            cv.put(COL_LOOKUP_ID, 9);
            cv.put(COL_VALUE, 125);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 2);
            cv.put(COL_LOOKUP_ID, 10);
            cv.put(COL_VALUE, 222);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 2);
            cv.put(COL_LOOKUP_ID, 11);
            cv.put(COL_VALUE, 950);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 2);
            cv.put(COL_LOOKUP_ID, 12);
            cv.put(COL_VALUE, 133);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 2);
            cv.put(COL_LOOKUP_ID, 13);
            cv.put(COL_VALUE, 7);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 2);
            cv.put(COL_LOOKUP_ID, 14);
            cv.put(COL_VALUE, 35);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 2);
            cv.put(COL_LOOKUP_ID, 15);
            cv.put(COL_VALUE, 6);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);

            cv = new ContentValues();
            cv.put(COL_RECORD_ID, 2);
            cv.put(COL_LOOKUP_ID, 16);
            cv.put(COL_VALUE, 200);
            db.insert(TABLE_HEALTH_RECORD_DETAILS, null, cv);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKUP_GUIDE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEALTH_RECORD_LOOKUP);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEALTH_RECORD);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEALTH_RECORD_DETAILS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
            onCreate(db);
        }
    }
}

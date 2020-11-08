package com.example.avjindersinghsekhon.minimaltodo.DataStore;


/*
    this class
    1. holds data items to be stored into file
    2. returns data items as a single unit using ContentValue
*/


import android.content.ContentValues;

public class DataModel {

    // data items to be stored
    String title;
    String description;

    String date;    // can be java.util.Date and java.util.time
    String time;
    boolean reminder;

    // keys
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_DATE = "date";
    public static final String KEY_TIME = "time";
    public static final String KEY_REMINDER = "reminder";

    public DataModel() {
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isReminder() {
        return reminder;
    }

    public void setReminder(boolean reminder) {
        this.reminder = reminder;
    }


    // set the data items by taking values from UI
    public String storeData(String title, String description,
                            String date, String time,
                            boolean reminder) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.reminder = reminder;

        Transact t = new Transact();
        String fileStoreStatus = t.storeInFile(getData());

        return fileStoreStatus;

    }

    // return all data items as a single unit
    public ContentValues getData() {

        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_TITLE, title);
        contentValues.put(KEY_DESCRIPTION, description);
        contentValues.put(KEY_DATE, date);
        contentValues.put(KEY_TIME, time);
        contentValues.put(KEY_REMINDER, reminder);

        return contentValues;
    }

}

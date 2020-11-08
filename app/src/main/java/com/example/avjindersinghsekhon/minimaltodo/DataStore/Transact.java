package com.example.avjindersinghsekhon.minimaltodo.DataStore;


// this class will store and retrieve data from file

import android.annotation.TargetApi;
import android.app.admin.DeviceAdminInfo;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

public class Transact {

    public static Context context;
    public static Uri uri;
    DataModel dm;

    // stores tasks read from file in form of list of strings
    public ArrayList<DataModel> tasks = new ArrayList<>();

    // delim : to keep task items separate
    final String delim = " -##- ";
    final String newline = "\n";
    String customData;

    // default constructor
    public Transact() { }

    public void setURIandContext(Uri location, Context appContext) {
        this.uri = location;
        this.context = appContext;
    }


    // store the data into file
    public String storeInFile(ContentValues cv) {

        // default task storing status
        String status = "Cannot store Task";


        /*
            this code can be improved by actually appending the new data to the
            old existing data
        */
        String oldData = readContent(uri) ;

        String newData = cv.get(DataModel.KEY_TITLE) + delim +
                cv.get(DataModel.KEY_DESCRIPTION) + delim +
                cv.get(DataModel.KEY_DATE) + delim +
                cv.get(DataModel.KEY_TIME) + delim +
                cv.get(DataModel.KEY_REMINDER).toString();

        if(oldData != null) {
            customData = oldData  + newData;
        }
        else {
            customData = newData;
        }

        try {
            ParcelFileDescriptor pfd = context.getContentResolver().
                    openFileDescriptor(uri, "w");

            FileOutputStream fileOutputStream =
                    new FileOutputStream(pfd.getFileDescriptor());

            fileOutputStream.write(customData .getBytes());

            // Let the document provider know you're done by closing the stream.
            fileOutputStream.close();
            pfd.close();

            // when task is successfully stored, status is updated
            status = "Task Stored";
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }


    public ArrayList<DataModel> loadFromFile() {
        readContent(uri);

        return tasks;
        // now we have list of tasks as strings
        // separate the tasks using delim
    }


    // read contents of this file in external public storage
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String readContent(Uri uri)  {

        dm = new DataModel();
        StringBuilder stringBuilder = new StringBuilder();

        InputStream inputStream = null;

        try {
            inputStream = context.getContentResolver().openInputStream(uri);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader( Objects.requireNonNull(inputStream) ) ) ;

            String line = "";

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");

                String words[] = line.split(delim);
                dm.setTitle(words[0]);
                dm.setDescription(words[1]);
                dm.setDate(words[2]);
                dm.setTime(words[3]);
                dm.setReminder(words[4].equals("true") ? true : false);

                tasks.add(dm);

            }
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }

        return stringBuilder.toString();
    }

}

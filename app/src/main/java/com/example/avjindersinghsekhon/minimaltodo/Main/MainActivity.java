package com.example.avjindersinghsekhon.minimaltodo.Main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.avjindersinghsekhon.minimaltodo.About.AboutActivity;
import com.example.avjindersinghsekhon.minimaltodo.AppDefault.AppDefaultActivity;
import com.example.avjindersinghsekhon.minimaltodo.DataStore.Transact;
import com.example.avjindersinghsekhon.minimaltodo.R;
import com.example.avjindersinghsekhon.minimaltodo.Settings.SettingsActivity;

import java.io.File;
import java.security.spec.ECField;
import java.util.ArrayList;

public class MainActivity extends AppDefaultActivity {

//start---------------------------------------------------------------------------------------------

    // external file utility values
        // name of file which stores status of actual file
    private static final String FILE_STATUS = "filestatus";

        // actual file which will store to do tasks
    private static final String FILE_NAME = "LIST.txt";

    private static final int CREATE_FILE = 1001;
    private static final String TAG = "mytag";

    // shows the status of actual file : whether it exists or not
    private static String KEY_EXIST_STATUS = "status";
    private static String KEY_URI = "uri";

    private Uri uri = null;
    private final String DEFAULT_URI = "NF";
    private boolean status;

//end-----------------------------------------------------------------------------------------------


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }


//start---------------------------------------------------------------------------------------------

        // on app start, query the preference to check if file exists
        // if yes, do not ask the user to create one but process as usual

        if(!fileExists()) {
//            Log.d(TAG, "onCreate: file does NOT exists");
            createFile();
        }
        else {
            // file already exists, fetch the uri of file from pref and send it to transact
//            Log.d(TAG, "onCreate: file exists");
            SharedPreferences sharedPreferences = getSharedPreferences(FILE_STATUS, MODE_PRIVATE);
            String uriString = sharedPreferences.getString(KEY_URI, DEFAULT_URI);
            uri = Uri.parse(uriString);
            setURI(uri);
        }
    }

    // check whether the file exists
    public boolean fileExists() {

        /*
        * status | doesexits => meaning
        * true      true     => app is installed, file present
        * true      false    => app is installed, file manually removed
        * false     true     => app reinstalled, file present
        * false     false    => app and file both removed from phone
        * */

        // check existence of file via pref : apps internal record of file
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_STATUS, MODE_PRIVATE);
//        status = sharedPreferences.getBoolean(KEY_EXIST_STATUS, false);


        // get existence status of file from uri
        boolean doesExist = false;

        String uriString = sharedPreferences.getString(KEY_URI, DEFAULT_URI);
//        Log.d(TAG, "uri string: " + uriString);

        // if uri = defaulturi, file does not exist
        if( ! uriString.equals(DEFAULT_URI))  {

            uri = Uri.parse(uriString);
//            Log.d(TAG, "not default uri, uri: " + uri);

            Cursor cursor = getContentResolver().
                    query(uri, null, null, null, null);

            Log.d(TAG, "after cursor");
            try {
                // if true, file at uri is present
                // doesExist = (cursor != null && cursor.moveToFirst());

                String displayName = "default";

                if (cursor != null && cursor.moveToFirst()) {
//                    Log.d(TAG, "inside try " + cursor.toString());

                    displayName = cursor.getString(
                            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
//                Log.d(TAG, "FILE Name: " + displayName);

                if(displayName.equals(FILE_NAME)) {
//                    Log.d(TAG, "display name equals : " + displayName);
                    doesExist = true;
                }
            }
            catch (Exception e) {
//                Log.d(TAG, "some excep");
                e.printStackTrace();
            }
            finally {
                cursor.close();
            }
        }

        return doesExist;
    }

    /*
    this methods actually creates a file in external storage
    File Name : FILE_NAME
    */
    private void createFile() {

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, FILE_NAME);

        startActivityForResult(intent, CREATE_FILE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_FILE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Toast.makeText(this, "File Created", Toast.LENGTH_SHORT).show();
                    updateFileExistStatus(data.getData());
                    setURI(data.getData());

                    break;

                case Activity.RESULT_CANCELED:
                    Toast.makeText(this, "File not created", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


    // update the exist status and add status, uri to preference
    private void updateFileExistStatus(Uri uri) {

        status = true;
        SharedPreferences.Editor editor = getSharedPreferences(FILE_STATUS, MODE_PRIVATE).edit();
        editor.putBoolean(KEY_EXIST_STATUS, status);
        editor.putString(KEY_URI, uri.toString());
        editor.apply();
    }


    private void setURI(Uri uri) {
//        Transact t = new Transact();
//        t.setURIandContext(uri, this);
        Transact.uri = uri;
        Transact.context = this;
//        ArrayList<String> datalist = t.loadFromFile();
    }

//end-----------------------------------------------------------------------------------------------

    @Override
    protected int contentViewLayoutRes() {
        return R.layout.activity_main;
    }

    @NonNull
    @Override
    protected Fragment createInitialFragment() {
        return MainFragment.newInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutMeMenuItem:
                Intent i = new Intent(this, AboutActivity.class);
                startActivity(i);
                return true;
//            case R.id.switch_themes:
//                if(mTheme == R.style.CustomStyle_DarkTheme){
//                    addThemeToSharedPreferences(LIGHTTHEME);
//                }
//                else{
//                    addThemeToSharedPreferences(DARKTHEME);
//                }
//
////                if(mTheme == R.style.CustomStyle_DarkTheme){
////                    mTheme = R.style.CustomStyle_LightTheme;
////                }
////                else{
////                    mTheme = R.style.CustomStyle_DarkTheme;
////                }
//                this.recreate();
//                return true;
            case R.id.preferences:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



}




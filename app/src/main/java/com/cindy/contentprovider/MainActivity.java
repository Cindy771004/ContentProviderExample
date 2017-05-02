package com.cindy.contentprovider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ContentResolver mContentResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContentResolver=getContentResolver();

        //add to DB
        Uri uri = UserInfoTable.CONTENT_URI;
        ContentValues values = new ContentValues();
        values.put(UserInfoTable.COLUMN_NAME, "Cindy");
        values.put(UserInfoTable.COLUMN_AGE, "7");
        mContentResolver.insert(uri, values);

        //query personName from DB
        String personName = "";
        int personAge=-1;
        Cursor cursor = null;
        try {
//            Uri uri = UserInfoTable.CONTENT_URI;
            String[] projection = UserInfoTable.PROJECTION;
            String selection = UserInfoTable.COLUMN_NAME + "=?";
            String[] selectionArgs = {"Cindy"};
            cursor = mContentResolver.query(uri, projection, selection, selectionArgs, null);

            if (cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    Log.d(TAG, "cursor: "+cursor);
                    int indexName = cursor.getColumnIndexOrThrow(UserInfoTable.COLUMN_NAME);
                    personName = cursor.getString(indexName);
                    Log.d(TAG, "personName: "+personName);

                    int indexAge = cursor.getColumnIndex(UserInfoTable.COLUMN_AGE);
                    personAge = cursor.getInt(indexAge);
                    Log.d(TAG, "personAge: "+personAge);

                }
            }
        } catch (Exception e) {
            Log.e(TAG, "DB query fail:  "+e.getMessage() );
        } finally {
            cursor.close();
        }
    }
}

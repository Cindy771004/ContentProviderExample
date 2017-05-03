package com.cindy.contentprovider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ContentResolver mContentResolver;
    private TextView mLogInfoTextView;
    private ScrollView mScrollLogs;
    private Button mAddBtn;
    private Button mQueryBtn;
    private Button mUpdateBtn;
    private Button mDeleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContentResolver=getContentResolver();
        mLogInfoTextView=(TextView)findViewById(R.id.logInfoTextView);
        mScrollLogs=(ScrollView) findViewById(R.id.scrollLogs);

        mAddBtn= (Button) findViewById(R.id.addBtn);
        mAddBtn.setOnClickListener(addBtnClick);

        mQueryBtn= (Button) findViewById(R.id.queryBtn);
        mQueryBtn.setOnClickListener(queryBtnClick);

        mUpdateBtn= (Button) findViewById(R.id.updateBtn);
        mUpdateBtn.setOnClickListener(updateBtnClick);

        mDeleteBtn= (Button) findViewById(R.id.deleteBtn);
        mDeleteBtn.setOnClickListener(deleteBtnClick);

    }

    private View.OnClickListener addBtnClick= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            add();
        }
    };

    private View.OnClickListener queryBtnClick= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            query();
        }
    };

    private View.OnClickListener updateBtnClick= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            update();
        }
    };

    private View.OnClickListener deleteBtnClick= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            delete();
        }
    };

    private void add(){
        //add to DB
        Uri uri = UserInfoTable.CONTENT_URI;
        ContentValues values = new ContentValues();
        String personName="Cindy";
        int personAge=1;
        values.put(UserInfoTable.COLUMN_NAME, personName);
        values.put(UserInfoTable.COLUMN_AGE, personAge);
        mContentResolver.insert(uri, values);
        addStringToTextView("Add: (personName,personAge)=("+personName+","+personAge+")");
    }

    private void query(){
        //query personName from DB
        Uri uri = UserInfoTable.CONTENT_URI;
        String personName = "";
        int personAge=-1;
        Cursor cursor = null;
        try {
            //Uri uri = UserInfoTable.CONTENT_URI;
            String[] projection = UserInfoTable.PROJECTION;
            String selection = UserInfoTable.COLUMN_NAME + "=?";
            String[] selectionArgs = {"Cindy"};
            cursor = mContentResolver.query(uri, projection, selection, selectionArgs, null);

            if (cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    int indexName = cursor.getColumnIndexOrThrow(UserInfoTable.COLUMN_NAME);
                    personName = cursor.getString(indexName);
                    Log.d(TAG, "personName: "+personName);

                    int indexAge = cursor.getColumnIndex(UserInfoTable.COLUMN_AGE);
                    personAge = cursor.getInt(indexAge);
                    Log.d(TAG, "personAge: "+personAge);
                    addStringToTextView("Query: (personName,personAge)=("+personName+","+personAge+")");

                }
            }
        } catch (Exception e) {
            Log.e(TAG, "DB query fail:  "+e.getMessage() );
        } finally {
            cursor.close();
        }
    }

    private void delete(){
        //delete from DB where username is Cindy
        Uri uri = UserInfoTable.CONTENT_URI;
        String selection =  UserInfoTable.COLUMN_NAME + "=?";
        String[] selectionArgs = {"Cindy"};
        mContentResolver.delete(uri, selection, selectionArgs);
        addStringToTextView("Delete: personName=Cindy");
    }

    private void update(){
        //update to DB where username is Cindy
        Uri uri = UserInfoTable.CONTENT_URI;
        ContentValues values = new ContentValues();
        String personName="Cindy";
        int personAge=18;
        values.put(UserInfoTable.COLUMN_NAME,personName );
        values.put(UserInfoTable.COLUMN_AGE, personAge);
        String selection =  UserInfoTable.COLUMN_NAME + "=?";
        String[] selectionArgs = {"Cindy"};
        mContentResolver.update(uri,values,selection,selectionArgs);
        addStringToTextView("Update: (personName,personAge)=("+personName+","+personAge+")");
    }

    private void addStringToTextView(String text){
        String currentText = mLogInfoTextView.getText().toString();
        currentText += "\n" + text;
        mLogInfoTextView.setText(currentText);
        scrollToBottom();
    }

    private void scrollToBottom() {
        // 如果只想單純更新一個view 可透過View.post
        // View.post跟runOnUiThread很相近，只是變成元件自己呼叫post更新自己
        // 其原理是把runnable回調函數，post到主線程的Message Queue裡去，
        // 並將其包裝成一個android有效的消息對象，
        // 供位於主線程中的handler來處理，從而實現在主線程中更新這一視圖 。
        mScrollLogs.post(new Runnable() {
            public void run() {
                mScrollLogs.smoothScrollTo(0, mLogInfoTextView.getBottom());
            }
        });
    }
}

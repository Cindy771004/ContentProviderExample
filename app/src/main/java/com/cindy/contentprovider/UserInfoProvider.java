package com.cindy.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Cindyyh_Chou on 2017/4/27.
 */

public class UserInfoProvider extends ContentProvider {

    //android可透過ContentProvider共享與其他app資料，
    //其他app可通過ContentProvider對本app的資料進行新增、刪除、修改、查詢。

    private static final String TAG = "UserInfoProvider";

    public static final String AUTHORITY = "com.cindy.contentprovider.provider";

    //我們透過 UserInfoProvider 來調用 DatabaseHelper,
    //這樣之後在主程式裡使用的時候, 就只需要管理 UserInfoProvider 這個接口就好.
    private DatabaseHelper mHelper = null;

    private SQLiteDatabase mDB;
    private UserInfoTable mUserInfoTable;

    // Set up our URL matchers to help us determine what an
    // incoming URI parameter is.
    private static final UriMatcher mUriMatcher;
    private static final int URI_TYPE_TABLE1 = 1;
    static {
        //UriMatcher.NO_MATCH表示路径不匹配的返回碼
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //添加需要匹配的uri，如果匹配就会返回匹配码(URI_TYPE_TABLE1)
        mUriMatcher.addURI(AUTHORITY, UserInfoTable.TABLE_NAME, URI_TYPE_TABLE1);
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate");

        mHelper= DatabaseHelper.getInstance(getContext());
//        mHelper = new DatabaseHelper(getContext());
        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        try {
            mDB = mHelper.getWritableDatabase();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        if (mDB == null)
            return false;
        mUserInfoTable = new UserInfoTable(mDB);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query");

        Cursor ret = null;
        //mUriMatcher.match(uri):如果uri匹配則返回匹配碼
        switch (mUriMatcher.match(uri)) {
            case URI_TYPE_TABLE1:
                ret = mUserInfoTable.query(projection, selection, selectionArgs, sortOrder);
                break;
            default://uri不匹配
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return ret;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        Log.d(TAG, "getType");
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Log.d(TAG, "insert");
        long rowId = -1;
        Uri contentUri = null;
        //mUriMatcher.match(uri):如果uri匹配則返回匹配碼
        switch (mUriMatcher.match(uri)) {
            case URI_TYPE_TABLE1:
                rowId = mUserInfoTable.insert(contentValues);

                //content格式://authority/path/id
                //Id:Id號，表示要操作的data中的哪一項
                //通过withAppendedId方法，为该Uri加上ID
                contentUri = ContentUris.withAppendedId(mUserInfoTable.CONTENT_URI, rowId);
                break;
        }
        if (contentUri != null) {
            //我们在ContentProvider的insert,update,delete等改变之后
            //调用getContext().getContentResolver().notifyChange(uri, null);
            //来通知資料監聽者
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return contentUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete");

        int deleted = 0;
        //mUriMatcher.match(uri):如果uri匹配則返回匹配碼
        switch (mUriMatcher.match(uri)) {
            case URI_TYPE_TABLE1:
                deleted = mUserInfoTable.delete(selection, selectionArgs);
                break;
        }
        if (deleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        Log.d(TAG, "update");

        int updated = 0;
        switch (mUriMatcher.match(uri)) {
            case URI_TYPE_TABLE1:
                updated = mUserInfoTable.update(contentValues, selection, selectionArgs);
                break;
        }
        if (updated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updated;
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {
        private static String TAG="DatabaseHelper";

        // 資料庫名稱
        public static final String DATABASE_NAME = "mydata.db";
        // 資料庫版本
        public static final int DATABASE_VERSION = 2;
        // 資料庫物件

        private static DatabaseHelper sInstance;
        private static Context mContex;

        public static synchronized DatabaseHelper getInstance(Context context) {
            Log.d(TAG, "getInstance");

            // Use the application context, which will ensure that you
            // don't accidentally leak an Activity's context.
            // See this article for more information: http://bit.ly/6LRzfx
            if (sInstance == null) {
                sInstance = new DatabaseHelper(context.getApplicationContext());
            }
            return sInstance;
        }

        //建構子應該是private，以防止直接透過建構式實例化而不是用getInstance
        private DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mContex = context;
        }

        //Android載入時找不到生成的資料庫檔案，就會觸發onCreate
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            Log.d(TAG, "onCreate");

            sqLiteDatabase.execSQL(UserInfoTable.TABLE_CREATE);
        }

        //如果資料庫結構有改變了就會觸發onUpgrade
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(TAG, "onUpgrade - oldVersion = " + oldVersion + ", newVersion = " + newVersion);
            if (newVersion > oldVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + UserInfoTable.TABLE_NAME);
                onCreate(db);
            }
        }
    }
}




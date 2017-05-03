package com.cindy.contentprovider;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Cindyyh_Chou on 2017/4/27.
 */

public class UserInfoTable implements BaseColumns {
    private static final String TAG = "UserInfoTable";

    public static final String TABLE_NAME = "user_info_table";

    //CONTENT_URI:用於識別app資料的URI
    //content格式為://authority/path/id
    //Authority:授權，用以區別是否為不同的ContentProvider
    //Path:表名路徑，用來指向表格的名稱
    //Id:Id號，表示要操作的data中的哪一項
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + UserInfoProvider.AUTHORITY + "/" + TABLE_NAME);

    // 編號表格欄位名稱
    public static final String ID = "_id";
    // 其它表格欄位名稱
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AGE = "age";
    //列出所有欄位
    public static final String[] PROJECTION = { ID, COLUMN_NAME, COLUMN_AGE };

    // SQLite 的 Type 只有 TEXT, INTEGER, REAL ( similar to double in java)
    public static final String TABLE_CREATE =
            " CREATE TABLE IF NOT EXISTS " +TABLE_NAME+ "(" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_AGE + " INTEGER ) ";

    private final SQLiteDatabase mDb;

    public UserInfoTable(SQLiteDatabase database){
        mDb=database;
    }

    public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return mDb.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    public long insert(ContentValues values) {
        return mDb.insert(TABLE_NAME, null, values);
    }

    public int delete(String selection, String[] selectionArgs) {
        return mDb.delete(TABLE_NAME, selection, selectionArgs);
    }

    public int update(ContentValues values, String selection, String[] selectionArgs) {
        return mDb.update(TABLE_NAME, values, selection, selectionArgs);
    }
}

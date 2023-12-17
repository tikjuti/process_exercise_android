package com.tikjuti.bai2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sqlite.db";
    private static final int DATABASE_VERSION = 6;

    public static final String TABLE_NOTES = "Notes";
    public static final String COLUMN_ID = "Id";
    public static final String COLUMN_TITLE = "Title";
    public static final String COLUMN_CONTENT = "Content";
    public static final String COLUMN_IMAGE_PATH = "ImagePath";
    public static final String COLUMN_DATE_TIME = "DateTime";
    public static final String COLUMN_REMINDER = "Reminder";
    public static final Long DEFAUlT_TIME = 0L;
    private static final String TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NOTES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " VARCHAR(255), " +
                    COLUMN_CONTENT + " TEXT, " +
                    COLUMN_IMAGE_PATH + " BLOB, " +
                    COLUMN_DATE_TIME + " DATETIME, " +
                    COLUMN_REMINDER + " LONG DEFAULT 0" +
                    ");";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    public void insertData(String title,String content,byte[] image,String dateTime) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO Notes VALUES(null, ?, ?, ?, ?, ?)";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, title);
        statement.bindString(2, content);
        statement.bindBlob(3, image);
        statement.bindString(4, dateTime);
        statement.bindLong(5, DEFAUlT_TIME);

        statement.executeInsert();
    }

    public void updateData(int id, String title, String content, byte[] image, String dateTime) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "UPDATE Notes SET Title = ?, Content = ?, ImagePath = ?, DateTime = ? WHERE Id = ?";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, title);
        statement.bindString(2, content);
        statement.bindBlob(3, image);
        statement.bindString(4, dateTime);
        statement.bindLong(5, id);

        statement.executeUpdateDelete();
        db.close();
    }

}

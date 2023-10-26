package com.example.aplicatierunn;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "RunData.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_RUNS = "runs";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_TIME = "time";

    private static final String DATABASE_CREATE =
            "create table " + TABLE_RUNS + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_LATITUDE + " real not null, " +
                    COLUMN_LONGITUDE + " real not null, " +
                    COLUMN_TIME + " integer not null);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUNS);
        onCreate(db);
    }
}

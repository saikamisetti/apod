package com.knolskape.app.apod.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;
import com.knolskape.app.apod.models.DailyPicture;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by knolly on 30/11/16.
 */

public class DbHelper extends SQLiteOpenHelper{
  private String TAG = "DB_HELPER";
  private static final String TEXT_TYPE = " TEXT";
  private static final String COMMA_SEP = ",";
  private static final String SQL_CREATE_ENTRIES =
       "CREATE TABLE " + ApodContract.FeedEntry.TABLE_NAME + " (" +
        ApodContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
        ApodContract.FeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
           ApodContract.FeedEntry.COLUMN_NAME_IMAGE + TEXT_TYPE + COMMA_SEP +
           ApodContract.FeedEntry.COLUMN_NAME_DESC + TEXT_TYPE + COMMA_SEP +
           ApodContract.FeedEntry.COLUMN_NAME_DATE + TEXT_TYPE + " )";

  private static final String SQL_DELETE_ENTRIES =
    "DROP TABLE IF EXISTS " + ApodContract.FeedEntry.TABLE_NAME;

  // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

  @Override public void onCreate(SQLiteDatabase sqLiteDatabase) {
    Log.d(TAG, "creating the table");
    sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
  }

  @Override public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
    onCreate(sqLiteDatabase);
  }

  public DbHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  public void insertToDb(DailyPicture dailyPicture) {
    Log.d(TAG, "inserting record to db " + dailyPicture);
    SQLiteDatabase db = this.getWritableDatabase();

    // Create a new map of values, where column names are the keys
    ContentValues values = new ContentValues();
    values.put(ApodContract.FeedEntry.COLUMN_NAME_TITLE, dailyPicture.title());
    values.put(ApodContract.FeedEntry.COLUMN_NAME_DESC, dailyPicture.explanation());
    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    String currentDate = df.format(c.getTime());
    values.put(ApodContract.FeedEntry.COLUMN_NAME_DATE, currentDate);
    values.put(ApodContract.FeedEntry.COLUMN_NAME_IMAGE, dailyPicture.url());

    // Insert the new row, returning the primary key value of the new row
    db.insert(ApodContract.FeedEntry.TABLE_NAME, null, values);
  }

  public DailyPicture getApodForDate(String currentDate) {
    Log.d(TAG, "fetching record from db for date" + currentDate);
    SQLiteDatabase database = this.getReadableDatabase();
    Cursor cursor = database.query(ApodContract.FeedEntry.TABLE_NAME, new String[]{ApodContract.FeedEntry.COLUMN_NAME_DATE,
        ApodContract.FeedEntry.COLUMN_NAME_TITLE, ApodContract.FeedEntry.COLUMN_NAME_IMAGE,
        ApodContract.FeedEntry.COLUMN_NAME_DESC}, ApodContract.FeedEntry.COLUMN_NAME_DATE + "= ?", new String[] {currentDate}, null, null, null);
    cursor.moveToFirst();
    if(cursor.getCount() > 0) {
      DailyPicture dailyPicture = new DailyPicture() {
        @Nullable @Override public String copyright() {
          return null;
        }

        @Nullable @Override public String date() {
          return cursor.getString(cursor.getColumnIndex(ApodContract.FeedEntry.COLUMN_NAME_DATE));
        }

        @Nullable @Override public String explanation() {
          return cursor.getString(cursor.getColumnIndex(ApodContract.FeedEntry.COLUMN_NAME_DESC));
        }

        @Nullable @Override public String hdurl() {
          return null;
        }

        @Nullable @Override public String mediaType() {
          return null;
        }

        @Nullable @Override public String serviceVersion() {
          return null;
        }

        @Nullable @Override public String title() {
          return cursor.getString(cursor.getColumnIndex(ApodContract.FeedEntry.COLUMN_NAME_TITLE));
        }

        @Nullable @Override public String url() {
          return cursor.getString(cursor.getColumnIndex(ApodContract.FeedEntry.COLUMN_NAME_IMAGE));
        }
      };
          return dailyPicture;
    }
    else
      return null;
  }
}

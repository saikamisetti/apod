package com.knolskape.app.apod.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import com.knolskape.app.apod.models.DailyPicture;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import timber.log.Timber;

/**
 * Created by knolly on 30/11/16.
 */

public class DbHelper extends SQLiteOpenHelper {
  // If you change the database schema, you must increment the database version.
  public static final int DATABASE_VERSION = 3;
  public static final String DATABASE_NAME = "FeedReader.db";
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

  public DbHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override public void onCreate(SQLiteDatabase sqLiteDatabase) {
    Timber.d("creating the table");
    sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
  }

  @Override public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
    onCreate(sqLiteDatabase);
  }

  public void insertToDb(DailyPicture dailyPicture) {
    Timber.d("inserting record to db " + dailyPicture);
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

  public List<DailyPicture> fetchAllPics() {
    Timber.d("fetching all pics saved in db so far");
    SQLiteDatabase db = this.getReadableDatabase();
    final Cursor cursor = db.query(ApodContract.FeedEntry.TABLE_NAME, new String[] {
            ApodContract.FeedEntry.COLUMN_NAME_DATE, ApodContract.FeedEntry.COLUMN_NAME_TITLE,
            ApodContract.FeedEntry.COLUMN_NAME_IMAGE, ApodContract.FeedEntry.COLUMN_NAME_DESC
        }, null, null, null, null,
        ApodContract.FeedEntry.COLUMN_NAME_DATE + " DESC");
    cursor.moveToFirst();
    List<DailyPicture> dailyPictures = new ArrayList<>();
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
      dailyPictures.add(dailyPicture);
    }
    return dailyPictures;
  }

  public DailyPicture getLatestApodForDate(String currentDate) {
    Timber.d("fetching record from db for date" + currentDate);
    SQLiteDatabase database = this.getReadableDatabase();
    final Cursor cursor = database.query(ApodContract.FeedEntry.TABLE_NAME, new String[] {
            ApodContract.FeedEntry.COLUMN_NAME_DATE, ApodContract.FeedEntry.COLUMN_NAME_TITLE,
            ApodContract.FeedEntry.COLUMN_NAME_IMAGE, ApodContract.FeedEntry.COLUMN_NAME_DESC
        }, ApodContract.FeedEntry.COLUMN_NAME_DATE + "= ?", new String[] { currentDate }, null, null,
        ApodContract.FeedEntry.COLUMN_NAME_DATE + " DESC");
    cursor.moveToFirst();
    if (cursor.getCount() > 0) {
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
    } else {
      return null;
    }
  }
}

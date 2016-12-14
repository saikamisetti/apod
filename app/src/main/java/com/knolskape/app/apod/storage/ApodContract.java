package com.knolskape.app.apod.storage;

import android.provider.BaseColumns;

/**
 * Created by knolly on 30/11/16.
 */

public class ApodContract {
  // To prevent someone from accidentally instantiating the contract class,
  // make the constructor private.
  private ApodContract() {
  }

  public static class FeedEntry implements BaseColumns {
    public static final String TABLE_NAME = "apod";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_IMAGE = "image";
    public static final String COLUMN_NAME_IMAGE_HD = "hd_image";
    public static final String COLUMN_NAME_DESC = "desc";
    public static final String COLUMN_NAME_DATE = "date";
  }
}

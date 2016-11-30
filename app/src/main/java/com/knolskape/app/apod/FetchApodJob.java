package com.knolskape.app.apod;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

/**
 * Created by knolly on 30/11/16.
 */

public class FetchApodJob extends Job {
  public static final int PRIORITY = 1;

  protected FetchApodJob(Params params) {
    super(new Params(PRIORITY).requireNetwork().persist());
  }

  @Override public void onAdded() {

  }

  @Override public void onRun() throws Throwable {

  }

  @Override protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

  }

  @Override
  protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount,
      int maxRunCount) {
    return null;
  }
}

package com.knolskape.app.apod;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import com.knolskape.app.apod.connectivity.ConnectivityChangeReceiver;
import com.knolskape.app.apod.controllers.ApiInterface;
import com.knolskape.app.apod.controllers.NetworkController;
import com.knolskape.app.apod.models.DailyPicture;
import com.knolskape.app.apod.storage.DbHelper;
import com.knolskape.app.apod.utils.Utils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity
    implements ConnectivityChangeReceiver.ConnectivityReceiverListener {
  RecyclerView rv_images;
  RecyclerView.LayoutManager layoutManager;

  ImagesAdapter adapter;
  Subscription subscribe;
  private DbHelper dbHelper;
  private String currentDate;
  private List<DailyPicture> dailyPictures = new ArrayList<>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initializeViews();
    adapter = new ImagesAdapter(MainActivity.this, dailyPictures);
    rv_images.setAdapter(adapter);
    dbHelper = new DbHelper(MainActivity.this);

    populateData();
  }

  private void initializeViews() {
    rv_images = (RecyclerView) findViewById(R.id.rv_images);
    rv_images.setHasFixedSize(true);
    layoutManager = new GridLayoutManager(this, 2);
    rv_images.setLayoutManager(layoutManager);
  }

  private void populateData() {
    Calendar c = Calendar.getInstance();

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    currentDate = df.format(c.getTime());
    DailyPicture dbLatestDailyPicture = dbHelper.getLatestApodForDate(currentDate);
    dailyPictures.clear();
    if (dbLatestDailyPicture == null) {
      Timber.d("empty db; making api call");
      fetchApodFromNetwork();
    } else { //db is up to date
      Timber.d("db is not empty");
      dailyPictures.addAll(dbHelper.fetchAllPics());
      adapter.notifyDataSetChanged();
      if(!dbLatestDailyPicture.date().equals(currentDate)) {
        Timber.d("making extra api call");
        fetchApodFromNetwork();
      }
    }
  }

  public void fetchApodFromNetwork() {
    if (Utils.isUserOffline(MainActivity.this)) {
      Toast.makeText(this, "Please ensure you are online to proceed further", Toast.LENGTH_SHORT)
          .show();
      Timber.d("user is offline");
    } else {
      ApiInterface apiService = NetworkController.getClient().create(ApiInterface.class);
      Observable<DailyPicture> apod = apiService.fetchAPOD();
      subscribe = apod.subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Subscriber<DailyPicture>() {
            @Override public void onCompleted() {
              adapter.notifyDataSetChanged();
            }

            @Override public void onError(Throwable e) {
              Toast.makeText(MainActivity.this,
                  "There has been an error on the server. Please try again later.",
                  Toast.LENGTH_SHORT).show();
              Timber.e(e);
              adapter.notifyDataSetChanged();
            }

            @Override public void onNext(DailyPicture dailyPicture) {
              Timber.d("onNext of Subscriber", dailyPicture.title());
              dbHelper.insertToDb(dailyPicture);
              dailyPictures.add(0, dailyPicture);
            }
          });
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (subscribe != null && !subscribe.isUnsubscribed()) subscribe.unsubscribe();
  }

  @Override public void onNetworkConnectionChanged(boolean isConnected) {
    fetchApodFromNetwork();
  }

  @Override protected void onResume() {
    super.onResume();
    // register connection status listener
    ApodApplication.getInstance().setConnectivityListener(this);
  }
}

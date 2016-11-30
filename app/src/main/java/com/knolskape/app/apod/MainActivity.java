package com.knolskape.app.apod;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.knolskape.app.apod.connectivity.ConnectivityChangeReceiver;
import com.knolskape.app.apod.controllers.ApiInterface;
import com.knolskape.app.apod.controllers.NetworkController;
import com.knolskape.app.apod.models.DailyPicture;
import com.knolskape.app.apod.storage.DbHelper;
import com.knolskape.app.apod.utils.Utils;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity
    implements ConnectivityChangeReceiver.ConnectivityReceiverListener {
  public String TAG = "MAIN_ACTIVITY";
  TextView title;
  TextView desc;
  ImageView image;
  Subscription subscribe;
  BroadcastReceiver connectivityBroadcastReceiver;
  private DbHelper dbHelper;
  private String currentDate;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initializeViews();
    dbHelper = new DbHelper(MainActivity.this);
    populateData();
  }

  private void initializeViews() {
    title = (TextView) findViewById(R.id.title);
    desc = (TextView) findViewById(R.id.desc);
    image = (ImageView) findViewById(R.id.image);
  }

  private void populateData() {
    Calendar c = Calendar.getInstance();

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    currentDate = df.format(c.getTime());
    if (dbHelper.getApodForDate(currentDate) != null) {
      DailyPicture dailyPicture = dbHelper.getApodForDate(currentDate);
      title.setText(dailyPicture.title());
      desc.setText(dailyPicture.explanation());
      Picasso.with(MainActivity.this).load(dailyPicture.url()).into(image);
    } else {
      fetchApodFromNetwork();
    }
  }

  public void fetchApodFromNetwork() {
    if (Utils.isUserOffline(MainActivity.this)) {
      Toast.makeText(this, "Please ensure you are online to proceed further", Toast.LENGTH_SHORT)
          .show();
      Log.d(TAG, "user is offline");
    } else {
      ApiInterface apiService = NetworkController.getClient().create(ApiInterface.class);
      Observable<DailyPicture> apod = apiService.fetchAPOD();
      subscribe = apod.subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Subscriber<DailyPicture>() {
            @Override public void onCompleted() {

            }

            @Override public void onError(Throwable e) {
              Toast.makeText(MainActivity.this,
                  "There has been an error on the server. Please try again later.",
                  Toast.LENGTH_SHORT).show();
              Log.e(TAG, e.toString());
            }

            @Override public void onNext(DailyPicture dailyPicture) {
              Log.d("onNext of Subscriber", dailyPicture.title());
              title.setText(dailyPicture.title());
              desc.setText(dailyPicture.explanation());
              title.setText(dailyPicture.title());
              desc.setText(dailyPicture.explanation());
              Picasso.with(MainActivity.this).load(dailyPicture.url()).into(image);
              dbHelper.insertToDb(dailyPicture);
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

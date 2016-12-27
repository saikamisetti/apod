package com.knolskape.app.apod;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.knolskape.app.apod.connectivity.NetworkUtils;
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
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
  TextView title;
  TextView desc;
  ImageView image;
  Subscription subscribe;
  private DbHelper dbHelper;
  private String currentDate;

  private NetworkUtils.ConnectionStateChangeListener connectionClassStateChangeListener;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initializeViews();

    connectionClassStateChangeListener = new NetworkUtils.ConnectionStateChangeListener() {
      @Override public void onConnectionRevoked() {
        Timber.d("onConnectionRevoked: ");
      }

      @Override public void onConnectionRestored() {
        //if no db data or if db is not updated, make an api call
        if (currentDate == null || !currentDate.equals(
            dbHelper.getApodForDate(currentDate).date())) {
          fetchApodFromNetwork();
        }
      }

      @Override public void onBandwidthStateChange(ConnectionQuality bandwidthState) {
        Timber.d("onBandwidthStateChange: %s", bandwidthState);
      }
    };

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
    if (dbHelper.getApodForDate(currentDate) != null && dbHelper.getApodForDate(currentDate)
        .date()
        .equals(currentDate)) {
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
      Timber.d("user is offline");
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
              Timber.e(e);
            }

            @Override public void onNext(DailyPicture dailyPicture) {
              Timber.d("onNext of Subscriber", dailyPicture.title());
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
    NetworkUtils.unwatchConnection(connectionClassStateChangeListener);
  }

  @Override protected void onResume() {
    super.onResume();
    // register connection status listener
    NetworkUtils.watchConnection(connectionClassStateChangeListener);
  }
}

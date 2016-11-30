package com.knolskape.app.apod;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.knolskape.app.apod.controllers.ApiInterface;
import com.knolskape.app.apod.controllers.NetworkController;
import com.knolskape.app.apod.models.DailyPicture;
import com.knolskape.app.apod.storage.DbHelper;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
     TextView title;
     TextView desc;
     ImageView image;

    public String TAG = "MAIN_ACTIVITY";
    private DbHelper dbHelper;
    private String currentDate;

    Subscription subscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
//    @Nullable
//    private DailyPicture getDailyPicture() throws IOException{
//
//    }

    private void populateData() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = df.format(c.getTime());
        if(dbHelper.getApodForDate(currentDate) != null )
        {
            DailyPicture dailyPicture = dbHelper.getApodForDate(currentDate);
            title.setText(dailyPicture.title());
            desc.setText(dailyPicture.explanation());
            Picasso.with(MainActivity.this).load(dailyPicture.url()).into(image);
        } else {
            ApiInterface apiService = NetworkController.getClient().create(ApiInterface.class);
            Observable<DailyPicture> apod = apiService.fetchAPOD();
            subscribe = apod.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<DailyPicture>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(DailyPicture dailyPicture) {
                            Log.d("onNext of Subscriber", dailyPicture.title());
                            title.setText(dailyPicture.title());
                            desc.setText(dailyPicture.explanation());
                            Picasso.with(MainActivity.this).load(dailyPicture.url()).into(image);
                          dbHelper.insertToDb(dailyPicture);
                        }
                    });
        }
    }

//    public Observable<DailyPicture> getDailyPictureObservable() {
//        return
//        return Observable.just(getDailyPicture());
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
      if(subscribe != null && !subscribe.isUnsubscribed())
        subscribe.unsubscribe();
    }
}

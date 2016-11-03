package com.knolskape.app.apod;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.knolskape.app.apod.controllers.NetworkController;
import com.knolskape.app.apod.models.DailyPicture;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
     TextView title;
     TextView desc;
     ImageView image;

    public String TAG = "MAIN_ACTIVITY";
    private SharedPreferences sharedPreferences;
    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        sharedPreferences = getPreferences(MODE_PRIVATE);
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
        if(sharedPreferences.contains("date") && sharedPreferences.getString("date", "").equals(currentDate) )
        {
            title.setText(sharedPreferences.getString("title", ""));
            desc.setText(sharedPreferences.getString("desc", ""));
            Picasso.with(MainActivity.this).load(sharedPreferences.getString("image","")).into(image);
        } else {
            NetworkController.getInstance().getApod(new Callback<DailyPicture>() {
                @Override
                public void onResponse(Call<DailyPicture> call, Response<DailyPicture> response) {
                    Log.d(TAG, "received response : " + response.body());
                    title.setText(response.body().title());
                    desc.setText(response.body().explanation());
                    Picasso.with(MainActivity.this).load(response.body().url()).into(image);
                    SharedPreferences.Editor editor = sharedPreferences.edit(); //save data to shared prefs
                    editor.putString("date", response.body().date());
                    editor.putString("title", response.body().title());
                    editor.putString("desc", response.body().explanation());
                    editor.putString("image", response.body().url());
                    editor.commit();
                }

                @Override
                public void onFailure(Call<DailyPicture> call, Throwable t) {
                    Log.d(TAG, "api request failed ");
                }
            });
        }
    }
}

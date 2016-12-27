package com.knolskape.app.apod;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by knolly on 2/12/16.
 */

public class ImageDetailActivity extends AppCompatActivity {

  public static String IMAGE_URL = "image_url";
  private ImageView iv_fullscreen;
  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_image_detail);
    iv_fullscreen = (ImageView) findViewById(R.id.iv_fullscreen);
    if(getIntent() != null && getIntent().hasExtra(IMAGE_URL)) Picasso.with(this).load(getIntent().getStringExtra(IMAGE_URL)).into(iv_fullscreen);
  }
}

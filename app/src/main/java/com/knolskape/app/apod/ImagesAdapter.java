package com.knolskape.app.apod;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.knolskape.app.apod.models.DailyPicture;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

/**
 * Created by knolly on 1/12/16.
 */

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

  private List<DailyPicture> imagesList;
  private Context context;
  public ImagesAdapter(Context context, List<DailyPicture> imagesList) {
    this.imagesList = imagesList;
    this.context = context;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    // create a new view
    View v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.image_list_item, parent, false);
    ViewHolder vh = new ViewHolder((ImageView) v);
    return vh;
  }

  @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
    Timber.d("inside bindviewholder + " + imagesList.get(position).title());
    Picasso.with(context).load(imagesList.get(position).url()).into(holder.imageView);
    holder.imageView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Intent intent = new Intent(context, ImageDetailActivity.class);
        // Pass data object in the bundle and populate details activity.
        intent.putExtra(ImageDetailActivity.IMAGE_URL, imagesList.get(position).url());
        ActivityOptionsCompat options = ActivityOptionsCompat.
            makeSceneTransitionAnimation((Activity) context, holder.imageView, "detail");
        context.startActivity(intent, options.toBundle());
      }
    });
  }

  @Override public int getItemCount() {
    return imagesList.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;
    public ViewHolder(ImageView imageView) {
      super(imageView);
      this.imageView = imageView;
    }
  }
}

package com.jwcheung.hw9;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

    private ArrayList<Bitmap> photos;
    private Context context;

    public PhotosAdapter(ArrayList<Bitmap> photos, Context context) {
        this.photos = photos;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public ConstraintLayout constraintLayout;

        public ViewHolder(View view) {
            super(view);

            image = (ImageView) view.findViewById(R.id.photo_result);
            constraintLayout = (ConstraintLayout) view.findViewById(R.id.photo_line_constraint);
        }
    }

    @Override
    public PhotosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_line, parent, false);
        return new PhotosAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PhotosAdapter.ViewHolder holder, int position) {
        Bitmap bitmap = photos.get(position);
        holder.image.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

}

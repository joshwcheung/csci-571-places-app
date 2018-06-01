package com.jwcheung.hw9;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private ArrayList<ReviewItem> reviews;
    private Context context;

    public ReviewsAdapter(ArrayList<ReviewItem> reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public RatingBar rating;
        public TextView time;
        public TextView text;
        public ImageView image;

        public ConstraintLayout constraintLayout;

        public ViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.textView6);
            rating = (RatingBar) view.findViewById(R.id.ratingBar);
            time = (TextView) view.findViewById(R.id.textView7);
            text = (TextView) view.findViewById(R.id.textView8);
            image = (ImageView) view.findViewById(R.id.icon_image);
            constraintLayout = (ConstraintLayout) view.findViewById(R.id.review_line_constraint);
        }
    }

    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_line, parent, false);
        return new ReviewsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ViewHolder holder, int position) {
        ReviewItem review = reviews.get(position);
        holder.name.setText(review.getAuthor());
        holder.rating.setRating(Float.parseFloat(review.getRating()));
        holder.time.setText(review.getTime());
        holder.text.setText(review.getText());
        if(!review.getPhoto().isEmpty()) {
            Picasso.get().load(review.getPhoto()).into(holder.image);
        }
        holder.constraintLayout.setOnClickListener((v) -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getLink()));
            context.startActivity(browserIntent);
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}

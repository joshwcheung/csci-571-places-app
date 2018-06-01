package com.jwcheung.hw9;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    public static final String KEY_ID = "placeId";
    public static final String KEY_NAME = "name";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LNG = "lng";

    private List<PlaceItem> placeItems;
    private Context context;
    private SharedPreference sharedPreference;

    public SearchResultsAdapter(List<PlaceItem> placeItems, Context context) {
        this.placeItems = placeItems;
        this.context = context;
        sharedPreference = new SharedPreference();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView address;
        public ImageView icon;
        public ImageView favorite;
        public ConstraintLayout constraintLayout;

        public ViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.name_text);
            address = (TextView) view.findViewById(R.id.address_text);
            icon = (ImageView) view.findViewById(R.id.icon_image);
            favorite = (ImageView) view.findViewById(R.id.favorite_checkbox);
            constraintLayout = (ConstraintLayout) view.findViewById(R.id.search_line_constraint);
        }
    }

    @Override
    public SearchResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_line, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlaceItem placeItem = placeItems.get(position);
        holder.name.setText(placeItem.getName());
        holder.address.setText(placeItem.getAddress());
        Picasso.get().load(placeItem.getIcon()).into(holder.icon);

        if(checkFavorite(placeItems.get(position))) {
            holder.favorite.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_fill_red));
            holder.favorite.setTag(R.drawable.heart_fill_red);
        } else {
            holder.favorite.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_outline_black));
            holder.favorite.setTag(R.drawable.heart_outline_black);
        }


        holder.constraintLayout.setOnClickListener((v) -> {
            PlaceItem item = placeItems.get(position);
            Intent mIntent = new Intent(v.getContext(), DetailActivity.class);
            mIntent.putExtra(KEY_ID, item.getPlaceId());
            mIntent.putExtra(KEY_NAME, item.getName());
            mIntent.putExtra(KEY_LAT, item.getLat());
            mIntent.putExtra(KEY_LNG, item.getLng());
            v.getContext().startActivity(mIntent);
        });
        holder.favorite.setOnClickListener((v) -> {
            String message = "";
            if((Integer) holder.favorite.getTag() == R.drawable.heart_outline_black) {
                sharedPreference.addFavorite(context, placeItems.get(position));
                holder.favorite.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_fill_red));
                holder.favorite.setTag(R.drawable.heart_fill_red);
                message = placeItem.getName() + " was added to favorites";
            } else if((Integer) holder.favorite.getTag() == R.drawable.heart_fill_red) {
                sharedPreference.removeFavorite(context, placeItems.get(position));
                holder.favorite.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_outline_black));
                holder.favorite.setTag(R.drawable.heart_outline_black);
                message = placeItem.getName() + " was removed from favorites";
            }
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            List<PlaceItem> favorites = sharedPreference.getFavorites(context);
            for(int i = 0; i < favorites.size(); ++i) {
                Log.v("asdf", favorites.get(i).getName() + ": " + favorites.get(i).getPlaceId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return placeItems.size();
    }

    public boolean checkFavorite(PlaceItem item) {
        boolean checked = false;
        List<PlaceItem> favorites = sharedPreference.getFavorites(context);
        if(favorites != null) {
            for(PlaceItem mPlaceItem : favorites) {
                if(mPlaceItem.getPlaceId().equals(item.getPlaceId())) {
                    checked = true;
                    break;
                }
            }
        }
        return checked;
    }

}

package com.jwcheung.hw9;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharedPreference {
    public SharedPreference() {
        super();
    }
    public void saveFavorites(Context context, List<PlaceItem> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences("places_search", Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);
        editor.putString("favorite_places", jsonFavorites);

        editor.commit();
    }

    public void addFavorite(Context context, PlaceItem placeItem) {
        List<PlaceItem> favorites = getFavorites(context);
        if(favorites == null) {
            favorites = new ArrayList<PlaceItem>();
        }
        favorites.add(placeItem);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, PlaceItem placeItem) {
        ArrayList<PlaceItem> favorites = getFavorites(context);
        if(favorites != null) {
            for(int i = 0; i < favorites.size(); ++i) {
                if(favorites.get(i).getPlaceId().equals(placeItem.getPlaceId())) {
                    favorites.remove(i);
                }
            }
            saveFavorites(context, favorites);
        }
    }

    public ArrayList<PlaceItem> getFavorites(Context context) {
        SharedPreferences settings;
        List<PlaceItem> favorites;

        settings = context.getSharedPreferences("places_search", Context.MODE_PRIVATE);

        if(settings.contains("favorite_places")) {
            String jsonFavorites = settings.getString("favorite_places", null);
            Gson gson = new Gson();
            PlaceItem[] favoriteItems = gson.fromJson(jsonFavorites, PlaceItem[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<PlaceItem>(favorites);
        } else {
            return null;
        }
        return (ArrayList<PlaceItem>) favorites;
    }
}

package com.jwcheung.hw9;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Tab2FragmentFavorites extends Fragment {

    private static final String TAG = "Tab2FragmentFavorites";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private SharedPreference sharedPreference;
    List<PlaceItem> favorites;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment_favorites, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.favorites);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        sharedPreference = new SharedPreference();
        favorites = sharedPreference.getFavorites(getActivity());
        if(favorites == null) {
            favorites = new ArrayList<PlaceItem>();
        }

        mAdapter = new SearchResultsAdapter(favorites, getActivity().getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }
}

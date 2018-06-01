package com.jwcheung.hw9;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class Tab2FragmentPhotos extends Fragment {
    private static final String TAG = "Tab2FragmentPhotos";

    private String placeId;
    private ArrayList<Bitmap> photos;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment_photos, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.photos);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        Bundle args = getArguments();
        placeId = args.getString("placeId");
        photos = args.getParcelableArrayList("photos");
        mAdapter = new PhotosAdapter(photos, getContext());
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }
}

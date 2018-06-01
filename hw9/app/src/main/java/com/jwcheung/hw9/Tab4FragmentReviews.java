package com.jwcheung.hw9;

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
import android.widget.AdapterView;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Tab4FragmentReviews extends Fragment {
    private static final String TAG = "Tab4FragmentReviews";

    private ArrayList<ReviewItem> googleReviews;
    private ArrayList<ReviewItem> yelpReviews;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Spinner typeSpinner;
    private Spinner sortSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab4_fragment_reviews, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.reviews);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        typeSpinner = (Spinner) view.findViewById(R.id.spinner3);
        sortSpinner = (Spinner) view.findViewById(R.id.spinner4);

        Bundle args = getArguments();
        ArrayList<String> googleNames = args.getStringArrayList("google_name");
        ArrayList<String> googleRatings = args.getStringArrayList("google_rating");
        ArrayList<String> googleTimes = args.getStringArrayList("google_time");
        ArrayList<String> googleTexts = args.getStringArrayList("google_text");
        ArrayList<String> googlePhotos = args.getStringArrayList("google_photo");
        ArrayList<String> googleLinks = args.getStringArrayList("google_link");
        ArrayList<Integer> googleOrders = args.getIntegerArrayList("google_order");

        ArrayList<String> yelpNames = args.getStringArrayList("yelp_name");
        ArrayList<String> yelpRatings = args.getStringArrayList("yelp_rating");
        ArrayList<String> yelpTimes = args.getStringArrayList("yelp_time");
        ArrayList<String> yelpTexts = args.getStringArrayList("yelp_text");
        ArrayList<String> yelpPhotos = args.getStringArrayList("yelp_photo");
        ArrayList<String> yelpLinks = args.getStringArrayList("yelp_link");
        ArrayList<Integer> yelpOrders = args.getIntegerArrayList("yelp_order");

        googleReviews = new ArrayList<>();

        for(int i = 0; i < googleNames.size(); ++i) {
            ReviewItem review = new ReviewItem(googleNames.get(i), googleRatings.get(i), googleTimes.get(i), googleTexts.get(i), googlePhotos.get(i), googleLinks.get(i), googleOrders.get(i));
            googleReviews.add(review);
        }

        yelpReviews = new ArrayList<>();

        for(int i = 0; i < yelpNames.size(); ++i) {
            ReviewItem review = new ReviewItem(yelpNames.get(i), yelpRatings.get(i), yelpTimes.get(i), yelpTexts.get(i), yelpPhotos.get(i), yelpLinks.get(i), yelpOrders.get(i));
            yelpReviews.add(review);
        }

        mAdapter = new ReviewsAdapter(googleReviews, getContext());
        mRecyclerView.setAdapter(mAdapter);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        mAdapter = new ReviewsAdapter(googleReviews, getContext());
                        break;
                    case 1:
                        mAdapter = new ReviewsAdapter(yelpReviews, getContext());
                        break;
                }
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        Collections.sort(googleReviews, (a, b) -> Integer.compare(a.getOrder(), b.getOrder()));
                        Collections.sort(yelpReviews, (a, b) -> Integer.compare(a.getOrder(), b.getOrder()));
                        break;
                    case 1:
                        Collections.sort(googleReviews, (a, b) -> Integer.compare(Integer.parseInt(b.getRating()), Integer.parseInt(a.getRating())));
                        Collections.sort(yelpReviews, (a, b) -> Integer.compare(Integer.parseInt(b.getRating()), Integer.parseInt(a.getRating())));
                        break;
                    case 2:
                        Collections.sort(googleReviews, (a, b) -> Integer.compare(Integer.parseInt(a.getRating()), Integer.parseInt(b.getRating())));
                        Collections.sort(yelpReviews, (a, b) -> Integer.compare(Integer.parseInt(a.getRating()), Integer.parseInt(b.getRating())));
                        break;
                    case 3:
                        Collections.sort(googleReviews, (a, b) -> b.getTime().compareTo(a.getTime()));
                        Collections.sort(yelpReviews, (a, b) -> b.getTime().compareTo(a.getTime()));
                        break;
                    case 4:
                        Collections.sort(googleReviews, (a, b) -> a.getTime().compareTo(b.getTime()));
                        Collections.sort(yelpReviews, (a, b) -> a.getTime().compareTo(b.getTime()));
                        break;
                }

                int type = typeSpinner.getSelectedItemPosition();
                switch(type) {
                    case 0:
                        mAdapter = new ReviewsAdapter(googleReviews, getContext());
                        break;
                    case 1:
                        mAdapter = new ReviewsAdapter(yelpReviews, getContext());
                        break;
                }
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }
}

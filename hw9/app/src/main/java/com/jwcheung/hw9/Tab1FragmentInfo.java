package com.jwcheung.hw9;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class Tab1FragmentInfo extends Fragment {
    private static final String TAG = "Tab1FragmentInfo";

    private String placeId;

    private TextView address;
    private TextView phone;
    private TextView price;
    private TextView google;
    private TextView website;
    private RatingBar rating;

    private String URL_DATA;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment_info, container, false);
        address = (TextView) view.findViewById(R.id.textView24);
        phone = (TextView) view.findViewById(R.id.textView25);
        price = (TextView) view.findViewById(R.id.textView26);
        google = (TextView) view.findViewById(R.id.textView27);
        website = (TextView) view.findViewById(R.id.textView28);
        rating = (RatingBar) view.findViewById(R.id.ratingBar);

        Bundle args = getArguments();

        address.setText(args.getString("address"));
        phone.setText(args.getString("phone"));
        String priceString = "";
        int priceInt = args.getInt("price");
        for(int i = 0; i < priceInt; ++i) {
            priceString += "$";
        }
        String ratingString = args.getString("rating");
        if(ratingString == null || ratingString.isEmpty()) {
            ratingString = "0";
        }
        rating.setRating(Float.parseFloat(ratingString));
        price.setText(priceString);
        google.setText(args.getString("google"));
        website.setText(args.getString("website"));

        placeId = args.getString("placeId");

        return view;
    }
}

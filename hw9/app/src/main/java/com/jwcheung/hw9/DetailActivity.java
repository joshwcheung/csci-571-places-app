package com.jwcheung.hw9;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";

    private DetailPageAdapter mDetailPageAdapter;
    private ViewPager mViewPager;

    private TabLayout mTabLayout;
    private Tab1FragmentInfo fragmentInfo;
    private Tab2FragmentPhotos fragmentPhotos;
    private Tab3FragmentMap fragmentMap;
    private Tab4FragmentReviews fragmentReviews;

    private String URL_DATA;
    private String placeId;
    private String name;
    private double lat;
    private double lng;

    private Bundle infoArgs;

    protected GeoDataClient mGeoDataClient;
    private List<PlacePhotoMetadata> photoMetadataList;
    private ArrayList<Bitmap> photoList;
    private Bundle photoArgs;

    private Bundle mapArgs;

    private Bundle reviewArgs;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        placeId = extras.getString("placeId");
        name = extras.getString("name");
        lat = extras.getDouble("lat");
        lng = extras.getDouble("lng");

        getSupportActionBar().setTitle(name);

        progressDialog = new ProgressDialog(this);

        mDetailPageAdapter = new DetailPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);


        mGeoDataClient = Places.getGeoDataClient(this, null);

        setupViewPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupViewPager() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("csci571-hw9-jwcheung-env.rebpum8z3n.us-west-1.elasticbeanstalk.com")
                .appendQueryParameter("placeid", placeId);
        URL_DATA = builder.build().toString();
        Log.v("asdf", URL_DATA);

        fragmentInfo = new Tab1FragmentInfo();
        fragmentPhotos = new Tab2FragmentPhotos();
        fragmentMap = new Tab3FragmentMap();
        fragmentReviews = new Tab4FragmentReviews();

        infoArgs = new Bundle();
        photoArgs = new Bundle();
        mapArgs = new Bundle();
        reviewArgs = new Bundle();

        photoMetadataList = new ArrayList<>();
        photoList = new ArrayList<Bitmap>();

        loadJSON();
    }

    private void tabHelper() {
        LinearLayout tabInfoLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.my_tab, null);
        TextView tabInfo = (TextView) tabInfoLayout.findViewById(R.id.customTab);
        tabInfo.setText(R.string.info_tab_text);
        tabInfo.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.info_outline, 0, 0, 0);
        mTabLayout.getTabAt(0).setCustomView(tabInfo);

        LinearLayout tabPhotoLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.my_tab, null);
        TextView tabPhotos = (TextView) tabPhotoLayout.findViewById(R.id.customTab);
        tabPhotos.setText(R.string.photos_tab_text);
        tabPhotos.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.photos, 0, 0, 0);
        mTabLayout.getTabAt(1).setCustomView(tabPhotos);

        LinearLayout tabMapLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.my_tab, null);
        TextView tabMap = (TextView) tabMapLayout.findViewById(R.id.customTab);
        tabMap.setText(R.string.map_tab_text);
        tabMap.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.maps, 0, 0, 0);
        mTabLayout.getTabAt(2).setCustomView(tabMap);

        LinearLayout tabReviewLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.my_tab, null);
        TextView tabReview = (TextView) tabReviewLayout.findViewById(R.id.customTab);
        tabReview.setText(R.string.reviews_tab_text);
        tabReview.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.review, 0, 0, 0);
        mTabLayout.getTabAt(3).setCustomView(tabReview);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void loadJSON() {
        progressDialog.setMessage("Fetching details");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.GET, URL_DATA, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject jsonGoogle = jsonObject.getJSONObject("google");
                String status = jsonGoogle.getString("status");
                if(status.equals("OK")) {
                    JSONObject result = jsonGoogle.getJSONObject("result");
                    String resultName = "";
                    String resultAddress = "";
                    String resultPhone = "";
                    int resultPrice = 0;
                    String resultRating = "";
                    String resultUrl = "";
                    String resultWebsite = "";
                    if(result.has("name")) {
                        resultName = result.getString("name");
                    }
                    if(result.has("formatted_address")) {
                        resultAddress = result.getString("formatted_address");
                    }
                    if(result.has("formatted_phone_number")) {
                        resultPhone = result.getString("formatted_phone_number");
                    }
                    if(result.has("price_level")) {
                        resultPrice = result.getInt("price_level");
                    }
                    if(result.has("rating")) {
                        resultRating = result.getString("rating");
                    }
                    if(result.has("url")) {
                        resultUrl = result.getString("url");
                    }
                    if(result.has("website")) {
                        resultWebsite = result.getString("website");
                    }



                    infoArgs.putString("name", resultName);
                    infoArgs.putString("address", resultAddress);
                    infoArgs.putString("phone", resultPhone);
                    infoArgs.putInt("price", resultPrice);
                    infoArgs.putString("rating", resultRating);
                    infoArgs.putString("google", resultUrl);
                    infoArgs.putString("website", resultWebsite);

                    fragmentInfo.setArguments(infoArgs);

                    ArrayList<String> googleNames = new ArrayList<>();
                    ArrayList<String> googleRatings = new ArrayList<>();
                    ArrayList<String> googleTimes = new ArrayList<>();
                    ArrayList<String> googleTexts = new ArrayList<>();
                    ArrayList<String> googlePhotos = new ArrayList<>();
                    ArrayList<String> googleLinks = new ArrayList<>();
                    ArrayList<Integer> googleOrders = new ArrayList<>();

                    if(result.has("reviews")) {
                        JSONArray jsonReviews = result.getJSONArray("reviews");
                        for(int i = 0; i < jsonReviews.length(); ++i) {
                            JSONObject review = jsonReviews.getJSONObject(i);
                            if(review.has("author_name")) {
                                googleNames.add(review.getString("author_name"));
                            } else {
                                googleNames.add("");
                            }
                            if(review.has("rating")) {
                                googleRatings.add(review.getString("rating"));
                            } else {
                                googleRatings.add("");
                            }
                            if(review.has("time")) {
                                int time = review.getInt("time");
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String dateString = formatter.format(new Date(time * 1000L));
                                googleTimes.add(dateString);
                            } else {
                                googleTimes.add("");
                            }
                            if(review.has("text")) {
                                googleTexts.add(review.getString("text"));
                            } else {
                                googleTexts.add("");
                            }
                            if(review.has("profile_photo_url")) {
                                googlePhotos.add(review.getString("profile_photo_url"));
                            } else {
                                googlePhotos.add("");
                            }
                            if(review.has("author_url")) {
                                googleLinks.add(review.getString("author_url"));
                            } else {
                                googleLinks.add("");
                            }
                            googleOrders.add(i);
                        }
                    }
                    reviewArgs.putStringArrayList("google_name", googleNames);
                    reviewArgs.putStringArrayList("google_rating", googleRatings);
                    reviewArgs.putStringArrayList("google_time", googleTimes);
                    reviewArgs.putStringArrayList("google_text", googleTexts);
                    reviewArgs.putStringArrayList("google_photo", googlePhotos);
                    reviewArgs.putStringArrayList("google_link", googleLinks);
                    reviewArgs.putIntegerArrayList("google_order", googleOrders);

                    ArrayList<String> yelpNames = new ArrayList<>();
                    ArrayList<String> yelpRatings = new ArrayList<>();
                    ArrayList<String> yelpTimes = new ArrayList<>();
                    ArrayList<String> yelpTexts = new ArrayList<>();
                    ArrayList<String> yelpPhotos = new ArrayList<>();
                    ArrayList<String> yelpLinks = new ArrayList<>();
                    ArrayList<Integer> yelpOrders = new ArrayList<>();

                    JSONObject jsonYelp = jsonObject.getJSONObject("yelp");
                    if(jsonYelp.has("reviews")) {
                        JSONArray jsonReviews = jsonYelp.getJSONArray("reviews");
                        for(int i = 0; i < jsonReviews.length(); ++i) {
                            JSONObject review = jsonReviews.getJSONObject(i);
                            if(review.has("rating")) {
                                yelpRatings.add(review.getString("rating"));
                            } else {
                                yelpRatings.add("");
                            }
                            if(review.has("time_created")) {
                                yelpTimes.add(review.getString("time_created"));
                            } else {
                                yelpTimes.add("");
                            }
                            if(review.has("text")) {
                                yelpTexts.add(review.getString("text"));
                            } else {
                                yelpTexts.add("");
                            }
                            if(review.has("url")) {
                                yelpLinks.add(review.getString("url"));
                            } else {
                                yelpLinks.add("");
                            }
                            if(review.has("user")) {
                                JSONObject user = review.getJSONObject("user");
                                if(user.has("name")) {
                                    yelpNames.add(user.getString("name"));
                                } else {
                                    yelpNames.add("");
                                }
                                if(user.has("image_url")) {
                                    yelpPhotos.add(user.getString("image_url"));
                                } else {
                                    yelpPhotos.add("");
                                }
                            }
                            yelpOrders.add(i);
                        }
                    }

                    reviewArgs.putStringArrayList("yelp_name", yelpNames);
                    reviewArgs.putStringArrayList("yelp_rating", yelpRatings);
                    reviewArgs.putStringArrayList("yelp_time", yelpTimes);
                    reviewArgs.putStringArrayList("yelp_text", yelpTexts);
                    reviewArgs.putStringArrayList("yelp_photo", yelpPhotos);
                    reviewArgs.putStringArrayList("yelp_link", yelpLinks);
                    reviewArgs.putIntegerArrayList("yelp_order", yelpOrders);

                    fragmentReviews.setArguments(reviewArgs);
                }

                getPhotoMetadata();
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(DetailActivity.this, "Error" + error.toString(), Toast.LENGTH_SHORT).show());

        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.add(request);
    }

    private void getPhotoMetadata() {
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                PlacePhotoMetadataResponse photos = task.getResult();
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                for(PlacePhotoMetadata photoMetadata : photoMetadataBuffer){
                    photoMetadataList.add(photoMetadata.freeze());
                }
                photoMetadataBuffer.release();
                if(photoMetadataList.size() > 0) {
                    for (int i = 0; i < photoMetadataList.size(); ++i) {
                        getPhoto(photoMetadataList.get(i));
                    }
                } else {
                    photoArgs.putString("placeId", placeId);
                    photoArgs.putParcelableArrayList("photos", photoList);
                    fragmentPhotos.setArguments(photoArgs);

                    mapArgs.putString("name", name);
                    mapArgs.putDouble("lat", lat);
                    mapArgs.putDouble("lng", lng);
                    fragmentMap.setArguments(mapArgs);

                    mDetailPageAdapter.addFragment(fragmentInfo, getString(R.string.info_tab_text));
                    mDetailPageAdapter.addFragment(fragmentPhotos, getString(R.string.photos_tab_text));
                    mDetailPageAdapter.addFragment(fragmentMap, getString(R.string.map_tab_text));
                    mDetailPageAdapter.addFragment(fragmentReviews, getString(R.string.reviews_tab_text));

                    mViewPager.setAdapter(mDetailPageAdapter);

                    mTabLayout = (TabLayout) findViewById(R.id.tabs);
                    mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                    mTabLayout.setupWithViewPager(mViewPager);

                    tabHelper();

                    progressDialog.dismiss();
                }
            }
        });
    }

    private void getPhoto(PlacePhotoMetadata photoMetadata) {
        Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                PlacePhotoResponse photo = task.getResult();
                Bitmap bitmap = photo.getBitmap();
                photoList.add(bitmap);

                if(photoList.size() == photoMetadataList.size()) {
                    photoArgs.putString("placeId", placeId);
                    photoArgs.putParcelableArrayList("photos", photoList);
                    fragmentPhotos.setArguments(photoArgs);

                    mapArgs.putString("name", name);
                    mapArgs.putDouble("lat", lat);
                    mapArgs.putDouble("lng", lng);
                    fragmentMap.setArguments(mapArgs);

                    mDetailPageAdapter.addFragment(fragmentInfo, getString(R.string.info_tab_text));
                    mDetailPageAdapter.addFragment(fragmentPhotos, getString(R.string.photos_tab_text));
                    mDetailPageAdapter.addFragment(fragmentMap, getString(R.string.map_tab_text));
                    mDetailPageAdapter.addFragment(fragmentReviews, getString(R.string.reviews_tab_text));

                    mViewPager.setAdapter(mDetailPageAdapter);

                    mTabLayout = (TabLayout) findViewById(R.id.tabs);
                    mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                    mTabLayout.setupWithViewPager(mViewPager);

                    tabHelper();

                    progressDialog.dismiss();
                }
            }
        });
    }
}

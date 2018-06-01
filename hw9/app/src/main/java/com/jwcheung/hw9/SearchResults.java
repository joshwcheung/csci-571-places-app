package com.jwcheung.hw9;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class SearchResults extends AppCompatActivity {

    private static final String TAG = "SearchResults";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<PlaceItem> placeItems;
    private RecyclerView.LayoutManager mLayoutManager;

    private String keyword;
    private String category;
    private String distance;
    private String locationRadio;
    private String location;
    private String hereCoord;
    private List<String> pageTokens;
    private int currentPage;

    private Button previousButton;
    private Button nextButton;

    private String fetchMessage;

    private String URL_DATA;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.search_results);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Bundle extras = getIntent().getExtras();
        keyword = extras.getString("keyword");
        category = extras.getString("category");
        distance = extras.getString("distance");
        locationRadio = extras.getString("locationRadio");
        location = extras.getString("location");
        hereCoord = extras.getString("hereCoord");
        pageTokens = new ArrayList<String>();
        currentPage = 0;

        previousButton = (Button) findViewById(R.id.button3);
        nextButton = (Button) findViewById(R.id.button4);

        fetchMessage = "Fetching results";

        // Generate request
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("csci571-hw9-jwcheung-env.rebpum8z3n.us-west-1.elasticbeanstalk.com")
                .appendQueryParameter("keyword", keyword)
                .appendQueryParameter("category", category)
                .appendQueryParameter("distance", distance)
                .appendQueryParameter("location_radio", locationRadio)
                .appendQueryParameter("location_text", location)
                .appendQueryParameter("here_coord", hereCoord);
        URL_DATA = builder.build().toString();

        placeItems = new ArrayList<>();

        loadJSON();
    }

    private void loadJSON() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(fetchMessage);
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.GET, URL_DATA, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray array = jsonObject.getJSONArray("results");
                for(int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    PlaceItem item = new PlaceItem(obj.getString("name"),
                            obj.getString("vicinity"),
                            obj.getString("icon"),
                            obj.getString("place_id"),
                            obj.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                            obj.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
                    placeItems.add(item);
                }

                if(currentPage == 0) {
                    previousButton.setEnabled(false);
                } else {
                    previousButton.setEnabled(true);
                }
                if(!jsonObject.has("next_page_token")) {
                    nextButton.setEnabled(false);
                } else {
                    nextButton.setEnabled(true);
                }

                if(currentPage == pageTokens.size() && jsonObject.has("next_page_token")) {
                    pageTokens.add(jsonObject.getString("next_page_token"));
                }
                previousButton.setOnClickListener((v) -> {
                    if(currentPage > 0) {
                        Uri.Builder builder = new Uri.Builder();
                        if(currentPage == 1) {
                            builder.scheme("http")
                                    .authority("csci571-hw9-jwcheung-env.rebpum8z3n.us-west-1.elasticbeanstalk.com")
                                    .appendQueryParameter("keyword", keyword)
                                    .appendQueryParameter("category", category)
                                    .appendQueryParameter("distance", distance)
                                    .appendQueryParameter("location_radio", locationRadio)
                                    .appendQueryParameter("location_text", location)
                                    .appendQueryParameter("here_coord", hereCoord);
                        } else {
                            builder.scheme("http")
                                    .authority("csci571-hw9-jwcheung-env.rebpum8z3n.us-west-1.elasticbeanstalk.com")
                                    .appendQueryParameter("keyword", keyword)
                                    .appendQueryParameter("pagetoken", pageTokens.get(currentPage - 2));
                        }
                        URL_DATA = builder.build().toString();
                        currentPage -= 1 ;
                        fetchMessage = "Fetching previous page";
                        placeItems = new ArrayList<>();
                        loadJSON();
                    }
                });

                nextButton.setOnClickListener((v) -> {
                    if(jsonObject.has("next_page_token")) {
                        Uri.Builder builder = new Uri.Builder();
                        builder.scheme("http")
                                .authority("csci571-hw9-jwcheung-env.rebpum8z3n.us-west-1.elasticbeanstalk.com")
                                .appendQueryParameter("keyword", keyword)
                                .appendQueryParameter("pagetoken", pageTokens.get(currentPage));
                        URL_DATA = builder.build().toString();
                        currentPage += 1;
                        fetchMessage = "Fetching next page";
                        placeItems = new ArrayList<>();
                        loadJSON();
                    }
                });

                mAdapter = new SearchResultsAdapter(placeItems, getApplicationContext());
                mRecyclerView.setAdapter(mAdapter);
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(SearchResults.this, "Error" + error.toString(), Toast.LENGTH_SHORT).show());

        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.add(request);
    }
}

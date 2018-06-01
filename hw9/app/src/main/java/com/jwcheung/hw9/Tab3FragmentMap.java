package com.jwcheung.hw9;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Tab3FragmentMap extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "Tab3FragmentMap";

    private String name;
    private double lat;
    private double lng;

    private AutoCompleteTextView autoCompleteTextView;
    private Spinner spinner;
    private GoogleMap map;

    private String URL_DATA;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_fragment_map, container, false);

        autoCompleteTextView = view.findViewById(R.id.editText);
        spinner = view.findViewById(R.id.spinner2);

        AutoCompleteAdapter adapter = new AutoCompleteAdapter(getActivity());
        autoCompleteTextView.setAdapter(adapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle args = getArguments();
        name = args.getString("name");
        lat = args.getDouble("lat");
        lng = args.getDouble("lng");

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng location = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions().position(location).title(name);
        map.addMarker(markerOptions);
        map.moveCamera(CameraUpdateFactory.newLatLng(location));
        map.setMinZoomPreference(15.0f);
        map.setMaxZoomPreference(20.0f);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String destination = Double.toString(lat) + "," + Double.toString(lng);
                String origin = autoCompleteTextView.getText().toString();
                String mode = spinner.getSelectedItem().toString().toLowerCase();

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("csci571-hw9-jwcheung-env.rebpum8z3n.us-west-1.elasticbeanstalk.com")
                        .appendQueryParameter("start", origin)
                        .appendQueryParameter("end", destination)
                        .appendQueryParameter("mode", mode);
                URL_DATA = builder.build().toString();
                Log.v("asdf", URL_DATA);

                loadJSON(origin);
            }
        });

        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    String destination = Double.toString(lat) + "," + Double.toString(lng);
                    String origin = autoCompleteTextView.getText().toString();
                    String mode = spinner.getSelectedItem().toString().toLowerCase();

                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("http")
                            .authority("csci571-hw9-jwcheung-env.rebpum8z3n.us-west-1.elasticbeanstalk.com")
                            .appendQueryParameter("start", origin)
                            .appendQueryParameter("end", destination)
                            .appendQueryParameter("mode", mode);
                    URL_DATA = builder.build().toString();
                    Log.v("asdf", URL_DATA);

                    loadJSON(origin);
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String destination = Double.toString(lat) + "," + Double.toString(lng);
                String origin = autoCompleteTextView.getText().toString();
                String mode = spinner.getSelectedItem().toString().toLowerCase();

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("csci571-hw9-jwcheung-env.rebpum8z3n.us-west-1.elasticbeanstalk.com")
                        .appendQueryParameter("start", origin)
                        .appendQueryParameter("end", destination)
                        .appendQueryParameter("mode", mode);
                URL_DATA = builder.build().toString();
                Log.v("asdf", URL_DATA);

                loadJSON(origin);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadJSON(String origin) {
        StringRequest request = new StringRequest(Request.Method.GET, URL_DATA, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject jsonRoute = jsonObject.getJSONArray("routes").getJSONObject(0);
                JSONObject leg = jsonRoute.getJSONArray("legs").getJSONObject(0);
                JSONObject startJSON = leg.getJSONObject("start_location");
                JSONObject endJSON = leg.getJSONObject("end_location");
                String points = jsonRoute.getJSONObject("overview_polyline").getString("points");

                LatLng start = new LatLng(startJSON.getDouble("lat"), startJSON.getDouble("lng"));
                LatLng end = new LatLng(endJSON.getDouble("lat"), endJSON.getDouble("lng"));
                List<LatLng> decoded = PolyUtil.decode(points);
                PolylineOptions polylineOptions = new PolylineOptions().geodesic(true).color(Color.BLUE).width(10);
                polylineOptions.add(start);
                for(int i = 0; i < decoded.size(); ++i) {
                    polylineOptions.add(decoded.get(i));
                }
                polylineOptions.add(end);

                map.clear();
                MarkerOptions startMarkerOptions = new MarkerOptions().position(start).title(origin);
                MarkerOptions endMarkerOptions = new MarkerOptions().position(end).title(name);
                map.addMarker(startMarkerOptions);
                map.addMarker(endMarkerOptions);

                map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
                map.setMinZoomPreference(15.0f);
                map.setMaxZoomPreference(20.0f);
                map.addPolyline(polylineOptions);

            } catch(JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(getActivity(), "Error" + error.toString(), Toast.LENGTH_SHORT).show());

        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        mRequestQueue.add(request);
    }
}

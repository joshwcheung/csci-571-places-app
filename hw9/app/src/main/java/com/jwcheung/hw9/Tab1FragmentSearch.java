package com.jwcheung.hw9;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.w3c.dom.Text;

public class Tab1FragmentSearch extends Fragment {
    private static final String TAG = "Tab1FragmentSearch";

    private Button searchButton;
    private Button clearButton;
    private EditText keywordInput;
    private Spinner categoryInput;
    private EditText distanceInput;
    private RadioButton hereRadio;
    private RadioButton otherRadio;
    private AutoCompleteTextView locationInput;
    private TextView keywordError;
    private TextView locationError;

    private LocationManager lm;

    private Double lat;
    private Double lng;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment_search, container, false);

        keywordInput = (EditText) view.findViewById(R.id.editText2);
        categoryInput = (Spinner) view.findViewById(R.id.spinner);
        distanceInput = (EditText) view.findViewById(R.id.editText3);
        hereRadio = (RadioButton) view.findViewById(R.id.radioButton);
        otherRadio = (RadioButton) view.findViewById(R.id.radioButton2);
        locationInput = (AutoCompleteTextView) view.findViewById(R.id.editText4);
        keywordError = (TextView) view.findViewById(R.id.textView9);
        locationError = (TextView) view.findViewById(R.id.textView10);

        AutoCompleteAdapter adapter = new AutoCompleteAdapter(getActivity());
        locationInput.setAdapter(adapter);
        locationInput.setEnabled(false);

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioButton) {
                    locationInput.setEnabled(false);
                    locationError.setVisibility(View.GONE);
                } else if(checkedId == R.id.radioButton2) {
                    locationInput.setEnabled(true);
                }
            }
        });

        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        try {
            Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = loc.getLongitude();
            double latitude = loc.getLatitude();
        } catch(SecurityException e) {
            e.printStackTrace();
        }

        searchButton = (Button) view.findViewById(R.id.button);
        searchButton.setOnClickListener((v) -> {
            // Get user input
            String keyword = keywordInput.getText().toString();
            String category = categoryInput.getSelectedItem().toString().replaceAll(" ", "_").toLowerCase();
            String distance = distanceInput.getText().toString();
            String locationRadio = "";
            String hereCoord = "";
            if(hereRadio.isChecked()) {
                locationRadio = "location_here";

                hereCoord = "34.031800,-118.287446";
            } else if(otherRadio.isChecked()) {
                locationRadio = "location_other";
            }

            String location = locationInput.getText().toString();

            if(keyword.isEmpty()) {
                keywordError.setVisibility(View.VISIBLE);
            } else {
                keywordError.setVisibility(View.GONE);
            }
            if(otherRadio.isChecked() && location.isEmpty()) {
                locationError.setVisibility(View.VISIBLE);
            } else {
                locationError.setVisibility(View.GONE);
            }
            if(!keyword.isEmpty() && (hereRadio.isChecked() || otherRadio.isChecked() && !location.isEmpty())) {
                Intent mIntent = new Intent(v.getContext(), SearchResults.class);
                mIntent.putExtra("keyword", keyword);
                mIntent.putExtra("category", category);
                mIntent.putExtra("distance", distance);
                mIntent.putExtra("locationRadio", locationRadio);
                mIntent.putExtra("hereCoord", hereCoord);
                mIntent.putExtra("location", location);

                startActivityForResult(mIntent, 0);
            }
        });

        clearButton = (Button) view.findViewById(R.id.button2);
        clearButton.setOnClickListener((v) -> {
            keywordInput.setText("", TextView.BufferType.EDITABLE);
            categoryInput.setSelection(0);
            distanceInput.setText("", TextView.BufferType.EDITABLE);
            hereRadio.setChecked(true);
            otherRadio.setChecked(false);
            locationInput.setText("", TextView.BufferType.EDITABLE);
            locationInput.setEnabled(false);
            keywordError.setVisibility(View.GONE);
            locationError.setVisibility(View.GONE);
        });

        return view;
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            lat = location.getLatitude();
            lng = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            try {
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                lat = location.getLatitude();
                lng = location.getLongitude();
            } catch(SecurityException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            if(lat == null || lng == null) {
                lat = 34.031800;
                lng = -118.287446;
            }
        }
    };
}

package com.jwcheung.hw9;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.TextView;

import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter {
    public static final String TAG = "AutoCompleteAdapter";

    private List<Place> placeList;
    private Context mContext;
    private GeoDataClient mGeoDataClient;

    private AutoCompleteAdapter.AutoCompleteFilter listFilter = new AutoCompleteAdapter.AutoCompleteFilter();

    public AutoCompleteAdapter(Context context) {
        super(context, android.R.layout.simple_dropdown_item_1line, new ArrayList<Place>());
        mContext = context;

        mGeoDataClient = Places.getGeoDataClient(mContext, null);
    }

    @Override
    public int getCount() {
        return placeList.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return placeList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            if(position != (placeList.size() - 1)) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.autocomplete_item, parent, false);
            } else {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.powered_by_google, parent, false);
            }
        }

        if(position != (placeList.size() - 1)) {
            TextView text = convertView.findViewById(R.id.autocompleteText);
            text.setText(placeList.get(position).getPlaceText());
        }

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class AutoCompleteFilter extends Filter {
        private Object lockOne = new Object();
        private Object lockTwo = new Object();
        private boolean placeResults = false;

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            placeResults = false;
            final List<Place> placesList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0) {
                synchronized(lockOne) {
                    results.values = new ArrayList<Place>();
                    results.count = 0;
                }
            } else {
                final String str = constraint.toString().toLowerCase();
                Task<AutocompletePredictionBufferResponse> task = getAutoCompletePlaces(str);
                task.addOnCompleteListener(new OnCompleteListener<AutocompletePredictionBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<AutocompletePredictionBufferResponse> task) {
                        if(task.isSuccessful()) {
                            AutocompletePredictionBufferResponse predictions = task.getResult();
                            Place place;
                            for(AutocompletePrediction prediction : predictions) {
                                place = new Place();
                                place.setPlaceId(prediction.getPlaceId());
                                place.setPlaceText(prediction.getFullText(null).toString());
                                placesList.add(place);
                            }
                            predictions.release();
                        }
                        placeResults = true;
                        synchronized(lockTwo) {
                            lockTwo.notifyAll();
                        }
                    }
                });

                while(!placeResults) {
                    synchronized(lockTwo) {
                        try {
                            lockTwo.wait();
                        } catch(InterruptedException e) {

                        }
                    }
                }
                results.values = placesList;
                results.count = placesList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results.values != null) {
                placeList = (ArrayList<Place>) results.values;
            } else {
                placeList = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

        private Task<AutocompletePredictionBufferResponse> getAutoCompletePlaces(String constraint) {
            AutocompleteFilter.Builder filterBuilder = new AutocompleteFilter.Builder();
            filterBuilder.setCountry("US");
            Task<AutocompletePredictionBufferResponse> results = mGeoDataClient.getAutocompletePredictions(constraint, null, filterBuilder.build());
            return results;
        }
    }
}

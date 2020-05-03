package com.aquarian.drivers.ui.vehicle;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aquarian.drivers.R;
import com.aquarian.drivers.ui.jobs.Job;
import com.aquarian.drivers.ui.jobs.JobsAdapter;
import com.aquarian.drivers.util.SaveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class VehicleFragment extends Fragment {

    public static ArrayList<ListItem> vehicleItems = new ArrayList<ListItem>();
    public static RecyclerView mRecyclerView;
    public static VehicleFragment newInstance() {
        return new VehicleFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.jobs_fragment, null);

        mRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.rViewJobs);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);

        vehicleItems.clear();

        loadData();

        return fragmentView;
    }


    private void loadData()
    {
        //Read JSON string from internal storage.
        SaveData sd = new SaveData();
        sd.read("vehicle", getActivity().getApplicationContext());
        //Parse JSON string and create objects
        try {
            JSONArray response = new JSONArray(sd.content); //Parse JSON Array
            for(int i=(response.length()-1); i > -1; i--) {
                JSONObject jsonobject = response.getJSONObject(i); //Parse JSON object

                Iterator<String> iter = jsonobject.keys();
                String[] headers = {"License Plate", "Brand", "Model", "Year", "Last MOT", "Next MOT", "Original Mileage", "Current Mileage", "Date Created"};
                int j = 0;
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        if (key.equals("VehicleID"))
                        {
                            ListItem item = new ListItem("", "");
                            vehicleItems.add(item);
                        }
                        else
                        {
                            Object value = jsonobject.get(key);
                            ListItem item = new ListItem(headers[j], value.toString());
                            vehicleItems.add(item);
                            j++;
                        }
                    } catch (JSONException e) {
                        // Something went wrong!
                    }
                }
            }
            //Pass the list of News Object to the NewsAdapter and inflate view in each element of the recyclerview
            VehicleAdapter nAdapter = new VehicleAdapter(vehicleItems);
            mRecyclerView.setAdapter(nAdapter);
        } catch (JSONException ex) {
            Log.e("App", "Failure", ex);
        }
    }
}

package com.aquarian.drivers.ui.receipts.receiptsList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.aquarian.drivers.R;
import com.aquarian.drivers.ui.receipts.Receipt;
import com.aquarian.drivers.util.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReceiptsList extends Fragment {

    public static ArrayList<Receipt> receiptList = new ArrayList<Receipt>();
    public static RecyclerView mRecyclerView;
    public static ReceiptsList newInstance() {
        return new ReceiptsList();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_receipts_list, null);

        mRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.rViewJobs);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), llm.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        receiptList.clear();
        loadData(this.getContext());
        return fragmentView;
    }

    public void loadData(Context c)
    {
            Log.d("RAN", "I RAN");
            RequestQueue queue = Volley.newRequestQueue(c);
            String url = "http://soc-web-liv-82.napier.ac.uk/api/receipts/driver/" + ((GlobalVariables) c.getApplicationContext()).getDriverID();
            JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>()
                    {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d("Response", response.toString());
                            try{
                                if(response.length()==0)
                                {
                                    updateView(c);
                                }
                                {
                                    for(int i=0; i < response.length(); i++) {
                                        ((GlobalVariables) getActivity().getApplicationContext()).setNumberOfReceipts(response.length());
                                        JSONObject obj = response.getJSONObject(i);
                                        Receipt receipt = new Receipt(obj.getString("Type"), obj.getString("Amount"));
                                        receiptList.add(receipt);
                                        ReceiptsListAdapter nAdapter = new ReceiptsListAdapter(receiptList);
                                        mRecyclerView.setAdapter(nAdapter);
                                }
                                }
                            } catch (JSONException e) {
                                Log.d("JSON Excepetion", e.toString());
                                updateView(c);
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error.Response", String.valueOf(error));
                        }
                    }
            );

            // Add the request to the RequestQueue.
            queue.add(getRequest);
    }

    private void updateView(Context c)
    {
        ViewGroup parent = (ViewGroup) mRecyclerView.getParent();
        int index = parent.indexOfChild(mRecyclerView);
        parent.removeView(mRecyclerView);
        TextView t = new TextView(c);
        t.setText("No receipts uploaded today.");
        t.setGravity(Gravity.CENTER);
        t.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        t.setTextSize(18);
        t.setTextColor(Color.parseColor("#000000"));
        parent.addView(t);
    }

}



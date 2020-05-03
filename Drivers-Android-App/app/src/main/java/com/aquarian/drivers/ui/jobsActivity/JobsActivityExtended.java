package com.aquarian.drivers.ui.jobsActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.aquarian.drivers.R;
import com.aquarian.drivers.util.SaveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JobsActivityExtended extends AppCompatActivity{

    private String jobID;
    //Declaring views
    private TextView pageTitle;
    private TextView jobStatus, parcelType, parcelWeight, parcelSize, dateDue;
    private TextView custName, custPhone, custEmail;
    private TextView pickupAddress, pickupType, deliveryAddress, deliveryType;
    private Button deliveredButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);
        int jobIndex = Integer.parseInt(getIntent().getStringExtra("JobIndex"));
        jobID = getIntent().getStringExtra("JobID");
        pageTitle = findViewById(R.id.jobTitle);
        pageTitle.setText("Job " + (jobIndex+1));
        loadPageData(jobIndex);
        deliveredButton = (Button) findViewById(R.id.markDeliveredButton);

        deliveredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(JobsActivityExtended.this, Signature.class);
                i.putExtra("JobID", jobID);
                startActivity(i);
                finish();
            }
        });
    }

    private void loadPageData(int index) {
        //Parse JSON string and create objects
        try {
            SaveData sd = new SaveData();
            sd.read("jobsFile", this);
            /*JSONObject obj = new JSONObject(sd.content);*/
            JSONArray ja = new JSONArray(sd.content);
            Log.d("JSON", ja.toString());
            JSONObject jsonobject = ja.getJSONObject(index); //Parse JSON object
            JSONArray jobArray = jsonobject.getJSONArray("Job");

            // JOB DETAILS

            JSONObject job = jobArray.getJSONObject(0);

            jobStatus = (TextView) findViewById(R.id.jobStatusCard);
            parcelType = (TextView) findViewById(R.id.jobParcelType);
            parcelWeight = (TextView) findViewById(R.id.jobParcelWeight);
            parcelSize = (TextView) findViewById(R.id.jobParcelSize);
            dateDue = (TextView) findViewById(R.id.jobDateDue);

            jobStatus.setText(sanitize(job.getString("Status")));
            parcelType.setText(sanitize(job.getString("ParcelType")));
            parcelWeight.setText(sanitize(job.getString("ParcelSize")));
            parcelSize.setText(sanitize(job.getString("ParcelWeight")));
            dateDue.setText(sanitize(job.getString("DateDue")));

            // CUSTOMER DETAILS

            jobArray = jsonobject.getJSONArray("Customer");
            JSONObject customer = jobArray.getJSONObject(0);

            custName = (TextView) findViewById(R.id.custName);
            custPhone = (TextView) findViewById(R.id.custPhone);
            custEmail = (TextView) findViewById(R.id.custEmail);

            custName.setText(sanitize(customer.getString("FirstName")) + " " + sanitize(customer.getString("LastName")));
            custPhone.setText(sanitize(customer.getString("Phone1")));
            custEmail.setText(sanitize(customer.getString("Email")));

            //PICK UP AND DROP OFF LOCATIONS
            pickupAddress = (TextView) findViewById(R.id.pickupAddress);
            pickupType = (TextView) findViewById(R.id.pickupType);
            deliveryAddress = (TextView) findViewById(R.id.deliveryAddress);
            deliveryType = (TextView) findViewById(R.id.deliveryType);

            jobArray = jsonobject.getJSONArray("Pickup");
            JSONObject pickup = jobArray.getJSONObject(0);
            pickupAddress.setText(cleanAddress(pickup.getString("PlaceName")) + "\n" + sanitize(pickup.getString("Address1")) + ", " + cleanAddress(pickup.getString("Address2"))  + sanitize(pickup.getString("City")) + ", " + sanitize(pickup.getString("PostCode")));
            pickupType.setText(sanitize(pickup.getString("Type")));

            jobArray = jsonobject.getJSONArray("Dropoff");
            JSONObject dropoff = jobArray.getJSONObject(0);
            deliveryAddress.setText(cleanAddress(dropoff.getString("PlaceName")) + "\n" + sanitize(dropoff.getString("Address1")) + ", " + cleanAddress(dropoff.getString("Address2")) + sanitize(dropoff.getString("City")) + ", " + sanitize(dropoff.getString("PostCode")));
            deliveryType.setText(sanitize(dropoff.getString("Type")));

        } catch (JSONException ex) {
            Log.e("App", "Failure", ex);
        }
    }

    private String sanitize(String input){
        if (input==null || input.equals("") || input.equals("null")){input="";}
        return input;
    }

    private String cleanAddress(String input){
        if (input==null || input.equals("") || input.equals("null")){input="";}
        else {input+=", ";}
        return input;
    }

}




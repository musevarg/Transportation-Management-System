package com.aquarian.drivers.ui.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aquarian.drivers.MainActivity;
import com.aquarian.drivers.R;
import com.aquarian.drivers.util.GetData;
import com.aquarian.drivers.util.GlobalVariables;
import com.aquarian.drivers.util.NetworkUtil;
import com.aquarian.drivers.util.SaveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class Login extends AppCompatActivity {

    TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button loginButton = findViewById(R.id.loginButton);
        error = findViewById(R.id.errorMessageView);
        final EditText email = findViewById(R.id.emailView);
        final EditText pass = findViewById(R.id.passView);

            loginButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (NetworkUtil.getConnectivityStatus(Login.this) == 0) {
                        new AlertDialog.Builder(Login.this)
                                .setTitle("No internet")
                                .setMessage("An internet connection is required to login.")
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                    } else {
                        if (checkFields(email, pass)) {
                            POST_Request(email.getText().toString(), pass.getText().toString());
                        } else {
                            error.setText("Please fill out both fields.");
                        }
                    }
                }
            });

    }

    private Boolean checkFields(EditText email, EditText pass) {

        if (TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(pass.getText())) {
            return false;
        } else {
            return true;
        }
    }

    private void POST_Request(String username, String password){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://soc-web-liv-82.napier.ac.uk/api/drivers/login?Username="+username+"&Password="+password,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("LOGIN",response);
                        parseData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("LOGIN",error.toString());
                    }
                }){
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void parseData(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            if (jsonObject.getString("Status").equals("Success")) {
                new GetData(Login.this).execute("http://soc-web-liv-82.napier.ac.uk/api/drivers/assigned/" + jsonObject.getString("DriverID"), "jobsFile");
                SaveData.write("Driver", response, getApplicationContext());
                ((GlobalVariables) this.getApplication()).setDriverID(jsonObject.getString("DriverID"));
                ((GlobalVariables) this.getApplication()).setDriverFirstname(jsonObject.getString("FirstName"));
                ((GlobalVariables) this.getApplication()).setDriverLastConnection(jsonObject.getString("LastConnected"));
                ((GlobalVariables) this.getApplication()).setVehicleID(jsonObject.getString("VehicleID"));
                new GetData(Login.this).execute("http://soc-web-liv-82.napier.ac.uk/api/vehicles/" + jsonObject.getString("VehicleID"), "vehicle");
                if (((GlobalVariables) this.getApplication()).getDriverFirstname() != null)
                {
                    TimerTask task = new TimerTask() {

                        @Override
                        public void run() {
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, 1000);
                }
            }
            else
            {
                error.setText(jsonObject.getString("Message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}

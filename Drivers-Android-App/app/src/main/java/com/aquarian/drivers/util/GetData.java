package com.aquarian.drivers.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.aquarian.drivers.ui.jobs.JobsFragment;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class GetData extends AsyncTask<String, Void, JSONArray>
{

    private String filename;
    private Context c;
    public String response;

    public GetData(Context c) {
        this.c = c;
    }

    @Override
    protected JSONArray doInBackground(String... params)
    {
        String str = params[0];
        filename = params[1];

        //String str="http://your.domain.here/yourSubMethod";
        URLConnection urlConn = null;
        BufferedReader bufferedReader = null;
        try
        {
            URL url = new URL(str);
            urlConn = url.openConnection();
            bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);
            }

            //return new JSONObject(stringBuffer.toString());
            return new JSONArray(stringBuffer.toString());
        }
        catch(Exception ex)
        {
            Log.e("App", "yourDataTask", ex);
            return null;
        }
        finally
        {
            if(bufferedReader != null)
            {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPostExecute(JSONArray response)
    {
        if(response != null)
        {
            Log.d("GET DATA RESPONSE", response.toString());
            this.response = response.toString();
            SaveData.write(filename, response.toString(), this.c);
        }
    }
}

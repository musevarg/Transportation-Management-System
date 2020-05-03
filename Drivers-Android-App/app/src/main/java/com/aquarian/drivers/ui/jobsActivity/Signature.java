package com.aquarian.drivers.ui.jobsActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.aquarian.drivers.R;
import com.aquarian.drivers.util.GetData;
import com.aquarian.drivers.util.GlobalVariables;
import com.aquarian.drivers.util.VolleyMultipartRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Signature extends AppCompatActivity {

    private int jobID, picNum;
    private Button completeButton, saveParcelPic;
    private ImageView picTest;
    Context context;
    boolean isPic1Uploaded, isPic2Uploaded;
    Bitmap pic1, pic2;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        CustomScrollView sv = (CustomScrollView) findViewById(R.id.signatureScrollView);

        jobID = Integer.parseInt(getIntent().getStringExtra("JobID"));
        picNum=1;
        isPic1Uploaded = false; isPic2Uploaded = false;

        LinearLayout parent = (LinearLayout) findViewById(R.id.signatureLayout);
        MyDrawView myDrawView = new MyDrawView(this);
        parent.addView(myDrawView);

        View rootlayout = findViewById(android.R.id.content);
        context = getApplicationContext();
        picTest = (ImageView) findViewById(R.id.picPreview);

        saveParcelPic = (Button) findViewById(R.id.saveParcelPic);
        saveParcelPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        completeButton = (Button) findViewById(R.id.completeButton);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pic1 != null)
                {
                    completeButton.setText("Sending...");
                    completeButton.setEnabled(false);
                    saveParcelPic.setEnabled(false);
                    createImageFromSignature(parent);
                }
                else
                {
                    Snackbar.make(sv, "Please take a picture of the parcel.", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        sv.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent){
                sv.setEnableScrolling(true);
                return sv.onTouchEvent(motionEvent);
            }
        });

        myDrawView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                sv.setEnableScrolling(false);
                return myDrawView.onTouchEvent(motionEvent);
            }
        });

    }

    private void createImageFromSignature(LinearLayout parent){
        parent.setDrawingCacheEnabled(true);
        pic2 = parent.getDrawingCache();
        uploadBitmap(pic1);
    }

    private void takePicture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(intent,1);
        File photo;
        try
        {
            // place where to store camera taken picture
            photo = this.createTemporaryFile("picture", ".jpg");
            photo.delete();
            mImageUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", photo);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            //start camera intent
            startActivityForResult(intent,1);
        }
        catch(Exception e)
        {
            Log.v("ERROR", "Can't create file to take picture!");
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            /*pic1 = (Bitmap) data.getExtras().get("data");
            picTest.setImageBitmap(pic1);*/
            this.grabImage(picTest);
        }
    }

    private void uploadBitmap(final Bitmap bitmap) {

        String UPLOAD_URL = "http://soc-web-liv-82.napier.ac.uk/api/receipts/uploads";
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONArray jarray = new JSONArray(new String(response.data));
                            JSONObject obj = jarray.getJSONObject(0);
                            //Toast.makeText(getApplicationContext(), obj.getString("Message"), Toast.LENGTH_SHORT).show();
                            Log.d("UPLOAD IMAGE", obj.getString("Message"));
                            if (!isPic1Uploaded){isPic1Uploaded=true;picNum++;uploadBitmap(pic2);}
                            else
                            {
                                callUpdateDatabase();
                            }
                        } catch (JSONException e) {
                            Log.d("uploadBitmap", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                String imagename = "J" + getDate("ddMMyy") + "D" + ((GlobalVariables) getApplicationContext()).getDriverID() + "-" + jobID + "-" + picNum;
                params.put("file", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(getApplicationContext()).add(volleyMultipartRequest);
    }

    private byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private String getDate(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            Date date = Calendar.getInstance().getTime();
            String dateTime = dateFormat.format(date);
            return dateTime;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void updateDatabase(String imagename1, String imagename2){
        String driverID = ((GlobalVariables) getApplicationContext()).getDriverID();
        String imageUrl1 = "http://soc-web-liv-82.napier.ac.uk/admin/static/images/receipts/" + imagename1 + ".png";
        String imageUrl2 = "http://soc-web-liv-82.napier.ac.uk/admin/static/images/receipts/" + imagename2 + ".png";
        String url = "http://soc-web-liv-82.napier.ac.uk/api/jobs/"+jobID+"?Picture1="+imageUrl1+"&Picture2="+imageUrl2+"&Status=Delivered&DateDelivered="+getDate("YYYY-MM-dd HH:mm:ss");
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.PUT, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("JOB UPDATED", response.toString());
                        new GetData(getApplicationContext()).execute("http://soc-web-liv-82.napier.ac.uk/api/drivers/assigned/" + ((GlobalVariables) getApplicationContext()).getDriverID(), "jobsFile");
                        finish();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("JOB UPDATE ERROR", String.valueOf(error));
                    }
                }
        );

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(getApplicationContext()).add(getRequest);
    }

    private void callUpdateDatabase()
    {
        String imagename1 = "J" + getDate("ddMMyy") + "D" + ((GlobalVariables) getApplicationContext()).getDriverID() + "-" + jobID + "-1";
        String imagename2 = "J" + getDate("ddMMyy") + "D" + ((GlobalVariables) getApplicationContext()).getDriverID() + "-" + jobID + "-2";
        updateDatabase(imagename1, imagename2);
    }

    /* GET FULL SIZE PICTURE */

    private Uri mImageUri;

    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
        if(!tempDir.exists())
        {
            tempDir.mkdirs();
        }
        return File.createTempFile(part, ext, tempDir);
    }

    public void grabImage(ImageView imageView)
    {
        this.getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = this.getContentResolver();
        try
        {
            Bitmap pic = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            int width = pic.getWidth(); int height = pic.getHeight();
            double scaleWidth =  0.3 * width;
            double scaleHeight = 0.3 * height;
            Bitmap resized = Bitmap. createScaledBitmap ( pic , (int) scaleWidth , (int) scaleHeight , true ) ;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.PNG, 100, out);
            pic1 = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
            imageView.setImageBitmap(pic1);
        }
        catch (Exception e)
        {
            Log.d("ERROR", "Failed to load image", e);
        }
    }

}

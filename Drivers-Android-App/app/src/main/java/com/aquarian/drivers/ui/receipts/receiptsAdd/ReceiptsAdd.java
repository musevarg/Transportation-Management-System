package com.aquarian.drivers.ui.receipts.receiptsAdd;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.aquarian.drivers.R;
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

import static android.app.Activity.RESULT_OK;

public class ReceiptsAdd extends Fragment {

    private EditText receiptAmount, receiptReason;
    Button uploadReceipt, takePicture;
    Bitmap pic;
    ImageView receiptPicView;
    private LinearLayout rootlayout;
    Context context;
    public static ReceiptsAdd newInstance() {
        return new ReceiptsAdd();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_receipt_add, null);
        rootlayout = (LinearLayout) fragmentView.findViewById(R.id.addReceiptLayout);
        context = getContext();
        receiptPicView = (ImageView) fragmentView.findViewById(R.id.receiptPicPreview);
        takePicture = (Button) fragmentView.findViewById(R.id.receiptTakePicture);
        uploadReceipt = (Button) fragmentView.findViewById(R.id.receiptUploadButton);
        receiptAmount = (EditText) fragmentView.findViewById(R.id.addReceiptAmount);
        receiptReason = (EditText) fragmentView.findViewById(R.id.addReceiptReason);

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((receiptAmount.getText().toString().equals("") || receiptAmount.getText().toString().equals(null)) || (receiptReason.getText().toString().equals("") || receiptReason.getText().toString().equals(null)))
                {
                    Snackbar.make(rootlayout, "Please fill in both fields.", Snackbar.LENGTH_SHORT).show();
                }
                else
                {
                    takePicture();
                }
            }
        });

        uploadReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((receiptAmount.getText().toString().equals("") || receiptAmount.getText().toString().equals(null)) || (receiptReason.getText().toString().equals("") || receiptReason.getText().toString().equals(null)))
                {
                    Snackbar.make(rootlayout, "Please fill in both fields.", Snackbar.LENGTH_SHORT).show();
                }
                else
                {
                    if (pic != null)
                    {
                        uploadReceipt.setText("Sending...");
                        uploadReceipt.setEnabled(false);
                        takePicture.setEnabled(false);
                        uploadBitmap(pic);
                    }
                    else
                    {
                        Snackbar.make(rootlayout, "Please take a picture of the receipt.", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });


        return fragmentView;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            /*pic1 = (Bitmap) data.getExtras().get("data");
            picTest.setImageBitmap(pic1);*/
            this.grabImage(receiptPicView);
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
                            //Toast.makeText(getContext(), obj.getString("Message"), Toast.LENGTH_SHORT).show();
                            Snackbar.make(rootlayout, obj.getString("Message"), Snackbar.LENGTH_SHORT).show();
                            Log.d("UPLOAD IMAGE", obj.getString("Message"));
                            String imagename = "R" + getDate() + "D" + ((GlobalVariables) getActivity().getApplicationContext()).getDriverID() + "-" + ((GlobalVariables) getActivity().getApplicationContext()).getNumberOfReceipts();
                            updateDatabase(receiptAmount.getText().toString(), receiptReason.getText().toString(), imagename);
                        } catch (JSONException e) {
                            Log.d("uploadBitmap", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                String imagename = "R" + getDate() + "D" + ((GlobalVariables) getActivity().getApplicationContext()).getDriverID() + "-" + ((GlobalVariables) getActivity().getApplicationContext()).getNumberOfReceipts();
                params.put("file", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(getActivity().getApplicationContext()).add(volleyMultipartRequest);
    }

    private byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("DDMMyy");
        try {
            Date date = Calendar.getInstance().getTime();
            String dateTime = dateFormat.format(date);
            return dateTime;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void updateDatabase(String amount, String type, String imagename){
        String driverID = ((GlobalVariables) getActivity().getApplicationContext()).getDriverID();
        String imageUrl = "http://soc-web-liv-82.napier.ac.uk/admin/static/images/receipts/" + imagename + ".png";
        String url = "http://soc-web-liv-82.napier.ac.uk/api/receipts?Amount="+amount+"&Type="+type+"&Picture="+imageUrl+"&DriverID="+driverID;
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        receiptAmount.setText("");
                        receiptReason.setText("");
                        receiptPicView.setImageResource(R.drawable.picpreview);
                        uploadReceipt.setText("Upload Receipt");
                        uploadReceipt.setEnabled(true);
                        takePicture.setEnabled(true);
                        pic = null;
                        Log.d("RECEIPT DB", response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("RECEIPT DB ERROR", String.valueOf(error));
                    }
                }
        );

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(getActivity().getApplicationContext()).add(getRequest);
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
        context.getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = context.getContentResolver();
        try
        {
            Bitmap tempPic = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            int width = tempPic.getWidth(); int height = tempPic.getHeight();
            double scaleWidth =  0.3 * width;
            double scaleHeight = 0.3 * height;
            Bitmap resized = Bitmap. createScaledBitmap ( tempPic , (int) scaleWidth , (int) scaleHeight , true ) ;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.PNG, 100, out);
            pic = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
            imageView.setImageBitmap(pic);
        }
        catch (Exception e)
        {
            Log.d("ERROR", "Failed to load image", e);
        }
    }

}
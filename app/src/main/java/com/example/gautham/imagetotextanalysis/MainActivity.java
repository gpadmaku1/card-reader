package com.example.gautham.imagetotextanalysis;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {


    private static final int IMAGE_PERMISSION = 4 ;
    private static int IMAGE_CAPTURE_REQUEST = 1001;
    private static String mCurrentPhotoPath;
    private ProgressBar progressBar;
    private static final String CLOUD_VISION_API_KEY =  BuildConfig.API_KEY;
    private EditText obtainedText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get access to buttons and editTexts
        ImageButton cameraButton = (ImageButton) findViewById(R.id.cameraButton);

        Button submitButton = (Button) findViewById(R.id.submitButton);

        obtainedText = (EditText) findViewById(R.id.obtainedText);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        cameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                obtainedText.setText("");
                startCameraActivityIntent();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Data saved.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Starts the camera and requests permission to use the camera if permission doesn't exist
     *
     */
    public void startCameraActivityIntent(){
        //Required camera permission
        String[] permissions = {"android.permission.CAMERA"};
        //Intent to startCamera
        Intent startCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager
                .PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, permissions, IMAGE_PERMISSION);
        }
        else {
            if (startCameraIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = createImageFile();
                if(photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.FileProvider", photoFile);
                    Log.w("YEE", "URI path: " + photoURI.toString());

                    //For non bitmap full sized images use EXTRA_OUTPUT during Intent
                    startCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(startCameraIntent, IMAGE_CAPTURE_REQUEST);
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_CAPTURE_REQUEST && resultCode == RESULT_OK){
            String base64EncodedString = convertImageToBase64EncodedString();
            Log.w("YEE", base64EncodedString);
            JSONObject object = makePostJSONObject(base64EncodedString);
            callGoogleVisionAPI(object);
            deleteCapturedImage();
        }
    }


    /**
     * Creates and writes a new image to send in the post request to Google Vision API
     *
     * @return , The captured image file
     */
    private File createImageFile(){
        //Create image filename
        String imageFileName = "JPEG_00";

        //Access storage directory for photos and create temporary image file
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName,".jpg",storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Store file path for usage with intents
        assert image != null;
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.w("YEE", "Photo filepath: " + mCurrentPhotoPath);
        return image;
    }


    /**
     * Converts the captured image to a base 64 encoded string.
     * Images are typically sent as long encoded strings in networks instead of bits and bytes of data
     *
     * Convert file to byteArrayOutputStream then to ByteArray and directly to a base64 encoded string
     *
     * @return , The encoded String that represents the captured image
     */
    private String convertImageToBase64EncodedString() {
        File f = new File(mCurrentPhotoPath);
        String base64EncodedString;

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try{
            assert inputStream != null;
            while((bytesRead = inputStream.read(buffer)) != -1){
                output.write(buffer, 0, bytesRead);
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        byte[] bytes = output.toByteArray();
        base64EncodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
        return base64EncodedString;
    }


    /**
     * Creates the JSON object that is to be sent in the POST HTTP call to Google Vision API
     *
     * @param base64EncodedString, The string that represents the captured image data
     * @return The JSONObject to be sent in the POST call
     */
    private JSONObject makePostJSONObject(String base64EncodedString) {
        //ImageObject and FeaturesArrayObject go inside InnerJSONObject
        //ImageObject
        JSONObject imageObject = new JSONObject();
        try {
            imageObject.put("content", base64EncodedString);
        } catch (JSONException e){
            e.printStackTrace();
        }

        JSONObject type = new JSONObject();
        try{
            type.put("type","DOCUMENT_TEXT_DETECTION");
        }catch (JSONException e){
            e.printStackTrace();
        }

        //FeaturesArrayObject
        JSONArray featuresArray = new JSONArray();
        featuresArray.put(type);

        //InnerJSONObject
        JSONObject innerJSONObject = new JSONObject();
        try {
            innerJSONObject.put("image", imageObject);
            innerJSONObject.put("features", featuresArray);
        } catch (JSONException e){
            e.printStackTrace();
        }

        //InnerJSONObject goes inside RequestsArray
        JSONArray requestsArray = new JSONArray();
        requestsArray.put(innerJSONObject);

        //RequestsArray goes inside MainObject
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("requests", requestsArray);
        } catch(JSONException e){
            e.printStackTrace();
        }

        return mainObject;
    }


    /**
     * Volley class to make HTTP Post Requests to Google Cloud Vision API
     *
     * @param object, The POST request JSON body
     */
    private void callGoogleVisionAPI(JSONObject object) {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://vision.googleapis.com/v1/images:annotate?key=" + CLOUD_VISION_API_KEY;
        JsonObjectRequest postRequest = new JsonObjectRequest(url, object,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        progressBar.setVisibility(View.GONE);
                        String bCardText = getRelevantString(response);
                        bCardText = bCardText.replace("\n"," ");
                        obtainedText.setText(bCardText);
                        Log.d("Response", bCardText);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Network error. Processing failed", Toast.LENGTH_LONG).show();
                        Log.w("Error.Response", error.toString());
                    }
                }
        );
        requestQueue.add(postRequest);
    }


    /**
     * Gets the text from the returned JSONObject
     *
     * @param response, The JSONObject response send by the API call
     * @return The relevant String to extract the account number from
     */
    String getRelevantString(JSONObject response) {
        JSONArray responses = null;
        try {
            responses = response.getJSONArray("responses");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject textAnnotation = null;
        try {
            assert responses != null;
            textAnnotation = responses.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray arr = null;
        try {
            assert textAnnotation != null;
            arr = textAnnotation.getJSONArray("textAnnotations");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String finalString = null;
        try {
            finalString = null;
            if (arr != null) {
                JSONObject object = arr.getJSONObject(0);
                finalString = object.get("description").toString();
            }
            //Log.w("YEE", finalString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return finalString;
    }


    /**
     * Method to delete the image after base64 encoded string has been obtained from it
     *
     * Avoids storing images that are unnecessary after use
     */
    private void deleteCapturedImage() {
        File fileToBeDeleted = new File(mCurrentPhotoPath);
        if(fileToBeDeleted.exists()){
            if(fileToBeDeleted.delete()){
                Log.w("YEE", "File Deleted: " + mCurrentPhotoPath);
            } else {
                Log.w("YEE", "File Not Deleted " + mCurrentPhotoPath);
            }
        }
    }


}

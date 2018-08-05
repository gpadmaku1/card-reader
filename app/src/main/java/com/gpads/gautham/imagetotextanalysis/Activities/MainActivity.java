package com.gpads.gautham.imagetotextanalysis.Activities;

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

import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.gpads.gautham.imagetotextanalysis.R;
import com.gpads.gautham.imagetotextanalysis.Tools.ConstructJSON;
import com.gpads.gautham.imagetotextanalysis.Tools.OkHttpNetworking;
import com.gpads.gautham.imagetotextanalysis.Tools.VolleyNetworking;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_PERMISSION = 4 ;
    private static final int IMAGE_CAPTURE_REQUEST = 1001;

    private static final String TAG = "MainActivity";
    private static final String INTENT_PHONE_NUMBER = "phoneNumber";
    private static final String INTENT_NAME = "name";
    private static final String INTENT_EMAIL = "email";

    private ProgressBar progressBar;

    private EditText obtainedText;

    private String mCurrentPhotoPath;
    private String phoneNumber;
    private String contactName;
    private String contactEmail;

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
                String results = obtainedText.getText().toString().trim();
                ArrayList<String> phoneNumbers = parseResults(results);

                if(phoneNumbers == null){
                    phoneNumber = "Error";
                }else{
                    if(!phoneNumbers.isEmpty())
                        try {
                            phoneNumber = phoneNumbers.get(0);
                        }catch(IndexOutOfBoundsException e){
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "There is no text!", Toast.LENGTH_SHORT).show();
                        }
                }
                if(!results.isEmpty()) {
                    try {
                        contactName = parseName(results);
                        contactEmail = parseEmail(results);
                        Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
                        intent.putExtra(INTENT_PHONE_NUMBER, phoneNumber);
                        intent.putExtra(INTENT_NAME, contactName);
                        intent.putExtra(INTENT_EMAIL, contactEmail);
                        startActivity(intent);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_CAPTURE_REQUEST && resultCode == RESULT_OK){
            String base64EncodedString = convertImageToBase64EncodedString();
            ConstructJSON constructJSON = new ConstructJSON(base64EncodedString);
            JSONObject object = constructJSON.doInBackground();
            VolleyNetworking volleyNetworking = new VolleyNetworking(this, progressBar, obtainedText);
            volleyNetworking.callGoogleVisionAPI(object);
            deleteCapturedImage();
        }
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
                    Uri photoURI = FileProvider.getUriForFile(this, "com.gpads.android.FileProvider", photoFile);

                    //For non bitmap full sized images use EXTRA_OUTPUT during Intent
                    startCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(startCameraIntent, IMAGE_CAPTURE_REQUEST);
                }
            }
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
     * Method to delete the image after base64 encoded string has been obtained from it
     *
     * Avoids storing images that are unnecessary after use
     */
    private void deleteCapturedImage() {
        File fileToBeDeleted = new File(mCurrentPhotoPath);
        if(fileToBeDeleted.exists()){
            if(fileToBeDeleted.delete()){
                Log.w(TAG, "File Deleted: " + mCurrentPhotoPath);
            } else {
                Log.w(TAG, "File Not Deleted " + mCurrentPhotoPath);
            }
        }
    }


    /**
     * Parses phoneNumbers from a string using Google's libphonenumber library
     *
     * @param bCardText, The text obtained from the vision API processing
     * @return ArrayList of parsed phone numbers from the vision API processed text string
     */
    private ArrayList<String> parseResults(String bCardText) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Iterable<PhoneNumberMatch> numberMatches = phoneNumberUtil.findNumbers(bCardText, Locale.US.getCountry());
        ArrayList<String> data = new ArrayList<>();
        for(PhoneNumberMatch number : numberMatches){
            String s = number.rawString();
            data.add(s);
        }
        return data;
    }


    /**
     * Parses email from the string returned from Google Vision APi
     * @param results, String returned from Google Vision API
     * @return String that is the parsed email. Uses REGEX
     */
    private String parseEmail(String results) {
        Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(results);
        String parsedEmail = "Error";
        while (m.find()) {
            parsedEmail = m.group();
        }
        return parsedEmail;
    }


    /**
     * Parses name from the string returned from Google Vision APi
     * @param results, String returned from Google Vision API
     * @return String that is the parsed email. Picks first two strings from the param
     */
    private String parseName(String results) throws ExecutionException, InterruptedException {
        OkHttpNetworking okHttpNetworking = new OkHttpNetworking(results);
        return okHttpNetworking.execute().get();
    }

}
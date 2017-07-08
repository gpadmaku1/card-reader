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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_PERMISSION = 4 ;
    private static int IMAGE_CAPTURE_REQUEST = 1001;
    private static String mCurrentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get access to buttons and editTexts
        ImageButton cameraButton = (ImageButton) findViewById(R.id.cameraButton);
        Button submitButton = (Button) findViewById(R.id.submitButton);

        EditText nameText = (EditText) findViewById(R.id.nameText);
        EditText phoneText = (EditText) findViewById(R.id.phoneText);
        EditText emailText = (EditText) findViewById(R.id.emailText);

        cameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startCameraActivityIntent();
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


    /**
     * Creates and writes a new image to send in the post request to Google Vision API
     *
     * @return , The captured image file
     */
    private File createImageFile(){
        //Create image filename
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CANADA).format(new Date());
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
}

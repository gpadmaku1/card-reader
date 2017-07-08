package com.example.gautham.imagetotextanalysis;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    
    private static final int IMAGE_PERMISSION = 4 ;
    private static int IMAGE_CAPTURE_REQUEST = 1001;

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
                startActivityForResult(startCameraIntent, IMAGE_CAPTURE_REQUEST);
            }
        }
    }
}

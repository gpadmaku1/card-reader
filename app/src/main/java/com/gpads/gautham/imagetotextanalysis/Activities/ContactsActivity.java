package com.gpads.gautham.imagetotextanalysis.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.gpads.gautham.imagetotextanalysis.R;

public class ContactsActivity extends AppCompatActivity {

    EditText phoneText;
    EditText nameText;
    EditText emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        phoneText = (EditText) findViewById(R.id.phoneText);
        nameText = (EditText) findViewById(R.id.nameText);
        emailText = (EditText) findViewById(R.id.emailText);
        String y = getIntent().getStringExtra("phoneNumber");
        Log.w("YEE", "contacts activity: " + y);
        phoneText.setText(y);
    }
}

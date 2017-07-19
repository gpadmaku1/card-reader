package com.gpads.gautham.imagetotextanalysis.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.gpads.gautham.imagetotextanalysis.R;

public class ContactsActivity extends AppCompatActivity {

    private EditText phoneText;
    private EditText nameText;
    private EditText emailText;

    private String name;
    private String email;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        phoneText = (EditText) findViewById(R.id.phoneText);
        nameText = (EditText) findViewById(R.id.nameText);
        emailText = (EditText) findViewById(R.id.emailText);
        phone = getIntent().getStringExtra("phoneNumber");
        email = getIntent().getStringExtra("email");
        name = getIntent().getStringExtra("name");
        phoneText.setText(phone);
        nameText.setText(name);
        emailText.setText(email);
    }
}

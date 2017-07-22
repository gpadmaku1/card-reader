package com.gpads.gautham.imagetotextanalysis.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gpads.gautham.imagetotextanalysis.R;


public class ContactsActivity extends AppCompatActivity {


    private EditText phoneText;
    private EditText nameText;
    private EditText emailText;

    private static final int CONTACTS_PERMISSION = 5;

    private Button contactsButton;

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
        contactsButton = (Button) findViewById(R.id.contactBtn);

        try {
            phone = getIntent().getStringExtra("phoneNumber");
            email = getIntent().getStringExtra("email");
            name = getIntent().getStringExtra("name");

        } catch (NullPointerException e){
            e.printStackTrace();
            Toast.makeText(ContactsActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }

        phoneText.setText(phone);
        nameText.setText(name);
        emailText.setText(email);

        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startContactsActivityIntent();
            }
        });
    }


    /**
     * Starts the contacts intent and requests permission to write to contacts if permission doesn't exist
     */
    public void startContactsActivityIntent(){
        String[] permissions = {"android.permission.WRITE_CONTACTS"};
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager
                .PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, permissions, CONTACTS_PERMISSION);
        }
        else {
            if (intent.resolveActivity(getPackageManager()) != null) {
                intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
                intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
                startActivity(intent);
            }
        }
    }
}

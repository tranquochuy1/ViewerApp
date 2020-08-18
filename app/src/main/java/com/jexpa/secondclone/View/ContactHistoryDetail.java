/*
  ClassName: ContactHistoryDetail.java
  AppName: SecondClone
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class ContactHistoryDetail use to display a contact and add a call or message function or copy a phone number.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.View;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.jexpa.secondclone.Model.Contact;
import com.jexpa.secondclone.R;

public class ContactHistoryDetail extends AppCompatActivity implements View.OnClickListener {
    TextView txt_name_Contact_Detail, txt_Number_Contact_Detail, txt_MakeCall_Contact_Detail_History,
            txt_SendMessages_Contact_Detail_History, txt_CopyNumber_Contact_Detail_History;
    Intent intent_Contact;
    Contact contact;
    boolean testCall = false;
    private static final int EXTERNAL_STORAGE_PERMISSION_CALL_PHONE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_history_detail);
        if (ActivityCompat.checkSelfPermission(ContactHistoryDetail.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ContactHistoryDetail.this, new String[]{Manifest.permission.CALL_PHONE}, EXTERNAL_STORAGE_PERMISSION_CALL_PHONE);
        } else {
            testCall = true;
        }
        contact = (Contact) getIntent().getSerializableExtra("contact_Detail");
        setID();

    }

    private void setID() {
        txt_name_Contact_Detail = findViewById(R.id.txt_name_Contact_Detail);
        txt_Number_Contact_Detail = findViewById(R.id.txt_Number_Contact_Detail);
        txt_MakeCall_Contact_Detail_History = findViewById(R.id.txt_MakeCall_Contact_Detail_History);
        txt_SendMessages_Contact_Detail_History = findViewById(R.id.txt_SendMessager_Contact_Detail_History);
        txt_CopyNumber_Contact_Detail_History = findViewById(R.id.txt_CopyNumber_Contact_Detail_History);
        txt_MakeCall_Contact_Detail_History.setOnClickListener(this);
        txt_SendMessages_Contact_Detail_History.setOnClickListener(this);
        txt_CopyNumber_Contact_Detail_History.setOnClickListener(this);
        txt_name_Contact_Detail.setText(contact.getContact_Name());
        txt_Number_Contact_Detail.setText(contact.getPhone());


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {

            case EXTERNAL_STORAGE_PERMISSION_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                testCall = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_MakeCall_Contact_Detail_History: {

                if (testCall) {
                    MyApplication.getInstance().trackEvent("ContactHistory", "Call: " + contact.getContact_Name(), "" + contact.getContact_Name());
                    //intent_Contact = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "001"+call.getPhone_Number().trim()));
                    intent_Contact = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.getPhone()));
                    startActivity(intent_Contact);

                } else {
                    Toast.makeText(this, "Please accept previous call access!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.txt_SendMessager_Contact_Detail_History: {
                MyApplication.getInstance().trackEvent("ContactHistory", "Send SMS: " + contact.getContact_Name(), "" + contact.getContact_Name());
                intent_Contact = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + contact.getPhone()));
                intent_Contact.putExtra("", "");
                startActivity(intent_Contact);
                break;
            }
            case R.id.txt_CopyNumber_Contact_Detail_History: {
                copyToClipBoard(txt_Number_Contact_Detail.getText().toString() + "");
                break;
            }
        }
    }

    private void copyToClipBoard(String content) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(
                "text label", // What should I set for this "label"?
                content);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), "Saved to clip board", Toast.LENGTH_SHORT).show();
    }
}
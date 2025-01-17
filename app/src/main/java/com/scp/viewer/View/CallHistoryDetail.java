/*
  ClassName: CallHistoryDetail.java
  AppName: ViewerApp
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class CallHistoryDetail use to display detailed history of who has called, at which time, call minutes, incoming or outgoing calls.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scp.viewer.Model.Call;
import com.scp.viewer.R;
import com.r0adkll.slidr.Slidr;

import static com.scp.viewer.API.APIDatabase.checkValueStringT;
import static com.scp.viewer.API.APIDatabase.getTimeItem;
import static com.scp.viewer.API.APIMethod.shareContact;

public class CallHistoryDetail extends AppCompatActivity implements View.OnClickListener {
    TextView txt_Name_User_Call_Detail_History, txt_Time_Call_Detail_History,
            txt_Status_Call_Detail_History, txt_TimeCall_Call_Detail_History,
            txt_PhoneNumber_Call_Detail_History, txt_MakeCall_Call_Detail_History,
            txt_SendMessages_Call_Detail_History,txt_ShareCallContact;
    ImageView img_Make_Call;
    private Toolbar toolbar;
    boolean testCall = false;
    private static final int EXTERNAL_STORAGE_PERMISSION_CALL_PHONE = 10;
    private Call call;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history_detail);
        // activity swipe back
        Slidr.attach(this);

        call = (Call) getIntent().getSerializableExtra("Call_Detail");
        setID();
        if (ActivityCompat.checkSelfPermission(CallHistoryDetail.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CallHistoryDetail.this, new String[]{Manifest.permission.CALL_PHONE}, EXTERNAL_STORAGE_PERMISSION_CALL_PHONE);
        } else {
            testCall = true;
        }
        setEvent();
    }

    @SuppressLint("SetTextI18n")
    private void setEvent() {
        txt_Name_User_Call_Detail_History.setText(call.getContact_Name());
        String time_Call = getTimeItem(checkValueStringT(call.getClient_Call_Time()),null);
        txt_Time_Call_Detail_History.setText(time_Call);
        if (call.getDuration() == 0 && call.getDirection() == 1) {
            txt_Status_Call_Detail_History.setText("Missed call");
            txt_TimeCall_Call_Detail_History.setText("");
           // if (call.getDirection() == 1) {
                txt_PhoneNumber_Call_Detail_History.setText(call.getPhone_Number());
            /*} else if (call.getDirection() == 0) {
                txt_PhoneNumber_Call_Detail_History.setText(call.getPhone_Number_SIM());
            }*/
        } else {
            // incoming call or outgoing call
            if(call.getDirection() == 0 && call.getDuration() == 0)
            {
                txt_TimeCall_Call_Detail_History.setText( "00:00");
            }else {
                txt_TimeCall_Call_Detail_History.setText(formatSeconds(call.getDuration()));
            }

            if (call.getDirection() == 1) {
                txt_PhoneNumber_Call_Detail_History.setText(call.getPhone_Number());
                txt_Status_Call_Detail_History.setText("Incoming call");
            } else if (call.getDirection() == 0) {
                txt_PhoneNumber_Call_Detail_History.setText(call.getPhone_Number_SIM());
                txt_Status_Call_Detail_History.setText("Outgoing");
            }
        }

        txt_MakeCall_Call_Detail_History.setOnClickListener(this);
        txt_SendMessages_Call_Detail_History.setOnClickListener(this);
        txt_ShareCallContact.setOnClickListener(this);
        img_Make_Call.setOnClickListener(this);
    }

    public static String formatSeconds(int timeInSeconds)
    {
        int secondsLeft = timeInSeconds % 3600 % 60;
        int minutes = (int) Math.floor(timeInSeconds % 3600 / 60);
        int hours = (int) Math.floor(timeInSeconds / 3600);

        String HH = ((hours       < 10) ? "0" : "") + hours;
        String MM = ((minutes     < 10) ? "0" : "") + minutes;
        String SS = ((secondsLeft < 10) ? "0" : "") + secondsLeft;

        if(HH.equals("00"))
        {
            if(MM.equals("00"))
                return SS+"s";
            else
                return MM + ":" + SS+"s";
        }
        else {
            return HH + ":" + MM + ":" + SS;
        }
    }

    private void setID() {

        toolbar = findViewById(R.id.toolbar_Call_Detail);
        toolbar.setTitle(call.getContact_Name());
        toolbar.setBackgroundResource(R.drawable.custom_bg_shopp);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // txt_ShareCallContact
        txt_ShareCallContact = findViewById(R.id.txt_ShareCallContact);
        txt_Name_User_Call_Detail_History = findViewById(R.id.txt_Name_User_Call_Detail_History);
        txt_Time_Call_Detail_History = findViewById(R.id.txt_Time_Call_Detail_History);
        txt_Status_Call_Detail_History = findViewById(R.id.txt_Tatus_Call_Detail_History);
        txt_TimeCall_Call_Detail_History = findViewById(R.id.txt_TimeCall_Call_Detail_History);
        txt_PhoneNumber_Call_Detail_History = findViewById(R.id.txt_Phonenumber_Call_Detail_History);
        txt_MakeCall_Call_Detail_History = findViewById(R.id.txt_MakeCall_Call_Detail_History);
        txt_SendMessages_Call_Detail_History = findViewById(R.id.txt_SendMessager_Call_Detail_History);
        img_Make_Call = findViewById(R.id.img_Make_Call);
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
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private String testPhoneCall(Call callHistory) {
        String phoneNumber = "";
        if (callHistory.getDirection() == 1) {
            phoneNumber = callHistory.getPhone_Number();
        } else if (callHistory.getDirection() == 0) {
            phoneNumber = callHistory.getPhone_Number_SIM();
        }
        return phoneNumber;
    }

    @Override
    public void onClick(View view) {
        Intent intent_Call;
        switch (view.getId()) {
            case R.id.txt_MakeCall_Call_Detail_History:
            case R.id.img_Make_Call: {
                if (testCall) {
                   // MyApplication.getInstance().trackEvent("CallHistory", "Call: " + call.getContact_Name(), "" + call.getContact_Name());
                    intent_Call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + testPhoneCall(call).trim()));
                    startActivity(intent_Call);
                } else {
                    Toast.makeText(this, "Please accept previous call access!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.txt_SendMessager_Call_Detail_History: {
                //MyApplication.getInstance().trackEvent("CallHistory", "Send SMS: " + call.getContact_Name(), "" + call.getContact_Name());
                intent_Call = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + testPhoneCall(call).trim()));
                intent_Call.putExtra("", "");
                startActivity(intent_Call);
                break;
            }
            case R.id.txt_ShareCallContact:
            {
                String phoneNumber = "0";
                if(call.getPhone_Number().equals("0"))
                    phoneNumber = call.getPhone_Number_SIM();
                else
                    phoneNumber = call.getPhone_Number();
                shareContact(getApplicationContext(), call.getContact_Name(), phoneNumber);
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            super.onBackPressed();

        }
        return true;
    }
}

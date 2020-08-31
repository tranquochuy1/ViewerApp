/*
  ClassName: AdapterDevice.java
  @Project: SecondClone
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: class AdapterDevice used to customize the adapter for the RecyclerView of the "ManagementDevice.class"
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jexpa.secondclone.API.APIGetTotalItemOfFeature;
import com.jexpa.secondclone.Model.Feature;
import com.jexpa.secondclone.Model.Table;
import com.jexpa.secondclone.R;
import com.jexpa.secondclone.View.ApplicationUsageHistory;
import com.jexpa.secondclone.View.CallHistory;
import com.jexpa.secondclone.View.ContactHistory;
import com.jexpa.secondclone.View.HistoryLocation;
import com.jexpa.secondclone.View.NotesHistory;
import com.jexpa.secondclone.View.PhoneCallRecordHistory;
import com.jexpa.secondclone.View.PhotoHistory;
import com.jexpa.secondclone.View.SMSHistory;
import com.jexpa.secondclone.View.URLHistory;
import java.util.ArrayList;

import static com.jexpa.secondclone.API.APIURL.isConnected;
import static com.jexpa.secondclone.API.Global.SMS_BBM_TYPE;
import static com.jexpa.secondclone.API.Global.SMS_DEFAULT_TYPE;
import static com.jexpa.secondclone.API.Global.SMS_FACEBOOK_TYPE;
import static com.jexpa.secondclone.API.Global.SMS_HANGOUTS_TYPE;
import static com.jexpa.secondclone.API.Global.SMS_KIK_TYPE;
import static com.jexpa.secondclone.API.Global.SMS_LINE_TYPE;
import static com.jexpa.secondclone.API.Global.SMS_SKYPE_TYPE;
import static com.jexpa.secondclone.API.Global.SMS_VIBER_TYPE;
import static com.jexpa.secondclone.API.Global.SMS_WHATSAPP_TYPE;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_BBM;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_FACEBOOK;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_HANGOUTS;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_KIK;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_LINE;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_SKYPE;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_SMS;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_VIBER;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_WHATSAPP;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.TABLE_GET_BBM;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.TABLE_GET_FACEBOOK;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.TABLE_GET_HANGOUTS;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.TABLE_GET_KIK;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.TABLE_GET_LINE;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.TABLE_GET_SKYPE;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.TABLE_GET_SMS;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.TABLE_GET_VIBER;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.TABLE_GET_WHATSAPP;

public class AdapterFeatureDashboard extends RecyclerView.Adapter<AdapterFeatureDashboard.ViewHolder> {
    private ArrayList<Feature> featureList;
    private Activity context;
    private Table table;

    //  constructor three parameters
    public AdapterFeatureDashboard(ArrayList<Feature> featureList, Activity context, Table table) {
        this.featureList = featureList;
        this.context = context;
        this.table = table;
        Log.d("dsdsd", "tabe = "+ table.getDevice_ID());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View view = layoutInflater.inflate(R.layout.item_feature_dashboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Feature feature = featureList.get(position);
        holder.txtFeatureName.setText(feature.getFeatureName());
        holder.imgFeature.setImageResource(feature.getImage());

        if(isConnected(context))
        {
            if(!feature.getFunctionName().isEmpty())
            {
                if(feature.getFunctionName().equals("GetSMSByDateTime"))
                {
                    new APIGetTotalItemOfFeature.contactAsyncTask(feature.getFunctionName(),table.getDevice_ID(),context,getSMSType(feature.getFeatureName(),context), holder.txt_total_number).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
                }
                else
                    new APIGetTotalItemOfFeature.contactAsyncTask(feature.getFunctionName(),table.getDevice_ID(),context, holder.txt_total_number).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
            }
            else {
                holder.txt_total_number.setVisibility(View.GONE);
            }
        }
    }

    // Count the number of elements in deviceList
    @Override
    public int getItemCount() {
        return featureList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtFeatureName, txt_total_number;
        ImageView imgFeature;
        LinearLayout ln_Feature;
        View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            txtFeatureName = itemView.findViewById(R.id.txt_Name_Feature);
            txt_total_number = itemView.findViewById(R.id.txt_total_number);
            txt_total_number.setVisibility(View.GONE);
            imgFeature = itemView.findViewById(R.id.img_feature);
            ln_Feature = itemView.findViewById(R.id.ln_Feature);
            ln_Feature.setOnClickListener(this);
        }

        //  The click event method of items Click in the RecyclerView
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION)
            {
                if(!featureList.get(position).getFeatureName().isEmpty())
                {
                    if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.LOCATION_HISTORY)))
                    {
                        setIntentDefault(context,table,"table",HistoryLocation.class);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.SMS_HISTORY)))
                    {
                        setIntentForMessage(context,table, TABLE_GET_SMS,COLUMN_LAST_SMS, getSMSType(featureList.get(position).getFeatureName(),context));
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.CALL_HISTORY)))
                    {
                        setIntentDefault(context,table,"call",CallHistory.class);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.URL_HISTORY)))
                    {
                        setIntentDefault(context,table,"tableURL",URLHistory.class);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.CONTACT_HISTORY)))
                    {
                        setIntentDefault(context,table,"tableContact",ContactHistory.class);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.PHOTO_HISTORY)))
                    {
                        setIntentDefault(context,table,"tablePhoto",PhotoHistory.class);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.PHONE_CALL_RECORDING)))
                    {
                        setIntentForCallRecording(context,table, "GetPhoneRecording");
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.AMBIENT_VOICE_RECORDING)))
                    {
                        setIntentForCallRecording(context,table, "GetAmbients");
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.WHATSAPP_HISTORY)))
                    {
                        setIntentForMessage(context,table, TABLE_GET_WHATSAPP,COLUMN_LAST_WHATSAPP, getSMSType(featureList.get(position).getFeatureName(),context));
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.APPLICATION_USAGE)))
                    {
                        setIntentDefault(context,table,"tableApplication", ApplicationUsageHistory.class);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.VIBER_HISTORY)))
                    {
                        setIntentForMessage(context,table, TABLE_GET_VIBER,COLUMN_LAST_VIBER, getSMSType(featureList.get(position).getFeatureName(),context));
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.FACEBOOK_HISTORY)))
                    {
                        setIntentForMessage(context,table, TABLE_GET_FACEBOOK,COLUMN_LAST_FACEBOOK, getSMSType(featureList.get(position).getFeatureName(),context));
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.SKYPE_HISTORY)))
                    {
                        setIntentForMessage(context,table, TABLE_GET_SKYPE,COLUMN_LAST_SKYPE, getSMSType(featureList.get(position).getFeatureName(),context));
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.NOTES_HISTORY)))
                    {
                        setIntentDefault(context,table,"tableNotes", NotesHistory.class);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.HANGOUTS_HISTORY)))
                    {
                        setIntentForMessage(context,table, TABLE_GET_HANGOUTS,COLUMN_LAST_HANGOUTS, getSMSType(featureList.get(position).getFeatureName(),context));
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.BBM_HISTORY)))
                    {
                        setIntentForMessage(context,table, TABLE_GET_BBM,COLUMN_LAST_BBM, getSMSType(featureList.get(position).getFeatureName(),context));
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.LINE_HISTORY)))
                    {
                        setIntentForMessage(context,table, TABLE_GET_LINE,COLUMN_LAST_LINE, getSMSType(featureList.get(position).getFeatureName(),context));
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.KIK_HISTORY)))
                    {
                        setIntentForMessage(context,table, TABLE_GET_KIK,COLUMN_LAST_KIK, getSMSType(featureList.get(position).getFeatureName(),context));
                    }
                }
            }
        }
    }

    /**
     * getSMSType is the method of finding out the type of message for which you want to get the total item.
     */
    public static String getSMSType(String nameFeature, Context context)
    {

       if(nameFeature.equals(context.getResources().getString(R.string.SMS_HISTORY)))
        {
            return SMS_DEFAULT_TYPE;
        }
        else if(nameFeature.equals(context.getResources().getString(R.string.WHATSAPP_HISTORY)))
        {
            return SMS_WHATSAPP_TYPE;
        }
        else if(nameFeature.equals(context.getResources().getString(R.string.VIBER_HISTORY)))
        {
            return SMS_VIBER_TYPE;
        }
        else if(nameFeature.equals(context.getResources().getString(R.string.FACEBOOK_HISTORY)))
        {
            return SMS_FACEBOOK_TYPE;
        }
        else if(nameFeature.equals(context.getResources().getString(R.string.SKYPE_HISTORY)))
        {
            return SMS_SKYPE_TYPE;
        }
        else if(nameFeature.equals(context.getResources().getString(R.string.HANGOUTS_HISTORY)))
        {
            return SMS_HANGOUTS_TYPE;
        }
        else if(nameFeature.equals(context.getResources().getString(R.string.BBM_HISTORY)))
        {
            return SMS_BBM_TYPE;
        }
        else if(nameFeature.equals(context.getResources().getString(R.string.LINE_HISTORY)))
        {
            return SMS_LINE_TYPE;
        }
        else if(nameFeature.equals(context.getResources().getString(R.string.KIK_HISTORY)))
        {
            return SMS_KIK_TYPE;
        }
        else {
           return SMS_DEFAULT_TYPE;
       }
    }

    /**
     * setIntentForMessage this is a method that supports moving the screen to SMSHistory activity for each type of application.
     */
    private void setIntentForMessage(Context context,Table table, String tableName, String columnName, String type)
    {
        Intent intent = new Intent(context, SMSHistory.class);
        intent.putExtra("table_SMS", table);
        intent.putExtra("nameTable", tableName + "");
        intent.putExtra("style", type);
        intent.putExtra("nameFeature", columnName + "");
        context.startActivity(intent);
    }

    /**
     * setIntentForMessage this is a method that supports moving the screen to SMSHistory activity for each type of application.
     */
    private void setIntentForCallRecording(Context context,Table table, String nameFeature)
    {
        Intent intent = new Intent(context, PhoneCallRecordHistory.class);
        intent.putExtra("tablePhoneCallRecord", table);
        intent.putExtra("nameFeature", nameFeature + "");
        context.startActivity(intent);
    }

    /**
     * setIntentDefault this is a method that supports moving the screen to cls activity for each type of application.
     */
    private void setIntentDefault(Context context,Table table,String tableName,  Class<?> cls)
    {
        Intent intent = new Intent(context, cls);
        intent.putExtra(tableName, table);
        context.startActivity(intent);
    }
}

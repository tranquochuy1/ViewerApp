/*
  ClassName: DatabasePhotos.java
  Project: ViewerApp
 author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabasePhotos is used to create, add, modify, delete databases, save
  the history Photo from the server, use the "PhotoHistory.class" and "PhotoHistoryDetail.class".
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */
package com.scp.viewer.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.scp.viewer.Model.Photo;
import java.util.ArrayList;
import java.util.List;
import static com.scp.viewer.API.APIDatabase.API_Add_Database;
import static com.scp.viewer.API.Global.TAG;
import static com.scp.viewer.Database.DatabaseContact.checkItemExist;
import static com.scp.viewer.Database.DatabaseHelper.getInstance;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_DEVICE_ID;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_ID;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_ROW_INDEX;
import static com.scp.viewer.Database.Entity.PhotoHistoryEntity.COLUMN_CAPTION_PHOTO;
import static com.scp.viewer.Database.Entity.PhotoHistoryEntity.COLUMN_CDN_URL_PHOTO;
import static com.scp.viewer.Database.Entity.PhotoHistoryEntity.COLUMN_CLIENT_CAPTURED_DATE_PHOTO;
import static com.scp.viewer.Database.Entity.PhotoHistoryEntity.COLUMN_CREATED_DATE_PHOTO;
import static com.scp.viewer.Database.Entity.PhotoHistoryEntity.COLUMN_EXT_PHOTO;
import static com.scp.viewer.Database.Entity.PhotoHistoryEntity.COLUMN_FILE_NAME_PHOTO;
import static com.scp.viewer.Database.Entity.PhotoHistoryEntity.COLUMN_ISLOADED_PHOTO;
import static com.scp.viewer.Database.Entity.PhotoHistoryEntity.COLUMN_MEDIA_URL_PHOTO;
import static com.scp.viewer.Database.Entity.PhotoHistoryEntity.TABLE_PHOTO_HISTORY;

public class DatabasePhotos {

    private DatabaseHelper database;

    public DatabasePhotos(Context context) {
        this.database = getInstance(context);
        if(!database.checkTableExist(TABLE_PHOTO_HISTORY))
            createTable();
    }


    public void createTable() {


        Log.i(TAG, "DatabaseCall.onCreate ... " + TABLE_PHOTO_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_PHOTO_HISTORY + "(" + COLUMN_ROW_INDEX + " LONG ," + COLUMN_ID + " LONG,"
                + COLUMN_ISLOADED_PHOTO + " INTEGER," + COLUMN_DEVICE_ID + " TEXT," + COLUMN_CLIENT_CAPTURED_DATE_PHOTO + " TEXT," + COLUMN_CAPTION_PHOTO + " TEXT,"
                + COLUMN_FILE_NAME_PHOTO + " TEXT," + COLUMN_EXT_PHOTO + " TEXT," + COLUMN_MEDIA_URL_PHOTO + " TEXT," +
                COLUMN_CREATED_DATE_PHOTO + " TEXT," + COLUMN_CDN_URL_PHOTO + " TEXT" + ")";
        database.getWritableDatabase().execSQL(scriptTable);
    }


    public void addDevice_Photos_Fast(List<Photo> photos) {

        database.getWritableDatabase().beginTransaction();
        Log.i("addPhoto", "dataURLPhotos add: " + photos.get(0).getID());
        try {
            for (int i = 0; i < photos.size(); i++) {
                Log.d("PhotoHistory"," Add Photo = "+  photos.get(i).getDevice_ID());
                if(!checkItemExist(database.getWritableDatabase(), TABLE_PHOTO_HISTORY, COLUMN_DEVICE_ID,
                        photos.get(i).getDevice_ID(), COLUMN_ID, photos.get(i).getID()))
                {
                    ContentValues contentValues1 = API_Add_Database(photos.get(i),false);
                    // Insert a row of data into the table.
                    database.getWritableDatabase().insert(TABLE_PHOTO_HISTORY, null, contentValues1);
                    Log.d("PhotoHistory"," Add Photo = "+  contentValues1.toString());
                }
            }
            database.getWritableDatabase().setTransactionSuccessful();
        } finally {
            database.getWritableDatabase().endTransaction();
        }
    }

    public List<Photo> getAll_Photo_ID_History(String deviceID, int offSet, boolean checkIsSaved, long numberLoad) {

        Log.i(TAG, "DatabasePhotos.getAll_Photo... " + TABLE_PHOTO_HISTORY);

        List<Photo> photos_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PHOTO_HISTORY +" WHERE Device_ID = '"+deviceID+ "' ORDER BY " + COLUMN_CLIENT_CAPTURED_DATE_PHOTO + " DESC LIMIT "+ numberLoad +" OFFSET "+offSet;
        //SQLiteDatabase database = this.getWritableDatabase();
        Log.d("PhotoHistory"," selectQuery = "+  selectQuery);
        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID)).equals(deviceID)) {

                    if(checkGetPhotoToApp(checkIsSaved, cursor.getInt(cursor.getColumnIndex(COLUMN_ISLOADED_PHOTO))))
                    {
                        Log.d("PhotoHistory"," COLUMN_FILE_NAME_PHOTO = "+  cursor.getString(cursor.getColumnIndex(COLUMN_FILE_NAME_PHOTO)));
                        Photo photo = new Photo();
                        photo.setRowIndex(cursor.getLong(cursor.getColumnIndex(COLUMN_ROW_INDEX)));
                        photo.setID(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                        photo.setIsLoaded(cursor.getInt(cursor.getColumnIndex(COLUMN_ISLOADED_PHOTO)));
                        photo.setDevice_ID(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID)));
                        photo.setClient_Captured_Date(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_CAPTURED_DATE_PHOTO)));
                        photo.setCaption(cursor.getString(cursor.getColumnIndex(COLUMN_CAPTION_PHOTO)));
                        photo.setFile_Name(cursor.getString(cursor.getColumnIndex(COLUMN_FILE_NAME_PHOTO)));
                        photo.setExt(cursor.getString(cursor.getColumnIndex(COLUMN_EXT_PHOTO)));
                        photo.setMedia_URL(cursor.getString(cursor.getColumnIndex(COLUMN_MEDIA_URL_PHOTO)));
                        photo.setCreated_Date(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE_PHOTO)));
                        photo.setCDN_URL(cursor.getString(cursor.getColumnIndex(COLUMN_CDN_URL_PHOTO)));
                        // photo.setCheckDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE_CHECK)));
                        // Add in List.
                        photos_List.add(photo);
                        Log.d("PhotoHistory"," Add Photo = "+  photo.getFile_Name());
                    }

                }
            } while (cursor.moveToNext());
        }
        // return note list
        database.close();
        return photos_List;
    }

    /**
     * checkGetPhotoToApp: This is the method of checking whether to get this image from SQlite or not.
     */
    private boolean checkGetPhotoToApp(boolean checkIsSaved, int isLoaded)
    {
        boolean checkAddPhoto;
        if(checkIsSaved)
        {
            if(isLoaded == 1)
            {
                checkAddPhoto = true;
            }
            else
            {
                checkAddPhoto = false;
            }
        }
        else {
            checkAddPhoto = true;
        }
        return checkAddPhoto;
    }

    public void update_Photos_History(int value, String nameDeviceID, long photoID) {

        // contentValues1 receives the value from the method API_Add_Database()
        Log.d("isLoading = ", COLUMN_ISLOADED_PHOTO + "=" + value + "");
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(COLUMN_ISLOADED_PHOTO, value);
        database.getWritableDatabase().update(TABLE_PHOTO_HISTORY, contentValues1, COLUMN_DEVICE_ID + " = ?" + " AND " + COLUMN_ID + "=?",
                new String[]{String.valueOf(nameDeviceID), String.valueOf(photoID)});
        //  Close the database connection.
        database.close();
    }

    public int getPhotoCount(String deviceID) {
        Log.i(TAG, "DatabasePhotos.getPhotoCount ... " + TABLE_PHOTO_HISTORY);

        Cursor cursor = database.getWritableDatabase().query(TABLE_PHOTO_HISTORY, new String[]{COLUMN_DEVICE_ID
                }, COLUMN_DEVICE_ID + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void delete_Photos_History(Photo photo) {
        Log.i("deletePhoto", "DatabasePhotos.deletePhoto ... " + photo.getID() + "== " + photo.getCaption());
        database.getWritableDatabase().delete(TABLE_PHOTO_HISTORY, COLUMN_ID + " = ?",
                new String[]{String.valueOf(photo.getID())});
        database.close();
    }

    public void delete_Photos_History_File(long id) {
        Log.i("deletePhoto", "DatabasePhotos.deletePhoto ... " + id);
        database.getWritableDatabase().delete(TABLE_PHOTO_HISTORY, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        database.close();
    }
}

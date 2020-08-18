/*
  ClassName: NotesHistory.java
  AppName: SecondClone
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class NotesHistory used to display the Notes history list from the sever on the RecyclerView of the class.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.View;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.jexpa.secondclone.API.APIMethod;
import com.jexpa.secondclone.API.APIURL;
import com.jexpa.secondclone.Adapter.AdapterNoteHistory;
import com.jexpa.secondclone.Database.DatabaseNotes;
import com.jexpa.secondclone.Database.DatabaseLastUpdate;
import com.jexpa.secondclone.Model.Notes;
import com.jexpa.secondclone.Model.Table;
import com.jexpa.secondclone.R;
//import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import static com.jexpa.secondclone.API.APIDatabase.getThread;
import static com.jexpa.secondclone.API.APIMethod.getProgressDialog;
import static com.jexpa.secondclone.API.APIURL.deviceObject;
import static com.jexpa.secondclone.API.APIURL.bodyLogin;
import static com.jexpa.secondclone.API.APIURL.getTimeNow;
import static com.jexpa.secondclone.API.APIURL.isConnected;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_NOTES;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;

public class NotesHistory extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    List<Notes> mData = new ArrayList<>();
    List<Notes> notesListAdd = new ArrayList<>();
    // action mode
    public static boolean isInActionMode = false;
    public static ArrayList<Notes> selectionList = new ArrayList<>();
    private DatabaseNotes database_notes;
    private DatabaseLastUpdate database_last_update;
    private Table table;
    private TextView txt_No_Data_Notes;
    //private Logger logger;
    private String max_Date = "";
    private String min_Time = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_history);
        toolbar = findViewById(R.id.toolbar_Notes_History);
        toolbar.setTitle("  " + MyApplication.getResourcses().getString(R.string.NOTES_HISTORY));
        toolbar.setLogo(R.drawable.notes_store_white);
        toolbar.setBackgroundResource(R.drawable.custombgshopp);
        setSupportActionBar(toolbar);
        database_notes = new DatabaseNotes(this);
        database_last_update = new DatabaseLastUpdate(this);
        //logger =  Log4jHelper.getLogger("NotesHistory.class");
        table = (Table) getIntent().getSerializableExtra("tableNotes");
        // show dialog Loading...
        getProgressDialog(MyApplication.getResourcses().getString(R.string.Loading)+"...",this);
        txt_No_Data_Notes = findViewById(R.id.txt_No_Data_Notes);
        txt_No_Data_Notes.setVisibility(View.GONE);
        // recyclerView
        mRecyclerView = findViewById(R.id.rcl_Notes_History);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getLocationInfo();

        // adapter
        mAdapter = new AdapterNoteHistory(this, (ArrayList<Notes>) mData);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void getLocationInfo() {
        //if there is a network call method
        //logger.debug("internet = "+isConnected(this)+"\n==================End!");
        if (isConnected(this)) {
            new ContactAsyncTask().execute();
        } else {
            Toast.makeText(this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
            //int i= databaseDevice.getDeviceCount();
            int i = database_notes.get_NotesCount_DeviceID(table.getDevice_ID());
            if (i == 0) {
                txt_No_Data_Notes.setVisibility(View.VISIBLE);
                txt_No_Data_Notes.setText(MyApplication.getResourcses().getString(R.string.NoData));
                getThread(APIMethod.progressDialog);
            } else {
                mData.clear();
                mData = database_notes.getAll_Notes_ID_History(table.getDevice_ID());
                mAdapter = new AdapterNoteHistory(this, (ArrayList<Notes>) mData);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                getThread(APIMethod.progressDialog);
            }
        }
    }

    // location get method from sever
    @SuppressLint("StaticFieldLeak")
    private class ContactAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            Log.d("Notes_Id", table.getDevice_ID() + "");
            // max_Date is get all the location from the min_date to the max_Date days
            min_Time = database_last_update.getLast_Time_Update(COLUMN_LAST_NOTES, TABLE_LAST_UPDATE, table.getDevice_ID()).substring(0, 10) + " 00:00:00";
            max_Date = getTimeNow().substring(0, 10) + " 23:59:59";
            Log.d("min_time", min_Time + "");
            String value = "<RequestParams Device_ID=\"" + table.getDevice_ID() + "\" Start=\"0\" Length=\"1000\" Min_Date=\"" + min_Time + "\" Max_Date=\"" + max_Date + "\"  />";
            String function = "GetNotes";
            return APIURL.POST(value, function);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                deviceObject(s);
                JSONObject jsonObj = new JSONObject(bodyLogin.getData());
                JSONArray GPSJson = jsonObj.getJSONArray("Table");
                if (GPSJson.length() != 0) {
                    List<Integer> listDateCheck = database_notes.getAll_Location_ID_History_Date(table.getDevice_ID(), min_Time.substring(0, 10));
                    int save;
                    Log.d("DateCheck", "NotesHistory = " + listDateCheck.size());
                    for (int i = 0; i < GPSJson.length(); i++) {
                        Gson gson = new Gson();
                        Notes notes = gson.fromJson(String.valueOf(GPSJson.get(i)), Notes.class);
                        mAdapter.notifyDataSetChanged();
                        Log.d("notes", notes.getRowIndex() + "");
                        //database_notes.addDevice_Application(notes);
                        save = 0;
                        if (listDateCheck.size() != 0) {
                            for (Integer listCheck : listDateCheck) {
                                if (notes.getID() == listCheck) {
                                    save = 1;
                                    break;
                                }
                            }
                            if (save == 0) {
                                notesListAdd.add(notes);
                            }
                        } else {
                            notesListAdd.add(notes);
                        }
                    }
                    if (notesListAdd.size() != 0) {
                        database_notes.addDevice_Notes(notesListAdd);
                    }
                }
                mData.clear();
                mData = database_notes.getAll_Notes_ID_History(table.getDevice_ID());
                mAdapter = new AdapterNoteHistory(NotesHistory.this, (ArrayList<Notes>) mData);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                if (mData.size() == 0) {
                    txt_No_Data_Notes.setVisibility(View.VISIBLE);
                    txt_No_Data_Notes.setText(MyApplication.getResourcses().getString(R.string.NoData));
                }
                database_last_update.update_Last_Time_Get_Update(TABLE_LAST_UPDATE, COLUMN_LAST_NOTES, max_Date, table.getDevice_ID());
                String min_Time1 = database_last_update.getLast_Time_Update(COLUMN_LAST_NOTES, TABLE_LAST_UPDATE, table.getDevice_ID());
                Log.d("min_time1", min_Time1 + "");
                // get Method getThread()
                //progressDialog.dismiss();
                getThread(APIMethod.progressDialog);
            } catch (JSONException e) {
                MyApplication.getInstance().trackException(e);
                e.printStackTrace();
                //logger.error("\n\n\n\tContactAsyncTask =="+ e+"\n================End");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void prepareToolbar(int position) {

        // prepare action mode
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_action_mode);
        isInActionMode = true;
        mAdapter.notifyDataSetChanged();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        prepareSelection(position);
    }

    public void prepareSelection(int position) {

        if (!selectionList.contains(mData.get(position))) {
            selectionList.add(mData.get(position));
        } else {
            selectionList.remove(mData.get(position));
        }

        updateViewCounter();
    }

    private void updateViewCounter() {
        int counter = selectionList.size();
        if (counter == 0) {
            clearActionMode();
            //toolbar.getMenu().getItem(0).setVisible(true);
        } else {
            //toolbar.getMenu().getItem(0).setVisible(false);
            toolbar.setTitle("  " + counter + " item selected");
        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.item_delete) {
            isInActionMode = false;
            if (isConnected(NotesHistory.this)) {
                getProgressDialog(MyApplication.getResourcses().getString(R.string.delete)+"...",this);
                new clear_Notes().execute();

            } else {
                Toast.makeText(this, getString(R.string.NoData)+"", Toast.LENGTH_SHORT).show();
                clearActionMode();
                mAdapter.notifyDataSetChanged();
            }


        } else if (item.getItemId() == android.R.id.home) {
            clearActionMode();
            mAdapter.notifyDataSetChanged();
        }

        return true;
    }


    @SuppressLint("StaticFieldLeak")
    private class clear_Notes extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Log.d("Notes_Id", table.getDevice_ID() + "");
            StringBuilder listID = new StringBuilder();
            //Toast.makeText(HistoryLocation.this, selectionList.get(0).getID()+"", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < selectionList.size(); i++) {
                if (i != selectionList.size() - 1) {
                    listID.append(selectionList.get(i).getID()).append(",");
                } else {

                    listID.append(selectionList.get(i).getID());
                }
            }
            String value = "<RequestParams Device_ID=\"" + table.getDevice_ID() + "\" List_ID=\"" + listID + "\" />";
            String function = "ClearMultiNote";
            return APIURL.POST(value, function);


        }

        @Override
        protected void onPostExecute(String s) {

            deviceObject(s);

            if (bodyLogin.getIsSuccess().equals("1") && bodyLogin.getResultId().equals("1")) {
                ((AdapterNoteHistory) mAdapter).removeData(selectionList);
                clearDataSQLite(selectionList);
                clearActionMode();
            } else {
                Toast.makeText(NotesHistory.this, bodyLogin.getDescription(), Toast.LENGTH_SHORT).show();
                clearActionMode();
            }


            // get Method getThread()
            //getThread(progressDialog);
            APIMethod.progressDialog.dismiss();

        }
    }

    // delete on SQLite
    public void clearDataSQLite(ArrayList<Notes> selectionList) {
        for (Notes notes : selectionList) {
            database_notes.delete_Contact_History(notes);
        }
    }

    // back toolbar home, clear List selectionList
    public void clearActionMode() {
        isInActionMode = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        toolbar.setTitle("  " + MyApplication.getResourcses().getString(R.string.NOTES_HISTORY));
        selectionList.clear();
    }

    // Check out the escape without the option will always exit,
    // the opposite will cancel the selection, not exit.
    @Override
    public void onBackPressed() {
        if (isInActionMode) {
            clearActionMode();
            mAdapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        mData.clear();
        mData = database_notes.getAll_Notes_ID_History(table.getDevice_ID());
        mAdapter = new AdapterNoteHistory(NotesHistory.this, (ArrayList<Notes>) mData);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (APIMethod.progressDialog != null && APIMethod.progressDialog.isShowing()) {
            APIMethod.progressDialog.dismiss();
        }
    }

}
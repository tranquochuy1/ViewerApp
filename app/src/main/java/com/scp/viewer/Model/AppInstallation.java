/*
  ClassName: AppInstallation.java
  Project: ViewerApp
  Author: Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description:
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Model;

import java.io.Serializable;

public class AppInstallation implements Serializable {

    private String Device_ID, Client_App_Time,
            App_Name, App_ID, Created_Date, Source;
    private int RowIndex, ID, From_Play_Store;

    public AppInstallation() {
    }

    public String getDevice_ID() {
        return Device_ID;
    }

    public void setDevice_ID(String device_ID) {
        Device_ID = device_ID;
    }

    public String getClient_App_Time() {
        return Client_App_Time;
    }

    public void setClient_App_Time(String client_App_Time) {
        Client_App_Time = client_App_Time;
    }

    public String getApp_Name() {
        return App_Name;
    }

    public void setApp_Name(String app_Name) {
        App_Name = app_Name;
    }

    public String getApp_ID() {
        return App_ID;
    }

    public void setApp_ID(String app_ID) {
        App_ID = app_ID;
    }

    public String getCreated_Date() {
        return Created_Date;
    }

    public void setCreated_Date(String created_Date) {
        Created_Date = created_Date;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public int getRowIndex() {
        return RowIndex;
    }

    public void setRowIndex(int rowIndex) {
        RowIndex = rowIndex;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getFrom_Play_Store() {
        return From_Play_Store;
    }

    public void setFrom_Play_Store(int from_Play_Store) {
        From_Play_Store = from_Play_Store;
    }
}
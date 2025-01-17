/*
  ClassName: MyApplication.java
  AppName: ViewerApp
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class MyApplication used to create log files and configure the project.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.View;

import android.app.Application;
import android.content.res.Resources;

public class MyApplication extends Application {
    private static Resources res;
    public static final String TAG = MyApplication.class
            .getSimpleName();

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        res = getResources();


    }

    public static Resources getResourcses() {
        return res;
    }

   /* public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }
*/
    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
  /*  public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }*/

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
   /* public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(
                            new StandardExceptionParser(this, null)
                                    .getDescription(Thread.currentThread().getName(), e))
                    .setFatal(false)
                    .build()
            );
        }
    }*/

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
  /*  public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }*/




}

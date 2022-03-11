package com.example.instalyticsjava;

import static java.lang.Thread.sleep;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.Nullable;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FetchData extends IntentService {
    private String TAG = "FetchData";

    private static FetchData instance = new FetchData();
    public static boolean isRunning = false;
    private UtilFunctions myUtils = new UtilFunctions();
    public FetchData() {
        super("FetchData");
        instance = this;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        DataSingelton.finishedDataInit = false;
        isRunning = true;
        myUtils.FetchAnalyticsData();
        return super.onStartCommand(intent, flags, startId);
    }

    public static void stopService() {
        if (isRunning && DataSingelton.finishedDataInit && DataSingelton.finishedProfileDataInit && DataSingelton.finishedDataPostsInit
        && DataSingelton.finishedLifetimeDataInit_audiencecountry && DataSingelton.finishedLifetimeDataInit_onlinefollowers){
            isRunning = false;
            instance.stopSelf();
        }
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            while(isRunning){
                //Log.d("FetchData", "service is running ...");
                Log.d(TAG, "onHandleIntent: "+isRunning+" && "+DataSingelton.finishedDataInit +" && "+ DataSingelton.finishedProfileDataInit +" && "+ DataSingelton.finishedDataPostsInit
                        +" && "+ DataSingelton.finishedLifetimeDataInit_audiencecountry +" && "+ DataSingelton.finishedLifetimeDataInit_onlinefollowers);
                Thread.sleep(10000);
            }

        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

}
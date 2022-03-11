package com.example.instalyticsjava;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.instalyticsjava.data.Data;
import com.example.instalyticsjava.dataposts.PostData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataSingelton {
    //with this one, the class is already initialised at lunch
    private static DataSingelton singleInstance = new DataSingelton();

    //for profile
    public static Map<String,Data> data;
    public static Map<String,Data> lifetimeData_onlineFollowers;
    public static Map<String,Data> lifetimeData_audienceCountry;
    public static String ig_username = null;
    public static String ig_name = null;
    public static String ig_profilePictureURL = null;
    public static Bitmap ig_BITMAPprofilePictureURL = null;

    //for posts
    public static List<String> ig_postIDs = new ArrayList<>();
    public static Map<String, PostData> ig_post_data = new LinkedHashMap<String, PostData>();       //hakka we have order by INSERTION !!!!
    public static Map<String, Map<String,Data>> ig_post_data_insights = new LinkedHashMap<String, Map<String,Data>>();      //UPDATE : nvm f linitialisation its not ordered...
    //                ID    , <metric,data>

    //control
    public static boolean finishedDataInit= false;
    public static boolean finishedDataPostsInit= false;
    public static boolean finishedLifetimeDataInit_onlinefollowers= false;
    public static boolean finishedLifetimeDataInit_audiencecountry= false;
    public static boolean finishedProfileDataInit= false;
    public static void ResetData(){
        data.clear();
        finishedDataInit = false;
        finishedDataPostsInit= false;
        finishedLifetimeDataInit_onlinefollowers = false;
        finishedLifetimeDataInit_audiencecountry= false;
        finishedProfileDataInit=false;
    }


    private DataSingelton() {
        finishedDataInit= false;
        finishedLifetimeDataInit_onlinefollowers= false;
        finishedLifetimeDataInit_audiencecountry= false;
        finishedProfileDataInit= false;
        data = new HashMap<>();
        Log.d("DataSingelton", "INITIALISED");
    }
    public static DataSingelton getSingleInstance() {
        return singleInstance;
    }



    private static String InstagramBusinessAccountID = null;
    public static void setInstagramBusinessAccountID(String instagramBusinessAccountID) {
        InstagramBusinessAccountID = instagramBusinessAccountID;
    }
    public static String getInstagramBusinessAccountID() {
        return InstagramBusinessAccountID;
    }


    @Override
    public String toString() {
        return "DataSingelton{" +
                "data=" + data +
                '}';
    }
}

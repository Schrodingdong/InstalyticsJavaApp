package com.example.instalyticsjava;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.instalyticsjava.data.Data;
import com.example.instalyticsjava.dataposts.PostData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataSingelton {
    //account info
    private static String InstagramBusinessAccountID = null;
    //for profile
    public static final List<String> profile_metrics = Arrays.asList("profile_views","reach","impressions","follower_count","website_clicks","email_contacts");
    public static Map<String,Data> data;
    public static Map<String,Data> lifetimeData_onlineFollowers;
    public static Map<String,Data> lifetimeData_audienceCountry;
    public static String ig_username = null;
    public static String ig_name = null;
    public static String ig_profilePictureURL = null;
    public static Bitmap ig_BITMAPprofilePictureURL = null;
    //for posts
    public static final List<String> post_metrics = Arrays.asList("caption","comments_count","like_count","media_type","media_product_type","permalink","timestamp");
    public static final List<String> post_insight_metrics = Arrays.asList("reach","impressions","engagement","saved","video_views");
    public static List<String> ig_postIDs = new ArrayList<>();
    public static List<String> top5Posts  = new ArrayList<>();
    public static Map<String, PostData> ig_post_data = new LinkedHashMap<String, PostData>();                           //for data
    public static Map<String, Map<String,Data>> ig_post_data_insights = new LinkedHashMap<String, Map<String,Data>>();  //for insights
    //                 ID   , <metric,data>
    //control booleans :
    public static boolean finishedDataInit= false;
    public static boolean finishedDataPostsInit= false;
    public static boolean finishedLifetimeDataInit_onlinefollowers= false;
    public static boolean finishedLifetimeDataInit_audiencecountry= false;
    public static boolean finishedProfileDataInit= false;


    //Singleton Pattern
    private static DataSingelton singleInstance = new DataSingelton();
    private DataSingelton() {
        finishedDataInit= false;
        finishedLifetimeDataInit_onlinefollowers= false;
        finishedLifetimeDataInit_audiencecountry= false;
        finishedProfileDataInit= false;

        data = new HashMap<>();
        Log.d("DataSingelton", "INITIALISED");
    }

    //_________________________________________ methods ____________________________________________
    //----------------------------------------------------------------------------------------------
    public static void ResetData(){
        data.clear();
        finishedDataInit = false;
        finishedDataPostsInit= false;
        finishedLifetimeDataInit_onlinefollowers = false;
        finishedLifetimeDataInit_audiencecountry= false;
        finishedProfileDataInit=false;
    }



    public static void setInstagramBusinessAccountID(String instagramBusinessAccountID) {
        InstagramBusinessAccountID = instagramBusinessAccountID;
    }
    public static DataSingelton getSingleInstance() {
        return singleInstance;
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

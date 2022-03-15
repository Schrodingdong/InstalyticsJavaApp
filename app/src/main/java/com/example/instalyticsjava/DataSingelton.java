package com.example.instalyticsjava;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

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
    private static final List<String> profile_fields= Arrays.asList("profile_picture_url","username","name","followers_count");
    private static final List<String> profile_insight_metrics = Arrays.asList("profile_views","reach","impressions","follower_count","website_clicks","email_contacts");
    private static final List<String> profile_insight_metrics_lifetime = Arrays.asList("online_followers","audience_country");
    private static Map<String,Data> data;
    private static Map<String,Data> lifetimeData_onlineFollowers;
    private static Map<String,Data> lifetimeData_audienceCountry;
    private static String ig_username = null;
    private static String ig_name = null;
    private static String ig_profilePictureURL = null;
    private static Bitmap ig_BITMAPprofilePictureURL = null;
    private static int followers_count = 0;
    //for posts
    private static final List<String> post_metrics = Arrays.asList("caption","comments_count","like_count","media_type","media_product_type","permalink","timestamp");
    private static final List<String> post_insight_metrics = Arrays.asList("reach","impressions","engagement","saved","video_views");
    private static List<String> ig_postIDs = new ArrayList<>();
    private static Map<String, PostData> ig_post_data = new LinkedHashMap<>();                           //for data
    private static Map<String, Map<String,Data>> ig_post_data_insights = new LinkedHashMap<>();  //for insights
    //                 ID   , <metric,data>
    //control booleans :
    private static boolean finishedDataInit= false;
    private static boolean finishedDataPostsInit= false;
    private static boolean finishedLifetimeDataInit_onlinefollowers= false;
    private static boolean finishedLifetimeDataInit_audiencecountry= false;
    private static boolean finishedProfileDataInit= false;


    //Singleton Pattern
    private static final DataSingelton singleInstance = new DataSingelton();
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

    @NonNull
    @Override
    public String toString() {
        return "DataSingelton{" +
                "data=" + data +
                '}';
    }


    //_________________________________________ setters ____________________________________________
    //----------------------------------------------------------------------------------------------

    public static void setData(Map<String, Data> data) {
        DataSingelton.data = data;
    }

    public static void setLifetimeData_onlineFollowers(Map<String, Data> lifetimeData_onlineFollowers) {
        DataSingelton.lifetimeData_onlineFollowers = lifetimeData_onlineFollowers;
    }

    public static void setLifetimeData_audienceCountry(Map<String, Data> lifetimeData_audienceCountry) {
        DataSingelton.lifetimeData_audienceCountry = lifetimeData_audienceCountry;
    }

    public static void setIg_username(String ig_username) {
        DataSingelton.ig_username = ig_username;
    }

    public static void setIg_name(String ig_name) {
        DataSingelton.ig_name = ig_name;
    }

    public static void setIg_profilePictureURL(String ig_profilePictureURL) {
        DataSingelton.ig_profilePictureURL = ig_profilePictureURL;
    }

    public static void setIg_BITMAPprofilePictureURL(Bitmap ig_BITMAPprofilePictureURL) {
        DataSingelton.ig_BITMAPprofilePictureURL = ig_BITMAPprofilePictureURL;
    }

    public static void setFollowers_count(int followers_count) {
        DataSingelton.followers_count = followers_count;
    }

    public static void setIg_postIDs(List<String> ig_postIDs) {
        DataSingelton.ig_postIDs = ig_postIDs;
    }

    public static void setIg_post_data(Map<String, PostData> ig_post_data) {
        DataSingelton.ig_post_data = ig_post_data;
    }

    public static void setIg_post_data_insights(Map<String, Map<String, Data>> ig_post_data_insights) {
        DataSingelton.ig_post_data_insights = ig_post_data_insights;
    }

    public static void setFinishedDataInit(boolean finishedDataInit) {
        DataSingelton.finishedDataInit = finishedDataInit;
    }

    public static void setFinishedDataPostsInit(boolean finishedDataPostsInit) {
        DataSingelton.finishedDataPostsInit = finishedDataPostsInit;
    }

    public static void setFinishedLifetimeDataInit_onlinefollowers(boolean finishedLifetimeDataInit_onlinefollowers) {
        DataSingelton.finishedLifetimeDataInit_onlinefollowers = finishedLifetimeDataInit_onlinefollowers;
    }

    public static void setFinishedLifetimeDataInit_audiencecountry(boolean finishedLifetimeDataInit_audiencecountry) {
        DataSingelton.finishedLifetimeDataInit_audiencecountry = finishedLifetimeDataInit_audiencecountry;
    }

    public static void setFinishedProfileDataInit(boolean finishedProfileDataInit) {
        DataSingelton.finishedProfileDataInit = finishedProfileDataInit;
    }

    //_________________________________________ getters ____________________________________________
    //----------------------------------------------------------------------------------------------

    public static List<String> getProfile_fields() {
        return profile_fields;
    }
    public static List<String> getProfile_insight_metrics() {
        return profile_insight_metrics;
    }
    public static List<String> getPost_metrics() {
        return post_metrics;
    }
    public static List<String> getPost_insight_metrics() {
        return post_insight_metrics;
    }
    public static List<String> getProfile_insight_metrics_lifetime() {
        return profile_insight_metrics_lifetime;
    }


    public static Map<String, Data> getData() {
        return data;
    }

    public static Map<String, Data> getLifetimeData_onlineFollowers() {
        return lifetimeData_onlineFollowers;
    }

    public static Map<String, Data> getLifetimeData_audienceCountry() {
        return lifetimeData_audienceCountry;
    }

    public static String getIg_username() {
        return ig_username;
    }

    public static String getIg_name() {
        return ig_name;
    }

    public static String getIg_profilePictureURL() {
        return ig_profilePictureURL;
    }

    public static Bitmap getIg_BITMAPprofilePictureURL() {
        return ig_BITMAPprofilePictureURL;
    }

    public static int getFollowers_count() {
        return followers_count;
    }

    public static List<String> getIg_postIDs() {
        return ig_postIDs;
    }

    public static Map<String, PostData> getIg_post_data() {
        return ig_post_data;
    }

    public static Map<String, Map<String, Data>> getIg_post_data_insights() {
        return ig_post_data_insights;
    }

    public static boolean isFinishedDataInit() {
        return finishedDataInit;
    }

    public static boolean isFinishedDataPostsInit() {
        return finishedDataPostsInit;
    }

    public static boolean isFinishedLifetimeDataInit_onlinefollowers() {
        return finishedLifetimeDataInit_onlinefollowers;
    }

    public static boolean isFinishedLifetimeDataInit_audiencecountry() {
        return finishedLifetimeDataInit_audiencecountry;
    }

    public static boolean isFinishedProfileDataInit() {
        return finishedProfileDataInit;
    }
}

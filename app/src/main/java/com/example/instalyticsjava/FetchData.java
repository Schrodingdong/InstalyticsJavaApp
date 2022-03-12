package com.example.instalyticsjava;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.instalyticsjava.data.Data;
import com.example.instalyticsjava.dataposts.PostData;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class FetchData extends IntentService {
    private final String TAG = "FetchData";

    private static FetchData instance = new FetchData();
    private static final int weeksOld = 2;
    public static boolean isRunning = false;
    public FetchData() {
        super("FetchData");
        instance = this;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        isRunning = true;
        FetchAnalyticsData();
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
                Log.d("FetchData", "service is running ...");
                Log.d(TAG, "onHandleIntent: "+isRunning+" && "+
                        DataSingelton.finishedDataInit +" && "+
                        DataSingelton.finishedProfileDataInit +" && "+
                        DataSingelton.finishedDataPostsInit +" && "+
                        DataSingelton.finishedLifetimeDataInit_audiencecountry +" && "+
                        DataSingelton.finishedLifetimeDataInit_onlinefollowers);
                Thread.sleep(2000);
            }
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }


    //_______________________________________ fetching _____________________________________________
    //----------------------------------------------------------------------------------------------
    public void FetchAnalyticsData() {
        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(@Nullable JSONObject jsonObject, @Nullable GraphResponse graphResponse) {
                        try {
                            //TODO for multiple accounts
                            String facebookPage_ID = getFacebookPageID(jsonObject);
                            GraphRequest getInstaId = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                                    facebookPage_ID,
                                    new GraphRequest.Callback() {
                                        @Override
                                        public void onCompleted(@NonNull GraphResponse graphResponse) {
                                            JSONObject JSONInstaID = graphResponse.getJSONObject();
                                            try {
                                                JSONObject jsonRelated_Pages = JSONInstaID.getJSONObject("instagram_business_account");
                                                DataSingelton.setInstagramBusinessAccountID(jsonRelated_Pages.get("id").toString());

                                                //get the analytics
                                                GetAnalytics(DataSingelton.getInstagramBusinessAccountID());
                                                GetAnalyticsLifetime_OnlineFollowers(DataSingelton.getInstagramBusinessAccountID());
                                                GetAnalyticsLifetime_AudienceCountry(DataSingelton.getInstagramBusinessAccountID());
                                                //for posts :
                                                GetPostIDs(DataSingelton.getInstagramBusinessAccountID());
                                                GetNameUsernameProfilePicture(DataSingelton.getInstagramBusinessAccountID());

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            Bundle bundle = new Bundle();
                            bundle.putString("fields","instagram_business_account");
                            getInstaId.setParameters(bundle);
                            getInstaId.executeAsync();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle bundle = new Bundle();
        bundle.putString("fields","accounts");
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();                                                                 //on a seprate thread in the background
    }

    @NonNull
    private String getFacebookPageID(@Nullable JSONObject jsonObject) throws JSONException {
        JSONObject accounts = jsonObject.getJSONObject("accounts");
        JSONArray related_Pages = accounts.getJSONArray("data");
        JSONObject InstaObject = related_Pages.getJSONObject(0);                                                //recheck had zmr
        String Insta_Id = InstaObject.getString("id");
        return Insta_Id;
    }
    private void GetAnalytics(String ID){
        //set a new request :
        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                ID+"/insights",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(@NonNull GraphResponse graphResponse) {
                        JSONObject BigData = graphResponse.getJSONObject();
                        if (BigData == null) return;
                        try {
                            //Data list li fiha kulchi : JSONArray retrievedData
                            CreateData(BigData.getJSONArray("data"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle bundle = new Bundle();
        bundle.putString("metric","profile_views,reach,impressions,follower_count,website_clicks,email_contacts");
        bundle.putString("period","day");

        long currentEpoch = System.currentTimeMillis() / 1000L;
        long weekEpoch = 604800;
        bundle.putString("since",Long.toString(currentEpoch-weekEpoch * weeksOld ));
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync(); //on a seprate thread in the background
    }
    private void GetAnalyticsLifetime_OnlineFollowers(String ID){
        //set a new request :
        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                ID+"/insights",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(@NonNull GraphResponse graphResponse) {
                        JSONObject BigData = graphResponse.getJSONObject();
                        try {
                            CreateLifetimeData_OnlineFollowers(BigData.getJSONArray("data"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle bundle = new Bundle();
        bundle.putString("metric","online_followers");
        bundle.putString("period","lifetime");
        long currentEpoch = System.currentTimeMillis() / 1000L;
        bundle.putString("since",Long.toString(currentEpoch-604800 ));

        graphRequest.setParameters(bundle);
        graphRequest.executeAsync(); //on a seprate thread in the background
    }
    private void GetAnalyticsLifetime_AudienceCountry(String ID){
        //set a new request :
        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                ID+"/insights",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(@NonNull GraphResponse graphResponse) {
                        JSONObject BigData = graphResponse.getJSONObject();
                        try {
                            CreateLifetimeData_AudienceCountry(BigData.getJSONArray("data"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle bundle = new Bundle();
        bundle.putString("metric","audience_country");
        bundle.putString("period","lifetime");
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync(); //on a seprate thread in the background
    }
    private void GetNameUsernameProfilePicture(String ID) {
        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                ID,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(@NonNull GraphResponse graphResponse) {
                        JSONObject smallData = graphResponse.getJSONObject();
                        if (smallData != null) CreateNameUsernameProfilePictureData(smallData);
                    }
                });
        Bundle bundle = new Bundle();
        bundle.putString("fields","profile_picture_url,username,name");
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync(); //on a seprate thread in the background
    }

    private void GetPostIDs(String ID){
        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                ID,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(@NonNull GraphResponse graphResponse) {
                        try {
                            JSONObject BigData = graphResponse.getJSONObject().getJSONObject("media");
                            JSONArray JSONpostIDArray = BigData.getJSONArray("data");
                            for(int i = 0; i < JSONpostIDArray.length(); i++ ){
                                JSONObject tmpPostId = JSONpostIDArray.getJSONObject(i);
                                DataSingelton.ig_postIDs.add(tmpPostId.get("id").toString());
                            }
                            //get the next page :
                            GetPostIDs_nextpage(BigData,ID);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle bundle = new Bundle();
        bundle.putString("fields","media");
        graphRequest.setParameters(bundle);

        graphRequest.executeAsync();                                                                //on a seprate thread in the background
    }
    private void GetPostIDs_nextpage(@NonNull JSONObject response, String ID){
        try {
            GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                    ID+"/media",
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(@NonNull GraphResponse graphResponse) {
                            try {
                                JSONObject BigData = graphResponse.getJSONObject();
                                JSONArray JSONpostIDArray = BigData.getJSONArray("data");
                                for(int i = 0; i < JSONpostIDArray.length(); i++ ){
                                    JSONObject tmpPostId = JSONpostIDArray.getJSONObject(i);
                                    DataSingelton.ig_postIDs.add(tmpPostId.get("id").toString());
                                }
                                GetPostIDs_nextpage(BigData,ID);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
            Bundle bundle = new Bundle();
            bundle.putString("pretty","0");
            bundle.putString("limit","25");
            bundle.putString("after",response.getJSONObject("paging").getJSONObject("cursors").getString("after"));
            graphRequest.setParameters(bundle);
            graphRequest.executeAsync();                                                                //on a seprate thread in the background
            //pretty=0&limit=100&after=QVFIUmVUdE1xM3N2MDhGcXNhaXgzaUdNcE5WUlZACblFyNklSbzBWcEJraU5oSjZAtR0EwWmZAoZAzl6aW5acDVlSGNWbzhHZA1dGU0dMeHA1MXg2bm44ZAkE2RWNn
        } catch (JSONException e) {
            //finished data init
            Log.d(TAG, "Post List : "+ DataSingelton.ig_postIDs);
            GetPostInfo();
            return;
        }

    }

    private void GetPostInfo(){                                                                     //gha fields
        for (String ID: DataSingelton.ig_postIDs) {                                                 //sends requests for each post
            GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                    ID, new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(@NonNull GraphResponse graphResponse) {
                            JSONObject response = graphResponse.getJSONObject();
                            PostData postData = new PostData(response);
                            DataSingelton.ig_post_data.put(ID,postData);                            //post data
                            GetPostAnalytics(ID);                                                   //post insights
                        }
                    });
            Bundle bundle = new Bundle();
            bundle.putString("fields", "caption,comments_count,like_count,media_type,media_product_type,permalink,timestamp");
            graphRequest.setParameters(bundle);
            graphRequest.executeAsync();
        }

    }
    private void GetPostAnalytics(@NonNull String ID){                                              //For analytics
        String MediaType = DataSingelton.ig_post_data.get(ID).getMedia_type();                      //the insights use the same Data class format
        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                ID+"/insights", new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(@NonNull GraphResponse graphResponse) {
                        JSONObject BigData = graphResponse.getJSONObject();
                        if (BigData == null) return;
                        try {
                            CreateDataPosts(BigData.getJSONArray("data"), ID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle bundle = new Bundle();
        switch (MediaType){
            case PostData.IMAGE:
                bundle.putString("metric", "reach,impressions,engagement,saved");
                break;
            case PostData.VIDEO:
                bundle.putString("metric", "reach,impressions,engagement,saved,video_views");
                break;
            //TODO ALMBUM AND STORIES
        }
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();

    }

    private void CreateNameUsernameProfilePictureData(@NonNull JSONObject json) {
        try {
            DataSingelton.ig_profilePictureURL = json.get("profile_picture_url").toString();
            DataSingelton.ig_name = json.get("name").toString();
            DataSingelton.ig_username = json.get("username").toString();
            //fetch the image and show it :
            new FetchImage(DataSingelton.ig_profilePictureURL).start();
            //chouf thread : it worked :D
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    class FetchImage extends Thread{
        String url;
        public FetchImage(String url){
            this.url = url;
        }
        @Override
        public void run() {
            try {
                InputStream inputStream = new java.net.URL(url).openStream();
                DataSingelton.ig_BITMAPprofilePictureURL = BitmapFactory.decodeStream(inputStream);
                DataSingelton.finishedProfileDataInit = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void CreateData(@NonNull JSONArray retrievedData) {
        //data is distributed in the HASHMAP
        try {
            for(int i = 0; i < retrievedData.length(); i++){
                //conversions and tmp shit
                JSONObject temp_JSONData = retrievedData.getJSONObject(i);
                String temp_JSONData_name = temp_JSONData.getString("name");
                //put data in the Map in the singelton
                Data temp_data = new Data(temp_JSONData);
                DataSingelton.data.put(temp_JSONData_name,temp_data);
            }
            DataSingelton.finishedDataInit = true;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //TODO RECHECK HADCHI
    private void CreateLifetimeData_OnlineFollowers(@NonNull JSONArray retrievedData){
        try {
            for(int i = 0; i < retrievedData.length(); i++){
                //conversions and tmp shit
                JSONObject temp_JSONData = new JSONObject(retrievedData.get(i).toString());
                String temp_JSONData_name = temp_JSONData.get("name").toString();

                //put data in the Map in the singelton
                Data temp_data = new Data(temp_JSONData);
                DataSingelton.lifetimeData_onlineFollowers.put(temp_JSONData_name,temp_data);
            }
            //End of initialisation : stopping the service
            DataSingelton.finishedLifetimeDataInit_onlinefollowers = true;
            //FetchData.stopService();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void CreateLifetimeData_AudienceCountry(@NonNull JSONArray retrievedData){
        try {
            if (retrievedData.length() == 0) DataSingelton.lifetimeData_audienceCountry = null;
            for(int i = 0; i < retrievedData.length(); i++){
                //conversions and tmp shit
                JSONObject temp_JSONData = new JSONObject(retrievedData.get(i).toString());
                String temp_JSONData_name = temp_JSONData.get("name").toString();

                //put data in the Map in the singelton
                Data temp_data = new Data(temp_JSONData);
                DataSingelton.lifetimeData_audienceCountry.put(temp_JSONData_name,temp_data);
            }
            DataSingelton.finishedLifetimeDataInit_audiencecountry = true;
            //FetchData.stopService();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void CreateDataPosts(@NonNull JSONArray retrievedData,String ID) {
        try {
            Map<String,Data> tmp_postData = new LinkedHashMap<>();
            for(int i = 0; i < retrievedData.length(); i++){
                //conversions and tmp shit
                JSONObject temp_JSONData = retrievedData.getJSONObject(i);
                String temp_JSONData_name = temp_JSONData.getString("name");
                //put data in the Map in the singelton
                Data temp_data = new Data(temp_JSONData);
                tmp_postData.put(temp_JSONData_name,temp_data);
            }
            DataSingelton.ig_post_data_insights.put(ID,tmp_postData);
            if (DataSingelton.ig_post_data_insights.size() == DataSingelton.ig_postIDs.size()) {
                DataSingelton.finishedDataPostsInit = true;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
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
import java.util.Objects;

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
        if (isRunning && DataSingelton.isFinishedDataInit() && DataSingelton.isFinishedProfileDataInit() && DataSingelton.isFinishedDataPostsInit()
        && DataSingelton.isFinishedLifetimeDataInit_audiencecountry() && DataSingelton.isFinishedLifetimeDataInit_onlinefollowers()){
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
                        DataSingelton.isFinishedDataInit() +" && "+
                        DataSingelton.isFinishedProfileDataInit() +" && "+
                        DataSingelton.isFinishedDataPostsInit() +" && "+
                        DataSingelton.isFinishedLifetimeDataInit_audiencecountry() +" && "+
                        DataSingelton.isFinishedLifetimeDataInit_onlinefollowers());
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
                                                assert JSONInstaID != null;
                                                JSONObject jsonRelated_Pages = JSONInstaID.getJSONObject("instagram_business_account");
                                                DataSingelton.setInstagramBusinessAccountID(jsonRelated_Pages.get("id").toString());

                                                //for profile
                                                getProfileData(DataSingelton.getInstagramBusinessAccountID());
                                                getProfileAnalyticsData(DataSingelton.getInstagramBusinessAccountID());
                                                getAnalyticsLifetime_OnlineFollowers(DataSingelton.getInstagramBusinessAccountID());
                                                getAnalyticsLifetime_AudienceCountry(DataSingelton.getInstagramBusinessAccountID());
                                                //for posts :
                                                getPostIDs(DataSingelton.getInstagramBusinessAccountID());

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
        assert jsonObject != null;
        JSONObject accounts = jsonObject.getJSONObject("accounts");
        JSONArray related_Pages = accounts.getJSONArray("data");
        JSONObject InstaObject = related_Pages.getJSONObject(0);                                                //recheck had zmr
        return InstaObject.getString("id");
    }
    private void getProfileData(String ID) {
        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                ID,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(@NonNull GraphResponse graphResponse) {
                        JSONObject smallData = graphResponse.getJSONObject();
                        if (smallData != null) createProfileData(smallData);
                    }
                });
        Bundle bundle = new Bundle();
        bundle.putString("fields","profile_picture_url,username,name,followers_count");
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync(); //on a seprate thread in the background
    }
    private void getProfileAnalyticsData(String ID){
        //set a new request :
        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                ID+"/insights",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(@NonNull GraphResponse graphResponse) {
                        JSONObject response = graphResponse.getJSONObject();
                        if (response == null) return;
                        try {
                            //Data list li fiha kulchi : JSONArray retrievedData
                            CreateData(response.getJSONArray("data"));
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
    private void getAnalyticsLifetime_OnlineFollowers(String ID){
        //set a new request :
        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                ID+"/insights",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(@NonNull GraphResponse graphResponse) {
                        JSONObject response = graphResponse.getJSONObject();
                        Log.d(TAG, "getAnalyticsLifetime_OnlineFollowers: "+response);
                        try {
                            assert response != null;
                            CreateLifetimeData_OnlineFollowers(response.getJSONArray("data"));
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
    private void getAnalyticsLifetime_AudienceCountry(String ID){
        //set a new request :
        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                ID+"/insights",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(@NonNull GraphResponse graphResponse) {
                        JSONObject response = graphResponse.getJSONObject();
                        Log.d(TAG, "getAnalyticsLifetime_AudienceCountry: "+response);
                        try {
                            assert response != null;
                            CreateLifetimeData_AudienceCountry(response.getJSONArray("data"));
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
    
    private void getPostIDs(String ID){
        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                ID,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(@NonNull GraphResponse graphResponse) {
                        JSONObject response = graphResponse.getJSONObject();
                        try {
                            assert response != null;
                            JSONObject MediaData = response.getJSONObject("media");
                            JSONArray JSONpostIDArray = MediaData.getJSONArray("data");
                            for(int i = 0; i < JSONpostIDArray.length(); i++ ){
                                String tmpPostId = JSONpostIDArray.getJSONObject(i).getString("id");
                                DataSingelton.getIg_postIDs().add(tmpPostId);
                            }
                            //get the next page :
                            getPostIDs_nextpage(response,ID);

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
    private void getPostIDs_nextpage(@NonNull JSONObject response, String ID){
        try {
            GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                    ID+"/media",
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(@NonNull GraphResponse graphResponse) {
                            try {
                                JSONObject response = graphResponse.getJSONObject();
                                assert response != null;
                                JSONArray JSONpostIDArray = response.getJSONArray("data");
                                for(int i = 0; i < JSONpostIDArray.length(); i++ ){
                                    JSONObject tmpPostId = JSONpostIDArray.getJSONObject(i);
                                    DataSingelton.getIg_postIDs().add(tmpPostId.get("id").toString());
                                }
                                getPostIDs_nextpage(response,ID);
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
            graphRequest.executeAsync();
            //pretty=0&limit=100&after=QVFIUmVUdE1xM3N2MDhGcXNhaXgzaUdNcE5WUlZACblFyNklSbzBWcEJraU5oSjZAtR0EwWmZAoZAzl6aW5acDVlSGNWbzhHZA1dGU0dMeHA1MXg2bm44ZAkE2RWNn
        } catch (JSONException e) {//finished data init
            getPostInfo();                                                                          //post data

        }

    }
    private void getPostInfo(){                                                                     //gha fields
        for (String ID: DataSingelton.getIg_postIDs()) {                                                 //sends requests for each post
            GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                    ID, new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(@NonNull GraphResponse graphResponse) {
                            JSONObject response = graphResponse.getJSONObject();
                            assert response != null;
                            PostData postData = new PostData(response);
                            DataSingelton.getIg_post_data().put(ID,postData);
                            GetPostAnalytics(ID);                                                                   //post insights
                        }
                    });
            Bundle bundle = new Bundle();
            bundle.putString("fields", "caption,comments_count,like_count,media_type,media_product_type,permalink,timestamp");
            graphRequest.setParameters(bundle);
            graphRequest.executeAsync();
        }

    }
    private void GetPostAnalytics(@NonNull String ID){                                              //For analytics
        String MediaType = Objects.requireNonNull(DataSingelton.getIg_post_data().get(ID)).getMedia_type();                 //the insights use the same Data class format
        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                ID+"/insights", new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(@NonNull GraphResponse graphResponse) {
                        JSONObject response = graphResponse.getJSONObject();
                        if (response == null) return;
                        try {
                            createDataPosts(response.getJSONArray("data"), ID);
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
    
    private void createProfileData(@NonNull JSONObject json) {
        try {
            DataSingelton.setIg_profilePictureURL(json.getString("profile_picture_url"));
            DataSingelton.setIg_name(json.getString("name"));
            DataSingelton.setIg_username(json.getString("username"));
            DataSingelton.setFollowers_count(json.getInt("followers_count"));
            new FetchImage(DataSingelton.getIg_profilePictureURL()).start();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    static class FetchImage extends Thread{
        String url;
        public FetchImage(String url){
            this.url = url;
        }
        @Override
        public void run() {
            try {
                InputStream inputStream = new java.net.URL(url).openStream();
                DataSingelton.setIg_BITMAPprofilePictureURL(BitmapFactory.decodeStream(inputStream));
                DataSingelton.setFinishedProfileDataInit(true);
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
                DataSingelton.getData().put(temp_JSONData_name,temp_data);
            }
            DataSingelton.setFinishedDataInit(true);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //TODO RECHECK HADCHI
    private void CreateLifetimeData_OnlineFollowers(@NonNull JSONArray retrievedData){
        try {
            if (retrievedData.length() == 0) DataSingelton.setLifetimeData_audienceCountry(null);
            for(int i = 0; i < retrievedData.length(); i++){
                //conversions and tmp shit
                JSONObject temp_JSONData = new JSONObject(retrievedData.get(i).toString());
                String temp_JSONData_name = temp_JSONData.get("name").toString();

                //put data in the Map in the singelton
                Data temp_data = new Data(temp_JSONData);
                DataSingelton.getLifetimeData_onlineFollowers().put(temp_JSONData_name,temp_data);
            }
            DataSingelton.setFinishedLifetimeDataInit_onlinefollowers(true);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void CreateLifetimeData_AudienceCountry(@NonNull JSONArray retrievedData){
        try {
            if (retrievedData.length() == 0) DataSingelton.setLifetimeData_audienceCountry(null);
            for(int i = 0; i < retrievedData.length(); i++){
                //conversions and tmp shit
                JSONObject temp_JSONData = new JSONObject(retrievedData.get(i).toString());
                String temp_JSONData_name = temp_JSONData.get("name").toString();

                //put data in the Map in the singelton
                Data temp_data = new Data(temp_JSONData);
                DataSingelton.getLifetimeData_audienceCountry().put(temp_JSONData_name,temp_data);
            }
            DataSingelton.setFinishedLifetimeDataInit_audiencecountry(true);
            //FetchData.stopService();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void createDataPosts(@NonNull JSONArray retrievedData,String ID) {
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
            DataSingelton.getIg_post_data_insights().put(ID,tmp_postData);
            if (DataSingelton.getIg_post_data_insights().size() == DataSingelton.getIg_postIDs().size()) {
                DataSingelton.setFinishedDataPostsInit(true);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
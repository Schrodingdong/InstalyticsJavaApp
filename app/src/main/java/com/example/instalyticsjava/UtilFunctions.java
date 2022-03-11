package com.example.instalyticsjava;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.BundleKt;


import com.example.instalyticsjava.data.Data;
import com.example.instalyticsjava.data.Value;
import com.example.instalyticsjava.dataposts.PostData;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.apache.commons.math3.distribution.LogisticDistribution;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.jetbrains.annotations.Contract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UtilFunctions extends AppCompatActivity {
    private final static String TAG = "UtilFunctions";

    public void FetchAnalyticsData() {
        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(@Nullable JSONObject jsonObject, @Nullable GraphResponse graphResponse) {
                        try {
                            //get the request li fiha every insta account linked to that page
                            String account_Insta_Id = getAccount_insta_id(jsonObject);
                            //get id of the business account : NEW REQUEST :
                            GraphRequest getInstaId = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                                account_Insta_Id,
                                new GraphRequest.Callback() {
                                    @Override
                                    public void onCompleted(@NonNull GraphResponse graphResponse) {
                                        JSONObject JSONInstaID = graphResponse.getJSONObject();
                                        try {
                                            Object ObjectInstaID = JSONInstaID.get("instagram_business_account");
                                            JSONObject jsonRelated_Pages = new JSONObject(ObjectInstaID.toString());
                                            //ITS FETCHED !
                                            //InstagramBusinessAccountID = jsonRelated_Pages.get("id").toString();

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
                            getInstaId.executeAsync();                                              //on a seprate thread in the background

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle bundle = new Bundle();
        bundle.putString("fields","accounts");
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();                                                                //on a seprate thread in the background
    }

    @NonNull
    private String getAccount_insta_id(@Nullable JSONObject jsonObject) throws JSONException {
        Object related_Pages = jsonObject.get("accounts");
        JSONObject jsonRelated_Pages = new JSONObject(related_Pages.toString());

        JSONArray related_Pages2 = jsonRelated_Pages.getJSONArray("data");
        Object objectInstaID = related_Pages2.get(0);
        JSONObject jsonObjectInstaID = new JSONObject(objectInstaID.toString());
        Object objectAccount_Insta_Id = jsonObjectInstaID.get("id");

        //the id of the multiple accounts page
        String account_Insta_Id = objectAccount_Insta_Id.toString();
        return account_Insta_Id;
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
        bundle.putString("since",Long.toString(currentEpoch-weekEpoch*2 ));

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
    private void GetPostIDs_nextpage(JSONObject response,String ID){
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
            GetPostInfo();
            return;
        }

    }

    private void GetPostInfo(){                                                                     //gha fields
        for (String ID: DataSingelton.ig_postIDs) {                                                 //sends requests for each post
            GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                    ID,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(@NonNull GraphResponse graphResponse) {
                            JSONObject JSONresponse = graphResponse.getJSONObject();
                            PostData postData = new PostData(JSONresponse);
                            DataSingelton.ig_post_data.put(ID,postData);
                            GetPostAnalytics(ID);                                                   //for each post, n3tiw insights dialo 3la 7ssab type d post that it is
                            //todo what the fuck is this
//                            if (DataSingelton.ig_postIDs.size() == DataSingelton.ig_post_data.size()){
//                                for (Map.Entry<String,PostData> element :
//                                        DataSingelton.ig_post_data.entrySet()) {
//
//                                }
//                            }
                        }
                    });
            Bundle bundle = new Bundle();
            bundle.putString("fields", "caption,comments_count,like_count,media_type,media_product_type,permalink,timestamp");
            graphRequest.setParameters(bundle);
            graphRequest.executeAsync();                    //on a separate thread in the background
        }

    }
    private void GetPostAnalytics(@NonNull String ID){                                                       //specific for analytics
        String MediaType = DataSingelton.ig_post_data.get(ID).getMedia_type();
        //the insights use the same Data class format
        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                ID+"/insights",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(@NonNull GraphResponse graphResponse) {
                        JSONObject BigData = graphResponse.getJSONObject();
                        if (BigData == null) return;
                        try {
                            //Data list li fiha kulchi : JSONArray retrievedData
                            //new CreatePostDataThread(BigData.getJSONArray("data"),ID);
                            CreateDataPosts(BigData.getJSONArray("data"), ID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
        switch (MediaType){
            case PostData.IMAGE: {
                Bundle bundle = new Bundle();
                bundle.putString("metric", "reach,impressions,engagement,saved");
                graphRequest.setParameters(bundle);
                graphRequest.executeAsync();
                break;
            }
            case PostData.VIDEO: {
                Bundle bundle = new Bundle();
                bundle.putString("metric", "reach,impressions,engagement,saved,video_views");
                graphRequest.setParameters(bundle);
                graphRequest.executeAsync();
                break;
            }
            //TODO ALMBUM AND STORIES
        }

    }

    private void CreateNameUsernameProfilePictureData(@NonNull JSONObject smallData) {
        try {
            DataSingelton.ig_profilePictureURL = smallData.get("profile_picture_url").toString();
            DataSingelton.ig_name = smallData.get("name").toString();
            DataSingelton.ig_username = smallData.get("username").toString();
            //fetch the image and show it :
            new FetchImage(DataSingelton.ig_profilePictureURL).start();

//
//            DataSingelton.finishedProfileDataInit = true;
//            FetchData.stopService();
            //chouf thread : it worked :D
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void CreateData(@NonNull JSONArray retrievedData) {
        //Now all the data is distributes in the HASHMAP
        /*
        the JASONArray is  an array of Objects, to make these Object as JSONObjects we shall use the method :
        JSONObject(retrievedData.get(i).toString())
         */
        try {
            for(int i = 0; i < retrievedData.length(); i++){
                //conversions and tmp shit
                JSONObject temp_JSONData = new JSONObject(retrievedData.get(i).toString());
                String temp_JSONData_name = temp_JSONData.get("name").toString();

                //put data in the Map in the singelton
                Data temp_data = new Data(temp_JSONData);
                DataSingelton.data.put(temp_JSONData_name,temp_data);
            }
            //End of initialisation : stopping the service
            DataSingelton.finishedDataInit = true;
            //FetchData.stopService();
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
                String temp_JSONData_name = temp_JSONData.get("name").toString();

                //put data in the Map in the singelton
                Data temp_data = new Data(temp_JSONData);
                tmp_postData.put(temp_JSONData_name,temp_data);
            }
            DataSingelton.ig_post_data_insights.put(ID,tmp_postData);
            //End of initialisation : stopping the service
            if (DataSingelton.ig_post_data_insights.size() == DataSingelton.ig_postIDs.size()) {
                DataSingelton.finishedDataPostsInit = true;
            }
            //FetchData.stopService();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void CreateDataPosts(Data emptyData,String ID) {
        Map<String,Data> tmp_postData = new LinkedHashMap<>();
        for(int i = 0; i < 1; i++){
            //conversions and tmp shit
            String temp_JSONData_name = emptyData.getName();
            //put data in the Map in the singelton
            tmp_postData.put(temp_JSONData_name,emptyData);
        }
        DataSingelton.ig_post_data_insights.put(ID,tmp_postData);
        //End of initialisation : stopping the service
        if (DataSingelton.ig_post_data_insights.size() == DataSingelton.ig_postIDs.size()) {
            DataSingelton.finishedDataPostsInit = true;
        }
        //FetchData.stopService();
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
                //FetchData.stopService();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //----------------------------------------------------------------------------------------------
    //--------------------------------Static function-----------------------------------------------
    //----------------------------------------------------------------------------------------------
    @NonNull
    public static List<String> GetEndTimeList(@NonNull Map<String, Data> profileData) {
        //if (profileData == null) return null;
        List<String> returnedResult = new ArrayList<>();
        for (Map.Entry<String, Data> element : profileData.entrySet()) {
            List<Value> valueList = element.getValue().getValues();
            for (Value v : valueList){
                returnedResult.add(v.getEnd_time().subSequence(5,10).toString());
            }
            break;
        }
        Collections.reverse(returnedResult);
        return returnedResult;
    }
    @NonNull
    public static BarData getProfileDataChartEntry(@NonNull Map<String, Data> profileData){
        //if (profileData == null) return null;
        BarData barData = new BarData();
        //create a list of bar entries + create the data sets
        float bar_width = 0.2f;
        ArrayList<BarEntry> entryArrayList_reach = new ArrayList<>();
        ArrayList<BarEntry> entryArrayList_impressions= new ArrayList<>();
        ArrayList<BarEntry> entryArrayList_profileViews= new ArrayList<>();

        for (Map.Entry<String, Data> element : profileData.entrySet()){
            switch (element.getKey()){
                case "reach" : {
                    int index = 0;
                    for (Value v : element.getValue().getValues()) {    //instantiate the entry for the ith metric
                        entryArrayList_reach.add(new BarEntry(index, v.getValue()));
                        index++;
                    }
                    Collections.reverse(entryArrayList_reach);
//                    BarDataSet barDataSet = new BarDataSet(entryArrayList_reach, "Reach");
//                    barDataSet.setColor(Color.rgb(224, 179, 255));
//                    barData.addDataSet(barDataSet);                     //insertion in the barData to return

                    break;
                }
                case "profile_views": {
                    int index = 0;
                    for (Value v : element.getValue().getValues()) {    //instantiate the entry for the ith metric
                        entryArrayList_profileViews.add(new BarEntry(index, v.getValue()));
                        index++;
                    }
                    Collections.reverse(entryArrayList_profileViews);
//                    BarDataSet barDataSet = new BarDataSet(entryArrayList_profileViews, "Profile Views");
//                    barDataSet.setColor(Color.rgb(255, 179, 215));
//                    barData.addDataSet(barDataSet);                     //insertion in the barData to return

                    break;
                }
                case "impressions" : {
                    int index = 0;
                    for (Value v : element.getValue().getValues()) {    //instantiate the entry for the ith metric
                        entryArrayList_impressions.add(new BarEntry(index, v.getValue()));
                        index++;
                    }
                    Collections.reverse(entryArrayList_impressions);
//                    BarDataSet barDataSet = new BarDataSet(entryArrayList_impressions, "Impressions");
//                    barDataSet.setColor(Color.rgb(161, 168, 255));
//                    barData.addDataSet(barDataSet);                     //insertion in the barData to return

                    break;
                }
                default:
                    break;
            }
            barData.setBarWidth(bar_width);
        }
//        Collections.reverse(entryArrayList_reach);
//        Collections.reverse(entryArrayList_engagement);
//        Collections.reverse(entryArrayList_impressions);
        BarDataSet barDataSet_reach = new BarDataSet(entryArrayList_reach,"reach");
        barDataSet_reach.setColor(Color.rgb(224, 179, 255));

        BarDataSet barDataSet_profileViews = new BarDataSet(entryArrayList_profileViews,"profile views");
        barDataSet_profileViews.setColor(Color.rgb(255, 179, 215));

        BarDataSet barDataSet_impressions = new BarDataSet(entryArrayList_impressions,"impressions");
        barDataSet_impressions.setColor(Color.rgb(161, 168, 255));


        barData = new BarData(barDataSet_profileViews,barDataSet_reach,barDataSet_impressions);
        barData.setBarWidth(bar_width);
        return barData;
        //return the value to add f the main thing brra dlfunction

    }
    @Nullable
    public static LineData getFollowersDataChartEntry(@NonNull Map<String, Data> profileData){
        if (profileData == null || profileData.get("follower_count") == null) return null;
        LineData lineData;
        for (Map.Entry<String, Data> element : profileData.entrySet()) {
            ArrayList<Entry> entryArrayList = new ArrayList<>();
            int index = 0;
            float offset = 0.5f;//bach yji nichan lwst
            if (element.getKey().compareTo("follower_count") == 0){
                ArrayList<Integer> tmp = new ArrayList<Integer>();
                for (Value v : element.getValue().getValues()) {    //instantiate the entry for the follower_count metric
                    tmp.add(v.getValue());
                }
                Collections.reverse(tmp);
                for (int i: tmp) {
                    entryArrayList.add(new Entry(index,i));
                    index ++;
                }
                LineDataSet lineDataSet = new LineDataSet(entryArrayList,"Followers");
                lineData = new LineData(lineDataSet);
                return lineData;
            }
        }
        return null;

    }


    @NonNull
    public static List<String> getTop5Posts(){
        //based on : engagement + impressions + reach
        //on veut afficher :engagement & impressions & reach
        List<String> Top5Posts = new ArrayList<>();
        List<Float> Top5Posts_notoriety = new ArrayList<>();
        for (int i=0; i < 5 ; i++){
            float max = -1;
            String maxID = "";
            //an sommiw dakchi li glna 9bl
            for (Map.Entry<String, Map<String,Data>> entry: DataSingelton.ig_post_data_insights.entrySet()) {
                if (Top5Posts.contains(entry.getKey()))
                    continue;
                float postNotoriety = 0;
                int eng = entry.getValue().get("engagement").getValues().get(0).getValue();
                int impr = entry.getValue().get("impressions").getValues().get(0).getValue();
                int reach = entry.getValue().get("reach").getValues().get(0).getValue();
                postNotoriety += eng+impr+reach;
                postNotoriety = postNotoriety / (float) 3;
                if (max < postNotoriety){
                    max = postNotoriety;
                    maxID = entry.getKey();
                }
            }
            Top5Posts_notoriety.add(max);
            Top5Posts.add(maxID);           //lakan 9l mn 5 posts kay koun le rest null
        }

        return Top5Posts;
    }
    @NonNull
    public static BarData getTop5PostsDataChartEntry(@NonNull Map<String, PostData> postData, @NonNull Map<String, Map<String,Data>> postDataInsights){
        List<String> Top5Posts = getTop5Posts();
        BarData barData;
        //create a list of bar entries + create the data sets
        float bar_width = 0.2f;
        ArrayList<BarEntry> entryArrayList_reach = new ArrayList<>();
        ArrayList<BarEntry> entryArrayList_engagement = new ArrayList<>();
        ArrayList<BarEntry> entryArrayList_impressions = new ArrayList<>();
        int index = 0;
        for (index =0 ; index < 5 ; index++){
            for (Map.Entry<String, Map<String,Data>> element : postDataInsights.entrySet()){

                String currentPostID = element.getKey();
                if (Top5Posts.get(index) !=  currentPostID) continue;
                for ( Map.Entry<String,Data> element2 : element.getValue().entrySet()){

                    switch (element2.getKey()){
                        case "reach" : {
                            entryArrayList_reach.add(new BarEntry(index, element2.getValue().getValues().get(0).getValue()));
                            break;
                        }
                        case "engagement": {
                            entryArrayList_engagement.add(new BarEntry(index, element2.getValue().getValues().get(0).getValue()));
                            break;
                        }
                        case "impressions" : {
                            entryArrayList_impressions.add(new BarEntry(index, element2.getValue().getValues().get(0).getValue()));
                            break;
                        }
                        //TODO chouf 3la 7ssab the media type ? (z3ma for albums)
                        default:
                            break;
                    }
                }
                break;
            }
        }


//        Collections.reverse(entryArrayList_reach);
//        Collections.reverse(entryArrayList_engagement);
//        Collections.reverse(entryArrayList_impressions);
        BarDataSet barDataSet_reach = new BarDataSet(entryArrayList_reach,"reach");
        barDataSet_reach.setColor(Color.rgb(224, 179, 255));

        BarDataSet barDataSet_engagement = new BarDataSet(entryArrayList_engagement,"engagement");
        barDataSet_engagement.setColor(Color.rgb(255, 179, 215));

        BarDataSet barDataSet_impressions = new BarDataSet(entryArrayList_impressions,"impressions");
        barDataSet_impressions.setColor(Color.rgb(161, 168, 255));


        barData = new BarData(barDataSet_engagement,barDataSet_reach,barDataSet_impressions);
        barData.setBarWidth(bar_width);
        return barData;

    }
    @NonNull
    public static BarData getLast10PostsDataChartEntry(@NonNull Map<String, PostData> postData, @NonNull Map<String, Map<String,Data>> postDataInsights){
        List<String> ig_postIDs = DataSingelton.ig_postIDs;
        BarData barData;
        //create a list of bar entries + create the data sets
        float bar_width = 0.2f;
        ArrayList<BarEntry> entryArrayList_reach = new ArrayList<>();
        ArrayList<BarEntry> entryArrayList_engagement = new ArrayList<>();
        ArrayList<BarEntry> entryArrayList_impressions = new ArrayList<>();
        int counter = 0;
        for (String ID : ig_postIDs){
            if (counter >= 10) break;
            Map<String,Data> element = DataSingelton.ig_post_data_insights.get(ID);
            for ( Map.Entry<String,Data> element2 : element.entrySet()){

                switch (element2.getKey()){
                    case "reach" : {
                        entryArrayList_reach.add(new BarEntry(counter, element2.getValue().getValues().get(0).getValue()));
                        break;
                    }
                    case "engagement": {
                        entryArrayList_engagement.add(new BarEntry(counter, element2.getValue().getValues().get(0).getValue()));
                        break;
                    }
                    case "impressions" : {
                        entryArrayList_impressions.add(new BarEntry(counter, element2.getValue().getValues().get(0).getValue()));
                        break;
                    }
                    //TODO chouf 3la 7ssab the media type ? (z3ma for albums)
                    default:
                        break;
                }
            }
            counter++;
        }

//        Collections.reverse(entryArrayList_reach);
//        Collections.reverse(entryArrayList_engagement);
//        Collections.reverse(entryArrayList_impressions);
        BarDataSet barDataSet_reach = new BarDataSet(entryArrayList_reach,"reach");
        barDataSet_reach.setColor(Color.rgb(224, 179, 255));

        BarDataSet barDataSet_engagement = new BarDataSet(entryArrayList_engagement,"engagement");
        barDataSet_engagement.setColor(Color.rgb(255, 179, 215));

        BarDataSet barDataSet_impressions = new BarDataSet(entryArrayList_impressions,"impressions");
        barDataSet_impressions.setColor(Color.rgb(161, 168, 255));


        barData = new BarData(barDataSet_engagement,barDataSet_reach,barDataSet_impressions);
        barData.setBarWidth(bar_width);
        return barData;

    }
    @NonNull
    public static List<String> getTop5PostsURL(){
        List<String> result = new ArrayList<>();
        List<String> top5Posts = getTop5Posts();
        for (String postID:
                top5Posts) {
            result.add(DataSingelton.ig_post_data.get(postID).getPermalink());
        }
        return result;
    }
    //----------------------------------------------------------------------------------------------
    //--------------------------------logic and maths-----------------------------------------------
    //----------------------------------------------------------------------------------------------

    public static double getAVGof(Data metric){
        if (metric == null) return 0;
        double average = 0;
        for (Value v: metric.getValues()){
            average += v.getValue();
        }
        average /= metric.getValues().size();
        return average;
    }
    public static double getPostAVGof(String metric){
        if (metric == null) return 0;
        double average = 0;
        for (Map.Entry<String , Map<String,Data>> element:
             DataSingelton.ig_post_data_insights.entrySet()) {
            average += element.getValue().get(metric).getValues().get(0).getValue();
        }
        average /= DataSingelton.ig_post_data_insights.size();
        return average;
    }

    public static double getDeviationPercentage(Data metric){
        if (metric == null) return 0;
        double average = getAVGof(metric);
        double result = 0;
        for (Value v: metric.getValues()){
            result += Math.abs(v.getValue() - average);
        }
        result /= metric.getValues().size();
        return result/average;
    }
    public static double getPostDeviationPercentage(String metric){
        if (metric == null) return 0;
        double average = getPostAVGof(metric);
        double result = 0;
        for (Map.Entry<String , Map<String,Data>> element:
                DataSingelton.ig_post_data_insights.entrySet()) {
            result += Math.abs(element.getValue().get(metric).getValues().get(0).getValue() - average);
        }
        result /= DataSingelton.ig_post_data_insights.size();
        return result/average;
    }

    public static double getCorrelationOf(Data metric1, Data metric2){
        if (metric1 == null || metric2 == null) return 0;
        double stdDev1 = getStddeviation(metric1);
        double stdDev2 = getStddeviation(metric2);
        double cov = getCovarience(metric1,metric2);
        return Math.abs(cov/(stdDev2 * stdDev1));
    }
    public static double getPostCorrelationOf(String metric1, String metric2){
        if (metric1 == null || metric2 == null) return 0;
        double stdDev1 = getPostStddeviation(metric1);
        double stdDev2 = getPostStddeviation(metric2);
        double cov = getPostCovarience(metric1,metric2);
        return Math.abs(cov/(stdDev2 * stdDev1));
    }
    @NonNull
    public static List<Integer> getMetricValueList(@NonNull Data metric){
        List<Integer> returnlist = new ArrayList<Integer>();
        List<Value> valueOfMetric = metric.getValues();
        for (Value v : valueOfMetric){
            returnlist.add(v.getValue());
        }
        return returnlist;
    }
    @NonNull
    public static List<Integer> getPostMetricValueList(@NonNull String metric){
        List<Integer> returnlist = new ArrayList<Integer>();
        for (Map.Entry<String , Map<String,Data>> element:
                DataSingelton.ig_post_data_insights.entrySet()) {
            returnlist.add(element.getValue().get(metric).getValues().get(0).getValue());
        }
        return returnlist;
    }


    private static double unfollowerReachCoefficient = 0.5; //50%
    public static Data getUnfollowedReach(Data reach, Map<String,Data> online_followers_data){
        if (reach == null || online_followers_data == null) return null ;
        Data online_followers = online_followers_data.get("data");
        Data returnData = new Data();
        returnData.setName("unfollowed_reach");
        List<Value> reachList = reach.getValues();
        List<Value> onlineFollowerList = online_followers.getValues();
        if (reachList.size() == 0 || onlineFollowerList.size() == 0) return null;
        List<Value> returnList = new ArrayList<>();
        Value tmpValue = new Value();
        for (int i = 0; i < reachList.size(); i++){
            int temp =reachList.get(i).getValue() - onlineFollowerList.get(i).getValue() ;
            tmpValue.setValue(Math.max(0,temp));
            tmpValue.setEnd_time(reachList.get(i).getEnd_time());
            returnList.add(tmpValue);
        }
        returnData.setValues(returnList);
        return returnData;
    }

    public static double getReachPercentage(@NonNull Map<String,Data> data){
        List<Value> reach = data.get("reach").getValues();
        List<Value> impressions = data.get("impressions").getValues();
        int sumReach = 0;
        int sumImpressions = 0;
        for (Value v: impressions){
            sumImpressions += v.getValue();
        }
        for (Value v: reach){
            sumReach += v.getValue();
        }
        return (double)sumReach/(double)sumImpressions;
    }
    public static double getPostReachPercentage(){
        //initialise lists :
        List<Value> reach = new ArrayList<>();
        List<Value> impressions = new ArrayList<>();
        for (Map.Entry<String , Map<String,Data>> element:
                DataSingelton.ig_post_data_insights.entrySet()) {
            reach.add(element.getValue().get("reach").getValues().get(0));
            impressions.add(element.getValue().get("impressions").getValues().get(0));
        }

        int sumReach = 0;
        int sumImpressions = 0;
        for (Value v: impressions){
            sumImpressions += v.getValue();
        }
        for (Value v: reach){
            sumReach += v.getValue();
        }
        return (double)sumReach/(double)sumImpressions;
    }
    public static double getReturningUsersPercentage(Map<String,Data> data){
        return 1-getReachPercentage(data);
    }

    public static double getConsistencyOf(@NonNull Data metric){
        return getStddeviation(metric)/E(metric);
    }
    private static double E(@NonNull Data metric){
        List<Integer> X = new ArrayList<>();
        for (Value value:metric.getValues()) {
            X.add(value.getValue());
        }
        double e = 0;
        for (int x:X) e+=x;
        e /= X.size();
        return e;
    }
    private static double EsquaredPost(@NonNull String metric){
        List<Integer> X = new ArrayList<>();
        for (Map.Entry<String , Map<String,Data>> element:
                DataSingelton.ig_post_data_insights.entrySet()) {
            X.add(element.getValue().get(metric).getValues().get(0).getValue());
        }
        double e = 0;
        for (int x:X) e+=x*x;
        e /= X.size();
        return e;
    }
    private static double Esquared(@NonNull Data metric){
        List<Integer> X = new ArrayList<>();
        for (Value value:metric.getValues()) {
            X.add(value.getValue());
        }
        double e = 0;
        for (int x:X) e+=x*x;
        e /= X.size();
        return e;
    }
    private static double EPost(@NonNull String metric){
        List<Integer> X = new ArrayList<>();
        for (Map.Entry<String , Map<String,Data>> element:
                DataSingelton.ig_post_data_insights.entrySet()) {
            X.add(element.getValue().get(metric).getValues().get(0).getValue());
        }
        double e = 0;
        for (int x:X) e+=x;
        e /= X.size();
        return e;
    }
    public static double getVarience(@NonNull Data metric){
        double expected = E(metric);
        double Varience= Esquared(metric) - expected*expected;
        return Varience;
    }
    public static double getPostVarience(@NonNull String metric){
        double expected = EPost(metric);
        double Varience= EsquaredPost(metric) - expected*expected;
        return Varience;
    }
    public static double getStddeviation(@NonNull Data metric){
        double stdDeviation = Math.sqrt(getVarience(metric));
        return stdDeviation;
    }
    public static double getPostStddeviation(@NonNull String metric){
        double stdDeviation = Math.sqrt(getPostVarience(metric));
        return stdDeviation;
    }
    public static double getCovarience(Data metric1, Data metric2){
        double expected1 = E(metric1);
        double expected2 = E(metric2);
        double EMetric1Metric2 = 0;
        List<Integer> X1 = new ArrayList<>();
        for (Value value:metric1.getValues()) {
            X1.add(value.getValue());
        }
        List<Integer> X2 = new ArrayList<>();
        for (Value value:metric1.getValues()) {
            X2.add(value.getValue());
        }
        for (int i = 0; i < X1.size() ; i++) EMetric1Metric2+= X1.get(i) * X2.get(i);
        EMetric1Metric2 /= X1.size();
        return EMetric1Metric2 - expected1*expected2;

    }
    public static double getPostCovarience(String metric1, String metric2){
        double expected1 = EPost(metric1);
        double expected2 = EPost(metric2);
        double EMetric1Metric2 = 0;
        List<Integer> X1 = new ArrayList<>();
        for (Map.Entry<String , Map<String,Data>> element:
                DataSingelton.ig_post_data_insights.entrySet()) {
            X1.add(element.getValue().get(metric1).getValues().get(0).getValue());
        }
        List<Integer> X2 = new ArrayList<>();
        for (Map.Entry<String , Map<String,Data>> element:
                DataSingelton.ig_post_data_insights.entrySet()) {
            X2.add(element.getValue().get(metric2).getValues().get(0).getValue());
        }
        for (int i = 0; i < X1.size() ; i++) EMetric1Metric2+= X1.get(i) * X2.get(i);
        EMetric1Metric2 /= X1.size();
        return EMetric1Metric2- expected1*expected2;

    }

    public static double[] getPolynomialRegressionCoefficient(@NonNull List<Integer> dataForRegression,int orderK){
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        //get the data that we want to interpolate :
        int i = 1; //z3ma te days
        for (double x : dataForRegression) {
            obs.add(1.0D,i,dataForRegression.get((i++)-1));
        }
        // Instantiate a second-degree polynomial fitter.
        final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(orderK);

        // Retrieve fitted parameters (coefficients of the polynomial function).
        final double[] coeff = fitter.fit(obs.toList());
        return coeff;
    }
    //this technique requires an already existing coefficients
    public static float BIC(List<Integer> dataForRegression,double[] coefficients,int orderK ){
        //we need n : number of datapoints
        int n = dataForRegression.size();
        //sum of squares residual :
        float sumSquaresRes = 0;
        for (int i = 0; i < n ; i ++){
            //get f(x) :
            int fx =0;
            for (int j =1; j <= coefficients.length ;j ++){
                fx += (i+1)*coefficients[j-1];
            }
            sumSquaresRes += Math.pow(dataForRegression.get(i) - fx,2);
        }
        return (float) (n*Math.log(sumSquaresRes)+orderK*Math.log(n));
    }
    public static int getPolynomialOrder(List<Integer> dataForRegression){
        List<Float> BicOrders = new ArrayList<>();
        BicOrders.add(9999f); //initialise the first hakka ywli 3ndna 2 listes //
        //we verify for the 7 orders :
        for (int k = 1; k <= 7; k++){
            double[] coeff = getPolynomialRegressionCoefficient(dataForRegression,k);
            BicOrders.add(BIC(dataForRegression,coeff,k));
        }
        int min = 0;
        for (int k =0; k <=7 ; k++){
            if(BicOrders.get(k) <= BicOrders.get(min)) min =k;
        }
        return min;
    }
}

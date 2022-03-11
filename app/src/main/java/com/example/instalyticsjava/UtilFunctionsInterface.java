package com.example.instalyticsjava;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.instalyticsjava.data.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//wtf
public interface UtilFunctionsInterface {
    void FetchAnalyticsData();
    String getAccount_insta_id(@Nullable JSONObject jsonObject) throws JSONException;
    void GetAnalytics(String ID);
    void GetAnalyticsLifetime_OnlineFollowers(String ID);
    void GetAnalyticsLifetime_AudienceCountry(String ID);
    void GetNameUsernameProfilePicture(String ID);
    void GetPostIDs(String ID);
    void GetPostIDs_nextpage(JSONObject response,String ID);
    void GetPostInfo();
    void GetPostAnalytics(@NonNull String ID);
    void CreateNameUsernameProfilePictureData(@NonNull JSONObject smallData);
    void CreateData(@NonNull JSONArray retrievedData);
    void CreateLifetimeData_OnlineFollowers(@NonNull JSONArray retrievedData);
    void CreateLifetimeData_AudienceCountry(@NonNull JSONArray retrievedData);
    void CreateDataPosts(@NonNull JSONArray retrievedData,String ID);
}

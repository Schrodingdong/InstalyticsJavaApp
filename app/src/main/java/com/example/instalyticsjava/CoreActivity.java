package com.example.instalyticsjava;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.RectEvaluator;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.se.omapi.Session;
import android.support.v4.os.IResultReceiver;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.instalyticsjava.data.Data;
import com.example.instalyticsjava.data.Value;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CoreActivity extends AppCompatActivity {
    private String TAG ="CoreActivity";

    private Button logOutButton;
    private UtilFunctions myUtils = new UtilFunctions();
    //references in XML
    private ProgressBar spinner;
    private ConstraintLayout MainContent;
    private TextView accountUsername;
    private TextView accountName;
    private ImageView accountProfilePicture;

    //profile brief
    private Button ProfileBriefNameButton;
    private com.github.mikephil.charting.charts.BarChart profile_brief_barChart;

    private TextView avgProfileViews;
    private TextView reachPercentage;
    private TextView avg_follower_gain_value;

    private TextView ConsistencyReach;

    //post brief
    private Button PostBriefNameButton;
    private com.github.mikephil.charting.charts.BarChart posts_brief_graph;
    private Button goto_post1;
    private Button goto_post2;
    private Button goto_post3;
    private Button goto_post4;
    private Button goto_post5;
    private TextView avg_engagement_value;
    private TextView avg_reach_value;
    private TextView avg_impressions_value;
    private TextView reach_percentage_post_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get rid of title bar SHOULD BE ON TOP OF THE PROGRAM
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_core);
        DataSingelton randomInstance;

        //profile name username picture
        accountUsername = (TextView) findViewById(R.id.AccountUsername);
        accountName = (TextView) findViewById(R.id.AccountName);
        accountProfilePicture = (ImageView) findViewById(R.id.AccountPictureInner);

        //dial chargement
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        MainContent = (ConstraintLayout)findViewById(R.id.scrollable_area);

        //profilebrief
        ProfileBriefNameButton = (Button) findViewById(R.id.ProfileBrief_Name_button);
        ProfileBriefNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CoreActivity.this, ProfileAnalytics.class);
                startActivity(intent);
            }
        });
        profile_brief_barChart = (com.github.mikephil.charting.charts.BarChart) findViewById(R.id.profile_brief_graph);
        profile_brief_barChart.setNoDataText("No Data Available :(");
        avgProfileViews = (TextView) findViewById(R.id.avg_followersViews_value);
        reachPercentage = (TextView) findViewById(R.id.reach_percentage_value);
        avg_follower_gain_value = (TextView) findViewById(R.id.avg_follower_gain_value);


        //postBriefs :
        posts_brief_graph = (com.github.mikephil.charting.charts.BarChart) findViewById(R.id.posts_brief_graph);
        posts_brief_graph.setNoDataText("No Data Available :(");
        PostBriefNameButton = (Button) findViewById(R.id.posts_brief_Name_button);
        PostBriefNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CoreActivity.this, PostAnalytics.class);
                startActivity(intent);
            }
        });
        goto_post1 = (Button) findViewById(R.id.goto_post1);
        goto_post2 = (Button) findViewById(R.id.goto_post2);
        goto_post3 = (Button) findViewById(R.id.goto_post3);
        goto_post4 = (Button) findViewById(R.id.goto_post4);
        goto_post5 = (Button) findViewById(R.id.goto_post5);
        avg_engagement_value = (TextView) findViewById(R.id.avg_engagement_value);
        avg_reach_value = (TextView) findViewById(R.id.avg_reach_value);
        avg_impressions_value = (TextView) findViewById(R.id.avg_impressions_value);
        reach_percentage_post_value = (TextView) findViewById(R.id.reach_percentage_post_value);


        //interpretation:
        ConsistencyReach = (TextView) findViewById(R.id.consistency_reach_value);


        //log out
        logOutButton = findViewById(R.id.logout_button_core);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(CoreActivity.this, LoggingActivity.class);
                startActivity(intent);
                DataSingelton.ResetData();
                Log.d(TAG, "onClick: data after reset : "+DataSingelton.data);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!DataSingelton.finishedDataInit || !DataSingelton.finishedProfileDataInit || !DataSingelton.finishedDataPostsInit
                || !DataSingelton.finishedLifetimeDataInit_audiencecountry || !DataSingelton.finishedLifetimeDataInit_onlinefollowers){
            spinner.setVisibility(View.VISIBLE);
            MainContent.setVisibility(View.GONE);
            Intent intent = new Intent(this, FetchData.class);
            startService(intent);
        }
        waitForData();


    }


    private float barSpace = 0f;
    private float groupSpace = 0.4f;

    //TODO add a "sorry we werent able to connect :/
    private void waitForData() {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (DataSingelton.finishedDataInit && DataSingelton.finishedProfileDataInit && DataSingelton.finishedDataPostsInit
                        && DataSingelton.finishedLifetimeDataInit_audiencecountry && DataSingelton.finishedLifetimeDataInit_onlinefollowers){
                    FetchData.stopService();
//                    Log.d(TAG, "run: FINISHED : " + DataSingelton.data);
//                    Log.d(TAG, "run: FINISHED : " + DataSingelton.ig_post_data_insights);
//                    Log.d(TAG, "run: FINISHED : " + DataSingelton.ig_post_data);
                    SetProfileNameUsernamePicture();
                    spinner.setVisibility(View.GONE);
                    MainContent.setVisibility(View.VISIBLE);

                    //TODO Set data in PROFILE charts
                    SetProfileBriefData();
                    //TODO fetch data of last 5 posts
                    SetLast5Posts();
                    //TODO Set data in Interpretation :
                    ConsistencyReach.setText(String.format("%.1f %%",UtilFunctions.getConsistencyOf(DataSingelton.data.get("profile_views"))*100));
                }
                else {
                    waitForData();
                }
            }
        };
        handler.postDelayed(runnable,1);
    }

    private void SetLast5Posts() {
        //for profile Brief :
        posts_brief_graph.setData(UtilFunctions.getTop5PostsDataChartEntry(DataSingelton.ig_post_data,DataSingelton.ig_post_data_insights));
        //for the X axis : ndiroha 3la 7ssab nhar :
        posts_brief_graph.getAxisRight().setEnabled(false);
        //List<String> PostID = UtilFunctions.getTop5Posts();
        List<String> PostNum = new ArrayList<String>();
        PostNum.add("post1");
        PostNum.add("post2");
        PostNum.add("post3");
        PostNum.add("post4");
        PostNum.add("post5");
        //Log.d(TAG, "run: size timefetched : "+timeFetched.size());
        XAxis post_Xaxis = posts_brief_graph.getXAxis();
        post_Xaxis.setValueFormatter(new IndexAxisValueFormatter(PostNum));
        post_Xaxis.setCenterAxisLabels(true);
        post_Xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        post_Xaxis.setGranularity(1);
        post_Xaxis.setGranularityEnabled(true);
        post_Xaxis.setDrawGridLines(false);
        post_Xaxis.setAxisMinimum(0);
        post_Xaxis.setAxisMaximum(PostNum.size());
        posts_brief_graph.setVisibleXRange(1,PostNum.size());
        posts_brief_graph.setVisibleXRangeMaximum(5);
        posts_brief_graph.setVisibleXRangeMinimum(1);
        posts_brief_graph.groupBars(0,groupSpace,barSpace);
        posts_brief_graph.setDragEnabled(true);
        posts_brief_graph.setSelected(false);
        posts_brief_graph.setPinchZoom(false);
        posts_brief_graph.setScaleEnabled(false);
        posts_brief_graph.getDescription().setEnabled(false);
        posts_brief_graph.animateY(1300, Easing.EaseOutQuart);
        posts_brief_graph.invalidate();

        //initialise buttons :
        List<String> top5PostsURL = UtilFunctions.getTop5PostsURL();
        Log.d(TAG, "SetLast5Posts: "+top5PostsURL);
        goto_post1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(top5PostsURL.get(0)); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        goto_post2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(top5PostsURL.get(1)); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        goto_post3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(top5PostsURL.get(2)); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        goto_post4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(top5PostsURL.get(3)); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        goto_post5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(top5PostsURL.get(4)); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        //set text :
        avg_engagement_value.setText(String.format("%.1f",UtilFunctions.getPostAVGof("engagement")));
        avg_reach_value.setText(String.format("%.1f",UtilFunctions.getPostAVGof("reach")));
        avg_impressions_value.setText(String.format("%.1f",UtilFunctions.getPostAVGof("impressions")));
        reach_percentage_post_value.setText(String.format("%.1f",UtilFunctions.getPostReachPercentage()*100) + "%");
    }

    private void SetProfileNameUsernamePicture() {
        accountUsername.setText(DataSingelton.ig_username);
        accountName.setText(DataSingelton.ig_name);
        accountProfilePicture.setImageBitmap(DataSingelton.ig_BITMAPprofilePictureURL);
    }

    private void SetProfileBriefData() {
        //for profile Brief :
        profile_brief_barChart.setData(UtilFunctions.getProfileDataChartEntry(DataSingelton.data));
        //for the X axis : ndiroha 3la 7ssab nhar :
        profile_brief_barChart.getAxisRight().setEnabled(false);
        List<String> timeFetched = UtilFunctions.GetEndTimeList(DataSingelton.data);
        //Log.d(TAG, "run: size timefetched : "+timeFetched.size());
        XAxis profile_Xaxis = profile_brief_barChart.getXAxis();
        profile_Xaxis.setValueFormatter(new IndexAxisValueFormatter(timeFetched));
        profile_Xaxis.setCenterAxisLabels(true);
        profile_Xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        profile_Xaxis.setGranularity(1);
        profile_Xaxis.setGranularityEnabled(true);
        profile_Xaxis.setDrawGridLines(false);
        profile_Xaxis.setAxisMinimum(0);
        profile_Xaxis.setAxisMaximum(timeFetched.size());
        profile_brief_barChart.setVisibleXRange(1,timeFetched.size());
        profile_brief_barChart.setVisibleXRangeMaximum(5);
        profile_brief_barChart.setVisibleXRangeMinimum(1);
        profile_brief_barChart.groupBars(0,groupSpace,barSpace);
        profile_brief_barChart.setDragEnabled(true);
        profile_brief_barChart.setSelected(false);
        profile_brief_barChart.setPinchZoom(false);
        profile_brief_barChart.setScaleEnabled(false);
        profile_brief_barChart.getDescription().setEnabled(false);
        profile_brief_barChart.animateY(1300, Easing.EaseOutQuart);
        profile_brief_barChart.invalidate();
        //set the text :
        avgProfileViews.setText(String.format("%.1f",UtilFunctions.getAVGof(DataSingelton.data.get("profile_views"))));
        reachPercentage.setText(String.format("%.1f",UtilFunctions.getReachPercentage(DataSingelton.data)*100) + "%");
        avg_follower_gain_value.setText(String.format("%.1f%%",UtilFunctions.getAVGof(DataSingelton.data.get("follower_gain"))));

    }


}
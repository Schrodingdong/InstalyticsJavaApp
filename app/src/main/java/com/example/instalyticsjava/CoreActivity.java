package com.example.instalyticsjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.instalyticsjava.Util.UtilFunctions;
import com.facebook.login.LoginManager;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CoreActivity extends AppCompatActivity {
    private final String TAG ="CoreActivity";

    private DataSingelton data;
    //references in XML
    private TextView accountUsername;
    private TextView accountName;
    private ImageView accountProfilePicture;
    private Button logOutButton;
    //profile brief
    private Button ProfileBriefNameButton;
    private com.github.mikephil.charting.charts.BarChart profile_brief_barChart;
    private TextView avgProfileViews;
    private TextView reachPercentage;
    private TextView avg_follower_gain_value;
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
    //interpretation
    private Button interpretation_Name_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_core);


        //profile name username picture
        accountUsername = findViewById(R.id.AccountUsername);
        accountName = findViewById(R.id.AccountName);
        accountProfilePicture = findViewById(R.id.AccountPictureInner);


        //profile brief
        ProfileBriefNameButton = findViewById(R.id.ProfileBrief_Name_button);
        ProfileBriefNameButton.setOnClickListener(view -> {
            Intent intent = new Intent(CoreActivity.this, ProfileAnalytics.class);
            startActivity(intent);
        });
        profile_brief_barChart = findViewById(R.id.profile_brief_graph);
        profile_brief_barChart.setNoDataText("No Data Available :(");
        avgProfileViews = findViewById(R.id.avg_followersViews_value);
        reachPercentage = findViewById(R.id.reach_percentage_value);
        avg_follower_gain_value = findViewById(R.id.avg_follower_gain_value);


        //postBriefs :
        PostBriefNameButton = findViewById(R.id.posts_brief_Name_button);
        PostBriefNameButton.setOnClickListener(view -> {
            Intent intent = new Intent(CoreActivity.this, PostAnalytics.class);
            startActivity(intent);
        });
        posts_brief_graph = findViewById(R.id.posts_brief_graph);
        posts_brief_graph.setNoDataText("No Data Available :(");
        goto_post1 = findViewById(R.id.goto_post1);
        goto_post2 = findViewById(R.id.goto_post2);
        goto_post3 = findViewById(R.id.goto_post3);
        goto_post4 = findViewById(R.id.goto_post4);
        goto_post5 = findViewById(R.id.goto_post5);
        avg_engagement_value = findViewById(R.id.avg_engagement_value);
        avg_reach_value = findViewById(R.id.avg_reach_value);
        avg_impressions_value = findViewById(R.id.avg_impressions_value);
        reach_percentage_post_value = findViewById(R.id.reach_percentage_post_value);

        //interpretation:
        interpretation_Name_button = findViewById(R.id.interpretation_Name_button);
        interpretation_Name_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CoreActivity.this, InterpretationActivity.class);
                startActivity(intent);
            }
        });

        //log out
        logOutButton = findViewById(R.id.logout_button_core);
        logOutButton.setOnClickListener(view -> LogOut());
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (!(  DataSingelton.isFinishedDataInit() &&
                DataSingelton.isFinishedProfileDataInit() &&
                DataSingelton.isFinishedDataPostsInit() &&
                DataSingelton.isFinishedLifetimeDataInit_audiencecountry() &&
                DataSingelton.isFinishedLifetimeDataInit_onlinefollowers())){
            Intent intent = new Intent(this, FetchData.class);
            new waitForData(intent).start();
        }
    }

    private void LogOut() {
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(CoreActivity.this, LoggingActivity.class);
        startActivity(intent);
        DataSingelton.ResetData();
        Log.d(TAG, "onClick: data after reset : "+DataSingelton.getData());
        finish();
    }

    //TODO add a "sorry we werent able to connect :/
    private class waitForData extends Thread {
        private final int time_limit = 10000;
        private int timer = 0;
        private final ProgressBar spinner;
        private final ConstraintLayout MainContent;
        private final Intent fetchIntent;

        public waitForData(Intent fetchIntent){
            this.fetchIntent = fetchIntent;
            spinner = findViewById(R.id.progressBar1);
            MainContent = findViewById(R.id.scrollable_area);
            spinner.setVisibility(View.VISIBLE);
            MainContent.setVisibility(View.GONE);
        }

        @Override
        public void run() {
            startService(fetchIntent);
            Log.d(TAG, "onStart: started service");
            while(!((DataSingelton.isFinishedDataInit() &&
                    DataSingelton.isFinishedProfileDataInit() &&
                    DataSingelton.isFinishedDataPostsInit() &&
                    DataSingelton.isFinishedLifetimeDataInit_audiencecountry() &&
                    DataSingelton.isFinishedLifetimeDataInit_onlinefollowers()) ||
                    ++timer == time_limit)) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }}
            if(timer >= time_limit) LogOut();
            else{
                FetchData.stopService();
                runOnUiThread(new Runnable() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void run() {
                        SetProfileNameUsernamePicture();
                        SetProfileBriefData();
                        SetLast5Posts();
                        spinner.setVisibility(View.GONE);
                        MainContent.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    private float barSpace = 0f;
    private float groupSpace = 0.4f;
    private void SetProfileNameUsernamePicture() {
        accountUsername.setText(DataSingelton.getIg_username());
        accountName.setText(DataSingelton.getIg_name());
        accountProfilePicture.setImageBitmap(DataSingelton.getIg_BITMAPprofilePictureURL());
    }
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void SetProfileBriefData() {
        //for profile Brief :
        profile_brief_barChart.setData(UtilFunctions.getProfileDataChartEntry(DataSingelton.getData()));
        //for the X axis : ndiroha 3la 7ssab nhar :
        profile_brief_barChart.getAxisRight().setEnabled(false);
        List<String> timeFetched = UtilFunctions.GetEndTimeList(DataSingelton.getData());
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
        avgProfileViews.setText(String.format("%.1f",
                UtilFunctions.getAVGof(DataSingelton.getData().get("profile_views"))));
        reachPercentage.setText(String.format("%.1f",
                UtilFunctions.getReachPercentage(DataSingelton.getData())*100) + "%");
        avg_follower_gain_value.setText(String.format("%.1f%%",
                UtilFunctions.getAVGof(DataSingelton.getData().get("follower_gain"))));

    }
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void SetLast5Posts() {
        //for profile Brief :
        posts_brief_graph.setData(UtilFunctions.getTop5PostsDataChartEntry());
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
                Uri uri = Uri.parse(top5PostsURL.get(1));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        goto_post3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(top5PostsURL.get(2));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        goto_post4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(top5PostsURL.get(3));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        goto_post5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(top5PostsURL.get(4));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        //set text :
        avg_engagement_value.setText(String.format("%.1f",
                UtilFunctions.getPostAVGof("engagement")));
        avg_reach_value.setText(String.format("%.1f",
                UtilFunctions.getPostAVGof("reach")));
        avg_impressions_value.setText(String.format("%.1f",
                UtilFunctions.getPostAVGof("impressions")));
        reach_percentage_post_value.setText(String.format("%.1f",
                UtilFunctions.getPostReachPercentage()*100) + "%");
    }

}
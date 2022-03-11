package com.example.instalyticsjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.example.instalyticsjava.data.Data;
import com.example.instalyticsjava.data.Value;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ProfileAnalytics extends AppCompatActivity {

    String TAG = "ProfileAnalytics";
    private com.github.mikephil.charting.charts.BarChart profile_analytics_graph;
    private com.github.mikephil.charting.charts.LineChart followers_LineChart;
    private com.github.mikephil.charting.charts.LineChart trendline_analytics_chart;

    private TextView avg_profile_views_value;
    private TextView avg_reach_value;
    private TextView avg_impressions_value;
    private TextView reach_percentage_value;
    private TextView deviation_profile_views_value;
    private TextView deviation_impressions_value;
    private TextView deviation_reach_value;

    private TextView avg_follower_gain_value;
    private TextView deviation_follower_gain_value;

    private TextView corr_profileViews_impressions_value;
    private TextView corr_newReach_followerGain_value;
    private TextView corr_profileViews_followerGain_value;

    private TextView returning_users_value;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile_analytics);

        //textViews :
        avg_profile_views_value = (TextView) findViewById(R.id.avg_profile_views_value);
        avg_reach_value = (TextView) findViewById(R.id.avg_reach_value);
        avg_impressions_value = (TextView) findViewById(R.id.avg_impressions_value);
        reach_percentage_value = (TextView) findViewById(R.id.reach_percentage_value);
        returning_users_value = (TextView) findViewById(R.id.returning_users_value);

        deviation_profile_views_value = (TextView) findViewById(R.id.deviation_profile_views_value);
        deviation_impressions_value = (TextView) findViewById(R.id.deviation_impressions_value);
        deviation_reach_value = (TextView) findViewById(R.id.deviation_reach_value);

        avg_follower_gain_value = (TextView) findViewById(R.id.avg_follower_gain_value);
        deviation_follower_gain_value = (TextView) findViewById(R.id.deviation_follower_gain_value);

        corr_profileViews_impressions_value = (TextView) findViewById(R.id.corr_profileViews_impressions_value);
        corr_newReach_followerGain_value = (TextView) findViewById(R.id.corr_newReach_followerGain_value);
        corr_profileViews_followerGain_value = (TextView) findViewById(R.id.corr_profileViews_followerGain_value);

        //charts
        profile_analytics_graph= (com.github.mikephil.charting.charts.BarChart) findViewById(R.id.profile_analytics_graph);
        profile_analytics_graph.setNoDataText("No Data Available :(");
        trendline_analytics_chart = (com.github.mikephil.charting.charts.LineChart) findViewById(R.id.trendline_analytics_chart);
        followers_LineChart = (com.github.mikephil.charting.charts.LineChart) findViewById(R.id.followers_graph);
        if (DataSingelton.data.get("follower_gain") == null){
            followers_LineChart.setNoDataText("Unavailable follower data");
        }




    }


    private List<String> timeFetched;
    private float barSpace = 0f;
    private float groupSpace = 0.4f;
    @Override
    protected void onStart() {
        super.onStart();
        timeFetched = UtilFunctions.GetEndTimeList(DataSingelton.data);

        //TODO Set data in charts
        //profile data
        SetProfileDataChart();
        SetProfileDataText();

        //for followers :
        SetFollowersDataChart();

        SetTrendlineDataChart();
    }

    private void SetProfileDataText() {
        avg_profile_views_value.setText(String.format("%.2f",UtilFunctions.getAVGof(DataSingelton.data.get("profile_views"))));
        avg_impressions_value.setText(String.format("%.2f",UtilFunctions.getAVGof(DataSingelton.data.get("impressions"))));
        avg_reach_value.setText(String.format("%.2f",UtilFunctions.getAVGof(DataSingelton.data.get("reach"))));
        reach_percentage_value.setText(String.format("%.2f",UtilFunctions.getReachPercentage(DataSingelton.data)*100) + "%");

        deviation_profile_views_value.setText(String.format("%.2f%%",UtilFunctions.getDeviationPercentage(DataSingelton.data.get("profile_views"))*100));
        deviation_impressions_value.setText(String.format("%.2f%%",UtilFunctions.getDeviationPercentage(DataSingelton.data.get("impressions"))*100));
        deviation_reach_value.setText(String.format("%.2f%%",UtilFunctions.getDeviationPercentage(DataSingelton.data.get("reach"))*100));

        avg_follower_gain_value.setText(String.format("%.2f%%",UtilFunctions.getAVGof(DataSingelton.data.get("follower_gain"))));
        deviation_follower_gain_value.setText(String.format("%.2f%%",UtilFunctions.getDeviationPercentage(DataSingelton.data.get("follower_gain"))));


        returning_users_value.setText(String.format("%.2f%%",UtilFunctions.getReturningUsersPercentage(DataSingelton.data)*100));

        corr_profileViews_impressions_value.setText(String.format("%.2f%%",UtilFunctions.getCorrelationOf(DataSingelton.data.get("profile_views"),
                                                                                                          DataSingelton.data.get("impressions"))*100));
        corr_newReach_followerGain_value.setText(String.format("%.2f%%",UtilFunctions.getCorrelationOf(UtilFunctions.getUnfollowedReach(DataSingelton.data.get("reach"),DataSingelton.lifetimeData_onlineFollowers), //TODO
                                                                                                       DataSingelton.data.get("follower_gain"))*100));
        corr_profileViews_followerGain_value.setText(String.format("%.2f%%",UtilFunctions.getCorrelationOf(DataSingelton.data.get("profile_views"),
                                                                                                           DataSingelton.data.get("follower_gain"))*100));
    }

    private void SetFollowersDataChart() {
        followers_LineChart.setData(UtilFunctions.getFollowersDataChartEntry(DataSingelton.data));//todo dont forget to change reach to followers in the function
        //for the X axis : ndiroha 3la 7ssab nhar :
        followers_LineChart.getAxisRight().setEnabled(false);
        XAxis followers_Xaxis = followers_LineChart.getXAxis();
        followers_Xaxis.setValueFormatter(new IndexAxisValueFormatter(timeFetched));
        //followers_Xaxis.setCenterAxisLabels(true);
        followers_Xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        followers_Xaxis.setGranularity(1);
        followers_Xaxis.setGranularityEnabled(true);
        followers_Xaxis.setDrawGridLines(false);
        followers_Xaxis.setAxisMinimum(-0.25f);
        followers_Xaxis.setAxisMaximum(timeFetched.size()-0.75f);
        followers_LineChart.setVisibleXRange(1,timeFetched.size());
        followers_LineChart.setVisibleXRangeMaximum(3);
        followers_LineChart.setVisibleXRangeMinimum(1);
        followers_LineChart.setVisibleXRangeMaximum(5);
        followers_LineChart.setDragEnabled(true);
        followers_LineChart.setSelected(false);
        followers_LineChart.setPinchZoom(false);
        followers_LineChart.setScaleEnabled(false);
        followers_LineChart.getDescription().setEnabled(false);
        followers_LineChart.invalidate();
    }
    private void SetProfileDataChart() {
        //for profile Brief :
        profile_analytics_graph.setData(UtilFunctions.getProfileDataChartEntry(DataSingelton.data));
        //for the X axis : ndiroha 3la 7ssab nhar :
        profile_analytics_graph.getAxisRight().setEnabled(false);
        //Log.d(TAG, "run: size timefetched : "+timeFetched.size());
        XAxis profile_Xaxis = profile_analytics_graph.getXAxis();
        profile_Xaxis.setValueFormatter(new IndexAxisValueFormatter(timeFetched));
        profile_Xaxis.setCenterAxisLabels(true);
        profile_Xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        profile_Xaxis.setGranularity(1);
        profile_Xaxis.setGranularityEnabled(true);
        profile_Xaxis.setDrawGridLines(false);
        profile_Xaxis.setAxisMinimum(0);
        profile_Xaxis.setAxisMaximum(timeFetched.size());
        profile_analytics_graph.setVisibleXRange(1,timeFetched.size());
        profile_analytics_graph.setVisibleXRangeMaximum(5);
        profile_analytics_graph.setVisibleXRangeMinimum(1);
        profile_analytics_graph.groupBars(0,groupSpace,barSpace);
        profile_analytics_graph.setDragEnabled(true);
        profile_analytics_graph.setSelected(false);
        profile_analytics_graph.setPinchZoom(false);
        profile_analytics_graph.setScaleEnabled(false);
        profile_analytics_graph.getDescription().setEnabled(false);
        profile_analytics_graph.animateY(1300, Easing.EaseOutQuart);
        profile_analytics_graph.invalidate();
    }
    private void SetTrendlineDataChart(){
        //todo ADD THE BIC TO GET THE PERFECT POLYNOMIAL ORDER
        LineData lineData = new LineData();
        //create a list of Line entries + create the data sets

        LineDataSet lineDataSet;
        int[] rgb = new int[3];
        int polynomialOrder;
        //TODO : profile views
        polynomialOrder = UtilFunctions.getPolynomialOrder(UtilFunctions.getMetricValueList(DataSingelton.data.get("profile_views")));
        double[] coef_profileViews = UtilFunctions.getPolynomialRegressionCoefficient(UtilFunctions.getMetricValueList(DataSingelton.data.get("profile_views")),polynomialOrder);
        rgb[0] = 255; rgb[1] = 179 ; rgb[2] = 215;
        SetLinedataSet(lineData, coef_profileViews,"Profile Views",rgb);
        Log.d(TAG, "SetTrendlineDataChart: profile_views order : "+polynomialOrder);

        //TODO : reach
        polynomialOrder = UtilFunctions.getPolynomialOrder(UtilFunctions.getMetricValueList(DataSingelton.data.get("reach")));
        double[] coef_reach = UtilFunctions.getPolynomialRegressionCoefficient(UtilFunctions.getMetricValueList(DataSingelton.data.get("reach")),polynomialOrder);
        rgb[0] = 224; rgb[1] = 179 ; rgb[2] = 255;
        SetLinedataSet(lineData, coef_reach,"Reach",rgb);
        Log.d(TAG, "SetTrendlineDataChart: reach order : "+polynomialOrder);

        //TODO : impressions
        polynomialOrder = UtilFunctions.getPolynomialOrder(UtilFunctions.getMetricValueList(DataSingelton.data.get("impressions")));
        double[] coef_impressions = UtilFunctions.getPolynomialRegressionCoefficient(UtilFunctions.getMetricValueList(DataSingelton.data.get("impressions")),polynomialOrder);
        Log.d(TAG, "SetTrendlineDataChart: impressions order : "+polynomialOrder);
        rgb[0] = 161; rgb[1] = 168 ; rgb[2] = 255;
        SetLinedataSet(lineData, coef_impressions,"Impressions",rgb);

        trendline_analytics_chart.setData(lineData);
        trendline_analytics_chart.getAxisRight().setEnabled(false);
        XAxis trendline_Xaxis = trendline_analytics_chart.getXAxis();
        Collections.reverse(timeFetched);
        trendline_Xaxis.setValueFormatter(new IndexAxisValueFormatter(timeFetched));
        Collections.reverse(timeFetched);
        trendline_Xaxis.setCenterAxisLabels(true);
        trendline_Xaxis.setGranularity(1);
        trendline_Xaxis.setGranularityEnabled(true);
        trendline_Xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        trendline_Xaxis.setDrawGridLines(false);
        //trendline_analytics_chart.getAxisLeft().setEnabled(false);
        trendline_analytics_chart.setDragEnabled(true);
        trendline_analytics_chart.setSelected(false);
        trendline_analytics_chart.setPinchZoom(false);
        trendline_analytics_chart.setScaleEnabled(false);
        trendline_analytics_chart.getDescription().setEnabled(false);
        trendline_analytics_chart.invalidate();


        //TODO : follower gain

    }

    private void SetLinedataSet(LineData lineData, double[] coeffs,String name,int[] rgb) {
        ArrayList<Entry> entryArrayList = new ArrayList<>();
        LineDataSet lineDataSet;
        final int stepNumber = 100;
        final float stepSize= (float)14/(float)stepNumber;
        for (float x = 1; x <= 14 ; x +=stepSize){
            float fx = 0;
            for (int i = 0; i < coeffs.length ; i++){
                fx += coeffs[i]*Math.pow(x,i);
            }
            entryArrayList.add(new Entry( x, fx));
        }
        lineDataSet = new LineDataSet(entryArrayList, name);
        lineDataSet.setCircleHoleColor(Color.rgb(rgb[0],rgb[1],rgb[2]));
        lineDataSet.setCircleColor(Color.rgb(rgb[0],rgb[1],rgb[2]));
        lineDataSet.setColor(Color.rgb(rgb[0],rgb[1],rgb[2]));
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setLineWidth(2f);
        lineData.addDataSet(lineDataSet);
    }
}
package com.example.instalyticsjava;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.example.instalyticsjava.Util.UtilFunctions;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PostAnalytics extends AppCompatActivity {
    private final String TAG = "PostAnalytics";

    private com.github.mikephil.charting.charts.BarChart post_analytics_graph;
    private TextView avg_engagement_value;
    private TextView avg_reach_value;
    private TextView avg_impressions_value;
    private TextView reach_percentage_value;
    private TextView deviation_engagement_value;
    private TextView deviation_reach_value;
    private TextView deviation_impressions_value;
    private TextView dominant_media_type_value;

    private TextView corr_reach_saved_value;
    private TextView corr_engagement_saved_value;

    private TextView Hashtag_post_analytics_title;
    private com.github.mikephil.charting.charts.LineChart trendline_post_analytics_chart;

    private final float barSpace = 0f;
    private final float groupSpace = 0.4f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_post_analytics);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setPostDataChart();
        setPostDataAVGText();
        setPostDataCorrText();
        setPostTrendlineDataChart();
        setHashtagSection();
    }

    private void setHashtagSection() {
        Hashtag_post_analytics_title = findViewById(R.id.Hashtag_post_analytics_title);
        Hashtag_post_analytics_title.setOnClickListener(view -> {
            Intent intent = new Intent(this,HashtagList.class);
            startActivity(intent);
        });
    }

    private void setPostDataChart(){                                                                //plot the last 10 posts
        post_analytics_graph = findViewById(R.id.post_analytics_graph);
        post_analytics_graph.setData(UtilFunctions.getLast10PostsDataChartEntry(DataSingelton.getIg_post_data(),
                                                                                    DataSingelton.getIg_post_data_insights()));
        post_analytics_graph.getAxisRight().setEnabled(false);                                      //for the X axis : ndiroha 3la 7ssab nhar :
        List<String> PostNum = new ArrayList<>();
        for (int i = 1; i <= 10 ; i++){
            PostNum.add("Post "+i);
        }
        XAxis post_Xaxis = post_analytics_graph.getXAxis();
        post_Xaxis.setValueFormatter(new IndexAxisValueFormatter(PostNum));
        post_Xaxis.setCenterAxisLabels(true);
        post_Xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        post_Xaxis.setGranularity(1);
        post_Xaxis.setGranularityEnabled(true);
        post_Xaxis.setDrawGridLines(false);
        post_Xaxis.setAxisMinimum(0);
        post_Xaxis.setAxisMaximum(PostNum.size());
        post_analytics_graph.setVisibleXRange(1,PostNum.size());
        post_analytics_graph.setVisibleXRangeMaximum(5);
        post_analytics_graph.setVisibleXRangeMinimum(1);
        post_analytics_graph.groupBars(0,groupSpace,barSpace);
        post_analytics_graph.setDragEnabled(true);
        post_analytics_graph.setSelected(false);
        post_analytics_graph.setPinchZoom(false);
        post_analytics_graph.setScaleEnabled(false);
        post_analytics_graph.getDescription().setEnabled(false);
        post_analytics_graph.animateY(1300, Easing.EaseOutQuart);
        post_analytics_graph.invalidate();
    }
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void setPostDataAVGText(){
        avg_engagement_value = findViewById(R.id.avg_engagement_value);
        avg_reach_value = findViewById(R.id.avg_reach_value);
        avg_impressions_value = findViewById(R.id.avg_impressions_value);
        reach_percentage_value = findViewById(R.id.reach_percentage_value);
        deviation_engagement_value = findViewById(R.id.deviation_engagement_value);
        deviation_reach_value = findViewById(R.id.deviation_reach_value);
        deviation_impressions_value = findViewById(R.id.deviation_impressions_value);
        dominant_media_type_value = findViewById(R.id.dominant_media_type_value);

        avg_engagement_value.setText(String.format("%.1f",
                UtilFunctions.getPostAVGof("engagement")));
        avg_reach_value.setText(String.format("%.1f",
                UtilFunctions.getPostAVGof("reach")));
        avg_impressions_value.setText(String.format("%.1f",
                UtilFunctions.getPostAVGof("impressions")));
        reach_percentage_value.setText(String.format("%.1f",
                UtilFunctions.getPostReachPercentage()*100) + "%");
        deviation_engagement_value.setText(String.format("%.1f",
                UtilFunctions.getPostDeviationPercentage("engagement")*100) + "%");
        deviation_reach_value.setText(String.format("%.1f",
                UtilFunctions.getPostDeviationPercentage("reach")*100) + "%");
        deviation_impressions_value.setText(String.format("%.1f",
                UtilFunctions.getPostDeviationPercentage("impressions")*100) + "%");
        dominant_media_type_value.setText(UtilFunctions.getPredominantMediaType());
    }
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void setPostDataCorrText(){
        corr_reach_saved_value = findViewById(R.id.corr_reach_saved_value);
        corr_engagement_saved_value = findViewById(R.id.corr_engagement_saved_value);

        corr_reach_saved_value.setText(String.format("%.1f",UtilFunctions.getPostCorrelationOf("reach","saved")*100) + "%");
        corr_engagement_saved_value.setText(String.format("%.1f",UtilFunctions.getPostCorrelationOf("engagement","reach")*100) + "%");
    }
    private void setPostTrendlineDataChart(){
        trendline_post_analytics_chart = findViewById(R.id.trendline_post_analytics_chart);
        LineData lineData = new LineData();
        //create a list of Line entries + create the data sets
        LineDataSet lineDataSet;
        int[] rgb = new int[3];
        int polynomialOrder;
        //engagement
        polynomialOrder = UtilFunctions.getPolynomialOrder(UtilFunctions.getPostMetricValueList("engagement"));
        double[] coef_profileViews = UtilFunctions.getPolynomialRegressionCoefficient(UtilFunctions.getPostMetricValueList("engagement"),3);
        rgb[0] = 255; rgb[1] = 179 ; rgb[2] = 215;
        SetLinedataSet(lineData, coef_profileViews,"Profile Views",rgb);

        //reach
        polynomialOrder = UtilFunctions.getPolynomialOrder(UtilFunctions.getPostMetricValueList("reach"));
        double[] coef_reach = UtilFunctions.getPolynomialRegressionCoefficient(UtilFunctions.getPostMetricValueList("reach"),3);
        rgb[0] = 224; rgb[1] = 179 ; rgb[2] = 255;
        SetLinedataSet(lineData, coef_reach,"Reach",rgb);

        //impressions
        polynomialOrder = UtilFunctions.getPolynomialOrder(UtilFunctions.getPostMetricValueList("impressions"));
        double[] coef_impressions = UtilFunctions.getPolynomialRegressionCoefficient(UtilFunctions.getPostMetricValueList("impressions"),3);
        rgb[0] = 161; rgb[1] = 168 ; rgb[2] = 255;
        SetLinedataSet(lineData, coef_impressions,"Impressions",rgb);

        trendline_post_analytics_chart.setData(lineData);
        trendline_post_analytics_chart.getAxisRight().setEnabled(false);
        XAxis trendline_Xaxis = trendline_post_analytics_chart.getXAxis();
        List<String> PostNum = new ArrayList<>();
        for (int i = 1; i <= 10 ; i++){
            PostNum.add("Post "+i);
        }
        Collections.reverse(PostNum);
        trendline_Xaxis.setValueFormatter(new IndexAxisValueFormatter(PostNum));
        Collections.reverse(PostNum);

        trendline_Xaxis.setCenterAxisLabels(true);
        trendline_Xaxis.setGranularity(1);
        trendline_Xaxis.setGranularityEnabled(true);
        trendline_Xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        trendline_Xaxis.setDrawGridLines(false);
        trendline_post_analytics_chart.setDragEnabled(true);
        trendline_post_analytics_chart.setSelected(false);
        trendline_post_analytics_chart.setPinchZoom(false);
        trendline_post_analytics_chart.setScaleEnabled(false);
        trendline_post_analytics_chart.getDescription().setEnabled(false);
        trendline_post_analytics_chart.invalidate();
    }
    private void SetLinedataSet(LineData lineData, double[] coeffs,String name,int[] rgb) {
        ArrayList<Entry> entryArrayList = new ArrayList<>();
        LineDataSet lineDataSet;
        final int stepNumber = 100;
        final float stepSize= (float)6/(float)stepNumber;
        for (float x = 1; x <= 10 ; x +=stepSize){
            float fx = 0;
            for (int i = 0; i < coeffs.length ; i++){
                fx += coeffs[i]*Math.pow(11-x,i);   //dak le 11 = 10+1 bach n3kso le graph
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
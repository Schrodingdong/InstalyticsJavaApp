package com.example.instalyticsjava.Util;

import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.instalyticsjava.DataSingelton;
import com.example.instalyticsjava.data.Data;
import com.example.instalyticsjava.data.Value;
import com.example.instalyticsjava.dataposts.PostData;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UtilFunctions extends AppCompatActivity {
    private final static String TAG = "UtilFunctions";


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
                    break;
                }
                case "profile_views": {
                    int index = 0;
                    for (Value v : element.getValue().getValues()) {    //instantiate the entry for the ith metric
                        entryArrayList_profileViews.add(new BarEntry(index, v.getValue()));
                        index++;
                    }
                    Collections.reverse(entryArrayList_profileViews);
                    break;
                }
                case "impressions" : {
                    int index = 0;
                    for (Value v : element.getValue().getValues()) {    //instantiate the entry for the ith metric
                        entryArrayList_impressions.add(new BarEntry(index, v.getValue()));
                        index++;
                    }
                    Collections.reverse(entryArrayList_impressions);
                    break;
                }
                default:
                    break;
            }
            barData.setBarWidth(bar_width);
        }
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
        if (profileData.get("follower_count") == null) return null;
        LineData lineData;
        for (Map.Entry<String, Data> element : profileData.entrySet()) {
            ArrayList<Entry> entryArrayList = new ArrayList<>();
            int index = 0;
            float offset = 0.5f;//bach yji nichan lwst
            if (element.getKey().compareTo("follower_count") == 0){
                ArrayList<Integer> tmp = new ArrayList<>();
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
        for (int i=0; i < 5 ; i++){
            float max = -1;
            String maxID = "";
            //an sommiw dakchi li glna 9bl
            for (Map.Entry<String, Map<String,Data>> entry: DataSingelton.getIg_post_data_insights().entrySet()) {
                if (Top5Posts.contains(entry.getKey()))
                    continue;
                int eng = Objects.requireNonNull(entry.getValue().get("engagement")).getValues().get(0).getValue();
                int impr = Objects.requireNonNull(entry.getValue().get("impressions")).getValues().get(0).getValue();
                int reach = Objects.requireNonNull(entry.getValue().get("reach")).getValues().get(0).getValue();
                float postNotoriety = (float) eng+impr+reach / 3f;
                if (max < postNotoriety){
                    max = postNotoriety;
                    maxID = entry.getKey();
                }
            }
            Top5Posts.add(maxID);           //lakan 9l mn 5 posts kay koun le rest null
        }

        return Top5Posts;
    }
    @NonNull
    public static BarData getTop5PostsDataChartEntry(){
        List<String> Top5Posts = getTop5Posts();
        Map<String, Map<String,Data>> postDataInsights = DataSingelton.getIg_post_data_insights();
        BarData barData;
        //create a list of bar entries + create the data sets
        float bar_width = 0.2f;
        ArrayList<BarEntry> entryArrayList_reach = new ArrayList<>();
        ArrayList<BarEntry> entryArrayList_engagement = new ArrayList<>();
        ArrayList<BarEntry> entryArrayList_impressions = new ArrayList<>();
        int index;
        for (index =0 ; index < 5 ; index++){
            for (Map.Entry<String, Map<String,Data>> element : postDataInsights.entrySet()){

                String currentPostID = element.getKey();
                if (!Top5Posts.get(index).equals(currentPostID)) continue;
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
        List<String> ig_postIDs = DataSingelton.getIg_postIDs();
        BarData barData;
        //create a list of bar entries + create the data sets
        float bar_width = 0.2f;
        ArrayList<BarEntry> entryArrayList_reach = new ArrayList<>();
        ArrayList<BarEntry> entryArrayList_engagement = new ArrayList<>();
        ArrayList<BarEntry> entryArrayList_impressions = new ArrayList<>();
        int counter = 0;
        for (String ID : ig_postIDs){
            if (counter >= 10) break;
            Map<String,Data> element = DataSingelton.getIg_post_data_insights().get(ID);
            assert element != null;
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
            result.add(Objects.requireNonNull(DataSingelton.getIg_post_data().get(postID)).getPermalink());
        }
        return result;
    }

    public static String getPredominantMediaType(){
        //based on engagement
        Map<String,Integer> engagementTracker = new HashMap<String,Integer>();
        engagementTracker.put("IMAGE",0);
        engagementTracker.put("VIDEO",0);
        engagementTracker.put("CAROUSEL_ALBUM",0);
        for (String id :
                DataSingelton.getIg_postIDs()) {
            int eng_value;
            switch (Objects.requireNonNull(DataSingelton.getIg_post_data().get(id)).getMedia_type()){
                case "IMAGE":
                    eng_value = engagementTracker.get("IMAGE");
                    eng_value+= DataSingelton.getIg_post_data_insights().get(id).get("engagement").getValues().get(0).getValue();
                    engagementTracker.put("IMAGE",eng_value);
                    break;
                case "VIDEO":
                    eng_value = engagementTracker.get("VIDEO");
                    eng_value+= DataSingelton.getIg_post_data_insights().get(id).get("engagement").getValues().get(0).getValue();
                    engagementTracker.put("VIDEO",eng_value);
                    break;
                case "CAROUSEL_ALBUM":
                    eng_value = engagementTracker.get("CAROUSEL_ALBUM");
                    eng_value+= DataSingelton.getIg_post_data_insights().get(id).get("engagement").getValues().get(0).getValue();
                    engagementTracker.put("CAROUSEL_ALBUM",eng_value);
                    break;
                default:
                    break;
            }
        }
        int max = -1;
        String dominant_mediaType = "";
        for (Map.Entry<String,Integer> element:
                engagementTracker.entrySet()) {
            if (max < element.getValue()){
                max  = element.getValue();
                dominant_mediaType = element.getKey();
            }
        }
        return dominant_mediaType;
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
    public static double getAVGof(List<?> list){
        if (list == null) return 0;
        double average = 0;
        for (int i = 0 ; i < list.size(); i++){
            average += (double) list.get(i);
        }
        average /= list.size();
        return average;
    }
    public static double getPostAVGof(String metric){
        if (metric == null) return 0;
        double average = 0;
        for (Map.Entry<String , Map<String,Data>> element:
             DataSingelton.getIg_post_data_insights().entrySet()) {
            average += Objects.requireNonNull(element.getValue().get(metric)).getValues().get(0).getValue();
        }
        average /= DataSingelton.getIg_post_data_insights().size();
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
                DataSingelton.getIg_post_data_insights().entrySet()) {
            result += Math.abs(Objects.requireNonNull(element.getValue().get(metric)).getValues().get(0).getValue() - average);
        }
        result /= DataSingelton.getIg_post_data_insights().size();
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
        List<Integer> returnlist = new ArrayList<>();
        List<Value> valueOfMetric = metric.getValues();
        for (Value v : valueOfMetric){
            returnlist.add(v.getValue());
        }
        return returnlist;
    }
    @NonNull
    public static List<Integer> getPostMetricValueList(@NonNull String metric){
        List<Integer> returnlist = new ArrayList<>();
        for (Map.Entry<String , Map<String,Data>> element:
                DataSingelton.getIg_post_data_insights().entrySet()) {
            returnlist.add(Objects.requireNonNull(element.getValue().get(metric)).getValues().get(0).getValue());
        }
        return returnlist;
    }


    //TODO what the fuck
    private static final double unfollowerReachCoefficient = 0.5; //50%
    public static Data getUnfollowedReach(Data reach, Map<String,Data> profile_data){
        if (reach == null || profile_data == null) return null ;
        Data online_followers = profile_data.get("data");
        Data returnData = new Data();
        returnData.setName("unfollowed_reach");
        List<Value> reachList = reach.getValues();
        assert online_followers != null;
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
        List<Value> reach = Objects.requireNonNull(data.get("reach")).getValues();
        List<Value> impressions = Objects.requireNonNull(data.get("impressions")).getValues();
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
                DataSingelton.getIg_post_data_insights().entrySet()) {
            reach.add(Objects.requireNonNull(element.getValue().get("reach")).getValues().get(0));
            impressions.add(Objects.requireNonNull(element.getValue().get("impressions")).getValues().get(0));
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
    public static double E(@NonNull Data metric){
        List<Integer> X = new ArrayList<>();
        for (Value value:metric.getValues()) {
            X.add(value.getValue());
        }
        double e = 0;
        for (int x:X) e+=x;
        e /= X.size();
        return e;
    }
    public static double EsquaredPost(@NonNull String metric){
        List<Integer> X = new ArrayList<>();
        for (Map.Entry<String , Map<String,Data>> element:
                DataSingelton.getIg_post_data_insights().entrySet()) {
            X.add(Objects.requireNonNull(element.getValue().get(metric)).getValues().get(0).getValue());
        }
        double e = 0;
        for (int x:X) e+=x*x;
        e /= X.size();
        return e;
    }
    public static double Esquared(@NonNull Data metric){
        List<Integer> X = new ArrayList<>();
        for (Value value:metric.getValues()) {
            X.add(value.getValue());
        }
        double e = 0;
        for (int x:X) e+=x*x;
        e /= X.size();
        return e;
    }
    public static double EPost(@NonNull String metric){
        List<Integer> X = new ArrayList<>();
        for (Map.Entry<String , Map<String,Data>> element:
                DataSingelton.getIg_post_data_insights().entrySet()) {
            X.add(Objects.requireNonNull(element.getValue().get(metric)).getValues().get(0).getValue());
        }
        double e = 0;
        for (int x:X) e+=x;
        e /= X.size();
        return e;
    }
    public static double getVarience(@NonNull Data metric){
        double expected = E(metric);
        return Esquared(metric) - expected*expected;
    }
    public static double getPostVarience(@NonNull String metric){
        double expected = EPost(metric);
        return EsquaredPost(metric) - expected*expected;
    }
    public static double getStddeviation(@NonNull Data metric){
        return Math.sqrt(getVarience(metric));
    }
    public static double getPostStddeviation(@NonNull String metric){
        return Math.sqrt(getPostVarience(metric));
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
                DataSingelton.getIg_post_data_insights().entrySet()) {
            X1.add(Objects.requireNonNull(element.getValue().get(metric1)).getValues().get(0).getValue());
        }
        List<Integer> X2 = new ArrayList<>();
        for (Map.Entry<String , Map<String,Data>> element:
                DataSingelton.getIg_post_data_insights().entrySet()) {
            X2.add(Objects.requireNonNull(element.getValue().get(metric2)).getValues().get(0).getValue());
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
        return fitter.fit(obs.toList());
    }
    //this technique requires an already existing coefficients
    public static float BIC(@NonNull List<Integer> dataForRegression, double[] coefficients, int orderK ){
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

package com.example.instalyticsjava.interpretation.ml;

import android.util.Log;
import android.util.StateSet;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;


public class LinearRegression {
    private final String TAG = "LinearRegression";
    private DMatrixRMaj X = new DMatrixRMaj();
    private DMatrixRMaj y = new DMatrixRMaj();
    private DMatrixRMaj theta= new DMatrixRMaj();
    private DMatrixRMaj F= new DMatrixRMaj();
    private List<DMatrixRMaj> costHistory = new ArrayList<>();
    private double modelAccuracy;

    private List<Integer> reach_list = new ArrayList<>();
    private List<Integer> profileViews_list = new ArrayList<>();
    private List<Integer> websiteClicks_list = new ArrayList<>();
    private List<Integer> order_list = new ArrayList<>();
    private List<List<Integer>> metricLists = new ArrayList<>();
    private int rowNumber,featureNumberAndOnes;


    public LinearRegression(List<Integer> reach_list,
                            List<Integer> profileViews_list,
                            List<Integer> websiteClicks_list,
                            List<Integer> order_list){
        this.reach_list = reach_list ;
        this.profileViews_list = profileViews_list;
        this.websiteClicks_list = websiteClicks_list;
        this.order_list = order_list;
        metricLists.add(reach_list);
        metricLists.add(profileViews_list);
        metricLists.add(websiteClicks_list);
        metricLists.add(order_list);
        rowNumber = reach_list.size();
        featureNumberAndOnes = 4;
        Log.d(TAG, "LinearRegression: "+metricLists);
        initiateModel();
        trainModel();
        Log.d(TAG, "LinearRegression: "+X);
        Log.d(TAG, "LinearRegression: "+y);
        Log.d(TAG, "LinearRegression: "+theta);
    }


    public int getPrediction(double reach, double web_click,double profile_views){
        DMatrixRMaj input = new DMatrixRMaj(1,featureNumberAndOnes);
        input.add(0,0,reach);
        input.add(0,1,profile_views);
        input.add(0,2,web_click);
        input.add(0,3,1);
        DMatrixRMaj output = model(input);
        double outputDouble = output.get(0,0);                                          //todo it spits out only one thing,look for multiple inputs and take the avg ?
        return (int) outputDouble;
    }

    private void initiateModel() {
        this.X = setMatrix();
        this.theta = setTheta();
        this.y = setY();
    }
    private void trainModel() {
        long iterations = 10000;
        double learning_rate = 0.001;
        gradientDescent(iterations,learning_rate);
        modelAccuracy = coef_determination(model(X));
        Log.d(TAG, "trainModel: modelAccuracy = "+modelAccuracy);
    }

    @NonNull
    private DMatrixRMaj setMatrix(){
        DMatrixRMaj X = new DMatrixRMaj();
        X.reshape(rowNumber,featureNumberAndOnes);
        for (int i = 0; i < 4;i++){
            for (int j = 0; j < rowNumber; j++){
                int valToAdd = (i == 3)? 1:metricLists.get(i).get(j);
                X.add(j,i,valToAdd);
            }
        }
        return X;
    }
    @NonNull
    private DMatrixRMaj setTheta(){
        DMatrixRMaj theta = new DMatrixRMaj(featureNumberAndOnes,1);
        for (int i = 0; i < featureNumberAndOnes; i++){
            theta.add(i,0,random(100));
        }
        return theta;
    }
    @NonNull
    private DMatrixRMaj setY(){
        DMatrixRMaj y = new DMatrixRMaj(rowNumber,1);
        for (int i = 0; i < rowNumber; i++){
            y.add(i,0,metricLists.get(3).get(i));
        }
        return y;
    }

    private DMatrixRMaj model(){
        DMatrixRMaj F = new DMatrixRMaj();
        CommonOps_DDRM.mult(X,theta,F);
        return F;
    }
    private DMatrixRMaj model(DMatrixRMaj inputData){
        DMatrixRMaj F = new DMatrixRMaj();
        CommonOps_DDRM.mult(inputData,theta,F);
        return F;
    }
    private double cost_function(){
        DMatrixRMaj sumParam = model();
        CommonOps_DDRM.subtract(sumParam, y,sumParam);
        CommonOps_DDRM.elementPower(sumParam,2,sumParam);
        double sum = CommonOps_DDRM.elementSum(sumParam);
        double coef = 1/(float)(2*rowNumber);
        return coef * sum;
    }
    private DMatrixRMaj grad(){
        double coef = 1/(float)rowNumber;
        DMatrixRMaj param = model();
        CommonOps_DDRM.subtract(param,setY(),param);

        DMatrixRMaj Xt = setMatrix();
        CommonOps_DDRM.transpose(Xt);
        DMatrixRMaj multiplicationResult = new DMatrixRMaj();
        CommonOps_DDRM.mult(Xt,param,multiplicationResult);

        DMatrixRMaj grad = new DMatrixRMaj();
        CommonOps_DDRM.scale(coef,multiplicationResult,grad);

        return grad;
    }
    private DMatrixRMaj gradientDescent(long iterations, double learning_rate){
        costHistory.clear();

        for (int i = 0; i < iterations ; i++){
            DMatrixRMaj newTheta = new DMatrixRMaj();
            CommonOps_DDRM.scale(learning_rate,grad(),newTheta);
            CommonOps_DDRM.subtract(theta,newTheta,newTheta);
            this.theta = newTheta;
            costHistory.add(theta);
        }
        return theta;
    }
    private double coef_determination(DMatrixRMaj prediciton){
        double u,v;
        DMatrixRMaj difference = new DMatrixRMaj();
        DMatrixRMaj differenceSquared = new DMatrixRMaj();

        CommonOps_DDRM.subtract(y,prediciton,difference);
        CommonOps_DDRM.elementPower(difference,2,differenceSquared);
        u = CommonOps_DDRM.elementSum(differenceSquared);

        double yAvg = CommonOps_DDRM.elementSum(y);
        yAvg /= y.getNumRows();
        CommonOps_DDRM.subtract(y,yAvg,difference);
        CommonOps_DDRM.elementPower(difference,2,differenceSquared);
        v = CommonOps_DDRM.elementSum(differenceSquared);

        return 1 - (u/v);

    }

    private double random(int max){
        return Math.random()*max;
    }

    public double getModelAccuracy() {
        return modelAccuracy;
    }
}

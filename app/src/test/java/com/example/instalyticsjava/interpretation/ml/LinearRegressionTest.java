package com.example.instalyticsjava.interpretation.ml;

import static org.junit.Assert.*;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class LinearRegressionTest {
    int [][] metricLists ={
            {8,4,5,9,8},
            {3,3,1,6,2},
            {0,1,2,0,0},
            {2,1,0,3,0},
    };
    int rowNumber = metricLists[0].length;
    int featureNumberAndOnes = metricLists.length;
    private DMatrixRMaj X = new DMatrixRMaj();
    private DMatrixRMaj y = new DMatrixRMaj();
    private DMatrixRMaj theta= new DMatrixRMaj();
    private DMatrixRMaj F= new DMatrixRMaj();
    private List<DMatrixRMaj> costHistory = new ArrayList<>();



    public DMatrixRMaj setMatrix(){
        DMatrixRMaj X = new DMatrixRMaj();
        X.reshape(rowNumber,featureNumberAndOnes);
        for (int i = 0; i < 4;i++){
            for (int j = 0; j < rowNumber; j++){
                int valToAdd = (i == 3)? 1:metricLists[i][j];
                X.add(j,i,valToAdd);
            }
        }
        return X;
    }
    public DMatrixRMaj setTheta(){
        DMatrixRMaj theta = new DMatrixRMaj(featureNumberAndOnes,1);
        for (int i = 0; i < featureNumberAndOnes; i++){
            theta.add(i,0,Math.random()*100);
        }
        return theta;
    }
    public DMatrixRMaj setY(){
        DMatrixRMaj y = new DMatrixRMaj(rowNumber,1);
        for (int i = 0; i < rowNumber; i++){
            y.add(i,0,metricLists[3][i]);
        }
        return y;
    }


    public DMatrixRMaj model(){
        DMatrixRMaj F = new DMatrixRMaj();
        CommonOps_DDRM.mult(X,theta,F);
        return F;
    }
    public double cost_function(){
        DMatrixRMaj sumParam = model();
        CommonOps_DDRM.subtract(sumParam, y,sumParam);
        CommonOps_DDRM.elementPower(sumParam,2,sumParam);
        double sum = CommonOps_DDRM.elementSum(sumParam);
        double coef = 1/(float)(2*rowNumber);
        return coef * sum;
    }
    public DMatrixRMaj grad(){
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

    public DMatrixRMaj gradientDescent(long iterations, double learning_rate){
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


    @Test
    public void coef_determination() {
        double u,v;
        y = setY();
        X = setMatrix();
        theta = setTheta();

        System.out.println("avant" + theta);
        gradientDescent(100000,0.01);
        System.out.println("apres" +theta);


        DMatrixRMaj prediciton = model();
        System.out.println(prediciton);



        DMatrixRMaj difference = new DMatrixRMaj();
        DMatrixRMaj differenceSquared = new DMatrixRMaj();

        CommonOps_DDRM.subtract(y,prediciton,difference);
        System.out.println(difference);
        CommonOps_DDRM.elementPower(difference,2,differenceSquared);
        System.out.println(differenceSquared);

        u = CommonOps_DDRM.elementSum(differenceSquared);
        System.out.println(u);

        double yAvg = CommonOps_DDRM.elementSum(y);
        yAvg /= y.getNumRows();
        CommonOps_DDRM.subtract(y,yAvg,difference);
        CommonOps_DDRM.elementPower(difference,2,differenceSquared);
        v = CommonOps_DDRM.elementSum(differenceSquared);
        System.out.println(v);

        System.out.println(1-(u/v));
        System.out.println("the prediction : "+prediciton);
        assertEquals(1,1);

    }
}
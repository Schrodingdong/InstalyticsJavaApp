package com.example.instalyticsjava;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.junit.Test;

import static org.junit.Assert.*;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        for (int i  =0 ; i < 10 ; i++){
                assertEquals(i, i);
        }
    }
    @Test
    public void epochConversion(){
        //sous format : 2022-03-14T07:00:00+0000
        String end_time = "2022-03-14T07:00:00+0000";
        String[] listOfString = end_time.split("T",3);
        end_time = (end_time.split("T",3))[0];
        System.out.println(end_time + " && " +listOfString[1]);

        SimpleDateFormat myDate = new SimpleDateFormat("yyyy-MM-dd");
        long epoch = 0;
        try {
            myDate.setTimeZone(TimeZone.getTimeZone("UTC"));
            epoch =myDate.parse(end_time).getTime()/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assertEquals(1647216000,epoch);
    }

    @Test
    public void setMatrix(){
        int [][] metricLists ={
                {3,4,5,6,8},
                {8,3,1,9,0},
                {0,1,2,0,0},
        };
        DMatrixRMaj ones= new DMatrixRMaj();
        int rowNumber = metricLists[0].length;
        int colNumber = 4;
        DMatrixRMaj X = new DMatrixRMaj();
        X.reshape(rowNumber,colNumber);
        for (int i = 0; i < 4;i++){
            for (int j = 0; j < rowNumber; j++){
                int valToAdd = (i == 3)? 1:metricLists[i][j] ;
                X.add(j,i,valToAdd);
            }
        }
        System.out.println(X);
        assertEquals(1647216000,1);

    }



}
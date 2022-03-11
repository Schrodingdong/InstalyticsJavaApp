package com.example.instalyticsjava.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Value {
    public Value(JSONObject valueObject){
        try {
            this.end_time = valueObject.get("end_time").toString();
        }
        catch (JSONException e) {;}
        try{
            this.value = Integer.parseInt(valueObject.get("value").toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public Value(){
        this.end_time = "";
        this.value = 0;
    }


    String end_time;
    int value;

    public int getValue() {
        return value;
    }
    public String getEnd_time() {
        return end_time;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}

/*
data class Value(
    val end_time: String,
    val value: Int
)
 */
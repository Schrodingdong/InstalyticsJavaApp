package com.example.instalyticsjava.data;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Data {
    public Data(@NonNull JSONObject jsonObject){
        try {
            Log.d("DataClass", "Start initialisation ...");
            this.description = jsonObject.get("description").toString();
            this.id = jsonObject.get("id").toString();
            this.name = jsonObject.get("name").toString();
            this.period = jsonObject.get("period").toString();
            this.title = jsonObject.get("title").toString();

            //for the value List :
            JSONArray valueArray = new JSONArray(jsonObject.get("values").toString()) ;
            for (int i = 0; i < valueArray.length() ; i++){
                JSONObject temp_valueArray_i= new JSONObject(valueArray.get(i).toString());
                Value tmp_value = new Value(temp_valueArray_i);
                values.add(tmp_value);
            }
            Log.d("DataClass", "Data initialisation "+this.name+" : SUCCESS");
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d("DataClass", "Data initialisation problem !");
        }
    }
    public Data(){
        this.description = "";
        this.id = "";
        this.name = "";
        this.period = "";
        this.title = "";
    }
    String description;
    String id;
    String name;
    String period;
    String title;
    List<Value> values = new ArrayList<Value>();

    public List<Value> getValues() {
        return values;
    }
    public String getDescription() {
        return description;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getPeriod() {
        return period;
    }
    public String getTitle() {
        return title;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String   toString() {
        return "Data{" +
                "description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", period='" + period + '\'' +
                ", title='" + title + '\'' +
                ", values=" + values +
                '}';
    }
}

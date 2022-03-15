package com.example.instalyticsjava.interpretation;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instalyticsjava.DataSingelton;
import com.example.instalyticsjava.InterpretationActivity;
import com.example.instalyticsjava.R;
import com.example.instalyticsjava.data.Data;
import com.example.instalyticsjava.data.Value;
import com.example.instalyticsjava.recyclerViewThings.AdapterDetailTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class InterpretationPopup extends Dialog {
    private final String TAG = "InterpretationPopup";
    private final InterpretationActivity dependencyActivity;


    private Date orderDate;
    private int orderNumber;
    private DatePicker datePicker;
    private EditText editTextNumber;
    private Button ADDbutton;
    private DatePickerDialog.OnDateSetListener setListener;
    private Context context = this.getContext();
    private RecyclerView detail_table;

    public InterpretationPopup(Activity activity,
                               RecyclerView detail_table,
                               InterpretationActivity dependencyActivity){
        super(activity, com.facebook.R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.popup_details_table_add);

        datePicker = findViewById(R.id.datePicker);
        editTextNumber = findViewById(R.id.editTextNumber);
        this.detail_table = detail_table;               //dependency injection
        this.dependencyActivity = dependencyActivity;
        ADDbutton = findViewById(R.id.ADDbutton);
        ADDbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToDB();
                dependencyActivity.updateInterpretation();
                dismiss();
            }
        });


        setDateBoundsInDatePicker();
    }



    private void setDateBoundsInDatePicker() {
        Calendar c = Calendar.getInstance();

        c.add(Calendar.DAY_OF_MONTH, -1);
        getDatePicker().setMaxDate(c.getTimeInMillis());
        c.add(Calendar.DAY_OF_MONTH, 2);
        c.add(Calendar.WEEK_OF_YEAR, -2); // subtract 2 weeks from now
        getDatePicker().setMinDate(c.getTimeInMillis());
        Log.d(TAG, "setDateBoundsInDatePicker: "+ c.getTimeInMillis());

    }

    private void addToDB() {
        long epochDate,value = -1;
        epochDate = getTimeFromPopupCalender();
        String textFromPopup = getEditTextNumber().getText().toString();
        if (textFromPopup.equals(""))
            Toast.makeText(context,"Make sure to fill all the fields",Toast.LENGTH_SHORT).show();
        else{
            value = Long.parseLong(getEditTextNumber().getText().toString());
            Map<String,Integer> reachWebclickProfileview = getReachWebclickProfileview(epochDate);
            OrderDataBase db = new OrderDataBase(context);
            db.addOrder(epochDate,reachWebclickProfileview,(int)value,detail_table);
        }
    }
    @NonNull
    private Map<String,Integer> getReachWebclickProfileview(long epochDate) {
        Map<String,Integer> mapResult = new HashMap<>();
        for (Map.Entry<String, Data> el:
                DataSingelton.getData().entrySet()) {
            int count = 0;
            for (Value val :
                    el.getValue().getValues()) {
                long epochConversion = convertToEpoch(val.getEnd_time());
                if(epochDate==epochConversion){
                    String metric = el.getKey();
                    mapResult.put(metric,(int)val.getValue());
                    Log.d(TAG, "getReachWebclickProfileview: "+count);
                }
                count++;
            }
        }
        //Log.d(TAG, "getReachWebclickProfileview: "+DataSingelton.getData());
        Log.d(TAG, "getReachWebclickProfileview: "+mapResult);
        return mapResult;
    }
    private long getTimeFromPopupCalender(){
        int dayOfMonth,month,year;
        dayOfMonth =getDatePicker().getDayOfMonth();
        month = getDatePicker().getMonth()+1;
        year = getDatePicker().getYear();
        Log.d(TAG, "addToDB epoch: day : "+dayOfMonth + " month "+month+" year "+year);
        return convertToEpoch(year+"-"+month+"-"+dayOfMonth);
    }
    private long convertToEpoch(@NonNull String end_time) {
        //sous format : 2022-03-14T07:00:00+0000
        String timeToParse = end_time.split("T",1)[0];
        SimpleDateFormat myDate = new SimpleDateFormat("yyyy-MM-dd");
        long epoch = 0;
        try {
            myDate.setTimeZone(TimeZone.getTimeZone("UTC"));
            epoch = myDate.parse(end_time).getTime()/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return epoch;
    }



    public void build(){
        show();
    }
    //___________________________________________getters____________________________________________
    //----------------------------------------------------------------------------------------------
    public DatePicker getDatePicker() {
        return datePicker;
    }
    public EditText getEditTextNumber() {
        return editTextNumber;
    }
    public Button getADDbutton() {
        return ADDbutton;
    }
}

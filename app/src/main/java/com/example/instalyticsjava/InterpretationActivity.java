package com.example.instalyticsjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instalyticsjava.Util.UtilFunctions;
import com.example.instalyticsjava.interpretation.InterpretationPopup;
import com.example.instalyticsjava.interpretation.InterpretationResetPopup;
import com.example.instalyticsjava.interpretation.OrderDataBase;
import com.example.instalyticsjava.interpretation.ml.LinearRegression;
import com.example.instalyticsjava.recyclerViewThings.AdapterDetailTable;

import org.jetbrains.annotations.Contract;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InterpretationActivity extends AppCompatActivity {
    private final String TAG = "InterpretationActivity";
    private final int necessaryNumberOfRowsInDbForPrediction = 10;
    private Button detail_table_ADD;
    private Button detail_table_RESET;
    private TextView model_accuracy_value;
    private TextView potential_client_value;
    private Activity activity = this;
    private RecyclerView detail_table;
    private InterpretationPopup interpretationPopup;
    private InterpretationResetPopup interpretationResetPopup;
    private OrderDataBase db;                                   //MY DB
    private LinearRegression mlBrain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_interpretation);

        potential_client_value = findViewById(R.id.potential_client_value);
        model_accuracy_value = findViewById(R.id.model_accuracy_value);
        detail_table = findViewById(R.id.detail_table);
        detail_table_ADD = findViewById(R.id.detail_table_ADD);
        detail_table_ADD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interpretationPopup.build();//delegates everything related to adding in DB
            }
        });
        detail_table_RESET = findViewById(R.id.detail_table_RESET);
        detail_table_RESET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interpretationResetPopup.build();
            }
        });
        db= new OrderDataBase(this);
        interpretationPopup = new InterpretationPopup(activity,detail_table,this);
        interpretationResetPopup = new InterpretationResetPopup(activity,this);


        updateInterpretation();


    }

    private List<Integer> ID_list = new ArrayList<>();
    private List<Integer> epochDate_value_list = new ArrayList<>();
    private List<String> date_value_list = new ArrayList<>();
    private List<Integer> reach_value_list = new ArrayList<>();
    private List<Integer> webClick_value_list = new ArrayList<>();
    private List<Integer> profileViews_value_list = new ArrayList<>();
    private List<Integer> orders_value_list = new ArrayList<>();
    public void updateInterpretation() {
        updateRecycler();
        if ( DbRowCount() >= necessaryNumberOfRowsInDbForPrediction){
            updatePrediction(reach_value_list,
                    webClick_value_list,
                    profileViews_value_list,
                    orders_value_list);
        }
        else{
            potential_client_value.setText("Please add more orders for prediction");
            Log.d(TAG, "updateInterpretation: not enough data : "+ DbRowCount());
        }

    }
    private int DbRowCount() {
        return db.getRowCount();
    }
    private void updateRecycler() {
        getAllTheDataFromDBInLists();
        Log.d(TAG, "updateInterpretation: "+epochDate_value_list);
        AdapterDetailTable myAdapter = new AdapterDetailTable(this,
                ID_list,
                epochDate_value_list,
                date_value_list,
                reach_value_list,
                webClick_value_list,
                profileViews_value_list,
                orders_value_list);
        detail_table.setAdapter(myAdapter);
        detail_table.setLayoutManager(new LinearLayoutManager(this));
    }
    private void updatePrediction(List<Integer> reach_list,
                                  List<Integer> profileViews_list,
                                  List<Integer> websiteClicks_list,
                                  List<Integer> order_list){
        mlBrain = new LinearRegression(reach_value_list,
                webClick_value_list,
                profileViews_value_list,
                orders_value_list);
        Map<String,Double> input = getInputValues();
        int potential_Clients = mlBrain.getPrediction(input.get("predicted_reach"),
                input.get("predicted_websiteClick"),
                input.get("predicted_profileView"));
        potential_client_value.setText(Integer.toString(potential_Clients));

        double modelAccuracy = mlBrain.getModelAccuracy();
        model_accuracy_value.setText(String.format("%.1f",modelAccuracy*100)+"%");
    }

    @NonNull
    private Map<String,Double> getInputValues() {
        double[] coef_reach = UtilFunctions.getPolynomialRegressionCoefficient(UtilFunctions.getMetricValueList(
                Objects.requireNonNull(DataSingelton.getData().get("reach"))),3);
        double[] coef_profileView = UtilFunctions.getPolynomialRegressionCoefficient(UtilFunctions.getMetricValueList(
                Objects.requireNonNull(DataSingelton.getData().get("profile_views"))),3);
        double[] coef_websiteClick = UtilFunctions.getPolynomialRegressionCoefficient(UtilFunctions.getMetricValueList(
                Objects.requireNonNull(DataSingelton.getData().get("website_clicks"))),3);
        //calculate for tomorrow :
        int indexForTomorrow = UtilFunctions.getMetricValueList(
                Objects.requireNonNull(DataSingelton.getData().get("profile_views"))).size() + 1 ;  //dak le +1 for tomorrow z3ma
        double predicted_reach = f(coef_reach,indexForTomorrow);
        double predicted_profileView = f(coef_profileView,indexForTomorrow);
        double predicted_websiteClick = f(coef_websiteClick,indexForTomorrow);
        Log.d(TAG, "getInputValues: "+ predicted_profileView+ " " +predicted_reach +" "+ predicted_websiteClick);

        Map<String,Double> returnResult = new HashMap<>();
        returnResult.put("predicted_reach",predicted_reach);
        returnResult.put("predicted_profileView",predicted_profileView);
        returnResult.put("predicted_websiteClick",predicted_websiteClick);
        return returnResult;
    }

    @Contract(pure = true)
    private double f(@NonNull double[] coef, int x) {
        double a,b,c,d;
        //rq les coef vont du rang 0 -> rang max (3 ici) de g a droite
        a = coef[0];
        b = coef[1] * x;
        c = coef[2] * x * x;
        d = coef[3] * x * x * x;
        return a+b+c+d <= 0 ? 0 : a+b+c+d  ;
    }

    @SuppressLint("Range")
    private void getAllTheDataFromDBInLists() {
        Cursor cursor =  db.readAllData();

        ID_list.clear();
        epochDate_value_list.clear();
        date_value_list.clear();
        reach_value_list.clear();
        webClick_value_list.clear();
        profileViews_value_list.clear();
        orders_value_list.clear();
        while(cursor.moveToNext()){
            ID_list.add(cursor.getInt(cursor.getColumnIndex("order_id")));
            epochDate_value_list.add(cursor.getInt(cursor.getColumnIndex("epoch")));
            date_value_list.add(cursor.getString(cursor.getColumnIndex("date")));
            reach_value_list.add(cursor.getInt(cursor.getColumnIndex("reach")));
            webClick_value_list.add(cursor.getInt(cursor.getColumnIndex("web_click")));
            profileViews_value_list.add(cursor.getInt(cursor.getColumnIndex("profile_views")));
            orders_value_list.add(cursor.getInt(cursor.getColumnIndex("orders")));
        }
        Log.d(TAG, "getAllTheDataFromDBInLists: cursor is null ? "+cursor.isLast());

    }
}
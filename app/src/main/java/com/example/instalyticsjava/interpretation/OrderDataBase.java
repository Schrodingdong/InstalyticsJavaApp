package com.example.instalyticsjava.interpretation;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instalyticsjava.recyclerViewThings.AdapterDetailTable;

import org.jetbrains.annotations.Contract;

import java.nio.file.SecureDirectoryStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderDataBase extends SQLiteOpenHelper {
    private final String TAG = "OrderDataBase";
    private Context context;
    private static final String DATABASE_NAME = "OrderDetail.db";
    private static final String TABLE_NAME = "OrderDetail";
    private static final int DATABASE_VERSION = 1;

    private static final String COLUMN_ID = "order_id";
    private static final String COLUMN_EPOCH = "epoch";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_REACH = "reach";
    private static final String COLUMN_WEBCLICK = "web_click";
    private static final String COLUMN_PROFILEVIEWS = "profile_views";
    private static final String COLUMN_ORDERS = "orders";

    public OrderDataBase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(@NonNull SQLiteDatabase sqLiteDatabase) {
        String Query = "CREATE TABLE "+TABLE_NAME+"(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_EPOCH + " INTEGER,"+
                COLUMN_DATE + " TEXT,"+
                COLUMN_REACH + " INTEGER,"+
                COLUMN_WEBCLICK + " INTEGER,"+
                COLUMN_PROFILEVIEWS + " INTEGER,"+
                COLUMN_ORDERS + " INTEGER"+
                ");";
        Log.d(TAG, "QUERY  : "+Query);
        sqLiteDatabase.execSQL(Query);
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void addOrder(long epochDate, @NonNull Map<String,Integer> reachWebclickProfileview,
                         int orderValue, @NonNull RecyclerView detail_table){

        int reach = reachWebclickProfileview.get("reach");
        int website_clicks = reachWebclickProfileview.get("website_clicks");
        int profile_views = reachWebclickProfileview.get("profile_views");

        if(epochAlreadyInDBAndWeUpdateDB(epochDate,orderValue)){
            Log.d(TAG, "addOrder: epochAlreadyInDBAndWeUpdateDB");
        }
        else {
            createNewRow(epochDate, orderValue, reach, website_clicks, profile_views);
            Log.d(TAG, "addOrder: createNewRow");
        }
    }

    private void createNewRow(long epochDate, int orderValue, int reach, int website_clicks, int profile_views) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_EPOCH, epochDate);
        cv.put(COLUMN_DATE,toDate(epochDate).toString());
        cv.put(COLUMN_REACH, reach);
        cv.put(COLUMN_WEBCLICK, website_clicks);
        cv.put(COLUMN_PROFILEVIEWS, profile_views);
        cv.put(COLUMN_ORDERS, orderValue);
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert(TABLE_NAME, null,cv);
        if (result == -1)
            Toast.makeText(context,"Failed to insert into DB",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context,"Added successfully !!",Toast.LENGTH_SHORT).show();
    }
    @SuppressLint("Range")
    private boolean epochAlreadyInDBAndWeUpdateDB(long epoch,int addedValue) {
        Cursor cursor = readAllData();
        while(cursor.moveToNext()){
            long pointedToEpoch = cursor.getLong(cursor.getColumnIndex("epoch"));
            if(epoch != pointedToEpoch) continue;
            int order_value = cursor.getInt(cursor.getColumnIndex("orders"));
            int orderId = cursor.getInt(cursor.getColumnIndex("order_id"));
            updateRowOnId(orderId,order_value+addedValue);
            Toast.makeText(context,"Updated the order "+cursor.getString(cursor.getColumnIndex("date")),Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public void deleteRowOnId(int id){
        String query = "DELETE FROM "+TABLE_NAME+" WHERE "+COLUMN_ID+" = "+id;
        Log.d(TAG, "deleteRowOnId: "+query);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }
    public void resetDatabase(){
        String Query = "DELETE FROM "+TABLE_NAME+";";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(Query);
    }
    public void updateRowOnId(int id,int orderValue){
        String query = "UPDATE "+TABLE_NAME+" SET "+ COLUMN_ORDERS +"="+ orderValue
                +" WHERE "+COLUMN_ID+" = "+id;
        Log.d(TAG, "deleteRowOnId: "+query);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }



    @NonNull
    @Contract("_ -> new")
    private Date toDate(long epochDate) {
        return new Date(epochDate*1000);
    }
    public Cursor readAllData(){
        String query = "SELECT * FROM "+TABLE_NAME+" ORDER BY "+COLUMN_EPOCH+" ASC;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
    @SuppressLint("Range")
    public int getRowCount(){
        String query = "SELECT * FROM "+TABLE_NAME+";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor.getCount();    }

}

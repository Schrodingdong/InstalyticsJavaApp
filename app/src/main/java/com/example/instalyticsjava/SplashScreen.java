package com.example.instalyticsjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.facebook.AccessToken;

public class SplashScreen extends AppCompatActivity {
    private final String TAG = "SplashScreen";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash_screen);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: "+AccessToken.getCurrentAccessToken().getToken());
        try {
            if (AccessToken.getCurrentAccessToken().getToken() != null ){
                //Go to main core
                Log.d(TAG, "onStart: "+AccessToken.getCurrentAccessToken());
                Intent intent = new Intent(this, CoreActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                Log.d(TAG, "onStart: "+AccessToken.getCurrentAccessToken());
                Intent intent = new Intent(this, LoggingActivity.class);
                startActivity(intent);
                finish();
            }
        }
        catch (NullPointerException  e ){
            Log.e("onStart", "onStart: ", e);
        }
    }
}
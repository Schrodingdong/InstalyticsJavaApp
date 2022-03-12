package com.example.instalyticsjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.facebook.AccessToken;
import com.facebook.core.Core;

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
    protected void onResume() {
        super.onResume();
        Intent intentCore = new Intent(this, CoreActivity.class);
        Intent intentLogging = new Intent(this, LoggingActivity.class);
        new GotoNextActivity(intentCore,intentLogging).start();
    }

    class GotoNextActivity extends Thread {
        final Intent intentCore;
        final Intent intentLogging;
        public GotoNextActivity(Intent intentCore,Intent intentLogging){
            this.intentCore = intentCore;
            this.intentLogging = intentLogging;
        }
        @Override
        public void run() {
            Log.d(TAG, "onStart: "+AccessToken.getCurrentAccessToken());
            try {
                Thread.sleep(3000);
                if (AccessToken.getCurrentAccessToken() != null ){
                    //Go to main core
                    Log.d(TAG, "goto core: "+AccessToken.getCurrentAccessToken());
                    startActivity(intentCore);
                    finish();
                }
                else{
                    Log.d(TAG, "goto log: "+AccessToken.getCurrentAccessToken());
                    startActivity(intentLogging);
                    finish();
                }
            }
            catch (NullPointerException | InterruptedException e ){
                Log.e("onStart", "onStart: ", e);
            }
        }
    }
}
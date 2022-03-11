package com.example.instalyticsjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


//to import YOUR ids !!!!!!!!!!!!!!!

/*
DEV KEY     w5G662mc4o81BUzLaFO2xjZlnHw=

keytool -exportcert -alias androiddebugkey -keystore "C:\Users\Hamza\.android\debug.keystore" | "C:\msys64\mingw64\bin\openssl.exe" sha1 -binary | "C:\msys64\mingw64\bin\openssl.exe" base64

PUB KEY eKJJHinr+D3pnrXYFuIJnPbO7ag=

keytool -exportcert -alias YOUR_RELEASE_KEY_ALIAS -keystore YOUR_RELEASE_KEY_PATH | openssl sha1 -binary | openssl base64
 */


public class LoggingActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private String InstagramBusinessAccountID = null;


    //what happens when we open the app THE FIRST TIME !!!!
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_logging);


        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setPermissions("pages_show_list,instagram_basic,instagram_manage_insights,instagram_manage_comments,pages_read_engagement");

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("LoggingActivity","log SUCCESS");
            }
            @Override
            public void onCancel() {
                Log.d("LoggingActivity","log Canceled");
            }
            @Override
            public void onError(FacebookException exception) {
                Log.d("LoggingActivity",exception.getMessage());
            }
        });
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("LoggingActivity", "goto CORE");
        if (AccessToken.getCurrentAccessToken() != null){
            //Go to main core
            Intent intent = new Intent(this, CoreActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    //_______________________________________my functions___________________________________________
    //----------------------------------------------------------------------------------------------


    //Whenever the access token is changed, this method is called automaticaly;
    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            //whenever the access token is changed, this method is called automaticaly;
            if (currentAccessToken == null){
                LoginManager.getInstance().logOut(); //when it changes, we log out of the facebook
            }
        }
    };


}
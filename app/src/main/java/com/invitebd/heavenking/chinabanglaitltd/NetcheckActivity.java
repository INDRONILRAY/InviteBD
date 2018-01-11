package com.invitebd.heavenking.chinabanglaitltd;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class NetcheckActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private final int SPLASH_DISPLAY_LENGTH = 1000;
    private SwipeRefreshLayout swipeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netcheck);
        getSupportActionBar().hide();
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(Color.GRAY, Color.GREEN, Color.BLUE, Color.RED, Color.CYAN);
        swipeLayout.setDistanceToTriggerSync(20);// in dips
        swipeLayout.setSize(SwipeRefreshLayout.DEFAULT);// LARGE also can be used

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                android.net.NetworkInfo wifi = cm
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                android.net.NetworkInfo datac = cm
                        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if ((wifi != null & datac != null)
                        && (wifi.isConnected() | datac.isConnected())) {
                    //connection is avlilable
                    Intent splash = new Intent(NetcheckActivity.this, SplashActivity.class);
                    startActivity(splash);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }else{
                    //no connection
                    swipeLayout.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_LONG).show();
                }
//
            }
        },SPLASH_DISPLAY_LENGTH);
        
        
        
        
    }

    @Override
    public void onRefresh() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo datac = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifi != null & datac != null)
                && (wifi.isConnected() | datac.isConnected())) {
            //connection is avlilable
            swipeLayout.setRefreshing(false);
            Intent splash = new Intent(NetcheckActivity.this, SplashActivity.class);
            startActivity(splash);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }else{
            //no connection
            swipeLayout.setRefreshing(false);
            Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_LONG).show();
        }
    }
}

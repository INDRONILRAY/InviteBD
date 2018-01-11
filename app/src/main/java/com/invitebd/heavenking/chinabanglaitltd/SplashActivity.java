package com.invitebd.heavenking.chinabanglaitltd;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

public class SplashActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle icicle ) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);

//        View decorView = getWindow().getDecorView();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                    Intent splash = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(splash);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    SplashActivity.this.finish();
//
            }
        },SPLASH_DISPLAY_LENGTH);


    }

}
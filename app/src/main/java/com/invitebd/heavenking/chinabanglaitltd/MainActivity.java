package com.invitebd.heavenking.chinabanglaitltd;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity{
    WebView webView; AdView adView;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR=1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(Build.VERSION.SDK_INT >= 21){
            Uri[] results = null;
            //Check if response is positive
            if(resultCode== Activity.RESULT_OK){
                if(requestCode == FCR){
                    if(null == mUMA){
                        return;
                    }
                    if(intent == null || intent.getData() == null){
                        //Capture Photo if no image available
                        if(mCM != null){
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    }else{
                        String dataString = intent.getDataString();
                        if(dataString != null){
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        }else{
            if(requestCode == FCR){
                if(null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                android.net.NetworkInfo wifi = cm
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                android.net.NetworkInfo datac = cm
                        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if ((wifi != null & datac != null)
                        && (wifi.isConnected() | datac.isConnected())) {

                    MobileAds.initialize(MainActivity.this, "ca-app-pub-0344603362078430~6205654239");
                    AdView mAdView = (AdView) findViewById(R.id.adView);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdView.loadAd(adRequest);

                    if(Build.VERSION.SDK_INT >=23 &&
                            (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                    || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                    }

                    webView = (WebView) findViewById(R.id.webView);
                    assert webView != null;
                    WebSettings webSettings = webView.getSettings();
                    webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//                    webView.getSettings().setAppCacheEnabled(true);
                    webSettings.setDomStorageEnabled(true);
                    webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
                    webSettings.setSavePassword(true);
                    webSettings.setSaveFormData(true);
                    webSettings.setEnableSmoothTransition(true);
                    webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.addJavascriptInterface(new WebViewInterface(),"MainActivityInterface");
                    webView.getSettings().setLoadsImagesAutomatically(true);
                    webView.getSettings().setSupportZoom(false);
                    webView.getSettings().setBuiltInZoomControls(false);
                    webView.getSettings().setLoadWithOverviewMode(true);
                    webView.getSettings().setUseWideViewPort(true);
                    webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                    webView.setBackgroundColor(Color.WHITE);

                    if(Build.VERSION.SDK_INT >= 21){
                        webSettings.setMixedContentMode(0);
                        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    }else if(Build.VERSION.SDK_INT >= 19){
                        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    }else if(Build.VERSION.SDK_INT < 19){
                        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    }
                    webView.setWebViewClient(new Callback());
                    webView.loadUrl("http://mobile.invitebd.com");
                    webView.setWebChromeClient(new WebChromeClient(){
                        //For Android 3.0+
                        public void openFileChooser(ValueCallback<Uri> uploadMsg){
                            mUM = uploadMsg;
                            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                            i.addCategory(Intent.CATEGORY_OPENABLE);
                            i.setType("*/*");
                            MainActivity.this.startActivityForResult(Intent.createChooser(i,"File Chooser"), FCR);
                        }
                        // For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this
                        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType){
                            mUM = uploadMsg;
                            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                            i.addCategory(Intent.CATEGORY_OPENABLE);
                            i.setType("*/*");
                            MainActivity.this.startActivityForResult(
                                    Intent.createChooser(i, "File Browser"),
                                    FCR);
                        }
                        //For Android 4.1+
                        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
                            mUM = uploadMsg;
                            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                            i.addCategory(Intent.CATEGORY_OPENABLE);
                            i.setType("*/*");
                            MainActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), MainActivity.FCR);
                        }
                        //For Android 5.0+
                        public boolean onShowFileChooser(
                                WebView webView, ValueCallback<Uri[]> filePathCallback,
                                WebChromeClient.FileChooserParams fileChooserParams){
                            if(mUMA != null){
                                mUMA.onReceiveValue(null);
                            }
                            mUMA = filePathCallback;
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if(takePictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null){
                                File photoFile = null;
                                try{
                                    photoFile = createImageFile();
                                    takePictureIntent.putExtra("PhotoPath", mCM);
                                }catch(IOException ex){
                                    Log.e(TAG, "Image file creation failed", ex);
                                }
                                if(photoFile != null){
                                    mCM = "file:" + photoFile.getAbsolutePath();
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                                }else{
                                    takePictureIntent = null;
                                }
                            }
                            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                            contentSelectionIntent.setType("*/*");
                            Intent[] intentArray;
                            if(takePictureIntent != null){
                                intentArray = new Intent[]{takePictureIntent};
                            }else{
                                intentArray = new Intent[0];
                            }

                            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                            startActivityForResult(chooserIntent, FCR);
                            return true;
                        }
                    });

                }else{
                    //no connection
                    Intent splash = new Intent(MainActivity.this, NetcheckActivity.class);
                    startActivity(splash);
                    finish();
                }


    }


    public class Callback extends WebViewClient{
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse(url));
                startActivity(intent);
            }else if(url.startsWith("http:") || url.startsWith("https:")) {
                view.loadUrl(url);
            }
            return true;
        }
        public void onPageFinished(WebView view, String url) {
            view.clearCache(true);
//            view.clearHistory();
            super.onPageFinished(view, url);
        }
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
            Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_LONG).show();
            Intent splash = new Intent(MainActivity.this, SplashActivity.class);
            startActivity(splash);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }
    // Create an image file
    private File createImageFile() throws IOException{
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_"+timeStamp+"_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName,".jpg",storageDir);
    }


    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event){
        String webUrl = webView.getUrl();

        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode){
                case KeyEvent.KEYCODE_BACK:
                    if(webView.canGoBack()){
                        if((webUrl.contains("url"))){
                            new AlertDialog.Builder(this).setTitle("InviteBD")
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setMessage("Are you sure you want to exit the app?")
                                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Intent intent = new Intent(Intent.ACTION_MAIN);
                                            intent.addCategory(Intent.CATEGORY_HOME);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                            finish();
                                        }
                                    }).setNegativeButton("no", null).show();
                        }
                        else
                        if((webUrl.contains("url"))){
                            Toast.makeText(this, "Press the X button.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        if((webUrl.contains("url")||(webUrl.contains("http://mobile.invitebd.com/pagenotfound")||webUrl.contains("url")))) {
                            new AlertDialog.Builder(this).setTitle("InviteBD")
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setMessage("Are you sure you want to exit the app?")
                                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Intent intent = new Intent(Intent.ACTION_MAIN);
                                            intent.addCategory(Intent.CATEGORY_HOME);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                            finish();
                                        }
                                    }).setNegativeButton("no", null).show();
                        }else {
                            webView.goBack();
                        }
                    }else {
                        new AlertDialog.Builder(this).setTitle("InviteBD")
                                .setIcon(R.mipmap.ic_launcher)
                                .setMessage("Are you sure you want to exit the app?")
                                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent(Intent.ACTION_MAIN);
                                        intent.addCategory(Intent.CATEGORY_HOME);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        finish();
                                    }
                                }).setNegativeButton("no", null).show();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    public class WebViewInterface{
        @JavascriptInterface
        public void showToast(){
            new AlertDialog.Builder(MainActivity.this).setTitle("InviteBD")
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage("Are you sure you want to exit the app?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        }
                    }).setNegativeButton("no", null).show();
        }
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }



}

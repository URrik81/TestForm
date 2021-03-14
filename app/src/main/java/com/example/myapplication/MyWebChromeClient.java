package com.example.myapplication;

import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class MyWebChromeClient extends WebChromeClient {
    private final static String TAG = com.example.myapplication.MyWebChromeClient.class.getSimpleName();


    WebView currentWebView = null;

    Context context;

    MyWebChromeClient(Context context) {
        this.context = context;
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {
        currentWebView = view;
        WebView newWebView = new WebView(context);

        newWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        newWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.125 Mobile Safari/537.36");
        newWebView.getSettings().setLoadWithOverviewMode(true);
        newWebView.getSettings().setAllowContentAccess(true);
        newWebView.getSettings().setDatabaseEnabled(true);
        newWebView.getSettings().setLoadsImagesAutomatically(true);
        ((MainActivity)context).enableHTML5AppCache(newWebView);
        newWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onCloseWindow(WebView window) {
                currentWebView.removeView(window);
            }
        });
        currentWebView.addView(newWebView);
        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        transport.setWebView(newWebView);
        resultMsg.sendToTarget();
        return true;
    }

    @Override
    public void onCloseWindow(WebView window) {
        if (currentWebView != null) {
            currentWebView.removeView(window);
        }
    }
}

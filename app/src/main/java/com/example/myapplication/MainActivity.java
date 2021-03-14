package com.example.myapplication;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.util.Pair;

import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;


public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    /**
     * Logging TAG
     */
    private static final String TAG = MainActivity.class.getSimpleName();

    public final static String TEST_NAME_KEY = "test_name_key";
    public final static String TEST_LINK_KEY = "test_link_key";
    public final static String OPEN_LINK_KEY = "open_link_key";

    private List<Pair<String, String>> testDataList = new ArrayList<>();

    /**
     * Request code for Google Sign-In Intent.
     */
    private static final int RC_SIGN_IN = 9001;

    //Google API
    GoogleApiClient googleApiClient;

    //Google account
    private GoogleSignInAccount googleSignInAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (testDataList != null && testDataList.size() <= 0) {
            SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
            Set<String> testNameSet = sharedPreferences.getStringSet(MainActivity.TEST_NAME_KEY, null);
            Set<String> testLinkSet = sharedPreferences.getStringSet(MainActivity.TEST_LINK_KEY, null);
            if (testNameSet != null && testLinkSet != null) {
                Iterator<String> itName = testNameSet.iterator();
                Iterator<String> itLink = testLinkSet.iterator();
                while (itName.hasNext() && itLink.hasNext()) {
                    testDataList.add(new Pair(itName.next(), itLink.next()));
                }
            }
        }

        handleIntent(getIntent());

        //Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }//*/
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent intent : " + intent.getAction());
        handleIntent(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "GoogleSignInResult:" + result.isSuccess());
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                googleSignInAccount = result.getSignInAccount();
                Log.d(TAG, "googleSignInAccount:" + googleSignInAccount.getDisplayName());
            }
        }
    }

    public void handleIntent(Intent intent) {

        if (intent == null) {
            Log.d(TAG, "No Intent");
            return;
        }
        String appLinkAction = intent.getAction();
        ClipData clipData = intent.getClipData();

        Log.d(TAG, "handleIntent intent : " + appLinkAction
                + ", clipData : " + (clipData != null ? clipData.toString() : "null"));
        if (Intent.ACTION_SEND.equals(appLinkAction) && clipData != null && clipData.getItemCount() > 0){
            ClipData.Item item = clipData.getItemAt(0);
            String text = item.getText().toString();
            Log.d(TAG, "handleIntent URI : " + text);
            if (text.contains("https://docs.google.com/forms")) {
                SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
                preferences.edit().putString(OPEN_LINK_KEY, text).apply();
            } else {
                Log.i(TAG, "Not a google form, do nothing");
            }
        } else {
            Log.w(TAG, "No clip data");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void enableHTML5AppCache(WebView mWebView) {

        mWebView.getSettings().setDomStorageEnabled(true);

        // Set cache size to 8 mb by default. should be more than enough
        mWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);

        // This next one is crazy. It's the DEFAULT location for your app's cache
        // But it didn't work for me without this line
        mWebView.getSettings().setAppCachePath("/data/data/" + this.getPackageName() + "/cache");
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
    }

    // testDataList region

    public List<Pair<String, String>> getTestDataList() {
        return testDataList;
    }

    // endregion

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
    }
}
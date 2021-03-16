package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.List;
import java.util.Set;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class RunTestFragment extends Fragment {

    private static final String TAG = RunTestFragment.class.getSimpleName();
    WebView testWebView;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_run_test, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.back_to_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(RunTestFragment.this)
                        .navigate(R.id.action_FourthFragment_to_SecondFragment);
            }
        });

        String url = getArguments().getString(MainActivity.TEST_LINK_KEY, "https://docs.google.com/forms");
        Log.d(TAG, "URL : " + url);
        Uri uri = Uri.parse(url);
        String path = uri.getScheme() + "://" + uri.getEncodedAuthority() + uri.getEncodedPath();
        Log.d(TAG, "URL path: " + path);
        testWebView = view.findViewById(R.id.test_web_view);
        WebChromeClient webChromeClient = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                handleTestTitle(title);
            }
        };
        testWebView.setWebChromeClient(webChromeClient);
        testWebView.getSettings().setJavaScriptEnabled(true);
        testWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.125 Mobile Safari/537.36");
        testWebView.getSettings().setLoadWithOverviewMode(true);
        testWebView.getSettings().setAllowContentAccess(true);
        testWebView.getSettings().setDatabaseEnabled(true);
        testWebView.getSettings().setLoadsImagesAutomatically(true);
        testWebView.setWebViewClient(new WebViewClient());
        testWebView.loadUrl(path);
    }

    private void handleTestTitle(String title) {
        String link = getLinkFromClipBoard();
        Log.d(TAG, "onReceivedTitle : " + title + ", link : " + link);
        List<Pair<String, String>> testDataList = ((MainActivity)getActivity()).getTestDataList();
        if (testDataList != null && !link.isEmpty()) {
            for (Pair pair : testDataList) {
                Log.d(TAG, "checkPair : " + pair.first + ", link : " + pair.second);
                if (pair.first.equals(title) && pair.second.equals(link)) {
                    //link found - return
                    Log.d(TAG,"entry found - return");
                    return;
                }
            }
            //save new test
            saveNewTest(testDataList, title, link);
        } else {
            Log.w(TAG, "testDataList is null");
        }
    }

    private void saveNewTest(List<Pair<String, String>> testDataList, String title, String link) {
        Log.d(TAG, "saveNewTest : " + title + ", link : " + link);
        testDataList.add(new Pair<>(title, link));
        Set<String> testNameSet = new ArraySet<>();
        Set<String> testLinkSet = new ArraySet<>();
        for (Pair<String, String> data : testDataList) {
            testNameSet.add(data.first);
            testLinkSet.add(data.second);
        }
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putStringSet(MainActivity.TEST_NAME_KEY, testNameSet)
                .putStringSet(MainActivity.TEST_LINK_KEY, testLinkSet)
                .apply();
    }

    private String getLinkFromClipBoard() {
        String pasteData = "";
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        // If it does contain data, decide if you can handle the data.
        if (!(clipboard.hasPrimaryClip())) {
            Log.d(TAG, "! clipboard.hasPrimaryClip()");
        } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {

            // since the clipboard has data but it is not plain text
            Log.d(TAG, "! MIMETYPE_TEXT_PLAIN");
        } else {

            //since the clipboard contains plain text.
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

            // Gets the clipboard as text.
            String link = item.getText().toString();
            Log.d(TAG, "link : " + link);
            if (link.contains("docs.google.com/forms")) {
                pasteData = link;
            }
        }
        return pasteData;
    }
}

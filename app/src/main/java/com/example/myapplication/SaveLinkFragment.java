package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.List;
import java.util.Set;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class SaveLinkFragment extends Fragment {

    private final static String TAG = SaveLinkFragment.class.getSimpleName();

    WebView webView;
    String storedLink;
    TextView testName;
    Button saveTest;
    ClipboardManager clipboard;
    ClipboardManager.OnPrimaryClipChangedListener clipChangedListener;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_third, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        saveTest = view.findViewById(R.id.save_test);
        clipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                Log.d(TAG, "onPrimaryClipChanged");
                restoreLinkFromClipBoard();
            }
        };
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.addPrimaryClipChangedListener(clipChangedListener);
        saveTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add link to list
                List<Pair<String, String>> testDataList = ((MainActivity)getActivity()).getTestDataList();
                if (testDataList != null && storedLink != null) {
                    testDataList.add(new Pair<>(testName.getText().toString(), storedLink));
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
                    storedLink = "";
                    clipboard.setPrimaryClip(ClipData.newPlainText("", ""));
                }
                NavHostFragment.findNavController(SaveLinkFragment.this)
                        .navigate(R.id.action_ThirdFragment_to_SecondFragment);
            }
        });
        saveTest.setEnabled(storedLink != null && !storedLink.isEmpty()
                && testName != null && testName.getText().length() > 0);

        testName = view.findViewById(R.id.test_name);

        webView = view.findViewById(R.id.webview);
        WebChromeClient webChromeClient = new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                Log.d(TAG, "onReceivedTitle : " + title);
                if (title.contains("- Google Формы")) {
                    title = title.substring(0, title.length() - 14);
                    Log.d(TAG, "Title cropped to : " + title);
                }
                testName.setText(title);
            }
        };
        webView.setWebChromeClient(webChromeClient);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.125 Mobile Safari/537.36");
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://docs.google.com/forms");
        webView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "onFocusChange() hasFocus : " + hasFocus);
                if (hasFocus) {
                    restoreLinkFromClipBoard();
                    saveTest.setEnabled(storedLink != null && !storedLink.isEmpty()
                            && testName != null && testName.getText().length() > 0);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        restoreLinkFromClipBoard();
        saveTest.setEnabled(storedLink != null && !storedLink.isEmpty()
                && testName != null && testName.getText().length() > 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView()");
        clipboard.removePrimaryClipChangedListener(clipChangedListener);
        storedLink = "";
        clipboard.setPrimaryClip(ClipData.newPlainText("", ""));
    }

    private void restoreLinkFromClipBoard() {
        String pasteData = "";

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
                storedLink = link;
            } else {
                storedLink = "";
            }
            saveTest.setEnabled(!storedLink.isEmpty()
                    && testName != null && testName.getText().length() > 0);
        }
    }
}

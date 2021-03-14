package com.example.myapplication;

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
import android.widget.Button;
import android.widget.EditText;

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
    EditText testName;
    Button saveTest;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_third, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        restoreLinkFromClipBoard();
        saveTest = view.findViewById(R.id.save_test);
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
                }
                NavHostFragment.findNavController(SaveLinkFragment.this)
                        .navigate(R.id.action_ThirdFragment_to_SecondFragment);
            }
        });
        saveTest.setEnabled(storedLink != null && !storedLink.isEmpty());

        testName = view.findViewById(R.id.test_name);

        webView = view.findViewById(R.id.webview);
        WebChromeClient webChromeClient = new MyWebChromeClient(getActivity());
        webView.setWebChromeClient(webChromeClient);
        webView.loadUrl("https://docs.google.com/forms");
    }

    @Override
    public void onResume() {
        super.onResume();
        restoreLinkFromClipBoard();
        saveTest.setEnabled(storedLink != null && !storedLink.isEmpty());
    }

    private void restoreLinkFromClipBoard() {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
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
        }
    }
}

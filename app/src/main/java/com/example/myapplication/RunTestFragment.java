package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

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
        testWebView = view.findViewById(R.id.test_web_view);
        WebChromeClient webChromeClient = new MyWebChromeClient(getActivity());
        testWebView.setWebChromeClient(webChromeClient);
        testWebView.loadUrl(url);
    }
}

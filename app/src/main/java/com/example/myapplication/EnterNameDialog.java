package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.List;
import java.util.Set;

public class EnterNameDialog extends DialogFragment{

    private static final String TAG = EnterNameDialog.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.enter_name_dialog, null);
        final EditText testName = v.findViewById(R.id.enter_text);
        v.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                NavHostFragment.findNavController(EnterNameDialog.this)
                        .navigate(R.id.action_EnterNameDialog_to_FourthFragment, getArguments());
            }
        });
        v.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add link to list
                String url = getArguments().getString(MainActivity.TEST_LINK_KEY, null);
                Log.d(TAG, "Save URL : " + url);
                List<Pair<String, String>> testDataList = ((MainActivity)getActivity()).getTestDataList();
                if (url != null && testDataList != null) {
                    testDataList.add(new Pair<>(testName.getText().toString(), url));
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
                NavHostFragment.findNavController(EnterNameDialog.this)
                        .navigate(R.id.action_EnterNameDialog_to_FourthFragment, getArguments());
            }
        });
        return v;
    }
}

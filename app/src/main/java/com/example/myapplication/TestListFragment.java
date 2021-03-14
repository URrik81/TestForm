package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TestListFragment extends Fragment implements MyRecyclerViewAdapter.ItemClickListener {

    private final static String TAG = TestListFragment.class.getSimpleName();

    //Account googleAccount = null;
    RecyclerView linkList;
    MyRecyclerViewAdapter adapter;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_list, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.back_to_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(TestListFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        //Get data from shared preferences
        List<Pair<String, String>> testDataList = ((MainActivity)getActivity()).getTestDataList();
        linkList = view.findViewById(R.id.link_list);
        linkList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyRecyclerViewAdapter(getActivity(), testDataList);
        adapter.setClickListener(this);
        linkList.setAdapter(adapter);

        /*AccountManager accountManager = AccountManager.get(getActivity().getApplicationContext());
        Account[] accounts = accountManager.getAccounts();
        Log.d("SecondFragment", "accounts size : " + accounts.length);
        for (Account account : accounts) {
            Log.d("SecondFragment", "account name : " + account.name + ", type : " + account.type
                    + " all : " + account.toString());
            if (account.type.equalsIgnoreCase("com.google")) {
                googleAccount = account;
                break;
            }
        }
        Log.d("SecondFragment", "googleAccount : " + (googleAccount != null ? googleAccount.name : "null"));//*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onItemClick(View view, int position) {
        String url = ((MainActivity)getActivity()).getTestDataList().get(position).second;
        Log.d(TAG, "Tapped on #" + position + " URL : " + url);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.TEST_LINK_KEY, url);
        NavHostFragment.findNavController(TestListFragment.this)
                .navigate(R.id.action_SecondFragment_to_FourthFragment, bundle);
    }
}
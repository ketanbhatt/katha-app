package com.ketanbhatt.kathaapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ketanbhatt.kathaapp.R;


public class DonateFragment extends Fragment {


    public static DonateFragment newInstance() {
        return new DonateFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_donate, container, false);
    }
}

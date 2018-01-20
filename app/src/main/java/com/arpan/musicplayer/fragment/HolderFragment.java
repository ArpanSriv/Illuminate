package com.arpan.musicplayer.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arpan.musicplayer.R;

// Created on 16-01-2018
public class HolderFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();


        transaction.replace(R.id.RootFrame, new ArtistListFragment());


        transaction.commitAllowingStateLoss();

        return inflater.inflate(R.layout.fragment_holder, container, false);
    }
}

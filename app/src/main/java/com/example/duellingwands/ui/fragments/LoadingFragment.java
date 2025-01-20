package com.example.duellingwands.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.duellingwands.R;

public class LoadingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loading_fragment, container, false);
        View backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            requireActivity().finish();
        });
        return view;
    }
}

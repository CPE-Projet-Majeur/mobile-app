package com.example.duellingwands.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.duellingwands.R;
import com.example.duellingwands.databinding.SettingsFragmentBinding;

public class SettingsFragment extends Fragment {

    private SettingsFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.binding = DataBindingUtil.inflate(inflater, R.layout.settings_fragment, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView backgroundGif = new ImageView(requireContext());
        backgroundGif.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Charger le GIF avec Glide
        Glide.with(requireContext())
                .asGif()
                .load(R.raw.settings)
                .into(backgroundGif);
    }
}

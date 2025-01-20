package com.example.duellingwands.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.duellingwands.R;
import com.example.duellingwands.databinding.UserFragmentBinding;
import com.example.duellingwands.ui.activities.LoginActivity;
import com.example.duellingwands.utils.ApplicationStateHandler;
import com.example.duellingwands.viewmodel.UserViewModel;

public class UserFragment extends Fragment {

    private UserFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.binding = DataBindingUtil.inflate(inflater, R.layout.user_fragment, container, false);

        UserViewModel viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        binding.setUserViewModel(viewModel);

        binding.disconnectButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            ApplicationStateHandler.disconectUser(getContext());
            startActivity(intent);
        });

        ImageView houseGifView = binding.houseGifView;
        String house = viewModel.getUser().getHouse();
        Pair<Integer, Integer> houseResources = getResourcesByHouse(house);
        View backgroundView = binding.userFragmentBackground;
        backgroundView.setBackgroundResource(houseResources.second);
        Glide.with(this)
                .asGif()
                .load(houseResources.first)
                .into(houseGifView);

        return binding.getRoot();
    }

    private Pair<Integer, Integer> getResourcesByHouse(String house) {
        switch (house) {
            case "GRYFFINDOR":
                return new Pair<>(R.raw.gryffindor, R.drawable.gryffindor_background);
            case "HUFFLEPUFF":
                return new Pair<>(R.raw.hufflepuff, R.drawable.hufflepuff_background);
            case "RAVENCLAW":
                return new Pair<>(R.raw.ravenclaw, R.drawable.ravenclaw_background);
            case "SLYTHERIN":
                return new Pair<>(R.raw.slytherin, R.drawable.slytherin_background);
            default:
                return new Pair<>(R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_background);
        }
    }
}

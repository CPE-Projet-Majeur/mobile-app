package com.example.duellingwands.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

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
        /*
        ViewModelProvider allows us to retreive the same instance of the
        ViewModel instead of creating a new one each time
        since it's lifecycle is not tied to the fragment's lifecycle
        */
        UserViewModel viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        binding.setUserViewModel(viewModel);
        binding.disconnectButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            ApplicationStateHandler.disconectUser(getContext());
            startActivity(intent);
        });
        return binding.getRoot();
    }
}

package com.example.duellingwands.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.duellingwands.R;
import com.example.duellingwands.databinding.BattleFragmentBinding;
import com.example.duellingwands.viewmodel.BattleViewModel;

public class BattleFragment extends Fragment {

    private BattleFragmentBinding binding;
    private BattleViewModel viewModel;
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        binding = DataBindingUtil.inflate(inflater, R.layout.battle_fragment, container, false);
        //viewModel = new ViewModelProvider(this).get(BattleViewModel.class);
        viewModel = new ViewModelProvider(requireActivity()).get(BattleViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }
}

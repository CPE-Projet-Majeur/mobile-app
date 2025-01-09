package com.example.duellingwands.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.ViewModel;

import com.example.duellingwands.model.entities.User;

public class UserViewModel extends ViewModel {

    private User user;

    public UserViewModel() {
        user = new User();
        // Initialize user data here
        user.setName("John Doe");
        user.setEmail("james.moore.wayne@example-pet-store.com");
    }

    public User getUser() {
        return user;
    }
}

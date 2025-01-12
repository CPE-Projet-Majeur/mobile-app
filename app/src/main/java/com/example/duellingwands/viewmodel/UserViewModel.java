package com.example.duellingwands.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.ViewModel;

import com.example.duellingwands.model.entities.User;

public class UserViewModel extends ViewModel {

    private User user;

    public UserViewModel() {
        user = new User();
        user.setName("John Doe");
        user.setEmail("james.moore.wayne@example-pet-store.com");
        user.setHouse("Gryffindor");
        user.setAccount(100);
    }

    public User getUser() {
        return user;
    }
}

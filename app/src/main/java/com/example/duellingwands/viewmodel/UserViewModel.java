package com.example.duellingwands.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.duellingwands.R;
import com.example.duellingwands.model.entities.User;
import com.example.duellingwands.utils.ApplicationStateHandler;

public class UserViewModel extends ViewModel {

    private User user;
    private MutableLiveData<Integer> houseIcon = new MutableLiveData<>();

    // TODO : use sharedPreferences*

    public UserViewModel() {
        user = ApplicationStateHandler.getCurrentUser();
    }

    public User getUser() {
        return user;
    }

    public LiveData<Integer> getHouseIcon() {
        return houseIcon;
    }

    public void setHouseIcon(int houseIcon) {
        this.houseIcon.setValue(houseIcon);
    }

    public int getHouseIconFromUser(String house) {
        switch (house) {
            case "GRYFFINDOR":
                return R.drawable.gryffindor;
            case "SLYTHERIN":
                return R.drawable.slytherin;
            case "RAVENCLAW":
                return R.drawable.ravenclaw;
            case "HUFFLEPUFF":
                return R.drawable.hufflepuff;
            default:
                return R.drawable.ic_launcher_foreground;
        }
    }
}

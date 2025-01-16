package com.example.duellingwands.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.duellingwands.R;
import com.example.duellingwands.model.entities.User;

public class UserViewModel extends ViewModel {

    private User user;
    private MutableLiveData<Integer> houseIcon = new MutableLiveData<>();

    // TODO : use sharedPreferences*

    public UserViewModel() {
        user = new User();
        user.setFirstName("John Doe");
        user.setEmail("james.moore.wayne@example-pet-store.com");
        user.setHouse("Gryffindor");
        user.setAccount(100);
        setHouseIcon(getHouseIconFromUser(user));
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

    public int getHouseIconFromUser(User user) {
        switch (user.getHouse().toLowerCase()) {
            case "gryffindor":
                return R.drawable.gryffindor;
            case "slytherin":
                return R.drawable.slytherin;
            case "ravenclaw":
                return R.drawable.ravenclaw;
            case "hufflepuff":
                return R.drawable.hufflepuff;
            default:
                return R.drawable.ic_launcher_foreground;
        }
    }
}

package com.example.duellingwands.model.entities;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.duellingwands.BR;

enum House {
    GRYFFINDOR,
    SLYTHERIN,
    HUFFLEPUFF,
    RAVENCLAW
}

public class User extends BaseObservable {
    private int id;
    private String firstName;
    private String lastName;
    private int account;
    private House house;
    private String email;

    // ======================= GETTERS / SETTERS =======================


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Bindable
    public String getFirstName(){
        return firstName;
    }

    public void setFirstName(String val){
        firstName = val;
        notifyPropertyChanged(BR.firstName);
    }

    public String getLastName(){
        return lastName;
    }

    public void setLastName(String val){
        lastName = val;
    }

    @Bindable
    public String getEmail(){
        return email;
    }

    public void setEmail(String val){
        email = val;
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public int getAccount(){
        return account;
    }

    public void setAccount(int val){
        account = val;
        notifyPropertyChanged(BR.account);
    }

    @Bindable
    public String getHouse(){
        return house.name();
    }

    public void setHouse(String val){
        if (val == null) return;
        String houseName = val.toUpperCase();
        try {
            House.valueOf(val);
        } catch (IllegalArgumentException e) {
            houseName = House.GRYFFINDOR.name(); // Default value
        }
        this.house = House.valueOf(houseName);
        notifyPropertyChanged(BR.house);
    }
}

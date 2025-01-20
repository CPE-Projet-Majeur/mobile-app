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
    private float account;
    private House house;
    private String email;

    public User(int id, String firstName, String lastName, float account, String house, String email) {
        this.setId(id);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setAccount(account);
        this.setHouse(house);
    }

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
    public float getAccount(){
        return account;
    }

    public void setAccount(float val){
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

package com.example.duellingwands.model.entities;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.duellingwands.BR;

public class User extends BaseObservable {
    private String name;
    private String email;

    // ======================= GETTERS / SETTERS =======================

    @Bindable
    public String getName(){
        return name;
    }

    public void setName(String val){
        name = val;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getEmail(){
        return email;
    }

    public void setEmail(String val){
        email = val;
        notifyPropertyChanged(BR.email);
    }
}

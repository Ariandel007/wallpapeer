package pe.edu.upc.wallpapeer.model;

import android.text.TextUtils;

import androidx.annotation.Nullable;

public class User {
    private String username;
    private String password;
    private String email;

    public void setUserName(@Nullable String username){
        this.username = username;
    }

    public String getUserName(){
        return username;
    }

    public void setPassword(@Nullable String password){
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(@Nullable String email){
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public boolean isValidUsername(){
        if(this.username != null && !TextUtils.isEmpty(username)){
            return true;
        }
        return false;
    }
    public boolean isValidPassword(){
        if(this.password != null && this.password.length() >=6){
            return true;
        }
        return false;
    }

}
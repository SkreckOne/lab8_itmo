package org.lab6.utils;

import java.io.Serializable;

public class Session implements Serializable {
    private Boolean isLogin = false;
    private String username;
    private String password;

    public Session(String username, String password) {
        this.username = username;
        this.password = password;
        login();
    }

    public void login(){}
    public void logout(){}



    public String getUsername() {return this.username;}
    public String getPassword() {return this.password;}
    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.password = password;}
}

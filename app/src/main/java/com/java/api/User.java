package com.java.api;

import java.io.Serializable;

public class User implements Serializable
{
    String username;
    String password;
    String usertype;

    String name;
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name=name;
    }
    public String getName()
    {
        return  this.name;
    }
    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String toString()
    {
        return "UserName "+this.username+" Password "+this.password
                +" Name "+this.name+" UserType "+this.usertype;
    }
}

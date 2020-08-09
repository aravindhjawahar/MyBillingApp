package com.java.api;

import java.io.Serializable;
import java.util.ArrayList;

public class Users implements Serializable
{
    public ArrayList<User> usersList=new ArrayList<>();
    public void addUsers(User user)
    {
        this.usersList.add(user);
    }

    public boolean searchUser(User user)
    {
        boolean isFound=false;
        for(User u : usersList)
        {
            if(u.getUsername().equals(user.getUsername()) && u.getPassword().equals(user.getPassword()))
            {
                isFound=true;
            }
        }
        return isFound;
    }
    public User getUser(String username,String password)
    {
        User user=null;
        for(User u : usersList)
        {
            if(u.getUsername().equals(username) && u.getPassword().equals(password))
            {
                user=u;
            }
        }
        return user;
    }
    public User getUser(String username)
    {
        User user=null;
        for(User u : usersList)
        {
            if(u.getName().equals(username))
            {
                user=u;
            }
        }
        return user;
    }

}

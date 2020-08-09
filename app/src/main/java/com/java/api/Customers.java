package com.java.api;

import java.io.Serializable;
import java.util.ArrayList;

public class Customers implements Serializable
{
    public ArrayList<Customer> customers=new ArrayList<Customer>();
    public Customer getCustomer(String username)
    {
        Customer user=null;
        for(Customer u : customers)
        {
            if(u.name.equals(username))
            {
                user=u;
            }
        }
        return user;
    }
}

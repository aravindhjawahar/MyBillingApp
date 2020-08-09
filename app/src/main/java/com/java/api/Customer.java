package com.java.api;

import java.io.Serializable;
import java.sql.Array;
import java.util.ArrayList;

public class Customer implements Serializable
{
    public String name;
    public String address;
    public long phoneNumber;
    public ArrayList<Product> purchasedProducts=new ArrayList<Product>();

    public Customer()
    {

    }
    public Customer(String name, String address, long phoneNumber, ArrayList<Product> purchasedProducts)
    {
        this.name=name;
        this.address=address;
        this.phoneNumber=phoneNumber;
        this.purchasedProducts=purchasedProducts;
    }
}

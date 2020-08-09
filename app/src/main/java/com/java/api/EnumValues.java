package com.java.api;

import java.io.Serializable;
import java.util.ArrayList;

public class EnumValues implements Serializable
{
    ArrayList<String> paymentType=new ArrayList<String>();
    ArrayList<String> usertype=new ArrayList<>();
    ArrayList<Integer> quantity=new ArrayList<>();
    public  ArrayList<String> getList()
    {
        paymentType.add("CASH");
        paymentType.add("CARD");
        return this.paymentType;
    }
    public ArrayList<String> getUsers()
    {
        usertype.add("Admin");
        usertype.add("Seller");
        return this.usertype;
    }
    public ArrayList<Integer> getQuantity()
    {
        quantity.add(1); quantity.add(2); quantity.add(3); quantity.add(4); quantity.add(5);
        quantity.add(6); quantity.add(7); quantity.add(8); quantity.add(9); quantity.add(10);
        return this.quantity;
    }
}

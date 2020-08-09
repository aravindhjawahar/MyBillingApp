package com.java.api;

import java.io.Serializable;
import java.util.Date;

public class Invoice implements Serializable
{
    public String id;
    public  Customer customer;
    public String billLocation;
    public Date date;
    public  String payment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getBillLocation() {
        return billLocation;
    }

    public void setBillLocation(String billLocation) {
        this.billLocation = billLocation;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }
}
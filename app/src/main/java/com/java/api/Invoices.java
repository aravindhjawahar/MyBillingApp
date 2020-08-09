package com.java.api;

import java.util.ArrayList;

public class Invoices {

    public ArrayList<Invoice> invoiceList=new ArrayList<>();

    public Invoice getInvoice(String name)
    {
        Invoice i=null;
        for(Invoice pr : invoiceList)
        {
            if(pr.id.equalsIgnoreCase(name))
            {
                i=pr;
            }
        }
        return i;
    }
}

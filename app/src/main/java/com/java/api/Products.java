package com.java.api;

import java.io.Serializable;
import java.util.ArrayList;

public class Products implements Serializable
{
    public ArrayList<Product> products=new ArrayList<>();
    public void addProduct(Product product)
    {
        this.products.add(product);
    }
    public ArrayList<Product> getProducts()
    {
        return this.products;
    }
    public double getPriceOfProduct(String name)
    {
        double price=0;
        for(Product pr : products)
        {
            if(pr.getProductName().equals(name))
            {
                price=pr.getPrice();
            }
        }
        return price;
    }
    public Product getproduct(String name)
    {
        Product product=null;
        for(Product pr : products)
        {
            if(pr.getProductName().equalsIgnoreCase(name))
            {
                product=pr;
            }
        }
        return product;
    }
    public boolean checkProductAvailability(ArrayList<Product> purchasedProducts)
    {
        boolean isAvailable = false;
        int i=0;
        for(Product product: purchasedProducts)
        {
            for(Product prod: products)
            {
                if( product.getQuantity() <  prod.getQuantity() )
                {
                  i++;
                }
            }
        }
        return isAvailable == (i == purchasedProducts.size());
    }
}

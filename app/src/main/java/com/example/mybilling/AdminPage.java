package com.example.mybilling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.java.api.User;

import java.util.Calendar;
import java.util.Date;

public class AdminPage extends AppCompatActivity
{
    User currentUser;
    String usertype;
    Button addUser;
    Button addProduct;
    Button dispSeller;
    TextView userName;
    TextView date;
    boolean run=true;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);
        currentUser=(User)getIntent().getSerializableExtra("user");
        usertype=currentUser.getUsertype();
        userName=findViewById(R.id.userName);
        date=findViewById(R.id.dateText);
        userName.setText("USER: "+currentUser.getName());

        mHandler=new Handler();
        timer();

        //Hiding the button upon the type of user
        if(!usertype.equalsIgnoreCase("Admin"))
        {
            addUser=(Button)findViewById(R.id.addremovesellerBtn);
            addUser.setEnabled(false);

            addProduct=(Button)findViewById(R.id.addremovePrdt);
            addProduct.setEnabled(false);

            dispSeller=(Button)findViewById(R.id.DisplaySeller);
            dispSeller.setEnabled(false);
        }
    }
    public void timer()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(run)
                {
                    try
                    {
                        Thread.sleep(1000);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Date currentDate= Calendar.getInstance().getTime();
                                date.setText(currentDate.toString());
                            }
                        });
                    }
                    catch(Exception e)
                    {

                    }
                }
            }
        }).start();
    }
    public void onClickAddSeller(View view)
    {
        Intent i=new Intent(getApplicationContext(), AddSeller.class);
        i.putExtra("user",currentUser);
        startActivity(i);
    }
    public void onclickAddProduct(View view)
    {
        Intent i=new Intent(getApplicationContext(), AddProduct.class);
        i.putExtra("user",currentUser);
        startActivity(i);
    }
    public void onclickSellItem(View view)
    {
        Intent i=new Intent(getApplicationContext(), BillProduct.class);
        i.putExtra("user",currentUser);
        startActivity(i);
    }
    public void displaySeller(View view)
    {
        Intent i=new Intent(getApplicationContext(), DisplaySeller.class);
        i.putExtra("user",currentUser);
        startActivity(i);
    }
    public void displayproducts(View view)
    {
        Intent i=new Intent(getApplicationContext(),DisplayProducts.class);
        i.putExtra("user",currentUser);
        startActivity(i);
    }
    public void customerDetails(View view)
    {
        Intent i=new Intent(getApplicationContext(),CustomerPage.class);
        i.putExtra("user",currentUser);
        startActivity(i);
    }
    public void back(View view)
    {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
    }
    public void invoicePage(View view)
    {
        Intent i=new Intent(getApplicationContext(),InvoicePage.class);
        i.putExtra("user",currentUser);
        startActivity(i);
    }
    public void scanQR(View view)
    {
        Intent i=new Intent(getApplicationContext(),qrscan.class);
        i.putExtra("user",currentUser);
        startActivity(i);
    }
}
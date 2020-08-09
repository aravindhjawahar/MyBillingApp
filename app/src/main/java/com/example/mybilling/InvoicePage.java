package com.example.mybilling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.java.api.Customers;
import com.java.api.Invoice;
import com.java.api.Invoices;
import com.java.api.Product;
import com.java.api.Products;
import com.java.api.User;

import java.util.ArrayList;

public class InvoicePage extends AppCompatActivity {

    ListView listview;
    DatabaseReference reference;
    ArrayList<String> name=new ArrayList<>();
    ArrayList<Invoice> invoicelst=new ArrayList<>();
    Invoices  invoices=new Invoices();
    ArrayAdapter<String> adaptor;
    User currentUser;
    Customers ctrs=new Customers();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_page);
        reference= FirebaseDatabase.getInstance().getReference("Invoice");
        listview=  findViewById(R.id.listview);
        currentUser=new User();
        currentUser=(User)getIntent().getSerializableExtra("user");
        displayDataFromFireBase();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent , View view, int position, long id) {
                final String item = parent.getItemAtPosition(position).toString();
                final Invoice i = invoices.getInvoice(item);

                StringBuilder builder=new StringBuilder();
                final AlertDialog.Builder adialog = new AlertDialog.Builder(InvoicePage.this);
                adialog.setCancelable(false);
                adialog.setTitle("INVOICE DETAILS FOR  - " + i.id);
                builder.append("\nCustomer Name  " + i.customer.name + "\nContact   " + i.customer.phoneNumber
                        + "\nAddress " + i.customer.address + "\n"+"Products Purchased \n");
                for (Product pr : i.customer.purchasedProducts)
                {
                    int pNo=i.customer.purchasedProducts.indexOf(pr)+1;
                    builder.append(" Product - " +pNo+" " +pr.getProductName() + "\n Price of "+pr.getProductName()+" " +
                            pr.getPrice() + "\n Quantity of "+pr.getProductName()+" " + pr.getQuantity() + "\n");

                }
                builder.append("Date " + i.date);
                adialog.setMessage(builder.toString());
                adialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                          reference.getRef().child(i.id).removeValue();
                    }
                });
                adialog.setNegativeButton("BACK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adialog.setCancelable(true);
                    }
                }).show();
            }

        });

    }
    private void displayDataFromFireBase()
    {
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                for( DataSnapshot data : dataSnapshot.getChildren() )
                {
                    Invoice invoice=data.getValue(Invoice.class);
                    invoicelst.add(invoice);
                    ctrs.customers.add(invoice.customer);
                    name.add(invoice.id);
                    System.out.println(invoice.customer.name);
                }
                invoices.invoiceList=invoicelst;
                adaptor = new ArrayAdapter<>(InvoicePage.this,android.R.layout.simple_list_item_1,name);
                listview.setAdapter(adaptor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
    public void  back(View view)
    {
        Intent intent=new Intent(getApplicationContext(),AdminPage.class);
        intent.putExtra("user",currentUser);
        startActivity(intent);
    }

}


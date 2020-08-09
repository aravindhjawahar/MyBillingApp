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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.java.api.Customer;
import com.java.api.Customers;
import com.java.api.User;

import java.util.ArrayList;

public class CustomerPage extends AppCompatActivity {

    DatabaseReference reference;
    ListView listview;
    ArrayAdapter<String> adaptor;
    ArrayList<String> customerNames;
    User currentUser;
    ArrayList<Customer> customersList;
    Customers customers;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_page);
        currentUser=new User();
        currentUser=(User)getIntent().getSerializableExtra("user");
        customersList=new ArrayList<>();
        customerNames=new ArrayList<>();

        reference= FirebaseDatabase.getInstance().getReference("Customers");
        listview=findViewById(R.id.listview);
        displayDataFromFireBase();
        customers=new Customers();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent , View view, int position, long id)
            {
                final String  item=parent.getItemAtPosition(position).toString();
                Customer cst=customers.getCustomer(item);
                final AlertDialog.Builder adialog=new AlertDialog.Builder(CustomerPage.this);
                adialog.setCancelable(false);
                adialog.setTitle("INFORMATION OF CUSTOMER "+item);
                adialog.setMessage("\nCUSTOMER-NAME : "+cst.name+"\nCONTACT : "+cst.phoneNumber +"\nADDRESS : "+cst.address);
                adialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        adialog.setCancelable(true);
                    }
                }).show();
            }
        });
    }
    void displayDataFromFireBase()
    {
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for( DataSnapshot data : dataSnapshot.getChildren() )
                {
                    Customer customer=data.getValue(Customer.class);
                    customersList.add(customer);
                    customerNames.add(customer.name);
                }
                adaptor = new ArrayAdapter<String>(CustomerPage.this,android.R.layout.simple_list_item_1,customerNames);
                listview.setAdapter(adaptor);
                customers.customers=customersList;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });
    }
    public void  back(View view)
    {
        Intent intent=new Intent(getApplicationContext(),AdminPage.class);
        intent.putExtra("user",currentUser);
        startActivity(intent);
    }
}
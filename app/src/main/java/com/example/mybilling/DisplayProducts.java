package com.example.mybilling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.java.api.Product;
import com.java.api.Products;
import com.java.api.User;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class DisplayProducts extends AppCompatActivity
{
    ListView listview;
    DatabaseReference reference;
    ArrayList<String> productName=new ArrayList<>();
    ArrayList<Product> products=new ArrayList<>();
    ArrayAdapter<String> adaptor;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_products);

        reference= FirebaseDatabase.getInstance().getReference("Products");
        listview= (ListView) findViewById(R.id.listview);
        currentUser=new User();
        currentUser=(User)getIntent().getSerializableExtra("user");
        displayDataFromFireBase();


            listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent , View view, int position, long id)
                {
                    final String  item=parent.getItemAtPosition(position).toString();
                    Products pts=new Products();
                    pts.products=products;
                    final Product product=pts.getproduct(item);
                        final AlertDialog.Builder adialog=new AlertDialog.Builder(DisplayProducts.this);
                        adialog.setCancelable(false);
                        adialog.setTitle("Product Details - "+item);
                        adialog.setMessage(" Price per piece "+ product.getPrice()+" Quantity "+ product.getQuantity());
                            adialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(currentUser.getUsertype().equalsIgnoreCase("Admin"))
                                    {
                                        Intent i = new Intent(getApplicationContext(), EditDetails.class);
                                        i.putExtra("user", currentUser);
                                        i.putExtra("product", product);
                                        startActivity(i);
                                        Toast.makeText(getApplicationContext(), "Product Deleted", Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), "You dont have access to delete", Toast.LENGTH_LONG).show();
                                        adialog.setCancelable(true);
                                    }
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
                    Product product=data.getValue(Product.class);
                    products.add(product);
                    productName.add(product.getProductName());
                }
                adaptor = new ArrayAdapter<>(DisplayProducts.this,android.R.layout.simple_list_item_1,productName);
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

package com.example.mybilling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.java.api.Product;
import com.java.api.User;

public class EditDetails extends AppCompatActivity
{
    String name;
    EditText edit1;
    EditText edit2;
    EditText edit3;
    Product product;
    User currentUser;

    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;

    ImageView qr;
    ImageView productPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_details);
        storage= FirebaseStorage.getInstance();

        currentUser=(User)getIntent().getSerializableExtra("user");
        product=(Product)getIntent().getSerializableExtra("product");
        reference= FirebaseDatabase.getInstance().getReference("Products");

        edit1=findViewById(R.id.edit1);
        edit2=findViewById(R.id.edit2);
        edit3=findViewById(R.id.edit3);
        qr=findViewById(R.id.qr);
        productPhoto=findViewById(R.id.product);
        edit1.setText(product.getProductName());
        edit2.setText(String.valueOf(product.getQuantity()));
        edit3.setText(Double.toString(product.getPrice()));

        displayImage("Products/"+product.getProductName(),productPhoto);
        displayImage("QR/Products/"+product.getProductName(),qr);

    }
    private  void deleteImage(String path)
    {
        storageReference= storage.getReference(path);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }
    private  void displayImage(String path, final ImageView imageView)
    {
        storageReference= storage.getReference(path);
        storageReference.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                DisplayMetrics metrics=new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                imageView.setImageBitmap(bitmap);
            }
        });
    }
    public void updateData(View view)
    {
        if(edit1.toString().isEmpty() && edit2.toString().isEmpty() && edit3.toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Please enter correct Data or proper data", Toast.LENGTH_LONG).show();
        }
        else
        {
            Product newProduct=new Product();
            newProduct.setProductName(edit1.getText().toString());
            newProduct.setQuantity(Integer.parseInt(edit2.getText().toString()));
            newProduct.setPrice(Double.parseDouble(edit3.getText().toString()));
            reference.child(newProduct.getProductName()).setValue(newProduct);

            Toast.makeText(getApplicationContext(), "Data Updated", Toast.LENGTH_LONG).show();
            Intent i=new Intent(getApplicationContext(),DisplayProducts.class);
            i.putExtra("user",currentUser);
            startActivity(i);
        }
    }
    public void deleteData(View view)
    {
        reference.child(product.getProductName()).getRef().removeValue();
        deleteImage("QR/Products/"+product.getProductName());
        deleteImage("Products/"+product.getProductName());
        Toast.makeText(getApplicationContext(), "Data Deleted", Toast.LENGTH_LONG).show();
        Intent i=new Intent(getApplicationContext(),DisplayProducts.class);
        i.putExtra("user",currentUser);
        startActivity(i);
    }
    public void  back(View view)
    {
        Intent intent=new Intent(getApplicationContext(),DisplayProducts.class);
        intent.putExtra("user",currentUser);
        startActivity(intent);
    }
}
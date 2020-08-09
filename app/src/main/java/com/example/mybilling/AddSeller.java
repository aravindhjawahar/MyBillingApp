package com.example.mybilling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.java.api.EnumValues;
import com.java.api.User;

import org.w3c.dom.Text;

public class AddSeller extends AppCompatActivity
{
    DatabaseReference  ref= FirebaseDatabase.getInstance().getReference();
    String email;
    String password;
    String name;
    Spinner userspinner;
    EnumValues enums= new EnumValues();
    ArrayAdapter<String> adaptor;
    User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_seller);

        userspinner=(Spinner)findViewById(R.id.spinner2);
        adaptor=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,enums.getUsers());
        userspinner.setAdapter(adaptor);
        currentUser=(User)getIntent().getSerializableExtra("user");
    }
    public  void addSeller(View view)
    {
        email=((TextView)findViewById(R.id.usernameText)).getText().toString();
        password=((TextView)findViewById(R.id.passwordUser)).getText().toString().trim();
        name=((TextView)findViewById(R.id.username)).getText().toString().trim();
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(getApplicationContext(), "Please enter email!!", Toast.LENGTH_LONG).show();
            return;
        }
        else  if(TextUtils.isEmpty(password))
        {
            Toast.makeText(getApplicationContext(), "Please enter password!!", Toast.LENGTH_LONG).show();
            return;
        }
        else
        {
            User user=new User();
            user.setUsername(email);
            user.setPassword(password);
            String usertype=((Spinner)findViewById(R.id.spinner2)).getSelectedItem().toString();
            user.setUsertype(usertype);
            user.setName(name);
            user.setId("1234");
            ref.child("Users").child(name).setValue(user);
            Toast.makeText(getApplicationContext(), "New User/Admin Added", Toast.LENGTH_LONG).show();
            Intent intent=new Intent(getApplicationContext(),AdminPage.class);
            intent.putExtra("user",currentUser);
            startActivity(intent);

        }
    }
    public void  back(View view)
    {
        Intent intent=new Intent(getApplicationContext(),AdminPage.class);
        intent.putExtra("user",currentUser);
        startActivity(intent);
    }
}

package com.example.mybilling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.java.api.User;
import com.java.api.Users;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity
{
    private DatabaseReference reference;
    private Users usersclass;
    private User user;
    String email;
    String password;
    EditText emailUser;
    EditText passwordUser;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailUser=findViewById(R.id.username);
        passwordUser=findViewById(R.id.password);
        emailUser.setText("");
        passwordUser.setText("");
        reference= FirebaseDatabase.getInstance().getReference("Users");
        usersclass=new Users();
        user=new User();
        setFirebaseDatatoList();
    }
    public void setFirebaseDatatoList()
    {
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    User user=data.getValue(User.class);
                    usersclass.addUsers(user);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });
    }
    public void onClickLogin(View view)
    {

        ProgressDialog d=new ProgressDialog(this);
        if(!d.isShowing())
            d.show();
        if(TextUtils.isEmpty(emailUser.getText()))
        {
            Toast.makeText(getApplicationContext(), "Enter User email", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(passwordUser.getText()))
        {
            Toast.makeText(getApplicationContext(), "Enter User password", Toast.LENGTH_LONG).show();
        }
        else
        {
            email=emailUser.getText().toString().trim();
            password=passwordUser.getText().toString().trim();
            User user=usersclass.getUser(email,password);
            if(user!=null)
            {
                Intent i=new Intent(getApplicationContext(),AdminPage.class);
                i.putExtra("user",user);
                startActivity(i);
                d.dismiss();
                emailUser.setText("");
                passwordUser.setText("");
            }
            else
            {
                d.dismiss();
                Toast.makeText(getApplicationContext(), "Please enter correct credentials! or Try again", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void onClickCancel(View view)
    {
       this.finish();
       System.exit(0);
    }
}
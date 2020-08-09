package com.example.mybilling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.java.api.User;
import com.java.api.Users;

import java.util.ArrayList;

public class DisplaySeller extends AppCompatActivity
{
    DatabaseReference reference;
    ListView listview;
    ArrayAdapter<String> adaptor;
    ArrayList<User> users;
    ArrayList<String> sellerList;
    User currentUser;
    Users urs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_seller);
        currentUser=new User();
        currentUser=(User)getIntent().getSerializableExtra("user");
        users=new ArrayList<>();
        sellerList=new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference("Users");
        listview=(ListView) findViewById(R.id.listview);
        displayDataFromFireBase();
        urs=new Users();


        listview.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent , View view, int position, long id)
            {

                final String  item=parent.getItemAtPosition(position).toString();
                User u=urs.getUser(item);


                final AlertDialog.Builder adialog=new AlertDialog.Builder(DisplaySeller.this);
                adialog.setCancelable(false);
                adialog.setTitle("INFORMATION OF SELLER ");
                adialog.setMessage("CLICK ON DELETE TO DELETE - "+item+"\nUSERNAME : "+u.getUsername()+"\nPASSWORD : "+u.getPassword()
                        +"\nNAME : "+u.getName()+"\nUSERTYPE : "+u.getUsertype());
                adialog.setPositiveButton("Delete", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        adaptor.remove(item);
                        reference.child(item).getRef().removeValue();
                        Intent i=new Intent(getApplicationContext(), DisplaySeller.class);
                        i.putExtra("user",currentUser);
                        startActivity(i);
                    }
                });
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
                    User user=data.getValue(User.class);
                    if(currentUser.getUsername().equals(user.getUsername()))
                    {

                    }
                    else
                    {
                        users.add(user);
                        sellerList.add(user.getName());
                    }
                }
                adaptor = new ArrayAdapter<String>(DisplaySeller.this,android.R.layout.simple_list_item_1,sellerList);
                listview.setAdapter(adaptor);
                urs.usersList=users;
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
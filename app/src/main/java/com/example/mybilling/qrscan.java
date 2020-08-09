package com.example.mybilling;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.java.api.User;

public class qrscan extends AppCompatActivity
{
    Button scanQR;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);
        scanQR=findViewById(R.id.scanQR);
        currentUser=(User)getIntent().getSerializableExtra("user");
    }

    public void scanQR(View view)
    {
        try
        {
         Intent intent=new Intent("com.google.xing.client.android.SCAN");
         intent.putExtra("SCAN_MODE","QR_CODE_MODE");
         startActivityForResult(intent,0);
        }
        catch (Exception e)
        {
          Uri uri=Uri.parse("market://details?id=com.google.zxing.client.android");
          Intent intentPlayStore=new Intent(Intent.ACTION_VIEW,uri);
          startActivity(intentPlayStore);
        }
    }
    @Override
    protected  void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==0)
        {
            if(resultCode== RESULT_OK)
            {
                String content=data.getStringExtra("SCAN_RESULT");
            }
            if(resultCode== RESULT_CANCELED)
            {

            }
        }
    }
    public void back(View view)
    {
        Intent i=new Intent(getApplicationContext(),AdminPage.class);
        i.putExtra("user",currentUser);
        startActivity(i);
    }
}

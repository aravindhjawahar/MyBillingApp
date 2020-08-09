package com.example.mybilling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.nfc.tech.NfcBarcode;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Tag;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.ByteMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.java.api.Product;
import com.java.api.User;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.UUID;

public class AddProduct extends AppCompatActivity
{
    Product product=new Product();
    DatabaseReference ref;
    StorageReference storageReference;
    FirebaseStorage storage;
    private final int PICK_IMAGE_REQUEST=22;
    EditText edit1;
    EditText edit2;
    EditText edit3;
    ImageView qr;
    ImageView productPhoto;
    Uri filpath;

    String productName;
    int quantity;
    Double price;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        currentUser=(User)getIntent().getSerializableExtra("user");
        storage=FirebaseStorage.getInstance();
        storageReference= storage.getReference();

        ref=FirebaseDatabase.getInstance().getReference();
        edit1=findViewById(R.id.productName);
        edit2=findViewById(R.id.productCost);
        edit3=findViewById(R.id.quantity);

        qr=findViewById(R.id.qrCode);
        productPhoto=findViewById(R.id.productImage);

        productPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
            }
        });
    }
    public  Bitmap generateQrCode(String myCodeText) throws WriterException {
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int size = 256;
        ByteMatrix bitMatrix=qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE,size,size,hintMap);
        int width=bitMatrix.width();
        Bitmap bitmap=Bitmap.createBitmap(width,width,Bitmap.Config.RGB_565);
        for( int x = 0 ; x < width ; x++ )
        {
            for( int y = 0; y < width ;y++ )
            {
                bitmap.setPixel(y,x,bitMatrix.get(x,y) == 0 ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            filpath = data.getData();
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filpath);
                productPhoto.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    private void addImageToFireBase(StorageReference refe, Uri path )
    {
        refe.putFile(path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(getApplicationContext(), "Image Uploaded!!", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Toast.makeText(getApplicationContext(), "Failed ", Toast.LENGTH_SHORT).show();
    }
    public  void addProduct(View view)
    {
        try
        {
            if(TextUtils.isEmpty(edit1.getText().toString()))
            {
                Toast.makeText(getApplicationContext(), "Enter Product Name", Toast.LENGTH_LONG).show();
            }
            else if(TextUtils.isEmpty(edit2.getText().toString()))
            {
                Toast.makeText(getApplicationContext(), "Enter price of the product", Toast.LENGTH_LONG).show();
            }
            else if(TextUtils.isEmpty(edit3.getText().toString()))
            {
                Toast.makeText(getApplicationContext(), "Enter quantity of the product", Toast.LENGTH_LONG).show();
            }
            else
            {
                productName=edit1.getText().toString();
                price=Double.parseDouble(edit2.getText().toString());
                quantity=Integer.parseInt(edit3.getText().toString());
                if (filpath != null)
                {
                    product.setPrice(price);
                    product.setProductName(productName);
                    product.setQuantity(quantity);

                    StorageReference productRef = storageReference.child("Products/" + productName);
                    addImageToFireBase(productRef,filpath);

                    qr.setImageBitmap( generateQrCode((product.getProductName())));
                    qr.setDrawingCacheEnabled(true);
                    qr.buildDrawingCache();
                    Bitmap bitmap = qr.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    StorageReference qrRef=storageReference.child("QR/Products/"+productName);
                    UploadTask uploadTask = qrRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception)
                        {
                            Toast.makeText(getApplicationContext(), "Error adding QR to Firebase", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            Toast.makeText(getApplicationContext(), "QR added to Firebase", Toast.LENGTH_LONG).show();
                        }
                    }
                    );
                    ref.child("Products").child(productName).setValue(product);
                    Toast.makeText(getApplicationContext(), "Product Added", Toast.LENGTH_LONG).show();
                    this.finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Image Upload Error", Toast.LENGTH_LONG).show();
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error Occured Try again", Toast.LENGTH_LONG).show();
        }
    }
    public void  back(View view)
    {
        Intent intent=new Intent(getApplicationContext(),AdminPage.class);
        intent.putExtra("user",currentUser);
        startActivity(intent);
    }
}

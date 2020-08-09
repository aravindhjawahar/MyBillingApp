package com.example.mybilling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.ByteMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.java.api.Customer;
import com.java.api.EnumValues;
import com.java.api.Invoice;
import com.java.api.Product;
import com.java.api.Products;
import com.java.api.User;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

public class BillProduct extends AppCompatActivity
{
    ArrayList<String> productlist = new ArrayList<>();
    Customer cust=new Customer();
    ArrayList<Product> purchased=new ArrayList<Product>();
    ArrayList<String> purchasedName=new ArrayList<String>();
    Products productsClass;

    boolean run=true;
    int pageWidth = 1200;
    Bitmap logo, scaled;
    private DatabaseReference reference;

    Spinner spinner;
    Spinner paymentSpinner;
    EditText contact;
    EditText custAddress;
    EditText customer;
    EditText pricetext;
    Spinner quantity;
    EditText locationTe;
    ImageView imageView;

    double lat;
    double logi;
    double price;
    String product;
    private String cityName;
    TextView date;
    Handler mHandler;
    TextView userName;
    User currentUser;
    int currentProductAvailability;

    Double tprice;
    double amount;
    TextView amt;
    ListView listView;
    ArrayAdapter<String> adptr;

    StorageReference storageReference;
    FirebaseStorage storage;
    private ProgressDialog pDialog;
    private LocationManager locationMgr;
    List<Address> addresses ;
    private LocationListener onLocationChange = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            lat = location.getLatitude();
            logi = location.getLongitude();
            Geocoder geocoder = new Geocoder(BillProduct.this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(lat, logi, 1);
                cityName = addresses.get(0).getAddressLine(0);
                locationTe.setText(cityName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_product);

        logo = BitmapFactory.decodeResource(getResources(), R.drawable.admin);
        scaled = Bitmap.createScaledBitmap(logo, 1200, 400, false);

        amt=findViewById(R.id.priceTotal);
        storage=FirebaseStorage.getInstance();
        storageReference= storage.getReference();
        listView=findViewById(R.id.listview);

        spinner =  findViewById(R.id.productListDropdown);
        paymentSpinner =  findViewById(R.id.paymentType);
        pricetext =  findViewById(R.id.priceAmount);
        quantity = findViewById(R.id.quantity);
        locationTe = findViewById(R.id.locationText);
        customer=findViewById(R.id.customerName);
        contact=findViewById(R.id.phoneNumber);
        custAddress=findViewById(R.id.addressText);

        userName=findViewById(R.id.userName);
        currentUser=(User)getIntent().getSerializableExtra("user");
        date=findViewById(R.id.dateText);

        imageView =  findViewById(R.id.qrCode);
        productsClass = new Products();

        addresses=new ArrayList<>();

        pDialog=new ProgressDialog(this);
        pDialog.show();
        pDialog.setMessage("Preparing the Page for Access and Settings");
        locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        }
        locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, onLocationChange);

        final ProgressDialog lP=new ProgressDialog(this);
        lP.setCancelable(false);
        lP.show();
        lP.setMessage("Setting Location");
        Thread mThread=new Thread()
        {
            @Override
            public void run()
            {
                lP.dismiss();
            }
        };
        mThread.start();

        // userName.setText(currentUser.getName());
        mHandler=new Handler();
        timer();
        reference= FirebaseDatabase.getInstance().getReference("Products");
        addDropDownListItemsProduct();
        addDropDownQuantity();
        addDropDOwnItemPaymentType();

        locationTe.setText(cityName);
        pDialog.hide();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                product=spinner.getSelectedItem().toString();
                price=productsClass.getPriceOfProduct(product);
                pricetext.setText(Double.toString(price));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            { }
        });

        quantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(productsClass.products.size() >0 )
                {
                    product=spinner.getSelectedItem().toString();
                    Product pro=productsClass.getproduct(product);
                    Integer qty=Integer.parseInt(quantity.getSelectedItem().toString());
                    currentProductAvailability=pro.getQuantity();
                    if(currentProductAvailability < qty)
                    {
                        final AlertDialog.Builder ad=new AlertDialog.Builder(BillProduct.this);
                        ad.setMessage("Not Sufficent Availabile no of Product only "+pro.getQuantity()+" is available");
                        ad.setCancelable(false);
                        ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ad.setCancelable(true);
                            }
                        }).show();
                    }
                    else
                    {
                        tprice=productsClass.getPriceOfProduct(product);
                        price=tprice*qty;
                        pricetext.setText(Double.toString(price));
                    }
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent , View view, int position, long id)
            {
                final String  item=parent.getItemAtPosition(position).toString();
                final Products pts=new Products();
                pts.products=purchased;
                final Product product=pts.getproduct(item);
                final android.app.AlertDialog.Builder adialog=new android.app.AlertDialog.Builder(BillProduct.this);
                adialog.setCancelable(false);
                adialog.setTitle("Delete the Item "+item);
                adialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       adialog.setCancelable(true);
                    }
                });
                adialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        adptr.remove(item);
                        amount-=product.getPrice();
                        purchasedName.remove(item);
                        pts.products.remove(product);
                        purchased=pts.products;
                        amt.setText("TOTAL: "+String.valueOf(amount));
                    }
                }).show();
            }

        });
    }
    private void timer()
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
    public void back(View view)
    {
        this.finish();
    }
    public  Bitmap generateQrCode(String myCodeText) throws WriterException
    {
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int size = 256;
        ByteMatrix bitMatrix=qrCodeWriter.encode(myCodeText,BarcodeFormat.QR_CODE,size,size,hintMap);
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
    private void addDropDownQuantity()
    {
        EnumValues enums=new EnumValues();
        ArrayAdapter<Integer> quantityAdaptor=new ArrayAdapter<Integer>(
                this,android.R.layout.simple_dropdown_item_1line,enums.getQuantity());
        quantity.setAdapter(quantityAdaptor);
    }
    public void addDropDOwnItemPaymentType()
    {
        EnumValues enums=new EnumValues();
        ArrayAdapter<String> paymentType=new ArrayAdapter<String>(
                this,android.R.layout.simple_dropdown_item_1line,enums.getList());
        paymentSpinner.setAdapter(paymentType);
    }
    public void addDropDownListItemsProduct()
    {
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for( DataSnapshot data : dataSnapshot.getChildren() )
                {
                    Product product=data.getValue(Product.class);
                    productlist.add(product.getProductName());
                    productsClass.products.add(product);
                }
                ArrayAdapter<String> adaptor = new ArrayAdapter<>(
                        BillProduct.this,android.R.layout.simple_dropdown_item_1line,productlist);
                spinner.setAdapter(adaptor);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });
    }
    public boolean checkAvailabiliy()
    {
        boolean flag=false;
        int i=0;
        for(Product pr:purchased)
        {
            if(pr.getQuantity() <= productsClass.getproduct(pr.getProductName()).getQuantity())
            {
                i++;
            }
        }
        if(i == purchased.size())
        {
            flag=true;
        }
        return flag;
    }
    public void add(View view)
    {
        String productName=spinner.getSelectedItem().toString();
        String priceAmount=pricetext.getText().toString();
        String qty=quantity.getSelectedItem().toString();

        Product p=new Product();
        p.setProductName(productName);
        p.setPrice(Double.parseDouble(priceAmount));
        p.setQuantity(Integer.parseInt(qty));
        if(p.getQuantity() <= productsClass.getproduct(productName).getQuantity() )
        {
            amount+=Double.parseDouble(priceAmount);
            amt.setText("TOTAL: "+String.valueOf(amount));
            purchased.add(p);
            purchasedName.add(p.getProductName());
            adptr=new ArrayAdapter<String>(BillProduct.this,android.R.layout.simple_dropdown_item_1line,purchasedName);
            listView.setAdapter(adptr);
        }
        else
        {
            final android.app.AlertDialog.Builder adialog=new android.app.AlertDialog.Builder(BillProduct.this);
            adialog.setCancelable(false);
            adialog.setTitle("Insufficient Prouduct -Only "+
                    productsClass.getproduct(productName).getQuantity()+" is Available");
            adialog.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    adialog.setCancelable(true);
                }
            }).show();
        }
    }
    public void generateBill(View view) throws WriterException
    {
        DatabaseReference  refCust= FirebaseDatabase.getInstance().getReference();
        StorageReference qrRef;
        String payment=paymentSpinner.getSelectedItem().toString();
        String customerName;
        String customerContact;
        String customerAddress;

        if(TextUtils.isEmpty(customer.getText()))
        {
            Toast.makeText(getApplicationContext(), "Enter Customer Name", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(contact.getText()))
        {
            Toast.makeText(getApplicationContext(), "Enter Customer Contact", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(custAddress.getText()))
        {
            Toast.makeText(getApplicationContext(), "Enter Customer Address", Toast.LENGTH_LONG).show();
        }
        else if(purchasedName==null)
        {
            Toast.makeText(getApplicationContext(), "Add Products to purchase", Toast.LENGTH_LONG).show();
        }
        else
        {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
            {
                //if (checkAvailabiliy())
                {
                    Date date11 = new Date();

                    customerName = customer.getText().toString();
                    customerContact = contact.getText().toString();
                    customerAddress = custAddress.getText().toString();

                    cust.name = customerName;
                    cust.address = customerAddress;
                    cust.phoneNumber = Long.parseLong(customerContact);
                    cust.purchasedProducts = this.purchased;

                    Invoice invoice = new Invoice();
                    invoice.id = customerName + " " + date11;
                    invoice.billLocation = locationTe.getText().toString();
                    invoice.date = date11;

                    invoice.payment = payment;
                    invoice.customer = cust;

                    refCust.child("Customers").child(invoice.id ).setValue(cust);
                    refCust.child("Invoice").child(invoice.id ).setValue(invoice);

                    for (Product p : purchased) {
                        Product updatedProduct = new Product();
                        updatedProduct.setProductName(p.getProductName());
                        updatedProduct.setPrice(productsClass.getproduct(p.getProductName()).getPrice());
                        updatedProduct.setQuantity(productsClass.getproduct(p.getProductName()).getQuantity() - p.getQuantity());
                        refCust.child("Products").child(p.getProductName()).setValue(updatedProduct);
                    }

                    imageView.setImageBitmap(generateQrCode((customerName+" "+ customerContact+" " + customerAddress
                            +" "+ payment +" "+ date11+" "+invoice)));

                    imageView.setDrawingCacheEnabled(true);
                    imageView.buildDrawingCache();
                    Bitmap bitmap = imageView.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    String qrName = customerName + product + date11;
                    qrRef = storageReference.child("QR/Invoice/" + qrName);
                    UploadTask uploadTask = qrRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(), "Error adding QR to Firebase", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "QR added to Firebase", Toast.LENGTH_LONG).show();
                        }
                    });

                    ProgressDialog pDialog = new ProgressDialog(this);
                    pDialog.show();

                    pDialog.setMessage("On Billing !");
                    PdfDocument pdf = new PdfDocument();
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();

                    PdfDocument.Page page = pdf.startPage(pageInfo);
                    Canvas canvas = page.getCanvas();
                    Paint myPaint = new Paint();
                    Paint headText = new Paint();

                    myPaint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawBitmap(scaled, 0, 0, myPaint);

                    headText.setTextAlign(Paint.Align.CENTER);
                    headText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    headText.setTextSize(30);

                    myPaint.setColor(Color.rgb(0, 113, 188));
                    myPaint.setTextSize(20);

                    headText.setTextAlign(Paint.Align.CENTER);
                    headText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    headText.setTextSize(40);

                    canvas.drawText("DEAL ShOP --  INVOICE", pageWidth / 2, 520, headText);
                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    headText.setTextSize(20);
                    canvas.drawText("PHONE : 8870637220", pageWidth - 20, 590, myPaint);

                    canvas.drawText("DATE : " + date11, pageWidth - 20, 640, myPaint);

                    myPaint.setTextAlign(Paint.Align.LEFT);
                    myPaint.setColor(Color.BLACK);
                    myPaint.setTextSize(20);
                    canvas.drawText("Customer Name     : " + customerName, 20, 590, myPaint);
                    canvas.drawText("Customer Contact  : " + customerContact, 20, 640, myPaint);
                    canvas.drawText("Customer Address  : " + customerAddress, 20, 690, myPaint);

                    canvas.drawText("Payment Type      : " + payment, 20, 740, myPaint);
                    myPaint.setStyle(Paint.Style.STROKE);
                    myPaint.setStrokeWidth(2);
                    canvas.drawRect(20, 780, pageWidth - 20, 860, myPaint);

                    myPaint.setTextAlign(Paint.Align.LEFT);
                    myPaint.setStyle(Paint.Style.FILL);
                    canvas.drawText("Quantity", 40, 830, myPaint);
                    canvas.drawText("Product", 200, 830, myPaint);
                    canvas.drawText("Price", 700, 830, myPaint);
                    canvas.drawText("Amount", 900, 830, myPaint);

                    canvas.drawLine(180, 790, 180, 840, myPaint);
                    canvas.drawLine(680, 790, 680, 840, myPaint);
                    canvas.drawLine(880, 790, 880, 840, myPaint);


                    int yQty = 950, yPrt = 950, yCost = 950, yPrice = 950;
                    Double total=0.0;
                    for (Product pr : purchased)
                    {
                        Double cost = pr.getPrice() / pr.getQuantity();
                        total+=pr.getPrice();
                        canvas.drawText(Double.toString(pr.getQuantity()), 40, yQty, myPaint);
                        canvas.drawText(pr.getProductName(), 200, yPrt, myPaint);
                        canvas.drawText(Double.toString(cost), 700, yCost, myPaint);
                        canvas.drawText(Double.toString(pr.getPrice()), 900, yPrice, myPaint);

                        yQty += 30;yPrt += 30;yCost += 30;yPrice += 30;
                    }


                    canvas.drawText("Total "+Double.toString(total),900,yPrice+30,myPaint);
                    canvas.drawText("Bill Location     : "+invoice.billLocation,40,yQty+90,myPaint);

                    myPaint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawBitmap(generateQrCode((customerName+" "+ customerContact+" " + customerAddress
                                    +" "+ payment +" "+ date11+" "+invoice)),
                            40,1300 , myPaint);

                    pdf.finishPage(page);
                    String fileName = "/" + customerName + " " + date11 + ".pdf";
                    File file = new File(Environment.getExternalStorageDirectory(), fileName);
                    pDialog.dismiss();
                    try {
                        pdf.writeTo(new FileOutputStream(file));

                        Toast.makeText(getApplicationContext(), "PDF Created and Downloaded", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {

                        Toast.makeText(getApplicationContext(), "Error creating PDF FAILED PROCESS", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    pdf.close();
                    Intent i = new Intent(getApplicationContext(), AdminPage.class);
                    i.putExtra("user", currentUser);
                    startActivity(i);
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Error  Occured", Toast.LENGTH_LONG).show();
            }
        }
    }
}

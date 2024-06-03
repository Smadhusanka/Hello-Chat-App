package com.example.hello;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hello.model.UserModel;
import com.example.hello.utils.AndroidUtil;
import com.example.hello.utils.FirebaseUtil;

import java.util.List;

public class Emergency extends AppCompatActivity {

    ImageButton backBtn,location;
    EditText message, otherUserNumber;
    Button sendBtn, copyBtn;
    LocationManager locationManager;
    UserModel otherUser;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_emergency);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());

        //assign values to variables
        backBtn = findViewById(R.id.backBtn);
        otherUserNumber = findViewById(R.id.otherUserNumber);
        message = findViewById(R.id.message);
        sendBtn = findViewById(R.id.sendSms);
        location = findViewById(R.id.location);
        copyBtn = findViewById(R.id.copyBtn);

        //back Button
        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        //catch value passing from LoginPhoneNumberActivity
        phoneNumber = getIntent().getExtras().getString("phone");

        //get mobile number of other user
        otherUserNumber.setText(phoneNumber);

        //send message
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(Emergency.this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED){
                    sendSms();
                }
                else {
                    ActivityCompat.requestPermissions(Emergency.this, new String[]{Manifest.permission.SEND_SMS},100);
                }
            }
        });

        //copy Location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission()) {
                    Location location = getLastKnownLocation();
                    if (location != null) {
                        // Create link with latitude and longitude
                        String latitude = String.valueOf(location.getLatitude());
                        String longitude = String.valueOf(location.getLongitude());
                        String link = "https://maps.google.com/?q=" + latitude + "," + longitude;

                        // Copy link to clipboard
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Location Link", link);
                        clipboard.setPrimaryClip(clip);

                        Toast.makeText(Emergency.this, "Location link copied to clipboard", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Emergency.this, "Unable to retrieve location", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Emergency.this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //location open
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission()) {
                    Location location = getLastKnownLocation();
                    if (location != null) {
                        // Create link with latitude and longitude
                        String latitude = String.valueOf(location.getLatitude());
                        String longitude = String.valueOf(location.getLongitude());
                        String link = "https://maps.google.com/?q=" + latitude + "," + longitude;

                        // Open map application with location link
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                        intent.setPackage("com.google.android.apps.maps");
                        startActivity(intent);

                        Toast.makeText(Emergency.this, "Location open", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Emergency.this, "Unable to open location", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Emergency.this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            sendSms();
        }
        else{
            Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    //send sms to other user
    private void sendSms(){

        String phone = otherUserNumber.getText().toString();
        String msg = message.getText().toString();

        if(!phone.isEmpty() && !msg.isEmpty()){
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone,null,msg,null,null);
            Toast.makeText(this, "sms send success", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "please enter msg or mobile number", Toast.LENGTH_SHORT).show();
        }
    }


    //location permission
    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    //last location
    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return TODO;
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l != null && (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy())) {
                // Found best last known location
                bestLocation = l;
            }
        }
        return bestLocation;
    }

}
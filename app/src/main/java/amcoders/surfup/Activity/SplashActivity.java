package amcoders.surfup.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;

import amcoders.surfup.R;

public class SplashActivity extends AppCompatActivity implements LocationListener {

    private static int SPLASH_SCREEN_TIME_OUT=4000;
    private FirebaseAuth mAuth;
    private LocationManager  locationManager;
    private String cityName, BASE_URL;
    String API = "ab0c74db525dca837cf24d22f08b5cd4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        if(ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(SplashActivity.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }

        getlocation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i;
                getlocation();
                if (cityName != null)
                {
                    if (mAuth.getCurrentUser() == null) {
                        i = new Intent(SplashActivity.this,
                                LoginActivity.class);
                    } else {

                        i = new Intent(SplashActivity.this,
                                dashboardActivity.class);
                        i.putExtra("base_url", BASE_URL);
                        i.putExtra("city_name", cityName);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    }
                    startActivity(i);

                    finish();
                }

            }
        }, SPLASH_SCREEN_TIME_OUT);

    }

    @SuppressLint("MissingPermission")
    private void getlocation() {

        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,SplashActivity.this);

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        getlocation();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        double latitude = location.getLatitude();
        double longtitude = location.getLongitude();
        try {
            address = coder.getFromLocation(latitude, longtitude,1);
            Address locationAdd = address.get(0);
            cityName = locationAdd.getLocality();
            BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName +"&appid=" + API + "&units=metric";
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
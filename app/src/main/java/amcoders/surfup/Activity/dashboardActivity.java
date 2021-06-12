package amcoders.surfup.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.GeoPoint;
import com.google.logging.type.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amcoders.surfup.R;

public class dashboardActivity extends AppCompatActivity implements LocationListener {

    private String cityName;
    String API = "ab0c74db525dca837cf24d22f08b5cd4";
    private LocationManager locationManager;
    double latitude, longtitude;
    private List<String> beachList = null;
    private TextView CityNameTV, WeatherDescTV, TempTV, HumidTV, WindspeedTV, GustTV, RiskTV, RecommendationTV;
    private ImageView WeatherIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        if(ContextCompat.checkSelfPermission(dashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(dashboardActivity.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }

        getlocation();

        CityNameTV = findViewById(R.id.city_name);
        WeatherDescTV = findViewById(R.id.weather_field);
        TempTV = findViewById(R.id.temperature_field);
        HumidTV = findViewById(R.id.humidity_field);
        WindspeedTV = findViewById(R.id.speed_field);
        GustTV = findViewById(R.id.gust_field);
        RiskTV = findViewById(R.id.risk_field);
        RecommendationTV = findViewById(R.id.recommendation_field);
        WeatherIcon = findViewById(R.id.weather_icon);

    }



    public void weather(String url)
    {
        getlocation();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string
                        try {
                            JSONObject obj = new JSONObject(response);
                            String imageUrl = "https://openweathermap.org/img/wn/";
                            JSONObject weatherObj = obj.getJSONArray("weather").getJSONObject(0);
                            String desc = weatherObj.getString("description");
                            imageUrl = imageUrl + weatherObj.getString("icon") + "@2x.png";
                            String temp = obj.getJSONObject("main").getString("feels_like");
                            String humidity = obj.getJSONObject("main").getString("humidity");
                            JSONObject windObj = obj.getJSONObject("wind");
                            String speed = windObj.getString("speed");
                            String gust = windObj.getString("gust");
                            String firstLetter=desc.substring(0,1);
                            String remainingLetters=desc.substring(1);
                            WeatherDescTV.setText(firstLetter.toUpperCase() + remainingLetters);
                            TempTV.setText("Feels like " + temp + " °C");
                            HumidTV.setText(humidity + " %");
                            WindspeedTV.setText(speed + " kmph");
                            GustTV.setText(gust + " kmph");
                            Glide.with(getApplicationContext()).load(imageUrl).into(WeatherIcon);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERR",error.getMessage());
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        latitude = location.getLatitude();
        longtitude = location.getLongitude();
        try {
            address = coder.getFromLocation(latitude, longtitude,1);
            Address locationAdd = address.get(0);
            cityName = locationAdd.getLocality();
            CityNameTV.setText(cityName);
            String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName +"&appid=" + API + "&units=metric";
            weather(BASE_URL);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        getlocation();
    }

    @SuppressLint("MissingPermission")
    private void getlocation() {

        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,dashboardActivity.this);

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}
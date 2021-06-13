package amcoders.surfup.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amcoders.surfup.R;

public class dashboardActivity extends AppCompatActivity implements LocationListener {

    private String cityName, baseURL;
    String API = "ab0c74db525dca837cf24d22f08b5cd4";
    private LocationManager locationManager;
    double latitude, longtitude;
    private List<String> beachList = null;
    private TextView CityNameTV, WeatherDescTV, TempTV, HumidTV, WindspeedTV, GustTV, RiskTV, RecommendationTV;
    private ImageView WeatherIcon, SearchButton;
    private BottomNavigationView bottomNavigationView;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private EditText autoCompleteTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);



        CityNameTV = findViewById(R.id.city_name);
        WeatherDescTV = findViewById(R.id.weather_field);
        TempTV = findViewById(R.id.temperature_field);
        HumidTV = findViewById(R.id.humidity_field);
        WindspeedTV = findViewById(R.id.speed_field);
        GustTV = findViewById(R.id.gust_field);
        RiskTV = findViewById(R.id.risk_field);
        RecommendationTV = findViewById(R.id.recommendation_field);
        WeatherIcon = findViewById(R.id.weather_icon);
        bottomNavigationView = findViewById(R.id.bottom_nav_dash);
        autoCompleteTextView = findViewById(R.id.search_input_field);
        SearchButton = findViewById(R.id.search_button);

        baseURL = getIntent().getStringExtra("base_url");
        cityName = getIntent().getStringExtra("city_name");

        CityNameTV.setText(cityName);

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String place=autoCompleteTextView.getText().toString();
                String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=" + place +"&appid=" + API + "&units=metric";
                Intent bIntent = new Intent(dashboardActivity.this, dashboardActivity.class);
                bIntent.putExtra("base_url", BASE_URL);
                bIntent.putExtra("city_name", place);
                startActivity(bIntent);
            }
        });

        weather(baseURL);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.menu_search:
                        Intent sIntent = new Intent(dashboardActivity.this, SearchActivity.class);
                        startActivity(sIntent);
                        return true;
                    case R.id.menu_home:
                        return true;
                    case R.id.menu_profile:
                        Intent pIntent = new Intent(dashboardActivity.this, ProfileViewActivity.class);
                        startActivity(pIntent);
                        return true;
                }
                return false;
            }
        });

    }


    public void weather(String url)
    {
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
                            float surfing_constant = (Float.valueOf(humidity) *60/ 88)+(Float.valueOf(speed)*12/5)+(Float.valueOf(gust)*7) /3;
                            RiskTV.setText(String.valueOf((int) surfing_constant) + " %");
                            WeatherDescTV.setText(firstLetter.toUpperCase() + remainingLetters);
                            TempTV.setText("Feels like " + temp + " °C");
                            HumidTV.setText(humidity + " %");
                            WindspeedTV.setText(speed + " kmph");
                            GustTV.setText(gust + " kmph");
                            if ((int) surfing_constant > 70)
                            {
                                RecommendationTV.setText("It is dangerous to surf out there");
                            }
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


    }






}
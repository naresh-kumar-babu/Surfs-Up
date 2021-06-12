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


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
    double latitude, longtitude;
    private List<String> beachList = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);

        if(ContextCompat.checkSelfPermission(dashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(dashboardActivity.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        new Beach().execute();

    }

    public  class Beach extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... strings) {
            try {
                getlocation();
                Document doc = Jsoup.connect("https://www.google.com/search?q=beaches+near+washington+dc").get();
                Elements uList = doc.select("li.TrT0Xe");
                beachList = uList.eachText();
                for (int i=0; i<beachList.size(); i++)
                {
                    Log.d("BEACH "+ Integer.toString(i) ,beachList.get(i));
                }
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if (beachList != null)
            {
                for (int i = 0; i < beachList.size(); i++) {

                    String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=" + beachList.get(i) +"&appid=" + API;
                    weather(beachList.get(i), BASE_URL);
                }
            }

        }
    }

    public void weather(String location, String url)
    {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string
                        try {
                            String desc = new JSONObject(response).getJSONArray("weather").getJSONObject(0).getString("description");
                            Log.d("Description: ",desc);
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
        Geocoder coder = new Geocoder(getApplicationContext());
        List<Address> address;
        latitude = location.getLatitude();
        longtitude = location.getLongitude();
        try {
            address = coder.getFromLocation(latitude, longtitude,1);
            Address locationAdd = address.get(0);
            cityName = locationAdd.getLocality();
            Log.d("CITY NAME",cityName);
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
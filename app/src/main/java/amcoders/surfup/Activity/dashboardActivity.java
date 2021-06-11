package amcoders.surfup.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amcoders.surfup.R;

public class dashboardActivity extends AppCompatActivity {

    private TextView dash_txt;
    private EditText dash_loc;
    private Button submit;
    String location;
    String API = "ab0c74db525dca837cf24d22f08b5cd4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        dash_txt = findViewById(R.id.dash_txt);
        dash_loc = findViewById(R.id.dash_loc);
        submit = findViewById(R.id.dash_button);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String BASE_URL = "https://api.stormglass.io/v2/weather/point?lat=";
                String Akey = "a01eda1e-ca77-11eb-8d12-0242ac130002-a01eda96-ca77-11eb-8d12-0242ac130002";
                location = dash_loc.getText().toString();

                Geocoder coder = new Geocoder(getApplicationContext());
                List<Address> address;
                GeoPoint p1 = null;
                Double longi = 0.0;
                Double latt = 0.0;
                try {
                    address = coder.getFromLocationName(location, 5);

                    Address location = address.get(0);
                    latt = location.getLatitude();
                    longi = location.getLongitude();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Instant instant = Instant.now();
                String url = BASE_URL + String.valueOf(latt) + "&lng=" + String.valueOf(longi) + "&params=windSpeed,waveHeight,waterTemperature&start="+instant.toString()+"&end="+instant.toString();
                weather(location,Akey,url);
            }
        });
    }

    public void weather(String location, String Akey,String url)
    {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string
                        try {
                            JSONObject jsonObject = new JSONObject(response).getJSONArray("hours").getJSONObject(0).getJSONObject("waterTemperature");
                            String temp = jsonObject.getString("meto");
                            dash_txt.setText("Response: "+temp);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dash_txt.setText(error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders () throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", Akey);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}
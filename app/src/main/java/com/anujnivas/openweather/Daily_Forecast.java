package com.anujnivas.openweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Daily_Forecast extends AppCompatActivity {
    private RecyclerView daily_view_recyclerView;
    private Daily_Forecast_Adapter daily_forecast_adapter;
    private final ArrayList<DailyWeather> dailyWeatherArrayList=new ArrayList<>();
    private SharedPreferences sharedpreferences;
    private String city;
    private String unit;
    private static final String weatherURL = "https://api.openweathermap.org/data/2.5/onecall";
    String icon_code;
    int iconResId;
    private static final String TAG = "Daily_Forecast";
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        daily_view_recyclerView=findViewById(R.id.daily_recycle_view);
        daily_forecast_adapter= new Daily_Forecast_Adapter(dailyWeatherArrayList, this);
        daily_view_recyclerView.setAdapter(daily_forecast_adapter);
        daily_view_recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        sharedpreferences = getSharedPreferences("OpenWeather", Context.MODE_PRIVATE);
        city=sharedpreferences.getString("city","Chicago, Illinois");
        queue = Volley.newRequestQueue(this);
        connect();
    }

    public void connect() {
        Uri.Builder buildUrl = Uri.parse(weatherURL).buildUpon();
        String query_unit="";
        switch (sharedpreferences.getInt("metric",0)){
            case 0:
                query_unit = "imperial";
                unit = "\u00B0F";
                break;
            case 1:
                query_unit = "metric";
                unit = "\u00B0C";
                break;
        }
        double[] lat_lon=getLatLon(city);
        buildUrl.appendQueryParameter("lat", lat_lon==null?"41.8675766":lat_lon[0]+"");
        buildUrl.appendQueryParameter("lon", lat_lon==null?"-87.616232":lat_lon[1]+"");
        buildUrl.appendQueryParameter("appid", "c580cf0281638ecbd6197818a9757b7f");
        buildUrl.appendQueryParameter("exclude", "minutely");
        buildUrl.appendQueryParameter("units", query_unit);
        buildUrl.appendQueryParameter("lang", "en");
        String urlToUse = buildUrl.build().toString();

        Response.Listener<JSONObject> listener = response -> {
            try {

                setTitle(capitalizeAll(city));
                JSONArray daily = response.getJSONArray("daily");
                dailyWeatherArrayList.clear();
                int i = 0;
                while (i < daily.length()) {
                    icon_code = "_"+daily.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon");
                    iconResId = this.getResources().getIdentifier(icon_code,
                            "drawable", this.getPackageName());

                    dailyWeatherArrayList.add(new DailyWeather(daily.getJSONObject(i).getJSONObject("temp").getString("morn"),
                            capitalizeAll(daily.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description")),
                            daily.getJSONObject(i).getJSONObject("temp").getString("morn"),
                            iconResId,
                            response.getString("timezone_offset"),
                            daily.getJSONObject(i).getJSONObject("temp").getString("morn"),
                            daily.getJSONObject(i).getJSONObject("temp").getString("day"),
                            daily.getJSONObject(i).getJSONObject("temp").getString("eve"),
                            daily.getJSONObject(i).getJSONObject("temp").getString("night"),
                            daily.getJSONObject(i).getJSONObject("temp").getString("min"),
                            daily.getJSONObject(i).getJSONObject("temp").getString("max"),
                            daily.getJSONObject(i).getString("uvi"),
                            daily.getJSONObject(i).getString("pop"),
                            daily.getJSONObject(i).getString("dt")));
                    i++;
                }
                daily_forecast_adapter.notifyDataSetChanged();
                Toast.makeText(this, "Updated", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
                Log.d(TAG, "connect: " + e.getMessage());
            }
        };
        Response.ErrorListener error = error1 -> {
            try {
                JSONObject jsonObject = new JSONObject("{}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);
        queue.add(jsonObjectRequest);
    }

    private double[] getLatLon(String userProvidedLocation) {
        Geocoder geocoder = new Geocoder(this);
        // Here, “this” is an Activity
        try { List<Address> address = geocoder.getFromLocationName(userProvidedLocation, 1);
            if (address == null || address.isEmpty())
            {
                // Nothing returned!
                return null;
            }
            double lat = address.get(0).getLatitude();
            double lon = address.get(0).getLongitude();
            return new double[] {lat, lon};
        } catch (IOException e) {
            // Failure to get an Address object
            return null;

        }
    }

    public static String capitalizeAll(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Arrays.stream(str.split("\\s+"))
                .map(t -> t.substring(0, 1).toUpperCase() + t.substring(1))
                .collect(Collectors.joining(" "));
    }

}
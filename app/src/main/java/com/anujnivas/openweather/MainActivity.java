package com.anujnivas.openweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.CalendarContract;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String weatherURL = "https://api.openweathermap.org/data/2.5/onecall";
    private static final String APIKey = "c580cf0281638ecbd6197818a9757b7f";
    protected static String current_timestamp;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static boolean dataLoadedRecently=false;

    private final ArrayList<Weather> weatherHourlyArrayList = new ArrayList<>();
    private RecyclerView hourly_view_recyclerView;
    private WeatherAdapter weatherAdapter;
    private TextView city_tv;
    private TextView time_tv;
    private TextView current_temperature_tv;
    private TextView feels_like_tv;
    private TextView current_temperature_desc_tv;
    private TextView current_wind_speed_desc_tv;
    private TextView current_humidity_desc_tv;
    private TextView current_UV_index_tv;
    private TextView current_visibility_desc_tv;
    private TextView quartely_temp_tv1;
    private TextView quartely_temp_tv2;
    private TextView quartely_temp_tv3;
    private TextView quartely_temp_tv4;
    private TextView quartely_time_tv1;
    private TextView quartely_time_tv2;
    private TextView quartely_time_tv3;
    private TextView quartely_time_tv4;
    private TextView snow_rain_tv;
    private static final String TAG = "MainActivity";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RequestQueue queue;
    SimpleDateFormat jdf;
    SimpleDateFormat title_time;
    private TextView sunset_tv;
    ImageView main_icon;
    ImageView hourly_icon;
    private TextView sunrise_tv;
    SharedPreferences sharedpreferences;
    private String unit = "\u00B0F";
    private String speed_unit = "mph";
    private String visibility_unit = "Miles";
    private static Menu mMenu;
    private String city;
    private String location_lon;
    private String location_lat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);
        city_tv = findViewById(R.id.city_tv);
        time_tv = findViewById(R.id.time_tv);
        swipeRefreshLayout = findViewById(R.id.swiper);
        current_temperature_tv = findViewById(R.id.temperature_tv);
        feels_like_tv = findViewById(R.id.feels_like_tv);
        current_temperature_desc_tv = findViewById(R.id.current_temperature_desc_tv);
        current_wind_speed_desc_tv = findViewById(R.id.current_wind_speed_desc_tv);
        current_humidity_desc_tv = findViewById(R.id.current_humidity_desc_tv);
        current_UV_index_tv = findViewById(R.id.current_UV_index_tv);
        current_visibility_desc_tv = findViewById(R.id.current_visibility_desc_tv);
        quartely_temp_tv1 = findViewById(R.id.quartely_temp_tv1);
        quartely_temp_tv2 = findViewById(R.id.quartely_temp_tv2);
        quartely_temp_tv3 = findViewById(R.id.quartely_temp_tv3);
        quartely_temp_tv4 = findViewById(R.id.quartely_temp_tv4);
        quartely_time_tv1 = findViewById(R.id.quartely_time_tv1);
        quartely_time_tv2 = findViewById(R.id.quartely_time_tv2);
        quartely_time_tv3 = findViewById(R.id.quartely_time_tv3);
        quartely_time_tv4 = findViewById(R.id.quartely_time_tv4);
        snow_rain_tv = findViewById(R.id.snow_rain_tv);
        sunrise_tv = findViewById(R.id.sunrise_tv);
        sunset_tv = findViewById(R.id.sunset_tv);

        jdf = new SimpleDateFormat("HH:mm aa");
        title_time = new SimpleDateFormat("E MMM dd HH:mm a, Y");

        jdf.setTimeZone(TimeZone.getTimeZone("CST6CDT"));
        main_icon=findViewById(R.id.main_icon);
        hourly_icon=findViewById(R.id.hourly_icon);
        sharedpreferences = getSharedPreferences("OpenWeather", Context.MODE_PRIVATE);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                connect();
            }
        });
        connect();
        hourly_view_recyclerView = findViewById(R.id.hourly_view);
        weatherAdapter = new WeatherAdapter(weatherHourlyArrayList, this);
        hourly_view_recyclerView.setAdapter(weatherAdapter);
        hourly_view_recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        setUiElementsInvisible();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu,menu);
        mMenu=menu;
//        try {
//            String temp=sharedpreferences.getString("last_response","");
//            if(temp.length()>0){
//                setUiElements(new JSONObject(temp));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(!hasNetworkConnection()){
            swipeRefreshLayout.setRefreshing(false); // This stops the busy-circle

            Toast.makeText(this,"Disabled! Poor or No Internet Connection!",Toast.LENGTH_LONG).show();
        }else if(R.id.metric_item==item.getItemId()){
            int temp=sharedpreferences.getInt("metric",0);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("metric", (1+temp)%2);
            editor.apply();
            connect();
            switch (sharedpreferences.getInt("metric",0)){
                case 0:
                    item.setIcon(R.drawable.units_f);
                    speed_unit="mph";
                    Toast.makeText(this, "Units set to imperial", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    item.setIcon(R.drawable.units_c);
                    speed_unit="mps";
                    Toast.makeText(this, "Units set to metric", Toast.LENGTH_LONG).show();
                    break;
            }
        }else if(R.id.location_item==item.getItemId()){
            changeLocation();
        }else if(R.id.calender_item==item.getItemId()){
            Intent intent=new Intent(this,Daily_Forecast.class);
            startActivity(intent);
        }else{
            Log.d(TAG, "onOptionsItemSelected: Wrong menu item");
        }
        return super.onOptionsItemSelected(item);
    }

    private double[] getLatLon(String userProvidedLocation) {
        Geocoder geocoder = new Geocoder(this);
        // Here, “this” is an Activity
        try {
            List<Address> address = geocoder.getFromLocationName(userProvidedLocation, 1);
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

    public void setUiElementsInvisible(){

        current_temperature_tv.setVisibility(View.INVISIBLE);
        current_humidity_desc_tv.setVisibility(View.INVISIBLE);
        feels_like_tv.setVisibility(View.INVISIBLE);
        current_temperature_desc_tv.setVisibility(View.INVISIBLE);
        current_wind_speed_desc_tv.setVisibility(View.INVISIBLE);
        main_icon.setVisibility(View.INVISIBLE);
        quartely_temp_tv1.setVisibility(View.INVISIBLE);
        quartely_temp_tv2.setVisibility(View.INVISIBLE);
        quartely_temp_tv3.setVisibility(View.INVISIBLE);
        quartely_temp_tv4.setVisibility(View.INVISIBLE);
        quartely_time_tv1.setVisibility(View.INVISIBLE);
        quartely_time_tv2.setVisibility(View.INVISIBLE);
        quartely_time_tv3.setVisibility(View.INVISIBLE);
        quartely_time_tv4.setVisibility(View.INVISIBLE);
        current_visibility_desc_tv.setVisibility(View.INVISIBLE);
        current_UV_index_tv.setVisibility(View.INVISIBLE);
        current_visibility_desc_tv.setVisibility(View.INVISIBLE);
        sunset_tv.setVisibility(View.INVISIBLE);
        sunrise_tv.setVisibility(View.INVISIBLE);
        snow_rain_tv.setVisibility(View.INVISIBLE);
        if(hourly_view_recyclerView!=null)
            hourly_view_recyclerView.setVisibility(View.INVISIBLE);
    }

    public void setUiElementsVisible(){
        current_temperature_tv.setVisibility(View.VISIBLE);
        current_humidity_desc_tv.setVisibility(View.VISIBLE);
        feels_like_tv.setVisibility(View.VISIBLE);
        current_temperature_desc_tv.setVisibility(View.VISIBLE);
        current_wind_speed_desc_tv.setVisibility(View.VISIBLE);
        main_icon.setVisibility(View.VISIBLE);
        quartely_temp_tv1.setVisibility(View.VISIBLE);
        quartely_temp_tv2.setVisibility(View.VISIBLE);
        quartely_temp_tv3.setVisibility(View.VISIBLE);
        quartely_temp_tv4.setVisibility(View.VISIBLE);
        quartely_time_tv1.setVisibility(View.VISIBLE);
        quartely_time_tv2.setVisibility(View.VISIBLE);
        quartely_time_tv3.setVisibility(View.VISIBLE);
        quartely_time_tv4.setVisibility(View.VISIBLE);
        current_visibility_desc_tv.setVisibility(View.VISIBLE);
        current_UV_index_tv.setVisibility(View.VISIBLE);
        current_visibility_desc_tv.setVisibility(View.VISIBLE);
        sunset_tv.setVisibility(View.VISIBLE);
        sunrise_tv.setVisibility(View.VISIBLE);
        if(hourly_view_recyclerView!=null)
            hourly_view_recyclerView.setVisibility(View.VISIBLE);
    }

    public void connect() {
        if(!hasNetworkConnection()){
            if(!dataLoadedRecently) {
                setUiElementsInvisible();
                city_tv.setText("");
                time_tv.setText("No internet connection");
            }
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false); // This stops the busy-circle
            return;
        }
        Weather.unit=sharedpreferences.getInt("metric",0);
        city=sharedpreferences.getString("city","Chicago, Illinois");
        location_lon=sharedpreferences.getString("location_lon","-87.616232");
        location_lat=sharedpreferences.getString("location_lat","41.8675766");
        Uri.Builder buildUrl = Uri.parse(weatherURL).buildUpon();
        String query_unit="";
        switch (sharedpreferences.getInt("metric",0)){
            case 0:
                query_unit = "imperial";
                unit = "\u00B0F";
                speed_unit="mph";
                visibility_unit="Miles";
                break;
            case 1:
                query_unit = "metric";
                unit = "\u00B0C";
                speed_unit="mps";
                visibility_unit="KM's";
                break;
        }

        buildUrl.appendQueryParameter("lat",location_lat);
        buildUrl.appendQueryParameter("lon", location_lon);
        buildUrl.appendQueryParameter("appid", APIKey);
        buildUrl.appendQueryParameter("exclude", "minutely");
        buildUrl.appendQueryParameter("units", query_unit);
        buildUrl.appendQueryParameter("lang", "en");
        String urlToUse = buildUrl.build().toString();
        Response.Listener<JSONObject> listener = response -> {
            try {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("last_response", response.toString());
                editor.apply();
                dataLoadedRecently=true;
                setUiElements(response);
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

    private String getDirection(double degrees) {
        if (degrees >= 337.5 || degrees < 22.5)
            return "N";
        if (degrees >= 22.5 && degrees < 67.5) return "NE";
        if (degrees >= 67.5 && degrees < 112.5) return "E";
        if (degrees >= 112.5 && degrees < 157.5) return "SE";
        if (degrees >= 157.5 && degrees < 202.5) return "S";
        if (degrees >= 202.5 && degrees < 247.5) return "SW";
        if (degrees >= 247.5 && degrees < 292.5) return "W";
        if (degrees >= 292.5 && degrees < 337.5) return "NW";
        return "X"; // We'll use 'X' as the default if we get a bad value
        // }
    }

    public static String capitalizeAll(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Arrays.stream(str.split("\\s+"))
                .map(t -> t.substring(0, 1).toUpperCase() + t.substring(1))
                .collect(Collectors.joining(" "));
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    public void changeLocation() {
        // Single input value dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create an edittext and set it to be the builder's view
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);

        // lambda can be used here (as is below)
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                double[] location=getLatLon(et.getText().toString());
                if(location==null){
                    Toast.makeText(MainActivity.this, "Unable to determine the location right now! Please try again in sometime.",Toast.LENGTH_LONG).show();
                }else{
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    location_lon=location[1]+"";
                    location_lat=location[0]+"";
                    String city_name=doLocationName();
                    if(city_name.length()==0){
                        city_name=et.getText().toString();
                    }
                    editor.putString("location_lon", location[1]+"");
                    editor.putString("location_lat", location[0]+"");
                    editor.putString("city", city_name);
                    editor.apply();
                    connect();
                }

            }
        });
        // lambda can be used here (as is below)
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               //Do Nothing
            }
        });

        builder.setMessage("For US locations, enter as 'City', or 'City,State'\n\nFor international locations ener as 'City, Country'");
        builder.setTitle("Enter a Location");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public String doLocationName() {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> arr =geocoder.getFromLocation(Double.parseDouble(location_lat), Double.parseDouble(location_lon),10);
            if(arr.size()==0){
                Toast.makeText(this, "Empty location name returned from GeoCoder.", Toast.LENGTH_LONG).show();
                return "";
            }
            Address address=arr.get(0);
            String temp = (address.getLocality()==null?(address.getAdminArea()!=null?address.getAdminArea()+", ":""):address.getLocality()+", ")+address.getCountryName();
            if(address.getCountryCode().equals("US"))
                temp = address.getLocality()+", "+address.getAdminArea();
            return temp;
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return "";
    }


    protected static String formatDate(String format,Long time, Long timezone_offset){
        LocalDateTime ldt = LocalDateTime.ofEpochSecond(time + timezone_offset, 0, ZoneOffset.UTC);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format, Locale.getDefault());
        return ldt.format(dtf);
    }

    @Override
    public void onClick(View view) {
        //https://developer.android.com/guide/topics/providers/calendar-provider.html#intent-view
        int position = hourly_view_recyclerView.getChildLayoutPosition(view);
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");

        Long startMillis=(Long.parseLong(weatherHourlyArrayList.get(position).getTime())*1000L)+Long.parseLong(weatherHourlyArrayList.get(position).getTimeZone());
        ContentUris.appendId(builder, startMillis);
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,startMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,startMillis+5000)
                .setData(builder.build());
        startActivity(intent);
    }

    public void setUiElements(JSONObject response) throws JSONException {
        switch (sharedpreferences.getInt("metric",0)){
            case 0:
                mMenu.getItem(0).setIcon(R.drawable.units_f);
                break;
            case 1:
                mMenu.getItem(0).setIcon(R.drawable.units_c);
                break;
        }
        current_timestamp=response.getJSONObject("current").getString("dt");
        city_tv.setText(capitalizeAll(city));
        current_temperature_tv.setText(capitalizeAll(response.getJSONObject("current").getString("temp").split(Pattern.quote("."))[0] + unit));
        current_humidity_desc_tv.setText(capitalizeAll("Humidity: " + response.getJSONObject("current").getString("humidity") + "%"));
        current_UV_index_tv.setText("UV Index: " + response.getJSONObject("current").getString("uvi"));
        Double visibility=Double.parseDouble(response.getJSONObject("current").getString("visibility"))/1000;
        if(sharedpreferences.getInt("metric",0)==0){
            visibility=visibility/1.609;
        }
        current_visibility_desc_tv.setText("Visibility: " +  df.format(visibility) + " "+visibility_unit);
        feels_like_tv.setText("Feels Like " + response.getJSONObject("current").getString("feels_like").split(Pattern.quote("."))[0] + unit);
        sunset_tv.setText("Sunset: " +formatDate("hh:mm a",Long.parseLong(response.getJSONObject("current").getString("sunset")),Long.parseLong(response.getString("timezone_offset"))));
        sunrise_tv.setText("Sunrise: "+formatDate("hh:mm a",Long.parseLong(response.getJSONObject("current").getString("sunrise")),Long.parseLong(response.getString("timezone_offset"))));
        String snow_info="";
        if(response.getJSONObject("current").has("snow")){
            snow_rain_tv.setVisibility(View.VISIBLE);
            snow_info="Snow in last 1 hr: "+response.getJSONObject("current").getJSONObject("snow").getString("1h")+"mm";
        }
        if(response.getJSONObject("current").has("rain")){
            if(snow_info.length()>0)
                snow_info+="\n";
            snow_rain_tv.setVisibility(View.VISIBLE);
            snow_info+="Rain in last 1 hr: "+response.getJSONObject("current").getJSONObject("rain").getString("1h")+"mm";
        }
        snow_rain_tv.setText(snow_info);
        String wind_gust = "";
        if(response.getJSONObject("current").has("wind_gust")){
            wind_gust=", "+response.getJSONObject("current").get("wind_gust")+" "+speed_unit+" gusts";
        }
        String speed = response.getJSONObject("current").getString("wind_speed");


        String direction = getDirection(Double.parseDouble(response.getJSONObject("current").getString("wind_deg")));
        speed="Winds: "+direction+" at " + speed + " "+speed_unit;
        if(wind_gust.length()>0)
            speed+=wind_gust;
        current_wind_speed_desc_tv.setText(speed);
        String clouds_percentage = response.getJSONObject("current").getString("clouds");
        String cloud_info = response.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("description");
        if(Integer.parseInt(clouds_percentage)>0){
            cloud_info+= " ("+clouds_percentage+"% clouds)";
        }
        current_temperature_desc_tv.setText(capitalizeAll(cloud_info));
        String date=formatDate("EEE MMM dd h:mm a, yyyy",Long.parseLong(response.getJSONObject("current").getString("dt")),Long.parseLong(response.getString("timezone_offset")));
        time_tv.setText(date);

        String icon_code = "_"+response.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("icon");
        int iconResId = this.getResources().getIdentifier(icon_code,"drawable", this.getPackageName());
        main_icon.setImageResource(iconResId);

        quartely_temp_tv1.setText(response.getJSONArray("daily").getJSONObject(0).getJSONObject("temp").getString("morn").split(Pattern.quote("."))[0] + unit);
        quartely_temp_tv2.setText(response.getJSONArray("daily").getJSONObject(0).getJSONObject("temp").getString("day").split(Pattern.quote("."))[0] + unit);
        quartely_temp_tv3.setText(response.getJSONArray("daily").getJSONObject(0).getJSONObject("temp").getString("eve").split(Pattern.quote("."))[0] + unit);
        quartely_temp_tv4.setText(response.getJSONArray("daily").getJSONObject(0).getJSONObject("temp").getString("night").split(Pattern.quote("."))[0] + unit);
//        quartely_time_tv1.setText("8am");
//        quartely_time_tv2.setText("1pm");
//        quartely_time_tv3.setText("5pm");
//        quartely_time_tv4.setText("11pm");

        JSONArray hourly = response.getJSONArray("hourly");
        int i = 0;
        weatherHourlyArrayList.clear();
        while (i < hourly.length()) {
            icon_code = "_"+hourly.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon");
            iconResId = this.getResources().getIdentifier(icon_code,
                    "drawable", this.getPackageName());
            weatherHourlyArrayList.add(new Weather(hourly.getJSONObject(i).getString("temp"),
                    capitalizeAll(hourly.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description")),
                    hourly.getJSONObject(i).getString("dt"),iconResId,response.getString("timezone_offset")));
            i++;
        }
        weatherAdapter.notifyDataSetChanged();
        setUiElementsVisible();
        swipeRefreshLayout.setRefreshing(false); // This stops the busy-circle

    }
}

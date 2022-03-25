package com.anujnivas.openweather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherHorizontalViewHolder> {
    private final List<Weather> weatherList;
    private  final MainActivity mainActivity;

    WeatherAdapter(List<Weather> weatherList,MainActivity mainActivity){
        this.weatherList=weatherList;
        this.mainActivity=mainActivity;
    }
    @NonNull
    @Override
    public WeatherHorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly_weather_entry,parent,false);

        itemView.setOnClickListener(mainActivity);
        return new WeatherHorizontalViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherHorizontalViewHolder holder, int position) {
        String unit="\u00B0F";
        switch (Weather.unit){
            case 0:
                unit = "\u00B0F";
                break;
            case 1:
                unit = "\u00B0C";
        }

        String display_day=MainActivity.formatDate("EEEE",Long.parseLong(weatherList.get(position).getTime()),Long.parseLong(weatherList.get(position).getTimeZone()));
        String today=MainActivity.formatDate("EEEE",Long.parseLong(MainActivity.current_timestamp),Long.parseLong(weatherList.get(position).getTimeZone()));
        String time=MainActivity.formatDate("hh:mm a",Long.parseLong(weatherList.get(position).getTime()),Long.parseLong(weatherList.get(position).getTimeZone()));

        if(today.equals(display_day)){
            display_day="Today";
        }

        holder.day_tv_hourly.setText(display_day);
        holder.temp.setText(weatherList.get(position).getTemperature()+unit);
        holder.temp_desc.setText(weatherList.get(position).getTemperature_desc());
        holder.time.setText(time);
        holder.icon.setImageResource(weatherList.get(position).getIcon());
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }
}
